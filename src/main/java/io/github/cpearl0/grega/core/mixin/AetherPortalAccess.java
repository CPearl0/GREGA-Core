package io.github.cpearl0.grega.core.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Invoker;

@Pseudo
@Mixin(targets = "com.aetherteam.aether.event.hooks.DimensionHooks", remap = false)
public interface AetherPortalAccess {
    @Invoker("detectWaterInFrame")
    static boolean grega$openPortal(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluid) {
        throw new AssertionError("Mixin invoker");
    }
}
