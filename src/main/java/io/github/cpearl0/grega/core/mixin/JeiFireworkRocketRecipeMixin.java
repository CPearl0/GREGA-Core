package io.github.cpearl0.grega.core.mixin;

import io.github.cpearl0.grega.core.GregaCore;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Pseudo
@Mixin(targets = "mezz.jei.neoforge.platform.RecipeHelper", remap = false)
public abstract class JeiFireworkRocketRecipeMixin {
    @ModifyConstant(method = "getFireworkRocketRecipeData", constant = @Constant(intValue = 3))
    private int grega$displayRocketYield(int original) {
        return GregaCore.FIREWORK_ROCKET_YIELD;
    }
}
