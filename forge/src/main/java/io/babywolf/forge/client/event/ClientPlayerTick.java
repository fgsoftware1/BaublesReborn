package io.babywolf.forge.client.event;

import io.babywolf.forge.client.KeyRegistry;
import io.babywolf.forge.client.gui.HudConfigScreen;
import io.babywolf.forge.network.PacketHandler;
import io.babywolf.forge.network.msg.OpenBaublesInvPacket;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static io.babywolf.forge.BaublesRebornMod.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class ClientPlayerTick {

    @SubscribeEvent
    public static void playerTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            if (KeyRegistry.KEY_BAUBLES.consumeClick() && Minecraft.getInstance().isWindowActive()) {
                PacketHandler.INSTANCE.sendToServer(new OpenBaublesInvPacket());
            }
            if (KeyRegistry.KEY_HUD_CONFIG.consumeClick() && Minecraft.getInstance().screen == null) {
                Minecraft.getInstance().setScreen(new HudConfigScreen(null));
            }
        }
    }
}