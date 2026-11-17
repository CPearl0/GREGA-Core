package io.github.cpearl0.grega.core.mixin;

import com.gregtechceu.gtceu.api.recipe.kind.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.lookup.GTRecipeLookup;
import com.gregtechceu.gtceu.api.recipe.lookup.ingredient.AbstractMapIngredient;
import io.github.cpearl0.grega.core.GrindingBalls;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.List;

@Mixin(value = GTRecipeLookup.class, remap = false)
public abstract class GrindingBallRecipeLookupMixin {
    @Inject(method = "fromRecipe", at = @At("RETURN"))
    private void grega$indexGrindingBallFirst(GTRecipe recipe,
            CallbackInfoReturnable<List<List<AbstractMapIngredient>>> callback) {
        if (!GrindingBalls.isEnhanced(recipe)) return;
        var ingredients = callback.getReturnValue();
        // Enhanced macerator recipes have one material and one catalyst. Index
        // the ball first to avoid extending the ordinary recipe's leaf node,
        // without changing the recipe's material-first display order.
        if (ingredients.size() == 2) Collections.swap(ingredients, 0, 1);
    }
}
