package io.babywolf.forge.network.msg;

import io.babywolf.forge.client.network.ClientPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncPacket {
    private final int playerId;
    private final byte slot;
    private final ItemStack bauble;

    public SyncPacket(FriendlyByteBuf buf) {
        this.playerId = buf.readInt();
        this.slot = buf.readByte();
        this.bauble = buf.readItem();
    }

    public SyncPacket(int playerId, byte slot, ItemStack bauble) {
        this.playerId = playerId;
        this.slot = slot;
        this.bauble = bauble;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.playerId);
        buf.writeByte(this.slot);
        buf.writeItem(this.bauble);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        // Client classes are only touched inside the DistExecutor call, so this class stays loadable on a dedicated server.
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handleSync(playerId, slot, bauble)));
        ctx.get().setPacketHandled(true);
    }
}
