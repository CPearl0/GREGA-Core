package io.github.cpearl0.grega.core.machine;

import com.google.common.collect.HashBasedTable;
import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.UITemplate;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.steam.SimpleSteamMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.data.material.GTMaterials;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.lowdragmc.lowdraglib.utils.Position;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

import java.util.List;

/** Native steam processing with separate ingredient and product tanks for mixer recipes. */
public final class SteamMixerMachine extends SimpleSteamMachine {
    private static final ManagedFieldHolder FIELDS =
            new ManagedFieldHolder(SteamMixerMachine.class, SimpleSteamMachine.MANAGED_FIELD_HOLDER);

    @Persisted
    public final NotifiableFluidTank importFluids;
    @Persisted
    public final NotifiableFluidTank exportFluids;

    public SteamMixerMachine(IMachineBlockEntity holder, boolean highPressure) {
        super(holder, highPressure);
        importFluids = new NotifiableFluidTank(this, getRecipeType().getMaxInputs(FluidRecipeCapability.CAP),
                16000, IO.IN);
        // Steam belongs in the inherited fuel tank, not in the ingredient tanks.
        importFluids.setFilter(fluid -> !fluid.is(GTMaterials.Steam.getFluidTag()));
        exportFluids = new NotifiableFluidTank(this, getRecipeType().getMaxOutputs(FluidRecipeCapability.CAP),
                16000, IO.OUT);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return FIELDS;
    }

    @Override
    public ModularUI createUI(Player player) {
        var storage = HashBasedTable.<IO, RecipeCapability<?>, Object>create();
        storage.put(IO.IN, ItemRecipeCapability.CAP, importItems.storage);
        storage.put(IO.OUT, ItemRecipeCapability.CAP, exportItems.storage);
        storage.put(IO.IN, FluidRecipeCapability.CAP, importFluids);
        storage.put(IO.OUT, FluidRecipeCapability.CAP, exportFluids);
        var recipeSlots = getRecipeType().getRecipeUI().createUITemplate(recipeLogic::getProgressPercent,
                storage, new CompoundTag(), List.of(), true, isHighPressure);
        int width = Math.max(176, recipeSlots.getSize().width + 16);
        recipeSlots.setSelfPosition(new Position((width - recipeSlots.getSize().width) / 2, 24));
        int inventoryY = Math.max(84, 24 + recipeSlots.getSize().height + 10);
        return new ModularUI(width, inventoryY + 82, this, player)
                .background(GuiTextures.BACKGROUND_STEAM.get(isHighPressure))
                .widget(new LabelWidget(5, 5, getBlockState().getBlock().getDescriptionId()))
                .widget(recipeSlots)
                .widget(UITemplate.bindPlayerInventory(player.getInventory(),
                        GuiTextures.SLOT_STEAM.get(isHighPressure), (width - 162) / 2, inventoryY, true));
    }
}
