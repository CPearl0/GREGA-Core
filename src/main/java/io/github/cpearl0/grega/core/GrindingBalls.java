package io.github.cpearl0.grega.core;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.SimpleTieredMachine;
import com.gregtechceu.gtceu.api.machine.WorkableTieredMachine;
import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.gregtechceu.gtceu.api.recipe.kind.GTRecipe;
import com.gregtechceu.gtceu.api.tag.TagPrefix;
import com.gregtechceu.gtceu.common.recipe.builder.GTRecipeBuilder;
import com.gregtechceu.gtceu.data.machine.GTMachines;
import com.gregtechceu.gtceu.data.recipe.GTRecipeTypes;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Optional macerator recipes with an Elementium grinding medium. */
public final class GrindingBalls {
    public static final int DURABILITY = 64;
    private static final String ENHANCED = "grega_core:grinding_ball";
    private static final String HAS_VARIANT = "grega_core:grinding_ball_variant";
    private static final String BASE_DURATION = "grega_core:grinding_ball_base_duration";
    private static final ResourceLocation BALL_ID = CoreContent.id("elementium_grinding_ball");
    private static final Set<MachineDefinition> ELECTRIC_MACERATORS = new HashSet<>();
    private static boolean installed;

    private GrindingBalls() {}

    public static void install() {
        if (installed) return;
        installed = true;
        var type = GTRecipeTypes.MACERATOR_RECIPES;
        int originalInputs = type.getMaxInputs(ItemRecipeCapability.CAP);
        type.setMaxSize(IO.IN, ItemRecipeCapability.CAP, originalInputs + 1);

        // Preserve GREGA's filters and GT's own generation callbacks.
        var upstream = type.getRecipeBuilder().onSave;
        type.onRecipeBuild((builder, output) -> {
            if (upstream != null) upstream.accept(builder, output);
            deriveRecipe(builder, output, originalInputs);
        });

        for (var definition : GTMachines.MACERATOR) {
            if (definition == null) continue;
            ELECTRIC_MACERATORS.add(definition);
            var originalLimits = new Object2IntOpenHashMap<RecipeCapability<?>>(definition.getRecipeOutputLimits());
            var originalModifier = definition.getRecipeModifier();
            // fullModifyRecipe trims before invoking the modifier. Keep the full recipe
            // available here, then apply the original limits only to ordinary recipes.
            definition.getRecipeOutputLimits().put(ItemRecipeCapability.CAP,
                    type.getMaxOutputs(ItemRecipeCapability.CAP));
            definition.setRecipeModifier((machine, recipe) -> candidate -> {
                boolean enhanced = isEnhanced(candidate);
                boolean hasBall = hasBall(machine);
                if (enhanced && !hasBall) return null;
                if (!enhanced && hasBall && candidate.data.getBoolean(HAS_VARIANT)) return null;
                var limited = enhanced ? candidate.copy() : RecipeHelper.trimRecipeOutputs(candidate, originalLimits);
                // Calculate GT's overclock from the original duration, so its EU/t
                // and OC count remain unchanged even near the one-tick limit.
                if (enhanced) limited.duration = limited.data.getInt(BASE_DURATION);
                var modified = originalModifier.applyModifier(machine, limited);
                if (enhanced && modified != null) {
                    modified.duration = Math.max(1, (modified.duration + 1) / 2);
                }
                return modified;
            });
        }

        // Steam shares the larger input inventory but must never run enhanced recipes.
        for (var definition : List.of(GTMachines.STEAM_MACERATOR.left(),
                GTMachines.STEAM_MACERATOR.right())) {
            var originalModifier = definition.getRecipeModifier();
            definition.setRecipeModifier((machine, recipe) -> candidate ->
                    isEnhanced(candidate) ? null : originalModifier.applyModifier(machine, candidate));
        }
    }

    private static void deriveRecipe(GTRecipeBuilder original, RecipeOutput output, int originalInputs) {
        if (original.data.getBoolean(ENHANCED)) return;
        var inputs = original.input.getOrDefault(ItemRecipeCapability.CAP, List.of());
        if (inputs.isEmpty() || inputs.size() > originalInputs) return;
        original.data.putBoolean(HAS_VARIANT, true);
        var variant = original.copy(CoreContent.id("grinding_ball/" + original.id.getNamespace()
                + "/" + original.id.getPath())).onSave(null);
        variant.data.putBoolean(ENHANCED, true);
        variant.data.putInt(BASE_DURATION, original.duration);

        // Keep the material in the first displayed slot and the ball in the second.
        // GrindingBallRecipeLookupMixin changes only the ingredient-tree order.
        variant.notConsumable(CoreContent.item(BALL_ID.toString()));
        variant.duration(Math.max(1, (original.duration + 1) / 2));
        var itemOutputs = variant.output.get(ItemRecipeCapability.CAP);
        if (itemOutputs != null) {
            itemOutputs.replaceAll(content -> {
                var copy = content.copy(ItemRecipeCapability.CAP);
                if (copy.chance <= 0 || copy.chance >= copy.maxChance) return copy;
                return new Content(copy.content, (int) Math.min(copy.maxChance, 2L * copy.chance),
                        copy.maxChance, (int) Math.min(copy.maxChance, 2L * copy.tierChanceBoost));
            });
        }
        // Non-consumables are matched at startup; also guard removal mid-run.
        variant.conditions.add(new GrindingBallCondition());
        variant.save(output);
    }

    public static boolean isElectricMacerator(MetaMachine machine) {
        return machine instanceof SimpleTieredMachine && ELECTRIC_MACERATORS.contains(machine.getDefinition());
    }

    public static boolean hasBall(MetaMachine machine) {
        return findBall(machine) >= 0;
    }

    private static int findBall(MetaMachine machine) {
        if (!(machine instanceof WorkableTieredMachine workable)) return -1;
        for (int slot = 0; slot < workable.importItems.getSlots(); slot++) {
            var stack = workable.importItems.getStackInSlot(slot);
            if (stack.is(CoreContent.item(BALL_ID.toString()))
                    && stack.getDamageValue() < stack.getMaxDamage()) return slot;
        }
        return -1;
    }

    public static boolean isEnhanced(GTRecipe recipe) {
        return recipe.data.getBoolean(ENHANCED);
    }

    public static void onRecipeFinished(IRecipeLogicMachine machine, GTRecipe recipe) {
        var metaMachine = machine.self();
        if (recipe == null || !isEnhanced(recipe) || !isElectricMacerator(metaMachine)
                || !(metaMachine.getLevel() instanceof ServerLevel level)) return;
        var macerator = (SimpleTieredMachine) metaMachine;
        int slot = findBall(macerator);
        if (slot < 0) return;
        double wearChance = (10.0 + 1.5 * Math.max(0, macerator.getTier() - GTValues.LV)) / 100.0;
        if (level.random.nextDouble() >= Math.min(1.0, wearChance)) return;
        var ball = macerator.importItems.getStackInSlot(slot).copy();
        int damage = ball.getDamageValue() + 1;
        if (damage >= ball.getMaxDamage()) {
            macerator.importItems.setStackInSlot(slot, ItemStack.EMPTY);
        } else {
            ball.setDamageValue(damage);
            macerator.importItems.setStackInSlot(slot, ball);
        }
    }

    public static void addRecipes(RecipeOutput output) {
        GTRecipeTypes.LATHE_RECIPES.recipeBuilder(CoreContent.id("elementium_grinding_ball"))
                .inputItems(TagPrefix.block, CoreMaterials.ELEMENTIUM)
                .outputItems(CoreContent.item(BALL_ID.toString()))
                .duration(400).EUt(GTValues.VA[GTValues.LV]).save(output);
    }
}
