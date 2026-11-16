package io.babywolf.forge.event;

import io.babywolf.forge.BaublesRebornMod;
import io.babywolf.forge.api.bauble.IBauble;
import io.babywolf.forge.api.cap.CapabilityBaubles;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = BaublesRebornMod.MOD_ID)
public class AttachCapability {

    private static final ResourceLocation CAP = new ResourceLocation(BaublesRebornMod.MOD_ID, "bauble_cap");

    @SubscribeEvent
    public static void attachCaps(AttachCapabilitiesEvent<ItemStack> event) {
        ItemStack stack = event.getObject();
        if (stack.getItem() instanceof IBauble bauble) {
            event.addCapability(CAP, new ICapabilityProvider() {
                private final LazyOptional<IBauble> opt = LazyOptional.of(() -> bauble);

                @SuppressWarnings("ConstantConditions")
                @Nonnull
                @Override
                public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
                    return CapabilityBaubles.ITEM_BAUBLE.orEmpty(cap, opt);
                }
            });
        }
    }
}
