package io.github.cpearl0.grega.core.mixin;

import io.github.cpearl0.grega.core.GregaCore;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Pseudo
@Mixin(targets = "dev.emi.emi.recipe.special.EmiFireworkRocketRecipe", remap = false)
public abstract class EmiFireworkRocketRecipeMixin {
    @ModifyConstant(method = "getFireworkRocket", constant = @Constant(longValue = 3L))
    private long grega$displayRocketYield(long original) {
        return GregaCore.FIREWORK_ROCKET_YIELD;
    }
}
