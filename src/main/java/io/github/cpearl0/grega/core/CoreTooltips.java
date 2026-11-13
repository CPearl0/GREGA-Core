package io.github.cpearl0.grega.core;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

public final class CoreTooltips {
    private CoreTooltips() {}

    public static void add(ItemTooltipEvent event) {
        var id = BuiltInRegistries.ITEM.getKey(event.getItemStack().getItem());
        if (id.getNamespace().equals(GregaCore.MOD_ID)) {
            if (id.getPath().equals("germanium_press")) {
                event.getToolTip().add(Component.translatable("grega_core.germanium.tooltip"));
            } else if (id.getPath().startsWith("laminated_nano_")) {
                event.getToolTip().add(Component.translatable("grega_core.laminated.tooltip"));
            }
        } else if (id.getNamespace().equals("ars_nouveau") && (id.getPath().equals("novice_spell_book")
                || id.getPath().equals("apprentice_spell_book") || id.getPath().equals("archmage_spell_book"))) {
            event.getToolTip().add(Component.translatable("grega_core.book.tooltip"));
        }
    }
}
