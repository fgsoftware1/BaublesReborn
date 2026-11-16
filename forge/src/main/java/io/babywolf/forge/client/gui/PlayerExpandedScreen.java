package io.babywolf.forge.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.babywolf.forge.BaublesRebornMod;
import io.babywolf.forge.api.bauble.BaubleType;
import io.babywolf.forge.client.KeyRegistry;
import io.babywolf.forge.container.PlayerExpandedContainer;
import io.babywolf.forge.container.slots.SlotBauble;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class PlayerExpandedScreen extends EffectRenderingInventoryScreen<PlayerExpandedContainer> {
    public static final ResourceLocation BACKGROUND = new ResourceLocation(BaublesRebornMod.MOD_ID, "textures/gui/arcane_inventory.png");

    // A 224x208 GUI panel is blitted from the top-left of a 256x256 atlas.
    // Normal/hover ring states are in the right-hand spare atlas strip.
    public static final int GUI_WIDTH = 224;
    public static final int GUI_HEIGHT = 208;
    public static final int BUTTON_U = 224;
    public static final int BUTTON_HOVERED_U = 240;
    public static final int BUTTON_RING_V = 212;
    public static final int BUTTON_SIZE = 12;

    // Base item icon of each bauble type, drawn faded in the empty slots for that type.
    private static final Map<BaubleType, ResourceLocation> PLACEHOLDER_ICONS = new EnumMap<>(BaubleType.class);
    private static final float PLACEHOLDER_ALPHA = 0.4f;
    // Canister slots 0..2 take the red, yellow and green canister.
    private static final int[] CANISTER_TINTS = {0xE4515D, 0xE9B64D, 0x53C875};

    static {
        for (BaubleType type : BaubleType.values()) {
            if (type == BaubleType.TRINKET) continue;
            String icon = type == BaubleType.CANISTER ? "heart_canister_empty" : type.name().toLowerCase(Locale.ROOT);
            PLACEHOLDER_ICONS.put(type, new ResourceLocation(BaublesRebornMod.MOD_ID, "textures/item/" + icon + ".png"));
        }
    }

    public PlayerExpandedScreen(PlayerExpandedContainer container, Inventory inventory, Component name) {
        super(container, inventory, name);
        this.imageWidth = GUI_WIDTH;
        this.imageHeight = GUI_HEIGHT;
    }

    /**
     * The specific type a bauble slot is for (TRINKET fits several slots and is not a slot type of its own).
     */
    private static BaubleType baseTypeOf(int baubleSlot) {
        for (BaubleType type : BaubleType.values()) {
            if (type != BaubleType.TRINKET && type.hasSlot(baubleSlot)) return type;
        }
        return null;
    }

    @Override
    public void containerTick() {
        this.menu.baubles.setEventBlock(false);
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

    @Override
    protected void renderBg(@Nonnull PoseStack poseStack, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        // The atlas is 256x256; the GUI itself occupies [0..223, 0..207].
        // GuiComponent.blit's six-argument overload samples a 256x256 atlas.
        this.blit(poseStack, this.leftPos, this.topPos, 0, 0, GUI_WIDTH, GUI_HEIGHT);
        renderEmptySlotPlaceholders(poseStack);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        // Valid equipment destinations glow when an item is carried; the mouse target
        // also receives an outline. These are drawn below actual item sprites.
        renderEquipmentSlotHints(poseStack, mouseX, mouseY);
        RenderSystem.disableBlend();

        if (this.minecraft != null && this.minecraft.player != null) {
            int entityX = this.leftPos + 73;
            // Feet position of the paper-doll; 3px above the frame's floor line so the model sits a bit higher.
            int entityY = this.topPos + 91;
            InventoryScreen.renderEntityInInventory(entityX, entityY, 27, entityX - mouseX, entityY - 50 - mouseY, this.minecraft.player);
        }
    }

    /**
     * Empty bauble slots show a faded copy of the base item of the type they accept (the grey, untinted
     * item icon: textures/item/ring.png, amulet.png, ...). The background sprite itself has plain slots.
     * Canister slots use the empty canister, tinted with the colour of the canister that goes there.
     */
    private void renderEmptySlotPlaceholders(PoseStack poseStack) {
        for (Slot slot : this.menu.slots) {
            if (!(slot instanceof SlotBauble) || slot.hasItem() || !slot.isActive()) continue;

            int index = slot.getSlotIndex();
            BaubleType type = baseTypeOf(index);
            if (type == null) continue;

            int tint = type == BaubleType.CANISTER ? CANISTER_TINTS[index % CANISTER_TINTS.length] : 0xFFFFFF;
            RenderSystem.setShaderColor((tint >> 16 & 0xFF) / 255f, (tint >> 8 & 0xFF) / 255f, (tint & 0xFF) / 255f, PLACEHOLDER_ALPHA);
            RenderSystem.setShaderTexture(0, PLACEHOLDER_ICONS.get(type));
            // Stand-alone 16x16 texture, not a region of the GUI atlas.
            blit(poseStack, this.leftPos + slot.x, this.topPos + slot.y, 0, 0, 16, 16, 16, 16);
        }
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    /**
     * Slot indices: 5..8 armor, 9..11 Heart Canisters, 12..18 Baubles.
     * We deliberately test slot.mayPlace(), which enforces the item's BaubleType,
     * instead of marking every accessory slot as valid for every accessory.
     */
    private void renderEquipmentSlotHints(PoseStack poseStack, int mouseX, int mouseY) {
        ItemStack carried = this.menu.getCarried();
        boolean carrying = !carried.isEmpty();
        int max = Math.min(this.menu.slots.size(), 19);

        for (int menuIndex = 5; menuIndex < max; menuIndex++) {
            Slot slot = this.menu.slots.get(menuIndex);
            if (!slot.isActive()) continue;

            int x = this.leftPos + slot.x;
            int y = this.topPos + slot.y;
            boolean hovered = mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16;
            boolean accepted = carrying && slot.mayPlace(carried);

            // Show valid destinations while dragging an item, or the current empty
            // equipment slot when the player is simply inspecting their inventory.
            if (carrying ? !accepted : (!hovered || slot.hasItem())) continue;

            int rgb;
            if (menuIndex == 9) rgb = 0xE4515D;       // red canister
            else if (menuIndex == 10) rgb = 0xE9B64D;  // yellow canister
            else if (menuIndex == 11) rgb = 0x53C875;  // green canister
            else if (menuIndex < 9) rgb = 0x75B6E9;   // armor
            else rgb = 0xC189FF;                      // baubles

            // Subtle fill leaves the empty-slot ghost illustration visible.
            int alpha = hovered ? 0x5A : 0x30;
            fill(poseStack, x, y, x + 16, y + 16, (alpha << 24) | rgb);
            int edge = (hovered ? 0xDD : 0x99) << 24 | rgb;
            fill(poseStack, x, y, x + 16, y + 1, edge);
            fill(poseStack, x, y + 15, x + 16, y + 16, edge);
            fill(poseStack, x, y + 1, x + 1, y + 15, edge);
            fill(poseStack, x + 15, y + 1, x + 16, y + 15, edge);
        }
    }

    @Override
    protected void renderTooltip(@Nonnull PoseStack poseStack, int mouseX, int mouseY) {
        // Empty bauble slots say what they accept, the slot icons alone are hard to tell apart.
        if (this.menu.getCarried().isEmpty() && this.hoveredSlot instanceof SlotBauble slot && !slot.hasItem()) {
            int index = slot.getSlotIndex();
            for (BaubleType type : BaubleType.values()) {
                if (type != BaubleType.TRINKET && type.hasSlot(index)) {
                    this.renderTooltip(poseStack, new TranslatableComponent("tooltip.baublesreborn.empty_slot", new TranslatableComponent("name." + type)), mouseX, mouseY);
                    return;
                }
            }
        }
        super.renderTooltip(poseStack, mouseX, mouseY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int what) {
        if (KeyRegistry.KEY_BAUBLES.isActiveAndMatches(InputConstants.getKey(keyCode, scanCode))) {
            if (this.minecraft != null && this.minecraft.player != null) {
                this.minecraft.player.closeContainer();
            }
            return true;
        } else {
            return super.keyPressed(keyCode, scanCode, what);
        }
    }
}
