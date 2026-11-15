package io.github.cpearl0.grega.core;

import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.common.recipe.builder.GTRecipeBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import static com.gregtechceu.gtceu.data.recipe.GTRecipeTypes.*;
import static io.github.cpearl0.grega.core.CoreContent.item;
import static com.gregtechceu.gtceu.data.material.GTMaterials.SolderingAlloy;

/** Pack circuit route. Nano is a short transition; lamination carries the process cost. */
public final class CircuitProduction {
    private CircuitProduction() {}

    private static GTRecipeBuilder recipe(GTRecipeType type, String name, String result,
                                           int count, int voltage, int ticks, String... inputs) {
        var recipe = type.recipeBuilder(CoreContent.id(name)).EUt(voltage).duration(ticks);
        for (String input : inputs) {
            String[] split = input.split(" ", 2);
            int amount = split.length == 2 ? Integer.parseInt(split[0]) : 1;
            String key = split.length == 2 ? split[1] : input;
            if (key.startsWith("#")) {
                recipe.inputItems(TagKey.create(Registries.ITEM, ResourceLocation.parse(key.substring(1))), amount);
            } else {
                recipe.inputItems(item(key), amount);
            }
        }
        return recipe.outputItems(item(result), count);
    }

    private static void circuit(RecipeOutput out, String name, String result, int count,
                                int voltage, int ticks, int solder, String... inputs) {
        recipe(CIRCUIT_ASSEMBLER_RECIPES, name, result, count, voltage, ticks, inputs)
                .inputFluids(SolderingAlloy.getFluid(solder)).save(out);
    }

    public static void addRecipes(RecipeOutput out) {
        recipe(ASSEMBLER_RECIPES, "basic_component", "grega_core:basic_electronic_component", 2, 16, 200,
                "ae2:logic_processor", "ae2:calculation_processor", "ae2:engineering_processor",
                "2 gtceu:red_alloy_single_cable", "gtceu:steel_plate").save(out);

        // Preserve the micro family while removing the separate microchip shortcut.
        circuit(out, "micro_processor", "gtceu:micro_processor", 2, 15, 200, 72,
                "gtceu:plastic_printed_circuit_board", "gtceu:cpu_chip", "4 #gtceu:resistors",
                "4 #gtceu:capacitors", "4 #gtceu:transistors", "4 gtceu:fine_red_alloy_wire");
        circuit(out, "micro_assembly", "gtceu:micro_processor_assembly", 2, 30, 400, 144,
                "gtceu:plastic_printed_circuit_board", "2 gtceu:micro_processor", "4 #gtceu:inductors",
                "8 #gtceu:capacitors", "4 gtceu:ram_chip", "8 gtceu:fine_red_alloy_wire");
        circuit(out, "micro_computer", "gtceu:micro_processor_computer", 1, 30, 400, 144,
                "gtceu:plastic_printed_circuit_board", "2 gtceu:micro_processor_assembly", "4 #gtceu:diodes",
                "4 gtceu:ram_chip", "16 gtceu:fine_electrum_wire", "16 gtceu:blue_alloy_bolt");
        circuit(out, "micro_mainframe", "gtceu:micro_processor_mainframe", 1, 120, 800, 288,
                "2 gtceu:aluminium_frame", "2 gtceu:micro_processor_computer", "8 #gtceu:inductors",
                "16 #gtceu:capacitors", "16 gtceu:ram_chip", "16 gtceu:annealed_copper_single_wire");

        // Direct die coating avoids the upstream EV wafer/cleanroom gate for this early transition.
        recipe(FORMING_PRESS_RECIPES, "early_nano_die", "gtceu:nano_cpu_chip", 2, 120, 100,
                "gtceu:cpu_chip", "2 gtceu:carbon_fibers").save(out);
        circuit(out, "nano_processor", "gtceu:nano_processor", 2, 120, 100, 36,
                "gtceu:plastic_printed_circuit_board", "gtceu:nano_cpu_chip", "2 #gtceu:resistors", "2 #gtceu:transistors",
                "2 #gtceu:capacitors", "4 gtceu:fine_electrum_wire");
        circuit(out, "nano_assembly", "gtceu:nano_processor_assembly", 2, 120, 160, 72,
                "gtceu:plastic_printed_circuit_board", "2 gtceu:nano_processor", "2 #gtceu:inductors", "8 #gtceu:capacitors",
                "4 gtceu:ram_chip", "4 gtceu:fine_electrum_wire");
        circuit(out, "nano_computer", "gtceu:nano_processor_computer", 1, 120, 200, 72,
                "gtceu:plastic_printed_circuit_board", "2 gtceu:nano_processor_assembly", "4 #gtceu:diodes",
                "4 gtceu:nor_memory_chip", "8 gtceu:ram_chip",
                "8 gtceu:fine_electrum_wire");
        circuit(out, "nano_mainframe", "gtceu:nano_processor_mainframe", 1, 480, 300, 144,
                "2 gtceu:aluminium_frame", "2 gtceu:nano_processor_computer", "4 #gtceu:inductors", "8 #gtceu:capacitors",
                "8 gtceu:ram_chip", "8 gtceu:annealed_copper_single_wire");

        // Heavy but fully GregTech: drill vias, bond dies, then laminate the routing layers.
        recipe(CUTTER_RECIPES, "drill_vias", "grega_core:via_substrate", 1, 120, 600,
                "gtceu:plastic_printed_circuit_board")
                .inputFluids(com.gregtechceu.gtceu.data.material.GTMaterials.Water.getFluid(1000)).save(out);
        recipe(FORMING_PRESS_RECIPES, "bond_nano_die", "grega_core:bonded_nano_die", 1, 120, 600,
                "4 gtceu:nano_cpu_chip", "8 gtceu:gold_foil", "gtceu:silicone_rubber_plate").save(out);
        recipe(ASSEMBLER_RECIPES, "multilayer_interconnect", "grega_core:multilayer_interconnect", 1, 120, 900,
                "4 grega_core:via_substrate", "16 gtceu:copper_foil", "4 gtceu:polyethylene_plate",
                "8 gtceu:fine_gold_wire").inputFluids(SolderingAlloy.getFluid(288)).save(out);
        // Keep GT's family structure: PCB for stages 1-3, two frames for stage 4.
        // Stage 1 makes two; upgrades always consume two and produce 2 / 1 / 1.
        // Interconnect fabrication carries the 3D process cost, rather than extra PCBs at every upgrade.
        circuit(out, "compute_unit", "grega_core:laminated_nano_processor", 2, 120, 1200, 288,
                "grega_core:multilayer_interconnect", "2 grega_core:bonded_nano_die", "8 #gtceu:resistors",
                "8 #gtceu:transistors", "8 #gtceu:capacitors", "8 gtceu:fine_electrum_wire");
        circuit(out, "processor_core", "grega_core:laminated_nano_processor_assembly", 2, 480, 1600, 576,
                "grega_core:multilayer_interconnect", "2 grega_core:laminated_nano_processor",
                "8 #gtceu:inductors", "16 #gtceu:capacitors", "16 gtceu:ram_chip", "16 gtceu:fine_electrum_wire");
        circuit(out, "multicore_processor", "grega_core:laminated_nano_processor_computer", 1, 1920, 2000, 576,
                "grega_core:multilayer_interconnect", "2 grega_core:laminated_nano_processor_assembly",
                "8 #gtceu:diodes", "4 gtceu:nor_memory_chip", "32 gtceu:ram_chip", "32 gtceu:fine_electrum_wire");
        circuit(out, "laminated_mainframe", "grega_core:laminated_nano_processor_mainframe", 1, 7680, 2400, 1152,
                "2 gtceu:tungsten_steel_frame", "2 grega_core:laminated_nano_processor_computer",
                "16 #gtceu:inductors", "32 #gtceu:capacitors", "32 gtceu:ram_chip", "32 gtceu:annealed_copper_single_wire");
    }
}
