package io.github.cpearl0.grega.core;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(GregaCore.MOD_ID)
public final class GregaCore {
    public static final String MOD_ID = "grega_core";

    public GregaCore(IEventBus modBus) {
        modBus.addListener(CoreContent::register);
        NeoForge.EVENT_BUS.addListener(CoreTooltips::add);
        NeoForge.EVENT_BUS.addListener(BookTravel::interact);
        NeoForge.EVENT_BUS.addListener(BookTravel::portalSpawn);
        NeoForge.EVENT_BUS.addListener(BookTravel::clonePlayer);
    }
}
