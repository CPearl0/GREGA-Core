package io.github.cpearl0.grega.core;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

public final class CoreTooltips {
    private CoreTooltips() {}

    public static void add(ItemTooltipEvent event) {
        var id = BuiltInRegistries.ITEM.getKey(event.getItemStack().getItem());
        if (!id.getNamespace().equals(GregaCore.MOD_ID)) return;
        String tier = switch (id.getPath()) {
            case "laminated_nano_processor" -> "HV";
            case "laminated_nano_processor_assembly" -> "EV";
            case "laminated_nano_processor_computer" -> "IV";
            case "laminated_nano_processor_mainframe" -> "LuV";
            default -> null;
        };
        if (tier == null) return;
        event.getToolTip().add(Component.translatable("grega_core.circuit.rating", tier)
                .withStyle(ChatFormatting.LIGHT_PURPLE));
    }
}
