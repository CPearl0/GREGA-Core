package io.github.cpearl0.grega.core.mixin;

import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import io.github.cpearl0.grega.core.GrindingBalls;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RecipeLogic.class, remap = false)
public abstract class GrindingBallWearMixin {
    @Inject(method = "onRecipeFinish", at = @At("HEAD"))
    private void grega$wearGrindingBallOnCompletion(CallbackInfo callback) {
        var logic = (RecipeLogic) (Object) this;
        GrindingBalls.onRecipeFinished(logic.machine, logic.getLastRecipe());
    }
}
