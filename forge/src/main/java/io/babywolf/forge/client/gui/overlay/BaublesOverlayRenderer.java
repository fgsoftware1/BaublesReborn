package io.babywolf.forge.client.gui.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import io.babywolf.forge.api.bauble.BaubleType;
import io.babywolf.forge.api.cap.CapabilityBaubles;
import io.babywolf.forge.setup.ModConfigs;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;

public class BaublesOverlayRenderer implements IIngameOverlay {
    @Override
    public void render(ForgeIngameGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
        if (!ModConfigs.RENDER_BAUBLE_OVERLAY.get()) return;

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        player.getCapability(CapabilityBaubles.BAUBLES).ifPresent(baubles -> {
            int xPos = 5;
            int itemSize = 20;
            int margin = 5;
            int totalHeight = (baubles.getSlots() * itemSize) + ((baubles.getSlots() - 1) * margin);
            int yPos = screenHeight - totalHeight - margin;

            for (int i = 0; i < baubles.getSlots(); i++) {
                ItemStack bauble = baubles.getStackInSlot(i);
                if (!bauble.isEmpty()) {
                    mc.getItemRenderer().renderGuiItem(bauble, xPos, yPos);
                    yPos += itemSize - margin;
                }
            }
        });
    }
}

