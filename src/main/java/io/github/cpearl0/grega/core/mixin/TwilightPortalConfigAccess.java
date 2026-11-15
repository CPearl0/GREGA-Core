package io.github.cpearl0.grega.core.mixin;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Pseudo
@Mixin(targets = "twilightforest.config.TFConfig", remap = false)
public interface TwilightPortalConfigAccess {
    @Accessor("originDimension")
    static String grega$originDimension() { throw new AssertionError("Mixin accessor"); }
    @Accessor("allowPortalsInOtherDimensions")
    static boolean grega$allowOtherDimensions() { throw new AssertionError("Mixin accessor"); }
    @Accessor("disablePortalCreation")
    static boolean grega$disablePortalCreation() { throw new AssertionError("Mixin accessor"); }
    @Accessor("portalCreationPermission")
    static int grega$portalCreationPermission() { throw new AssertionError("Mixin accessor"); }
    @Invoker("getPortalLockingAdvancement")
    static ResourceLocation grega$lockingAdvancement(Player player) { throw new AssertionError("Mixin invoker"); }
}
