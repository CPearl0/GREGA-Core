package io.github.cpearl0.grega.core;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import pokefenn.totemic.api.TotemicAPI;
import pokefenn.totemic.api.ceremony.Ceremony;
import pokefenn.totemic.api.ceremony.CeremonyEffectContext;
import pokefenn.totemic.api.ceremony.CeremonyInstance;
import pokefenn.totemic.api.ceremony.StartupContext;
import pokefenn.totemic.api.music.MusicInstrument;
import pokefenn.totemic.api.registry.RegistryAPI;

import java.util.function.Supplier;
import java.util.stream.Stream;

/** Rubber follows the Buffalo Dance and Eagle Dance through their instruments. */
public final class TotemicCeremonies {
    private static final int RANGE = 6;
    private static final ResourceLocation OAK_SAPLING = ResourceLocation.parse("minecraft:oak_sapling");
    private static final ResourceLocation RUBBER_SAPLING = ResourceLocation.parse("gtceu:rubber_sapling");
    private static final String MISSING_SAPLING = "grega_core.ceremony.rubber_awakening.no_saplings";
    private static final DeferredRegister<Ceremony> CEREMONIES =
            DeferredRegister.create(RegistryAPI.CEREMONY_REGISTRY, GregaCore.MOD_ID);

    // Rattle -> eagle-bone whistle is unused by Totemic and requires both animal ceremonies.
    public static final Supplier<Ceremony> RUBBER_AWAKENING = CEREMONIES.register("rubber_awakening",
            () -> new Ceremony(9600, 600, RubberAwakening::new,
                    () -> instrument("rattle"), () -> instrument("eagle_bone_whistle")));

    private TotemicCeremonies() {}

    public static void register(IEventBus modBus) {
        CEREMONIES.register(modBus);
    }

    private static MusicInstrument instrument(String name) {
        return TotemicAPI.get().registry().instruments()
                .get(ResourceLocation.fromNamespaceAndPath("totemic", name));
    }

    private static Stream<BlockPos> saplings(Level level, BlockPos origin) {
        var oak = BuiltInRegistries.BLOCK.get(OAK_SAPLING);
        return BlockPos.betweenClosedStream(origin.offset(-RANGE, -RANGE, -RANGE),
                        origin.offset(RANGE, RANGE, RANGE))
                .filter(pos -> level.getBlockState(pos).is(oak));
    }

    private static Component missingSaplingMessage() {
        return Component.translatable(MISSING_SAPLING,
                Component.translatable("block.totemic.totem_base"),
                Component.translatable("block.minecraft.oak_sapling"));
    }

    private static final class RubberAwakening implements CeremonyInstance {
        @Override
        public boolean canSelect(Level level, BlockPos pos, Entity initiator) {
            if (saplings(level, pos).findAny().isPresent()) return true;
            if (initiator != null) initiator.sendSystemMessage(missingSaplingMessage());
            return false;
        }

        @Override
        public boolean canStartEffect(Level level, BlockPos pos, StartupContext context) {
            if (saplings(level, pos).findAny().isPresent()) return true;
            context.getInitiatingPlayer().ifPresent(player ->
                    player.displayClientMessage(missingSaplingMessage(), true));
            return false;
        }

        @Override
        public void effect(Level level, BlockPos pos, CeremonyEffectContext context) {
            if (!(level instanceof ServerLevel server)) return;
            var rubber = BuiltInRegistries.BLOCK.get(RUBBER_SAPLING).defaultBlockState();
            // Snapshot positions before block updates; BlockPos streams reuse mutable positions.
            var targets = saplings(level, pos).map(BlockPos::immutable).toList();
            for (var target : targets) {
                if (server.setBlock(target, rubber, 3)) {
                    server.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                            target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5,
                            10, 0.3, 0.3, 0.3, 0.0);
                }
            }
        }
    }
}
