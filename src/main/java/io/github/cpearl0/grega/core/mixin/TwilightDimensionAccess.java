package io.github.cpearl0.grega.core.mixin;

import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Invoker;

@Pseudo
@Mixin(targets = "twilightforest.init.TFDimension", remap = false)
public interface TwilightDimensionAccess {
    @Invoker("isTwilightPortalDestination")
    static boolean grega$isDestination(Level level) { throw new AssertionError("Mixin invoker"); }
}
