package io.github.cpearl0.grega.core;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.gregtechceu.gtceu.common.recipe.builder.GTRecipeBuilder;
import com.gregtechceu.gtceu.data.recipe.GTRecipes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Consumer;

import static io.github.cpearl0.grega.core.CoreContent.item;

/** Filters obsolete GT recipes while preserving GT solder and research generation. */
public final class PackRecipeChanges {
    private PackRecipeChanges() {}

    public static void install() {
        for (var type : BuiltInRegistries.RECIPE_TYPE) {
            if (!(type instanceof GTRecipeType gt)) continue;
            var upstream = gt.getRecipeBuilder().onSave;
            gt.onRecipeBuild((builder, output) -> {
                if (builder.id.getNamespace().equals("gtceu")) filterLegacyRecipes(builder);
                if (upstream != null) upstream.accept(builder, output);
            });
        }
    }

    public static void removeRecipes(Consumer<ResourceLocation> remove) {
        // IGTAddon.removeRecipes also removes other mods' datapack recipes before decoding.
        // These are all inscriber-type recipes and the machine recipe in the pinned AE2 19.2.19.
        for (String id : new String[] {
                "create:crafting/kinetics/mechanical_press",
                "ars_nouveau:novice_spell_book",
                "ars_nouveau:novice_spellbook_alt",
                "ae2:inscriber/calculation_processor",
                "ae2:inscriber/calculation_processor_press",
                "ae2:inscriber/calculation_processor_print",
                "ae2:inscriber/certus_quartz_dust",
                "ae2:inscriber/ender_dust",
                "ae2:inscriber/engineering_processor",
                "ae2:inscriber/engineering_processor_press",
                "ae2:inscriber/engineering_processor_print",
                "ae2:inscriber/fluix_dust",
                "ae2:inscriber/logic_processor",
                "ae2:inscriber/logic_processor_press",
                "ae2:inscriber/logic_processor_print",
                "ae2:inscriber/silicon_press",
                "ae2:inscriber/silicon_print",
                "ae2:inscriber/sky_stone_dust",
                "ae2:network/blocks/inscribers"
        }) remove.accept(ResourceLocation.parse(id));
        remove.accept(ResourceLocation.parse("gtceu:shaped/vacuum_tube"));
        remove.accept(ResourceLocation.parse("gtceu:shaped/electronic_circuit_lv"));
        // Keep machine-made hammers, but require Create-pressed plates for the first hand tool.
        for (var material : GTCEuAPI.materialManager) {
            remove.accept(ResourceLocation.parse("gtceu:shaped/hammer_" + material.getName()));
        }
    }

    private static boolean contains(List<Content> contents, String id) {
        ItemStack stack = new ItemStack(item(id));
        return contents.stream().anyMatch(c -> ItemRecipeCapability.CAP.of(c.content).ingredient().test(stack));
    }

    private static boolean family(List<Content> outputs, String family) {
        for (String suffix : new String[] { "", "_assembly", "_computer", "_mainframe" }) {
            if (contains(outputs, "gtceu:" + family + "_processor" + suffix)) return true;
        }
        return false;
    }

    private static void filterLegacyRecipes(GTRecipeBuilder recipe) {
        var outputs = recipe.output.getOrDefault(ItemRecipeCapability.CAP, List.of());
        if (contains(outputs, "gtceu:vacuum_tube") || contains(outputs, "gtceu:microchip_processor")
                || family(outputs, "micro") || family(outputs, "nano")) {
            // GT's recipe consumer filters these IDs after onRecipeBuild, including generated variants.
            // Core provides the replacement micro/nano route with no legacy-voltage alternatives.
            GTRecipes.RECIPE_FILTERS.add(recipe.id.withPrefix(recipe.recipeType.registryName.getPath() + "/"));
        }
    }
}
