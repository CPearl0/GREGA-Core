package io.github.cpearl0.grega.core;

import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;
import com.gregtechceu.gtceu.data.recipe.GTCraftingComponents;
import com.gregtechceu.gtceu.data.tag.CustomTags;
import com.gregtechceu.gtceu.api.material.material.stack.MaterialEntry;
import com.gregtechceu.gtceu.api.tag.TagPrefix;
import com.gregtechceu.gtceu.data.material.GTMaterials;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import static io.github.cpearl0.grega.core.CoreContent.item;

/** Create plates, steam processors, then steel-bound Ars travel. */
public final class EarlyProgressionRecipes {
    private EarlyProgressionRecipes() {}
    private static ItemStack stack(String id) { return item(id).getDefaultInstance(); }
    private static TagKey<Item> tag(String id) { return TagKey.create(Registries.ITEM, ResourceLocation.parse(id)); }

    public static void addRecipes(RecipeOutput out) {
        VanillaRecipeHelper.addShapelessRecipe(out, CoreContent.id("early/iron_nuggets"),
                new ItemStack(item("minecraft:iron_nugget"), 9), tag("c:ingots/iron"));
        VanillaRecipeHelper.addShapedRecipe(out, CoreContent.id("early/mechanical_press"), stack("create:mechanical_press"),
                " S ", " C ", "III", 'S', item("create:shaft"), 'C', item("create:andesite_casing"), 'I', tag("c:ingots/iron"));
        VanillaRecipeHelper.addShapedRecipe(out, CoreContent.id("early/pressed_iron_hammer"), stack("gtceu:iron_hammer"),
                "PP ", "PPS", "PP ", 'P', item("create:iron_sheet"), 'S', tag("c:rods/wooden"));
        VanillaRecipeHelper.addShapedRecipe(out, CoreContent.id("early/steel_bound_spell_book"), stack("ars_nouveau:novice_spell_book"),
                "fMh", "SBS", "LGR", 'M', new MaterialEntry(TagPrefix.dust, CoreMaterials.MANASTEEL),
                'S', tag("c:plates/steel"), 'B', item("grega_core:mana_book"),
                'L', tag("c:dusts/lapis"), 'G', tag("c:dusts/gold"), 'R', tag("c:dusts/redstone"));
        VanillaRecipeHelper.addShapedRecipe(out, CoreContent.id("early/steam_inscriber"), stack("grega_core:steam_inscriber"),
                "XPX", "XMX", "XPX", 'X', new MaterialEntry(TagPrefix.pipeSmallFluid, GTMaterials.Bronze),
                'P', CustomTags.PISTONS, 'M', item("gtceu:bronze_machine_casing"));
        // The forming press pattern: two pistons, two circuits, four cables and one tier-matched hull.
        for (var machine : CoreContent.ELECTRIC_INSCRIBERS) {
            if (machine == null) continue;
            int tier = machine.getTier();
            VanillaRecipeHelper.addShapedRecipe(out, true, CoreContent.id("machines/" + machine.getName()),
                    machine.asStack(), "WPW", "CMC", "WPW",
                    'M', GTCraftingComponents.HULL.get(tier), 'P', GTCraftingComponents.PISTON.get(tier),
                    'C', GTCraftingComponents.CIRCUIT.get(tier), 'W', GTCraftingComponents.CABLE.get(tier));
        }
        // GT's h/f/x/w symbols resolve the actual tool tags and preserve damaged crafting remainders.
        VanillaRecipeHelper.addShapedRecipe(out, CoreContent.id("early/basic_electronic_component"), stack("grega_core:basic_electronic_component"),
                "hfx", "LCE", "RSR", 'L', item("ae2:logic_processor"), 'C', item("ae2:calculation_processor"),
                'E', item("ae2:engineering_processor"), 'R', item("gtceu:red_alloy_single_cable"), 'S', tag("c:plates/steel"));
        VanillaRecipeHelper.addShapedRecipe(out, CoreContent.id("early/electronic_circuit_lv"), stack("gtceu:basic_electronic_circuit"),
                "RPR", "VBV", "CCC", 'R', item("gtceu:resistor"), 'P', tag("c:plates/steel"),
                'V', item("grega_core:basic_electronic_component"), 'B', item("gtceu:resin_printed_circuit_board"),
                'C', item("gtceu:red_alloy_single_cable"));
    }
}
