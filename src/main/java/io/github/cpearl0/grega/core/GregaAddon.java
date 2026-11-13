package io.github.cpearl0.grega.core;

import com.gregtechceu.gtceu.api.addon.GTAddon;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import net.minecraft.data.recipes.RecipeOutput;

@GTAddon(GregaCore.MOD_ID)
public final class GregaAddon implements IGTAddon {
    public static final GTRegistrate REGISTRATE = GTRegistrate.create(GregaCore.MOD_ID);

    @Override
    public GTRegistrate getRegistrate() { return REGISTRATE; }

    @Override
    public void gtInitComplete() { /* Content belongs to this mod's own register event. */ }

    @Override
    public void addRecipes(RecipeOutput output) { CoreContent.addRecipes(output); }
}
