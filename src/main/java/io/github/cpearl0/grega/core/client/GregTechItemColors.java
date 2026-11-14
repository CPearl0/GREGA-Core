package io.github.cpearl0.grega.core.client;

import com.gregtechceu.gtceu.api.item.MaterialPipeBlockItem;
import com.mojang.logging.LogUtils;
import io.github.cpearl0.grega.core.GregaCore;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

@EventBusSubscriber(modid = GregaCore.MOD_ID, value = Dist.CLIENT)
public final class GregTechItemColors {
    private GregTechItemColors() {}

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerPipeColors(RegisterColorHandlersEvent.Item event) {
        ItemColor originalTint = MaterialPipeBlockItem.tintColor();
        // GTCEu 7.0.2 returns RGB without alpha, making pipe/cable item icons transparent.
        // Preserve its tint layers and RGB, including the untinted -1 sentinel.
        // Upstream fix: https://github.com/GregTechCEu/GregTech-Modern/pull/3543
        ItemColor opaqueTint = (stack, tintIndex) -> originalTint.getColor(stack, tintIndex) | 0xFF000000;
        int registered = 0;
        for (var item : BuiltInRegistries.ITEM) {
            if (item instanceof MaterialPipeBlockItem) {
                event.register(opaqueTint, item);
                registered++;
            }
        }
        LogUtils.getLogger().info("Registered opaque item colors for {} GregTech cables and pipes", registered);
    }
}
