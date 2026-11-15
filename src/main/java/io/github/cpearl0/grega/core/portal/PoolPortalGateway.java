package io.github.cpearl0.grega.core.portal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface PoolPortalGateway {
    boolean grega$canFormPortal(BlockState state);
    boolean grega$openPortal(Level level, BlockPos pos, ItemEntity catalyst, Player player);
}
