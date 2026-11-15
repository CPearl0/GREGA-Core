package io.github.cpearl0.grega.core;

import com.gregtechceu.gtceu.api.material.material.Material;
import com.gregtechceu.gtceu.api.material.material.info.MaterialFlags;
import com.gregtechceu.gtceu.api.material.material.info.MaterialIconSet;
import com.gregtechceu.gtceu.api.tag.TagPrefix;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

/** Register before GT freezes materials and generates their item forms. */
public final class CoreMaterials {
    public static Material MANASTEEL;

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
    }
}
