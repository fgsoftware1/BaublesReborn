package io.babywolf.forge.capability;

import io.babywolf.forge.api.bauble.IBaublesItemHandler;
import io.babywolf.forge.api.cap.CapabilityBaubles;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BaublesContainerProvider implements INBTSerializable<CompoundTag>, ICapabilityProvider {

    private final BaublesContainer container;
    private final LazyOptional<IBaublesItemHandler> baublesHandlerCap;

    public BaublesContainerProvider(Player player) {
        this.container = new BaublesContainer(player);
        this.baublesHandlerCap = LazyOptional.of(() -> this.container).cast();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
        return CapabilityBaubles.BAUBLES.orEmpty(capability, this.baublesHandlerCap);
    }

    @Override
    public CompoundTag serializeNBT() {
        return this.container.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.container.deserializeNBT(nbt);
    }
}