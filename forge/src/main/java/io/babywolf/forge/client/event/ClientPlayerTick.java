package io.babywolf.forge.client.event;

import io.babywolf.forge.BaublesRebornMod;
import io.babywolf.forge.network.PacketHandler;
import io.babywolf.forge.network.msg.OpenBaublesInvPacket;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static io.babywolf.forge.BaublesRebornMod.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT)
public class ClientPlayerTick {

    @SubscribeEvent
    public static void playerTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            if (BaublesRebornMod.KEY_BAUBLES.consumeClick() && Minecraft.getInstance().isWindowActive()) {
                PacketHandler.INSTANCE.sendToServer(new OpenBaublesInvPacket());
            }
        }
    }
}