package io.github.cpearl0.grega.core.mixin;

import io.github.cpearl0.grega.core.GregaCore;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.FireworkRocketRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FireworkRocketRecipe.class)
public abstract class FireworkRocketRecipeMixin {
    // Keep vanilla's flight duration, explosions and ingredient matching intact.
    @Inject(method = {
            "assemble(Lnet/minecraft/world/item/crafting/CraftingInput;Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/world/item/ItemStack;",
            "getResultItem(Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/world/item/ItemStack;"
    }, at = @At("RETURN"))
    private void grega$increaseRocketYield(CallbackInfoReturnable<ItemStack> callback) {
        callback.getReturnValue().setCount(GregaCore.FIREWORK_ROCKET_YIELD);
    }
}
