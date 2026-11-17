package io.github.cpearl0.grega.core;

import com.gregtechceu.gtceu.api.material.material.Material;
import com.gregtechceu.gtceu.api.material.material.info.MaterialFlags;
import com.gregtechceu.gtceu.api.material.material.info.MaterialIconSet;
import com.gregtechceu.gtceu.api.tag.TagPrefix;
import com.gregtechceu.gtceu.data.material.GTMaterials;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

/** Register before GT freezes materials and generates their item forms. */
public final class CoreMaterials {
    public static Material MANASTEEL;
    public static Material ELEMENTIUM;
    public static Material ANDESITE_ALLOY;

    private CoreMaterials() {}

    public static void register() {
        MANASTEEL = new Material.Builder(CoreContent.id("manasteel"))
                .ingot().color(0x69B5FF).iconSet(MaterialIconSet.METALLIC)
                .flags(MaterialFlags.GENERATE_PLATE, MaterialFlags.GENERATE_ROD, MaterialFlags.MORTAR_GRINDABLE)
                .buildAndRegister();
        // Reuse Botania's canonical forms; only generate the missing GT processing forms.
        TagPrefix.ingot.setIgnored(MANASTEEL, () -> CoreContent.item("botania:manasteel_ingot"));
        TagPrefix.nugget.setIgnored(MANASTEEL, () -> CoreContent.item("botania:manasteel_nugget"));
        TagPrefix.block.setIgnored(MANASTEEL,
                () -> BuiltInRegistries.BLOCK.get(ResourceLocation.parse("botania:manasteel_block")));

        ELEMENTIUM = new Material.Builder(CoreContent.id("elementium"))
                .ingot().color(0xE970A6).iconSet(MaterialIconSet.METALLIC)
                .flags(MaterialFlags.GENERATE_PLATE, MaterialFlags.GENERATE_ROD, MaterialFlags.MORTAR_GRINDABLE)
                .buildAndRegister();
        TagPrefix.ingot.setIgnored(ELEMENTIUM, () -> CoreContent.item("botania:elementium_ingot"));
        TagPrefix.nugget.setIgnored(ELEMENTIUM, () -> CoreContent.item("botania:elementium_nugget"));
        TagPrefix.block.setIgnored(ELEMENTIUM,
                () -> BuiltInRegistries.BLOCK.get(ResourceLocation.parse("botania:elementium_block")));

        ANDESITE_ALLOY = new Material.Builder(CoreContent.id("andesite_alloy"))
                .ingot().color(0x8A9C87).iconSet(MaterialIconSet.ROUGH)
                .components(GTMaterials.Andesite, 3, GTMaterials.Zinc, 1)
                .flags(MaterialFlags.MORTAR_GRINDABLE, MaterialFlags.DISABLE_DECOMPOSITION,
                        MaterialFlags.GENERATE_ROTOR)
                .buildAndRegister();
        TagPrefix.ingot.setIgnored(ANDESITE_ALLOY, () -> CoreContent.item("create:andesite_alloy"));
        TagPrefix.block.setIgnored(ANDESITE_ALLOY,
                () -> BuiltInRegistries.BLOCK.get(ResourceLocation.parse("create:andesite_alloy_block")));
    }
}
