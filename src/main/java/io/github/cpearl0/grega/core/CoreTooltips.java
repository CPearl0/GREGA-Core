package io.github.cpearl0.grega.core;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

public final class CoreTooltips {
    private CoreTooltips() {}

    public static void add(ItemTooltipEvent event) {
        var id = BuiltInRegistries.ITEM.getKey(event.getItemStack().getItem());
        if (!id.getNamespace().equals(GregaCore.MOD_ID)) return;
        if (id.getPath().equals("germanium_press")) {
            event.getToolTip().add(Component.translatable("grega_core.germanium.tooltip"));
        } else if (id.getPath().startsWith("laminated_nano_")) {
            event.getToolTip().add(Component.translatable("grega_core.laminated.tooltip"));
            String tier = switch (id.getPath()) {
                case "laminated_nano_processor" -> "HV";
                case "laminated_nano_processor_assembly" -> "EV";
                case "laminated_nano_processor_computer" -> "IV";
                default -> "LuV";
            };
            event.getToolTip().add(Component.translatable("grega_core.circuit.rating", tier));
        }
    }
}
