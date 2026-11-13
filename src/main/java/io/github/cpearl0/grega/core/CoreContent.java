package io.github.cpearl0.grega.core;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.steam.SimpleSteamMachine;
import com.gregtechceu.gtceu.api.multiblock.util.RelativeDirection;
import com.gregtechceu.gtceu.api.recipe.GTRecipeSerializer;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.api.machine.RotationState;
import com.lowdragmc.lowdraglib.gui.texture.ProgressTexture.FillDirection;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.RegisterEvent;

import static io.github.cpearl0.grega.core.GregaAddon.REGISTRATE;

/** Small GT addon: one steam recipe map, one machine, and pack-specific circuit items. */
public final class CoreContent {
    public static GTRecipeType INSCRIBING;
    public static MachineDefinition STEAM_INSCRIBER;
    private static boolean registered;

    private CoreContent() {}

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(GregaCore.MOD_ID, path);
    }

    public static void register(RegisterEvent event) {
        if (registered) return;
        registered = true;
        for (String name : new String[] { "basic_electronic_component", "germanium_press",
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
        STEAM_INSCRIBER = REGISTRATE.machine("steam_inscriber", holder -> new SimpleSteamMachine(holder, false))
                .tier(0).rotationState(RotationState.ALL)
                .recipeType(INSCRIBING).recipeModifier(SimpleSteamMachine::recipeModifier)
                .modelProperty(SimpleSteamMachine.VENT_DIRECTION_PROPERTY, RelativeDirection.BACK)
                .workableSteamHullModel(false, ResourceLocation.fromNamespaceAndPath("gtceu", "block/machines/compressor"))
                .tooltips(Component.translatable("grega_core.inscriber.tooltip"))
                .register();
        INSCRIBING.setIconSupplier(() -> STEAM_INSCRIBER.asStack());
    }

    private static Item item(String name) {
        ResourceLocation key = ResourceLocation.parse(name);
        if (!BuiltInRegistries.ITEM.containsKey(key)) throw new IllegalStateException("Missing item: " + key);
        return BuiltInRegistries.ITEM.get(key);
    }

    public static void addRecipes(RecipeOutput output) {
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
