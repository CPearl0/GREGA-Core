package io.github.cpearl0.grega.core.mixin;

import io.github.cpearl0.grega.core.TravelMagic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.aetherteam.aether.event.hooks.DimensionHooks", remap = false)
public abstract class AetherPortalMixin {
    // Gate activation hooks, leaving generated destination/return portals intact.
    @Inject(method = { "createPortal", "detectWaterInFrame" }, at = @At("HEAD"), cancellable = true)
    private static void grega$requirePortalSpell(CallbackInfoReturnable<Boolean> callback) {
        if (!TravelMagic.isOpeningPortal()) callback.setReturnValue(false);
    }
}
