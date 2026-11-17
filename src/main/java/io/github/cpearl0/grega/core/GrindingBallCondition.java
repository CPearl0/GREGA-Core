package io.github.cpearl0.grega.core;

import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.condition.RecipeCondition;
import com.gregtechceu.gtceu.api.recipe.condition.RecipeConditionType;
import com.gregtechceu.gtceu.api.recipe.kind.GTRecipe;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ItemStackTexture;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/** Keep the catalyst present until an enhanced operation has actually completed. */
public final class GrindingBallCondition extends RecipeCondition<GrindingBallCondition> {
    private static final MapCodec<GrindingBallCondition> CODEC = RecordCodecBuilder.mapCodec(instance ->
            RecipeCondition.isReverse(instance).apply(instance, GrindingBallCondition::new));
    private static RecipeConditionType<GrindingBallCondition> type;

    public GrindingBallCondition() {}

    private GrindingBallCondition(boolean reverse) {
        super(reverse);
    }

    public static void register() {
        type = GTRegistries.register(GTRegistries.RECIPE_CONDITIONS, CoreContent.id("grinding_ball"),
                new RecipeConditionType<>(GrindingBallCondition::new, CODEC));
    }

    @Override
    public RecipeConditionType<GrindingBallCondition> getType() {
        return type;
    }

    @Override
    public Component getTooltips() {
        return Component.translatable("grega_core.recipe.condition.grinding_ball",
                Component.translatable("item.grega_core.elementium_grinding_ball"));
    }

    @Override
    public IGuiTexture getValidTexture() {
        return new ItemStackTexture(new ItemStack(CoreContent.item("grega_core:elementium_grinding_ball")));
    }

    @Override
    public IGuiTexture getInValidTexture() {
        return getValidTexture();
    }

    @Override
    protected boolean testCondition(@NotNull GTRecipe recipe, @NotNull RecipeLogic logic) {
        return GrindingBalls.isElectricMacerator(logic.machine.self())
                && GrindingBalls.hasBall(logic.machine.self());
    }

    @Override
    public GrindingBallCondition createTemplate() {
        return new GrindingBallCondition();
    }
}
