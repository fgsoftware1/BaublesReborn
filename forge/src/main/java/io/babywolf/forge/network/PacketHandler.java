package io.babywolf.forge.network;

import io.babywolf.forge.BaublesRebornMod;
import io.babywolf.forge.network.msg.OpenBaublesInvPacket;
import io.babywolf.forge.network.msg.OpenNormalInvPacket;
import io.babywolf.forge.network.msg.SyncPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public class PacketHandler {
    public static SimpleChannel INSTANCE;
    private static int ID = 0;

    public static int nextID() {
        return ID++;
    }

    public static void registerMessages() {
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(BaublesRebornMod.MOD_ID, "net_channel"), () -> "1.0", s -> true, s -> true);

        // Each packet is bound to one direction, so neither side ever runs a handler meant for the other.
        var toServer = Optional.of(NetworkDirection.PLAY_TO_SERVER);
        var toClient = Optional.of(NetworkDirection.PLAY_TO_CLIENT);
        INSTANCE.registerMessage(nextID(), OpenBaublesInvPacket.class, OpenBaublesInvPacket::toBytes, OpenBaublesInvPacket::new, OpenBaublesInvPacket::handle, toServer);
        INSTANCE.registerMessage(nextID(), OpenNormalInvPacket.class, OpenNormalInvPacket::toBytes, OpenNormalInvPacket::new, OpenNormalInvPacket::handle, toServer);
        INSTANCE.registerMessage(nextID(), SyncPacket.class, SyncPacket::toBytes, SyncPacket::new, SyncPacket::handle, toClient);
    }
}
