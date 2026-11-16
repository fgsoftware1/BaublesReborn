package io.babywolf.forge.client.network;

import io.babywolf.forge.api.cap.CapabilityBaubles;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Client half of the server-to-client packets. Kept out of the packet classes because those are loaded on the
 * dedicated server too, where {@link Minecraft} does not exist.
 */
@OnlyIn(Dist.CLIENT)
public class ClientPacketHandler {

    public static void handleSync(int playerId, byte slot, ItemStack bauble) {
        var level = Minecraft.getInstance().level;
        if (level == null) return;
        if (level.getEntity(playerId) instanceof Player player) {
            player.getCapability(CapabilityBaubles.BAUBLES).ifPresent(b -> b.setStackInSlot(slot, bauble));
        }
    }
}
