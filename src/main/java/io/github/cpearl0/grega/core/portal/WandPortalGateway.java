package io.github.cpearl0.grega.core.portal;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface WandPortalGateway {
    boolean grega$openPortal(Player player, ItemStack glyph, Direction side);
}
