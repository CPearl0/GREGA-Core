package io.github.cpearl0.grega.core;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.SimpleTieredMachine;
import com.gregtechceu.gtceu.api.machine.steam.SimpleSteamMachine;
import com.gregtechceu.gtceu.api.multiblock.util.RelativeDirection;
import com.gregtechceu.gtceu.api.recipe.GTRecipeSerializer;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.api.machine.RotationState;
import com.gregtechceu.gtceu.data.machine.GTMachineUtils;
import com.gregtechceu.gtceu.data.recipe.GTRecipeModifiers;
import com.gregtechceu.gtceu.data.recipe.GTRecipeTypes;
import io.github.cpearl0.grega.core.machine.SteamMixerMachine;
import com.lowdragmc.lowdraglib.gui.texture.ProgressTexture.FillDirection;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import java.util.Locale;

import static io.github.cpearl0.grega.core.GregaAddon.REGISTRATE;

/** Shared steam/electric inscribing recipe map and pack-specific circuit items. */
public final class CoreContent {
    public static GTRecipeType INSCRIBING;
    public static MachineDefinition LP_STEAM_INSCRIBER;
    public static MachineDefinition HP_STEAM_INSCRIBER;
    public static final MachineDefinition[] STEAM_MIXERS = new MachineDefinition[2];
    public static final MachineDefinition[] ELECTRIC_INSCRIBERS = new MachineDefinition[GTValues.TIER_COUNT];
    private static boolean registered;

    private CoreContent() {}

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(GregaCore.MOD_ID, path);
    }

    public static void register(RegisterEvent event) {
        if (registered) return;
        registered = true;
        CoreMaterials.register();
        GrindingBallCondition.register();
        REGISTRATE.item("elementium_grinding_ball", Item::new)
                .properties(properties -> properties.durability(GrindingBalls.DURABILITY)).register();
        for (String name : new String[] { "basic_electronic_component", "germanium_press", "mana_book",
                "via_substrate", "bonded_nano_die", "multilayer_interconnect",
                "laminated_nano_processor", "laminated_nano_processor_assembly",
                "laminated_nano_processor_computer", "laminated_nano_processor_mainframe" }) {
            REGISTRATE.item(name, Item::new).register();
        }
        INSCRIBING = new GTRecipeType(id("inscribing"), "steam")
                .setMaxIOSize(3, 1, 0, 0).setEUIO(IO.IN)
                .setProgressBar(GuiTextures.PROGRESS_BAR_COMPRESS, FillDirection.LEFT_TO_RIGHT)
                .setSteamProgressBar(GuiTextures.PROGRESS_BAR_COMPRESS_STEAM, FillDirection.LEFT_TO_RIGHT);
        GTRegistries.register(BuiltInRegistries.RECIPE_TYPE, id("inscribing"), INSCRIBING);
        INSCRIBING.setSerializer(GTRegistries.register(BuiltInRegistries.RECIPE_SERIALIZER,
                id("inscribing"), new GTRecipeSerializer()));
        LP_STEAM_INSCRIBER = REGISTRATE.machine("lp_steam_inscriber", holder -> new SimpleSteamMachine(holder, false))
                .tier(0).rotationState(RotationState.ALL)
                .recipeType(INSCRIBING).recipeModifier(SimpleSteamMachine::recipeModifier)
                .modelProperty(SimpleSteamMachine.VENT_DIRECTION_PROPERTY, RelativeDirection.BACK)
                .workableSteamHullModel(false, ResourceLocation.fromNamespaceAndPath("gtceu", "block/machines/compressor"))
                .register();
        HP_STEAM_INSCRIBER = REGISTRATE.machine("hp_steam_inscriber", holder -> new SimpleSteamMachine(holder, true))
                .tier(1).rotationState(RotationState.ALL)
                .recipeType(INSCRIBING).recipeModifier(SimpleSteamMachine::recipeModifier)
                .modelProperty(SimpleSteamMachine.VENT_DIRECTION_PROPERTY, RelativeDirection.BACK)
                .workableSteamHullModel(true, ResourceLocation.parse("gtceu:block/machines/compressor"))
                .register();
        for (int pressure = 0; pressure < STEAM_MIXERS.length; pressure++) {
            boolean highPressure = pressure == 1;
            STEAM_MIXERS[pressure] = REGISTRATE
                    .machine((highPressure ? "hp" : "lp") + "_steam_mixer",
                            holder -> new SteamMixerMachine(holder, highPressure))
                    .tier(pressure).rotationState(RotationState.NON_Y_AXIS)
                    .recipeType(GTRecipeTypes.MIXER_RECIPES).recipeModifier(SimpleSteamMachine::recipeModifier)
                    .modelProperty(SimpleSteamMachine.VENT_DIRECTION_PROPERTY, RelativeDirection.BACK)
                    .workableSteamHullModel(highPressure, ResourceLocation.parse("gtceu:block/machines/mixer"))
                    .register();
        }
        // Follow GT's enabled electric tiers, hull storage and ordinary overclocking.
        for (int tier : GTMachineUtils.ELECTRIC_TIERS) {
            ELECTRIC_INSCRIBERS[tier] = REGISTRATE
                    .machine(GTValues.VN[tier].toLowerCase(Locale.ROOT) + "_inscriber",
                            holder -> new SimpleTieredMachine(holder, tier, GTMachineUtils.defaultTankSizeFunction))
                    .tier(tier).rotationState(RotationState.NON_Y_AXIS)
                    .editableUI(SimpleTieredMachine.EDITABLE_UI_CREATOR.apply(id("inscriber"), INSCRIBING))
                    .recipeType(INSCRIBING).recipeModifier(GTRecipeModifiers.OC_NON_PERFECT)
                    .workableTieredHullModel(ResourceLocation.parse("gtceu:block/machines/forming_press"))
                    .register();
        }
        INSCRIBING.setIconSupplier(() -> LP_STEAM_INSCRIBER.asStack());
    }

    public static void creativeTab(BuildCreativeModeTabContentsEvent event) {
        var tab = event.getTabKey().location();
        if (tab.equals(ResourceLocation.parse("gtceu:item"))) {
            event.remove(new ItemStack(item("gtceu:microchip_processor")),
                    TabVisibility.PARENT_AND_SEARCH_TABS);
            for (var key : BuiltInRegistries.ITEM.keySet()) {
                if (key.getNamespace().equals(GregaCore.MOD_ID) && !key.getPath().startsWith("glyph_")
                        && !key.getPath().endsWith("_inscriber") && !key.getPath().endsWith("_mixer")) {
                    event.accept(BuiltInRegistries.ITEM.get(key));
                }
            }
        } else if (tab.equals(ResourceLocation.parse("gtceu:machine"))) {
            event.accept(LP_STEAM_INSCRIBER.asStack());
            event.accept(HP_STEAM_INSCRIBER.asStack());
            for (var machine : STEAM_MIXERS) event.accept(machine.asStack());
            for (var machine : ELECTRIC_INSCRIBERS) {
                if (machine != null) event.accept(machine.asStack());
            }
        }
    }

    static Item item(String name) {
        ResourceLocation key = ResourceLocation.parse(name);
        if (!BuiltInRegistries.ITEM.containsKey(key)) throw new IllegalStateException("Missing item: " + key);
        return BuiltInRegistries.ITEM.get(key);
    }

    public static void addRecipes(RecipeOutput output) {
        CircuitProduction.addRecipes(output);
        String[] types = { "logic", "calculation", "engineering" };
        String[] raw = { "minecraft:gold_ingot", "ae2:certus_quartz_crystal", "minecraft:diamond" };
        for (int i = 0; i < types.length; i++) {
            String type = types[i];
            var print = INSCRIBING.recipeBuilder(id("print_" + type))
                    .notConsumable(item("ae2:" + type + "_processor_press"));
            if (type.equals("calculation")) {
                print.inputItems(TagKey.create(Registries.ITEM, ResourceLocation.parse("c:gems/certus_quartz")));
            } else {
                print.inputItems(item(raw[i]));
            }
            print.outputItems(item("ae2:printed_" + type + "_processor"))
                    .duration(100).EUt(4).save(output);
            INSCRIBING.recipeBuilder(id("assemble_" + type))
                    .inputItems(item("ae2:printed_" + type + "_processor"))
                    .inputItems(item("ae2:printed_silicon"))
                    .inputItems(item("gtceu:red_alloy_plate"))
                    .outputItems(item("ae2:" + type + "_processor"))
                    .duration(200).EUt(4).save(output);
        }
        INSCRIBING.recipeBuilder(id("print_silicon"))
                .notConsumable(item("ae2:silicon_press"))
                .inputItems(TagKey.create(Registries.ITEM, ResourceLocation.parse("c:silicon")))
                .outputItems(item("ae2:printed_silicon"))
                .duration(100).EUt(4).save(output);
        // Duplicate only the four usable presses, with a real press as the non-consumed master.
        for (String press : new String[] { "logic_processor_press", "calculation_processor_press",
                "engineering_processor_press", "silicon_press" }) {
            INSCRIBING.recipeBuilder(id("copy_" + press)).notConsumable(item("ae2:" + press))
                    .inputItems(Items.IRON_BLOCK).outputItems(item("ae2:" + press))
                    .duration(200).EUt(4).save(output);
        }
    }
}
