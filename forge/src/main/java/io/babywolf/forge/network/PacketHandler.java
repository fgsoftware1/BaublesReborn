package io.babywolf.forge.network;

import io.babywolf.forge.BaublesRebornMod;
import io.babywolf.forge.network.msg.OpenBaublesInvPacket;
import io.babywolf.forge.network.msg.OpenNormalInvPacket;
import io.babywolf.forge.network.msg.SyncPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
    public static SimpleChannel INSTANCE;
    private static int ID = 0;

    public static int nextID() {
        return ID++;
    }

    public static void registerMessages() {
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(BaublesRebornMod.MOD_ID, "net_channel"), () -> "1.0", s -> true, s -> true);

        INSTANCE.registerMessage(nextID(), OpenBaublesInvPacket.class, OpenBaublesInvPacket::toBytes, OpenBaublesInvPacket::new, OpenBaublesInvPacket::handle);
        INSTANCE.registerMessage(nextID(), OpenNormalInvPacket.class, OpenNormalInvPacket::toBytes, OpenNormalInvPacket::new, OpenNormalInvPacket::handle);
        INSTANCE.registerMessage(nextID(), SyncPacket.class, SyncPacket::toBytes, SyncPacket::new, SyncPacket::handle);
    }
}
