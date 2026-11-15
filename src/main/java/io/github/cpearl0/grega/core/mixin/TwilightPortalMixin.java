package io.github.cpearl0.grega.core.mixin;

import io.github.cpearl0.grega.core.TravelMagic;
import io.github.cpearl0.grega.core.portal.PoolPortalGateway;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "twilightforest.block.TFPortalBlock", remap = false)
public abstract class TwilightPortalMixin implements PoolPortalGateway {
    @Shadow public abstract boolean canFormPortal(BlockState state);
    @Shadow public abstract boolean tryToCreatePortal(Level level, BlockPos pos, ItemEntity catalyst, Player player);

    @Override
    public boolean grega$canFormPortal(BlockState state) { return canFormPortal(state); }

    @Override
    public boolean grega$openPortal(Level level, BlockPos pos, ItemEntity catalyst, Player player) {
        return tryToCreatePortal(level, pos, catalyst, player);
    }

    @Inject(method = "tryToCreatePortal", at = @At("HEAD"), cancellable = true)
    private void grega$requirePortalSpell(CallbackInfoReturnable<Boolean> callback) {
        if (!TravelMagic.isOpeningPortal()) callback.setReturnValue(false);
    }
}
