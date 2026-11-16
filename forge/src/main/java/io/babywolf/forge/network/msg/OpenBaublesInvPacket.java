package io.babywolf.forge.network.msg;

import io.babywolf.forge.container.GuiProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public class OpenBaublesInvPacket {

    public OpenBaublesInvPacket(FriendlyByteBuf buf) {
    }

    public OpenBaublesInvPacket() {
    }

    public void toBytes(FriendlyByteBuf buf) {
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            var playerEntity = ctx.get().getSender();
            if (playerEntity != null) {
                // No closeContainer() here: it sends a close packet that makes the client drop the
                // current screen (re-grabbing the mouse) before the new one opens, which snaps the
                // cursor to the screen centre. openGui already closes the old menu server-side.
                NetworkHooks.openGui(playerEntity, new GuiProvider());
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
