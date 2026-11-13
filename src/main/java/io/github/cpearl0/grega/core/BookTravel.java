package io.github.cpearl0.grega.core;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.PortalShape;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.Set;

/** Sneak-use gestures augment the existing Ars books; no commands or extra casting items. */
public final class BookTravel {
    private static final String DATA = "grega_book_travel";
    private static final long COOLDOWN = 20 * 60;
    private static final ThreadLocal<Boolean> OPENING_PORTAL = ThreadLocal.withInitial(() -> false);
    private static final Set<String> BOOKS = Set.of("novice_spell_book", "apprentice_spell_book", "archmage_spell_book");

    private BookTravel() {}

    private static boolean isBook(ItemStack stack) {
        var id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return id.getNamespace().equals("ars_nouveau") && BOOKS.contains(id.getPath());
    }

    public static void portalSpawn(BlockEvent.PortalSpawnEvent event) {
        // Also blocks lava/fire charges/dispensers and GT lighters from bypassing the book.
        if (!OPENING_PORTAL.get()) event.setCanceled(true);
    }

    public static void clonePlayer(PlayerEvent.Clone event) {
        var data = event.getOriginal().getPersistentData();
        if (data.contains(DATA)) event.getEntity().getPersistentData().put(DATA, data.getCompound(DATA).copy());
    }

    public static void interact(PlayerInteractEvent.RightClickBlock event) {
        if (event.getHand() != InteractionHand.MAIN_HAND || !event.getEntity().isShiftKeyDown()
                || !isBook(event.getItemStack())) return;
        var state = event.getLevel().getBlockState(event.getPos());
        boolean portal = state.is(Blocks.OBSIDIAN);
        boolean bind = state.getBlock() instanceof BedBlock;
        boolean home = state.is(Blocks.CRAFTING_TABLE);
        boolean random = state.is(Blocks.MOSS_BLOCK);
        if (!portal && !bind && !home && !random) return;
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        ServerLevel level = player.serverLevel();
        if (player.isPassenger() || player.isSleeping()) {
            message(player, "unavailable");
            return;
        }
        if (portal) {
            if (level.dimension() != Level.OVERWORLD && level.dimension() != Level.NETHER) {
                message(player, "overworld_or_nether");
                return;
            }
            BlockPos inside = event.getPos().relative(event.getFace());
            OPENING_PORTAL.set(true);
            try {
                var shape = PortalShape.findEmptyPortalShape(level, inside, Direction.Axis.X);
                if (shape.isEmpty()) shape = PortalShape.findEmptyPortalShape(level, inside, Direction.Axis.Z);
                if (shape.isPresent()) {
                    shape.get().createPortalBlocks();
                    message(player, "portal_opened");
                } else message(player, "portal_frame");
            } finally {
                OPENING_PORTAL.remove();
            }
            return;
        }
        CompoundTag data = player.getPersistentData().getCompound(DATA);
        if (bind) {
            if (level.dimension() != Level.OVERWORLD) {
                message(player, "overworld_only");
                return;
            }
            BlockPos safe = nearbySafe(level, player, player.blockPosition());
            if (safe == null) { message(player, "unsafe"); return; }
            data.putLong("home", safe.asLong());
            player.getPersistentData().put(DATA, data);
            message(player, "home_bound");
            return;
        }
        long now = player.server.overworld().getGameTime();
        if (data.getLong("ready_at") > now) {
            message(player, "cooldown");
            return;
        }
        ServerLevel target = player.server.overworld();
        BlockPos destination = null;
        if (home) {
            if (!data.contains("home")) { message(player, "no_home"); return; }
            BlockPos saved = BlockPos.of(data.getLong("home"));
            target.getChunk(saved);
            destination = nearbySafe(target, player, saved);
        } else {
            if (level.dimension() != Level.OVERWORLD) { message(player, "overworld_only"); return; }
            // Bound the search; never fall back to an unsafe location or use a command.
            BlockPos spawn = target.getSharedSpawnPos();
            for (int attempt = 0; attempt < 8 && destination == null; attempt++) {
                double angle = player.getRandom().nextDouble() * Math.PI * 2;
                int radius = 512 + player.getRandom().nextInt(1537);
                int x = spawn.getX() + (int) (Math.cos(angle) * radius);
                int z = spawn.getZ() + (int) (Math.sin(angle) * radius);
                BlockPos column = new BlockPos(x, target.getMinBuildHeight(), z);
                if (!target.getWorldBorder().isWithinBounds(column)) continue;
                target.getChunk(column);
                BlockPos feet = target.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, column);
                if (safe(target, player, feet)) destination = feet;
            }
        }
        if (destination == null) {
            // A short retry delay also limits repeated failed chunk searches.
            data.putLong("ready_at", now + 100);
            player.getPersistentData().put(DATA, data);
            message(player, "unsafe");
            return;
        }
        if (player.teleportTo(target, destination.getX() + 0.5, destination.getY(), destination.getZ() + 0.5,
                Set.of(), player.getYRot(), player.getXRot())) {
            player.setDeltaMovement(0, 0, 0);
            player.fallDistance = 0;
            data.putLong("ready_at", now + COOLDOWN);
            player.getPersistentData().put(DATA, data);
            message(player, home ? "home_arrived" : "rtp_arrived");
        }
    }

    private static BlockPos nearbySafe(ServerLevel level, ServerPlayer player, BlockPos origin) {
        for (int dy = 0; dy <= 2; dy++) {
            for (int dx = -2; dx <= 2; dx++) {
                for (int dz = -2; dz <= 2; dz++) {
                    BlockPos pos = origin.offset(dx, dy, dz);
                    if (safe(level, player, pos)) return pos;
                }
            }
        }
        return null;
    }

    private static boolean safe(ServerLevel level, ServerPlayer player, BlockPos pos) {
        var floor = level.getBlockState(pos.below());
        if (!level.getWorldBorder().isWithinBounds(pos) || pos.getY() < level.getMinBuildHeight() + 1
                || pos.getY() + 2 >= level.getMaxBuildHeight()
                || !floor.isFaceSturdy(level, pos.below(), Direction.UP)
                || floor.is(BlockTags.LEAVES) || floor.is(Blocks.MAGMA_BLOCK) || floor.is(Blocks.CACTUS)
                || floor.is(Blocks.CAMPFIRE) || floor.is(Blocks.SOUL_CAMPFIRE)
                || !floor.getFluidState().isEmpty()) return false;
        // Empty feet/head blocks exclude fire, powder snow, plants and portal blocks as well as fluids.
        if (!level.getBlockState(pos).isAir() || !level.getBlockState(pos.above()).isAir()) return false;
        return level.noCollision(player, player.getBoundingBox().move(
                pos.getX() + 0.5 - player.getX(), pos.getY() - player.getY(), pos.getZ() + 0.5 - player.getZ()));
    }

    private static void message(ServerPlayer player, String key) {
        player.displayClientMessage(Component.translatable("grega_core.travel." + key), true);
    }
}
