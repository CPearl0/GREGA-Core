package io.github.cpearl0.grega.core;

import com.gregtechceu.gtceu.api.material.material.stack.MaterialEntry;
import com.gregtechceu.gtceu.api.tag.TagPrefix;
import com.gregtechceu.gtceu.config.ConfigHolder;
import com.gregtechceu.gtceu.data.material.GTMaterials;
import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;
import com.gregtechceu.gtceu.data.recipe.WoodTypeEntry;
import com.gregtechceu.gtceu.data.recipe.misc.WoodMachineRecipes;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

import java.util.function.Consumer;

import static io.github.cpearl0.grega.core.CoreContent.item;

/** Hand-worked instruments and equipment; Totemic's ceremony ingredients remain the progression gates. */
public final class TotemicRecipes {
    private TotemicRecipes() {}

    private static ItemStack stack(String id) { return item(id).getDefaultInstance(); }
    private static TagKey<Item> tag(String id) { return TagKey.create(Registries.ITEM, ResourceLocation.parse(id)); }

    public static void addRecipes(RecipeOutput out) {
        // Keep the native wooden rods and leaves. GT tools carve, smooth and trim them.
        shaped(out, "flute", stack("totemic:flute"), "kLS", "fS ", "S  ",
                'L', tag("minecraft:leaves"), 'S', tag("c:rods/wooden"));
        shaped(out, "totemic_staff", stack("totemic:totemic_staff"), "sLS", "kS ", "S L",
                'L', tag("minecraft:leaves"), 'S', tag("c:rods/wooden"));

        // A skin head, wool damping, a wooden body and an iron retaining ring.
        // Reduce repeated body ingredients to make room for the reusable saw and knife.
        shaped(out, "drum", stack("totemic:drum"), "LLL", "WFW", "sRk",
                'L', tag("c:leathers"), 'W', tag("minecraft:logs_that_burn"),
                'F', tag("minecraft:wool"), 'R', new MaterialEntry(TagPrefix.ring, GTMaterials.Iron));
        shaped(out, "wind_chime", stack("totemic:wind_chime"), "WWW", "ShS", "PfP",
                'W', tag("totemic:cedar_logs"), 'S', tag("c:strings"),
                'P', new MaterialEntry(TagPrefix.plate, GTMaterials.Copper));
        shaped(out, "rattle", stack("totemic:rattle"), "sWW", "fBW", "SR ",
                'W', tag("minecraft:logs_that_burn"), 'B', item("totemic:buffalo_tooth"),
                'S', tag("c:rods/wooden"), 'R', new MaterialEntry(TagPrefix.ring, GTMaterials.Iron));
        // Preserve the native nuggets, adding a formed shell and suspension ring.
        shaped(out, "iron_bells", stack("totemic:iron_bells"), "hRf", "NPN", "NNN",
                'N', tag("c:nuggets/iron"), 'P', new MaterialEntry(TagPrefix.plate, GTMaterials.Iron),
                'R', new MaterialEntry(TagPrefix.ring, GTMaterials.Iron));
        shaped(out, "jingle_dress", stack("totemic:jingle_dress"), "kLS", "BHB", "LBL",
                'L', item("totemic:cedar_leaves"), 'B', item("totemic:iron_bells"),
                'H', tag("c:leathers"), 'S', tag("c:strings"));
        shaped(out, "eagle_bone_whistle", stack("totemic:eagle_bone_whistle"), "kSf", " BF",
                'S', tag("c:strings"), 'B', item("totemic:eagle_bone"), 'F', item("totemic:eagle_feather"));

        shaped(out, "medicine_bag", stack("totemic:medicine_bag"), "PST", "HDH", "kHS",
                'P', item("totemic:cedar_planks"), 'S', tag("c:strings"),
                'T', item("totemic:buffalo_tooth"), 'H', item("totemic:buffalo_hide"), 'D', tag("c:gems/diamond"));
        // Both original tent routes remain available; neither bypasses another instrument's gate.
        for (String variant : new String[] { "hide", "wool" }) {
            shaped(out, "tipi_from_" + variant, stack("totemic:tipi"), "kST", "SWS", "W W",
                    'S', tag("c:rods/wooden"), 'T', tag("c:strings"),
                    'W', variant.equals("hide") ? item("totemic:buffalo_hide") : tag("minecraft:wool"));
        }
        shaped(out, "totem_torch", new ItemStack(item("totemic:totem_torch"), 2), "STS", "SWS", "sSk",
                'S', tag("c:rods/wooden"), 'T', item("minecraft:torch"), 'W', tag("minecraft:logs_that_burn"));
        shaped(out, "leather_from_hide", stack("minecraft:leather"), "kH", 'H', item("totemic:buffalo_hide"));

        if (ModList.get().isLoaded("patchouli")) {
            shaped(out, "totempedia", totempedia(), "sPk", "WPW", "WPW",
                    'P', item("minecraft:paper"), 'W', tag("minecraft:logs_that_burn"));
        }
        addCedarRecipes(out);
    }

    private static void shaped(RecipeOutput out, String name, ItemStack result, Object... recipe) {
        // Lower-case tool symbols use GT's durability and crafting remainder handling.
        VanillaRecipeHelper.addShapedRecipe(out, CoreContent.id("totemic/" + name), result, recipe);
    }

    @SuppressWarnings("unchecked")
    private static ItemStack totempedia() {
        // Keep Patchouli's book identity without adding a compile dependency for one component.
        var component = (DataComponentType<ResourceLocation>) BuiltInRegistries.DATA_COMPONENT_TYPE
                .getOptional(ResourceLocation.parse("patchouli:book")).orElseThrow();
        var book = stack("patchouli:guide_book");
        book.set(component, ResourceLocation.parse("totemic:totempedia"));
        return book;
    }

    private static void addCedarRecipes(RecipeOutput out) {
        // GT only registers vanilla and its own wood types by default. Reuse the same
        // generator for cedar, including improved machine yields and the current config.
        var cedar = new WoodTypeEntry.Builder("totemic", "totemic_cedar")
                .logTag(tag("totemic:cedar_logs"))
                .log(item("totemic:cedar_log"))
                .strippedLog(item("totemic:stripped_cedar_log"))
                .wood(item("totemic:cedar_wood"))
                .strippedWood(item("totemic:stripped_cedar_wood"))
                .planks(item("totemic:cedar_planks"), "cedar_planks")
                .door(item("totemic:cedar_door"), "cedar_door")
                .trapdoor(item("totemic:cedar_trapdoor"), "cedar_trapdoor")
                .slab(item("totemic:cedar_slab"), "cedar_slab")
                .fence(item("totemic:cedar_fence"), "cedar_fence")
                .fenceGate(item("totemic:cedar_fence_gate"), "cedar_fence_gate")
                .stairs(item("totemic:cedar_stairs"), "cedar_stairs")
                .sign(item("totemic:cedar_sign"), "cedar_sign")
                .hangingSign(item("totemic:cedar_hanging_sign"), "cedar_hanging_sign")
                .button(item("totemic:cedar_button"), "cedar_button")
                .pressurePlate(item("totemic:cedar_pressure_plate"), "cedar_pressure_plate")
                .registerAllMaterialInfo()
                .build();
        WoodMachineRecipes.registerWoodMaterialInfo(cedar);
        WoodMachineRecipes.registerWoodTypeRecipe(out, cedar);
    }

    public static void removeRecipes(Consumer<ResourceLocation> remove) {
        // The already-replaced whittling knife stays in EarlyProgressionRecipes.
        for (String name : new String[] { "flute", "totemic_staff", "drum", "wind_chime", "rattle", "iron_bells",
                "jingle_dress", "eagle_bone_whistle", "medicine_bag", "tipi_from_hide", "tipi_from_wool",
                "totem_torch", "leather_from_hide", "totempedia" }) {
            remove.accept(ResourceLocation.fromNamespaceAndPath("totemic", name));
        }
        // Match the generator's config-dependent replacements. Bark blocks and stairs
        // retain their native crafts, just as their vanilla counterparts do in GT.
        if (ConfigHolder.INSTANCE.recipes.nerfWoodCrafting) {
            remove.accept(ResourceLocation.parse("totemic:cedar_planks"));
        }
        if (ConfigHolder.INSTANCE.recipes.hardWoodRecipes) {
            for (String name : new String[] { "door", "trapdoor", "slab", "fence", "fence_gate", "sign",
                    "hanging_sign", "button", "pressure_plate" }) {
                remove.accept(ResourceLocation.fromNamespaceAndPath("totemic", "cedar_" + name));
            }
        }
    }
}
