package io.github.cpearl0.grega.core.mixin;

import io.github.cpearl0.grega.core.TravelMagic;
import io.github.cpearl0.grega.core.portal.WandPortalGateway;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "vazkii.botania.common.block.block_entity.AlfheimPortalBlockEntity", remap = false)
public abstract class BotaniaPortalMixin implements WandPortalGateway {
    @Shadow public abstract boolean onUsedByWand(Player player, ItemStack stack, Direction side);

    @Override
    public boolean grega$openPortal(Player player, ItemStack glyph, Direction side) {
        return onUsedByWand(player, glyph, side);
    }

    @Inject(method = "onUsedByWand", at = @At("HEAD"), cancellable = true)
    private void grega$requirePortalSpell(CallbackInfoReturnable<Boolean> callback) {
        if (!TravelMagic.isOpeningPortal()) callback.setReturnValue(false);
    }
}
