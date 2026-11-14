package io.github.cpearl0.grega.core;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(GregaCore.MOD_ID)
public final class GregaCore {
    public static final String MOD_ID = "grega_core";

    public GregaCore(IEventBus modBus) {
        TravelSpells.register();
        modBus.addListener((FMLCommonSetupEvent event) -> event.enqueueWork(PackRecipeChanges::install));
        modBus.addListener(CoreContent::register);
        modBus.addListener(CoreContent::creativeTab);
        NeoForge.EVENT_BUS.addListener(CoreTooltips::add);
        NeoForge.EVENT_BUS.addListener(TravelMagic::portalSpawn);
        NeoForge.EVENT_BUS.addListener(TravelMagic::clonePlayer);
    }
}
