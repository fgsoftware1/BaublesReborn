package io.babywolf.forge.api.cap;

import io.babywolf.forge.api.bauble.IBauble;
import io.babywolf.forge.api.bauble.IBaublesItemHandler;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class CapabilityBaubles {
    public static final Capability<IBaublesItemHandler> BAUBLES = CapabilityManager.get(new CapabilityToken<>() {
    });
    public static final Capability<IBauble> ITEM_BAUBLE = CapabilityManager.get(new CapabilityToken<>() {
    });
}