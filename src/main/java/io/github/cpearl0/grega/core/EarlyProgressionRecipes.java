package io.github.cpearl0.grega.core;

import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;
import com.gregtechceu.gtceu.data.recipe.GTCraftingComponents;
import com.gregtechceu.gtceu.data.tag.CustomTags;
import com.gregtechceu.gtceu.api.material.material.stack.MaterialEntry;
import com.gregtechceu.gtceu.api.tag.TagPrefix;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.item.tool.GTToolType;
import com.gregtechceu.gtceu.api.item.tool.ToolHelper;
import com.gregtechceu.gtceu.api.material.ChemicalHelper;
import com.gregtechceu.gtceu.api.material.material.info.MaterialFlags;
import com.gregtechceu.gtceu.api.material.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.data.material.GTMaterials;
import com.gregtechceu.gtceu.data.recipe.GTRecipeTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

import static io.github.cpearl0.grega.core.CoreContent.item;

/** Create plates, steam processors, then steel-bound Ars travel. */
public final class EarlyProgressionRecipes {
    static final Map<String, String> PETAL_APOTHECARIES = Map.ofEntries(
            Map.entry("petal_apothecary", "minecraft:cobblestone"),
            Map.entry("mossy_petal_apothecary", "minecraft:mossy_cobblestone"),
            Map.entry("livingrock_petal_apothecary", "botania:livingrock"),
            Map.entry("deepslate_petal_apothecary", "minecraft:cobbled_deepslate"),
            Map.entry("blackstone_petal_apothecary", "minecraft:blackstone"),
            Map.entry("nether_brick_petal_apothecary", "minecraft:nether_bricks"),
            Map.entry("red_nether_brick_petal_apothecary", "minecraft:red_nether_bricks"),
            Map.entry("cataclasite_petal_apothecary", "botania:cobbled_cataclasite"),
            Map.entry("fuchsite_petal_apothecary", "botania:cobbled_fuchsite"),
            Map.entry("gneiss_petal_apothecary", "botania:cobbled_gneiss"),
            Map.entry("lunite_petal_apothecary", "botania:cobbled_lunite"),
            Map.entry("mycelite_petal_apothecary", "botania:cobbled_mycelite"),
            Map.entry("rosy_talc_petal_apothecary", "botania:cobbled_rosy_talc"),
            Map.entry("solite_petal_apothecary", "botania:cobbled_solite"),
            Map.entry("talc_petal_apothecary", "botania:cobbled_talc"));

    private EarlyProgressionRecipes() {}
    private static ItemStack stack(String id) { return item(id).getDefaultInstance(); }
    private static TagKey<Item> tag(String id) { return TagKey.create(Registries.ITEM, ResourceLocation.parse(id)); }

    public static void addRecipes(RecipeOutput out) {
        addPetalApothecaryRecipes(out);
        VanillaRecipeHelper.addShapelessRecipe(out, CoreContent.id("early/iron_nuggets"),
                new ItemStack(item("minecraft:iron_nugget"), 9), tag("c:ingots/iron"));
        // A mortar route is needed before the first Create press and GT forge hammer exist.
        VanillaRecipeHelper.addShapedRecipe(out, CoreContent.id("early/andesite_dust"),
                ChemicalHelper.get(TagPrefix.dust, GTMaterials.Andesite), "A", "m", 'A', item("minecraft:andesite"));
        VanillaRecipeHelper.addShapelessRecipe(out, CoreContent.id("early/andesite_alloy_dust"),
                ChemicalHelper.get(TagPrefix.dust, CoreMaterials.ANDESITE_ALLOY, 3),
                new MaterialEntry(TagPrefix.dust, GTMaterials.Andesite),
                new MaterialEntry(TagPrefix.dust, GTMaterials.Andesite),
                new MaterialEntry(TagPrefix.dust, GTMaterials.Andesite),
                new MaterialEntry(TagPrefix.dust, GTMaterials.Zinc));
        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(CoreContent.id("early/andesite_alloy_dust"))
                .inputItems(TagPrefix.dust, GTMaterials.Andesite, 3)
                .inputItems(TagPrefix.dust, GTMaterials.Zinc)
                .outputItems(TagPrefix.dust, CoreMaterials.ANDESITE_ALLOY, 4)
                .duration(200).EUt(GTValues.VA[GTValues.ULV]).save(out);
        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(CoreContent.id("early/fireclay_dust"))
                .inputItems(TagPrefix.dust, GTMaterials.Brick)
                .inputItems(TagPrefix.dust, GTMaterials.Clay)
                .outputItems(TagPrefix.dust, GTMaterials.Fireclay, 2)
                .duration(100).EUt(GTValues.VA[GTValues.ULV]).save(out);
        VanillaRecipeHelper.addShapedRecipe(out, CoreContent.id("early/mechanical_press"), stack("create:mechanical_press"),
                " S ", " C ", "III", 'S', item("create:shaft"), 'C', item("create:andesite_casing"), 'I', tag("c:ingots/iron"));
        VanillaRecipeHelper.addShapedRecipe(out, CoreContent.id("early/mechanical_mixer"), stack("create:mechanical_mixer"),
                "G", "C", "R", 'G', item("create:cogwheel"), 'C', item("create:andesite_casing"),
                'R', new MaterialEntry(TagPrefix.rotor, CoreMaterials.ANDESITE_ALLOY));
        VanillaRecipeHelper.addShapedRecipe(out, CoreContent.id("early/cogwheel"), stack("create:cogwheel"),
                "hPf", " S ", 'P', tag("minecraft:planks"), 'S', item("create:shaft"));
        VanillaRecipeHelper.addShapedRecipe(out, CoreContent.id("early/large_cogwheel"), stack("create:large_cogwheel"),
                "hPf", "PS ", 'P', tag("minecraft:planks"), 'S', item("create:shaft"));
        VanillaRecipeHelper.addShapedRecipe(out, CoreContent.id("early/pressed_iron_hammer"), stack("gtceu:iron_hammer"),
                "PP ", "PPS", "PP ", 'P', item("create:iron_sheet"), 'S', tag("c:rods/wooden"));
        addPlateHammerRecipes(out);
        VanillaRecipeHelper.addShapedRecipe(out, CoreContent.id("early/steel_bound_spell_book"), stack("ars_nouveau:novice_spell_book"),
                "fMh", "SBS", "LGR", 'M', item("botania:mana_powder"),
                'S', new MaterialEntry(TagPrefix.plate, CoreMaterials.MANASTEEL), 'B', item("grega_core:mana_book"),
                'L', tag("c:dusts/lapis"), 'G', tag("c:dusts/gold"), 'R', tag("c:dusts/redstone"));
        VanillaRecipeHelper.addShapedRecipe(out, CoreContent.id("early/lp_steam_inscriber"), stack("grega_core:lp_steam_inscriber"),
                "XPX", "XMX", "XPX", 'X', new MaterialEntry(TagPrefix.pipeSmallFluid, GTMaterials.Bronze),
                'P', CustomTags.PISTONS, 'M', item("gtceu:bronze_machine_casing"));
        VanillaRecipeHelper.addShapedRecipe(out, CoreContent.id("early/lp_steam_mixer"), CoreContent.STEAM_MIXERS[0].asStack(),
                "XRX", "GMG", "XPX", 'X', new MaterialEntry(TagPrefix.pipeSmallFluid, GTMaterials.Bronze),
                'R', new MaterialEntry(TagPrefix.rotor, GTMaterials.Bronze), 'G', tag("c:glass_blocks"),
                'M', item("gtceu:bronze_machine_casing"), 'P', CustomTags.PISTONS);
        // The native high-pressure compressor pattern upgrades its bronze machine with these same parts.
        for (var pair : new MachineDefinition[][] {
                { CoreContent.LP_STEAM_INSCRIBER, CoreContent.HP_STEAM_INSCRIBER },
                { CoreContent.STEAM_MIXERS[0], CoreContent.STEAM_MIXERS[1] } }) {
            VanillaRecipeHelper.addShapedRecipe(out, CoreContent.id("early/" + pair[1].getName()), pair[1].asStack(),
                    "PSP", "WMW", "PPP", 'P', new MaterialEntry(TagPrefix.pipeSmallFluid, GTMaterials.TinAlloy),
                    'S', new MaterialEntry(TagPrefix.plate, GTMaterials.Steel),
                    'W', new MaterialEntry(TagPrefix.plate, GTMaterials.WroughtIron), 'M', pair[0].asStack());
        }
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

    private static void addPetalApothecaryRecipes(RecipeOutput out) {
        // Preserve each native stone and petal ingredient, filling only its two empty slots.
        for (var entry : PETAL_APOTHECARIES.entrySet()) {
            VanillaRecipeHelper.addShapedRecipe(out, CoreContent.id("early/" + entry.getKey()),
                    stack("botania:" + entry.getKey()), "CPC", "fCh", "CCC",
                    'C', item(entry.getValue()), 'P', tag("botania:petals"));
        }
    }

    private static void addPlateHammerRecipes(RecipeOutput out) {
        // Match GT's native eligibility, replacing the ingot/gem head with same-material plates.
        // Iron keeps its separate Create-pressed sheet recipe for the first hand tool.
        for (var material : GTCEuAPI.materialManager) {
            if (material == GTMaterials.Iron || !material.hasProperty(PropertyKey.TOOL)
                    || !material.shouldGenerateRecipesFor(TagPrefix.plate)
                    || !material.hasFlag(MaterialFlags.GENERATE_PLATE)) continue;
            ItemStack hammer = ToolHelper.get(GTToolType.HARD_HAMMER, material);
            if (hammer.isEmpty()) continue;
            VanillaRecipeHelper.addShapedRecipe(out, CoreContent.id("early/plate_hammer_" + material.getName()),
                    hammer, "PP ", "PPS", "PP ",
                    'P', new MaterialEntry(TagPrefix.plate, material), 'S', tag("c:rods/wooden"));
        }
    }
}
