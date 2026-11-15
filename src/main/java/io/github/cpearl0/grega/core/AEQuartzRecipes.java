package io.github.cpearl0.grega.core;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.tag.TagPrefix;
import com.gregtechceu.gtceu.data.material.GTMaterials;
import com.gregtechceu.gtceu.data.recipe.GTRecipeTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.TagKey;

import static io.github.cpearl0.grega.core.CoreContent.item;

/** LV machine alternatives to AE's charger and in-world crystal transformation. */
public final class AEQuartzRecipes {
    private AEQuartzRecipes() {}

    public static void addRecipes(RecipeOutput out) {
        // AE also puts charged crystals in c:gems/certus_quartz. Keep this input uncharged.
        GTRecipeTypes.POLARIZER_RECIPES.recipeBuilder(CoreContent.id("ae2/charged_certus_quartz"))
                .inputItems(TagKey.create(Registries.ITEM, CoreContent.id("uncharged_certus_quartz")))
                .outputItems(item("ae2:charged_certus_quartz_crystal"))
                .duration(100).EUt(GTValues.VA[GTValues.LV]).save(out);

        // Preserve AE's three ingredients and 1+1+1 -> 2 yield; charging is still required.
        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(CoreContent.id("ae2/fluix_crystal"))
                .inputItems(item("ae2:charged_certus_quartz_crystal"))
                .inputItems(TagPrefix.gem, GTMaterials.NetherQuartz)
                .inputItems(TagPrefix.dust, GTMaterials.Redstone)
                .inputFluids(GTMaterials.Water.getFluid(1000))
                .outputItems(item("ae2:fluix_crystal"), 2)
                .duration(100).EUt(GTValues.VA[GTValues.LV]).save(out);
    }
}
