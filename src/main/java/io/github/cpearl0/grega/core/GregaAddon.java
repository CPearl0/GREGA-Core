package io.github.cpearl0.grega.core;

import com.gregtechceu.gtceu.api.addon.GTAddon;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import java.util.function.Consumer;

@GTAddon(GregaCore.MOD_ID)
public final class GregaAddon implements IGTAddon {
    public static final GTRegistrate REGISTRATE = GTRegistrate.create(GregaCore.MOD_ID);
    static {
        // Registrate defaults to SEARCH. Our GT tab entries already populate search;
        // adding them again when the search tab rebuilds causes a duplicate-entry crash.
        REGISTRATE.defaultCreativeTab((ResourceKey<CreativeModeTab>) null);
    }

    @Override
    public GTRegistrate getRegistrate() { return REGISTRATE; }

    @Override
    public void gtInitComplete() { /* Content belongs to this mod's own register event. */ }

    @Override
    public void addRecipes(RecipeOutput output) {
        EarlyProgressionRecipes.addRecipes(output);
        TotemicRecipes.addRecipes(output);
        AEQuartzRecipes.addRecipes(output);
        CoreContent.addRecipes(output);
    }

    @Override
    public void removeRecipes(Consumer<ResourceLocation> remove) { PackRecipeChanges.removeRecipes(remove); }
}
