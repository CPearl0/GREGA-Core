package io.github.cpearl0.grega.core;

import com.hollingsworth.arsnouveau.api.registry.GlyphRegistry;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.method.MethodSelf;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

import java.util.List;
import java.util.Set;

/** Genuine Ars glyphs: learned at the Scribe's Table, assembled and cast in spell books. */
public final class TravelSpells {
    public static final TravelEffect NETHER_GATE = new TravelEffect("nether_gate", "Nether Gate", 80, null);
    public static final TravelEffect SET_HOME = new TravelEffect("set_home", "Set Home", 20, TravelMagic.Action.SET_HOME);
    public static final TravelEffect HOME = new TravelEffect("home", "Homeward", 80, TravelMagic.Action.HOME);
    public static final TravelEffect RTP = new TravelEffect("rtp", "Wander", 100, TravelMagic.Action.RTP);
    public static final List<TravelEffect> ALL = List.of(NETHER_GATE, SET_HOME, HOME, RTP);

    private TravelSpells() {}

    public static void register() {
        ALL.forEach(GlyphRegistry::registerSpell);
    }

    public static final class TravelEffect extends AbstractEffect {
        private final int mana;
        private final TravelMagic.Action action;

        private TravelEffect(String name, String englishName, int mana, TravelMagic.Action action) {
            super(CoreContent.id("glyph_" + name), englishName);
            this.mana = mana;
            this.action = action;
        }

        @Override public int getDefaultManaCost() { return mana; }
        @Override public SpellTier defaultTier() { return SpellTier.ONE; }
        @Override public Set<AbstractAugment> getCompatibleAugments() { return Set.of(); }
        @Override public Set<SpellSchool> getSchools() { return Set.of(SpellSchools.MANIPULATION); }

        @Override
        public void onResolveBlock(BlockHitResult hit, Level level, LivingEntity caster,
                                   SpellStats stats, SpellContext context, SpellResolver resolver) {
            if (!(caster instanceof ServerPlayer player)) return;
            if (action == null && context.getSpell().getCastMethod() == MethodTouch.INSTANCE) {
                TravelMagic.openPortal(player, hit);
            } else usage(player);
        }

        @Override
        public void onResolveEntity(EntityHitResult hit, Level level, LivingEntity caster,
                                    SpellStats stats, SpellContext context, SpellResolver resolver) {
            if (!(caster instanceof ServerPlayer player)) return;
            if (action != null && hit.getEntity() == player
                    && context.getSpell().getCastMethod() == MethodSelf.INSTANCE) {
                TravelMagic.travel(player, action);
            } else usage(player);
        }

        private void usage(ServerPlayer player) {
            player.displayClientMessage(Component.translatable("grega_core.travel.use_" +
                    (action == null ? "touch" : "self")), true);
        }
    }
}
