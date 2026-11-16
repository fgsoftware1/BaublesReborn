package io.babywolf.forge.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.babywolf.forge.client.KeyRegistry;
import io.babywolf.forge.client.gui.PlayerExpandedScreen;
import io.babywolf.forge.network.PacketHandler;
import io.babywolf.forge.network.msg.OpenBaublesInvPacket;
import io.babywolf.forge.network.msg.OpenNormalInvPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class BaublesButton extends AbstractButton {
    private static final int FOCUS_OUTLINE_COLOR = 0xFFFFFFFF;

    private final AbstractContainerScreen parentGui;
    private final Minecraft minecraft;
    private final int xOffset;
    private final int yOffset;

    public BaublesButton(AbstractContainerScreen parentGui, int x, int y, int width, int height) {
        // The message is what the narrator reads and what the tooltip shows.
        super(x + parentGui.getGuiLeft(), parentGui.getGuiTop() + y, width, height, createLabel(parentGui));
        this.parentGui = parentGui;
        this.xOffset = x;
        this.yOffset = y;
        this.minecraft = Minecraft.getInstance();
    }

    private static Component createLabel(AbstractContainerScreen parentGui) {
        if (parentGui instanceof InventoryScreen) {
            Component key = KeyRegistry.KEY_BAUBLES.getTranslatedKeyMessage();
            return new TranslatableComponent("button.baublesreborn.open", key);
        }
        return new TranslatableComponent("button.baublesreborn.back");
    }

    @Override
    public void onPress() {
        if (parentGui instanceof InventoryScreen) {
            PacketHandler.INSTANCE.sendToServer(new OpenBaublesInvPacket());
        } else {
            if (this.minecraft.player != null) {
                PacketHandler.INSTANCE.sendToServer(new OpenNormalInvPacket());
                this.displayNormalInventory(this.minecraft.player);
            }
        }
    }

    @Override
    public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        if (!this.visible) return;

        // Follow the parent screen, its left edge moves when the recipe book is toggled.
        this.x = this.parentGui.getGuiLeft() + this.xOffset;
        this.y = this.parentGui.getGuiTop() + this.yOffset;
        this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        // Keyboard focus (Tab) gets the same highlight as the mouse.
        boolean highlighted = this.isHoveredOrFocused();

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, PlayerExpandedScreen.BACKGROUND);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        poseStack.pushPose();
        poseStack.translate(0, 0, 200);
        int u = highlighted ? PlayerExpandedScreen.BUTTON_HOVERED_U : PlayerExpandedScreen.BUTTON_U;
        // Single original-style ring in both screens: grey normally, gold on hover.
        // The sprite is BUTTON_SIZE square; a button created smaller than that draws it scaled down to fit.
        poseStack.pushPose();
        poseStack.translate(this.x, this.y, 0);
        poseStack.scale(this.width / (float) PlayerExpandedScreen.BUTTON_SIZE, this.height / (float) PlayerExpandedScreen.BUTTON_SIZE, 1f);
        this.blit(poseStack, 0, 0, u, PlayerExpandedScreen.BUTTON_RING_V,
                PlayerExpandedScreen.BUTTON_SIZE, PlayerExpandedScreen.BUTTON_SIZE);
        poseStack.popPose();

        if (this.isFocused()) {
            // Explicit focus ring, so the focused state does not rely on the sprite colour alone.
            int left = this.x - 1, top = this.y - 1, right = this.x + this.width + 1, bottom = this.y + this.height + 1;
            fill(poseStack, left, top, right, top + 1, FOCUS_OUTLINE_COLOR);
            fill(poseStack, left, bottom - 1, right, bottom, FOCUS_OUTLINE_COLOR);
            fill(poseStack, left, top, left + 1, bottom, FOCUS_OUTLINE_COLOR);
            fill(poseStack, right - 1, top, right, bottom, FOCUS_OUTLINE_COLOR);
        }
        poseStack.popPose();

        if (this.isHovered) {
            this.parentGui.renderTooltip(poseStack, this.getMessage(), mouseX, mouseY);
        } else if (this.isFocused()) {
            this.parentGui.renderTooltip(poseStack, this.getMessage(), this.x + this.width, this.y + this.height + 12);
        }
    }

    public void displayNormalInventory(Player player) {
        Minecraft.getInstance().setScreen(new InventoryScreen(player));
    }

    @Override
    public void updateNarration(@Nonnull NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }
}
