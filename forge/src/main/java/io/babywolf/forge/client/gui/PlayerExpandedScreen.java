package io.babywolf.forge.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.babywolf.forge.BaublesRebornMod;
import io.babywolf.forge.container.PlayerExpandedContainer;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nonnull;
import java.util.HashMap;

public class PlayerExpandedScreen extends EffectRenderingInventoryScreen<PlayerExpandedContainer> {
    public static final ResourceLocation BACKGROUND = new ResourceLocation(BaublesRebornMod.MOD_ID, "textures/gui/expanded_inventory_experimental.png");

    public PlayerExpandedScreen(PlayerExpandedContainer container, Inventory inventory, Component name) {
        super(container, inventory, name);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    public void containerTick() {
        this.menu.baubles.setEventBlock(false);
        this.canSeeEffects();
        this.resetGuiLeft();
    }

    @Override
    protected void init() {
        this.renderables.clear();
        super.init();
        this.resetGuiLeft();
    }

    @Override
    protected void renderLabels(@Nonnull PoseStack poseStack, int mouseX, int mouseY) {
    }

    @Override
    public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float delta) {
        renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, delta);
        renderTooltip(poseStack, mouseX, mouseY);
    }

    private final static HashMap<String, Object> guistate = PlayerExpandedScreen.guistate;

    @Override
    protected void renderBg(@Nonnull PoseStack poseStack, float delta, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderTexture(0, BACKGROUND);
        blit(poseStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);

        RenderSystem.setShaderTexture(0, BACKGROUND);
        blit(poseStack, this.leftPos + -23, this.topPos + 1, 0, 0, 256, 256, 256, 256);

        RenderSystem.disableBlend();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int what) {
        if (BaublesRebornMod.KEY_BAUBLES.isActiveAndMatches(InputConstants.getKey(keyCode, scanCode))) {
            if (this.minecraft != null && this.minecraft.player != null) {
                this.minecraft.player.closeContainer();
            }
            return true;
        } else {
            return super.keyPressed(keyCode, scanCode, what);
        }
    }

    private void resetGuiLeft() {
        this.leftPos = (this.width - this.imageWidth) / 2;
    }


}