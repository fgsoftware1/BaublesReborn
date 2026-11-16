package io.babywolf.forge.client.gui.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import io.babywolf.forge.api.cap.CapabilityBaubles;
import io.babywolf.forge.client.gui.HudConfigScreen;
import io.babywolf.forge.setup.ModConfigs;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class BaublesOverlayRenderer implements IIngameOverlay {
    private static final int ITEM_SIZE = 16;
    private static final int GAP = 2;
    private static final int MARGIN = 4;

    /**
     * Where an overlay holding {@code count} items goes on a screen of the given (scaled) size.
     * The position is a percentage of the room left over once the overlay itself is subtracted, so it is the same
     * relative spot at every window size and GUI scale and can never end up outside the screen.
     */
    public static Bounds layout(int count, int screenWidth, int screenHeight, int xPercent, int yPercent, boolean horizontal) {
        int length = count * ITEM_SIZE + Math.max(0, count - 1) * GAP;
        int width = horizontal ? length : ITEM_SIZE;
        int height = horizontal ? ITEM_SIZE : length;
        int x = MARGIN + Math.round(freeSpace(screenWidth, width) * xPercent / 100f);
        int y = MARGIN + Math.round(freeSpace(screenHeight, height) * yPercent / 100f);
        // Only matters when the overlay is longer than the screen: keep its start visible.
        return new Bounds(Math.max(0, Math.min(x, screenWidth - width)), Math.max(0, Math.min(y, screenHeight - height)), width, height);
    }

    /**
     * Inverse of {@link #layout}: the percentage that puts the overlay's left/top edge at {@code position}.
     */
    public static int toPercent(int position, int screenSize, int overlaySize) {
        int free = freeSpace(screenSize, overlaySize);
        if (free <= 0) return 0;
        return Math.max(0, Math.min(100, Math.round((position - MARGIN) * 100f / free)));
    }

    private static int freeSpace(int screenSize, int overlaySize) {
        return Math.max(0, screenSize - 2 * MARGIN - overlaySize);
    }

    public static void renderItems(Minecraft mc, List<ItemStack> stacks, Bounds bounds, boolean horizontal) {
        int x = bounds.x();
        int y = bounds.y();
        for (ItemStack stack : stacks) {
            mc.getItemRenderer().renderGuiItem(stack, x, y);
            mc.getItemRenderer().renderGuiItemDecorations(mc.font, stack, x, y);
            if (horizontal) x += ITEM_SIZE + GAP;
            else y += ITEM_SIZE + GAP;
        }
    }

    /**
     * The local player's equipped baubles, in slot order, without the empty slots.
     */
    public static List<ItemStack> equippedBaubles(Player player) {
        List<ItemStack> stacks = new ArrayList<>();
        if (player == null) return stacks;
        player.getCapability(CapabilityBaubles.BAUBLES).ifPresent(baubles -> {
            for (int i = 0; i < baubles.getSlots(); i++) {
                ItemStack stack = baubles.getStackInSlot(i);
                if (!stack.isEmpty()) stacks.add(stack);
            }
        });
        return stacks;
    }

    @Override
    public void render(ForgeIngameGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
        if (!ModConfigs.RENDER_BAUBLE_OVERLAY.get()) return;

        Minecraft mc = Minecraft.getInstance();
        // The settings screen draws its own (draggable) preview.
        if (mc.options.hideGui || mc.screen instanceof HudConfigScreen) return;

        List<ItemStack> stacks = equippedBaubles(mc.player);
        if (stacks.isEmpty()) return;

        boolean horizontal = ModConfigs.OVERLAY_HORIZONTAL.get();
        Bounds bounds = layout(stacks.size(), screenWidth, screenHeight, ModConfigs.OVERLAY_X_PERCENT.get(), ModConfigs.OVERLAY_Y_PERCENT.get(), horizontal);
        renderItems(mc, stacks, bounds, horizontal);
    }

    /**
     * Screen-space rectangle of the overlay, in scaled GUI pixels.
     */
    public record Bounds(int x, int y, int width, int height) {
        public boolean contains(double px, double py) {
            return px >= x && px < x + width && py >= y && py < y + height;
        }
    }
}
