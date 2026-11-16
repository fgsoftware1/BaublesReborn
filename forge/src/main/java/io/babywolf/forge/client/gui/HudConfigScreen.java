package io.babywolf.forge.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import io.babywolf.forge.client.gui.overlay.BaublesOverlayRenderer;
import io.babywolf.forge.client.gui.overlay.BaublesOverlayRenderer.Bounds;
import io.babywolf.forge.registry.ItemRegistry;
import io.babywolf.forge.setup.ModConfigs;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigGuiHandler;
import net.minecraftforge.fml.ModLoadingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.IntConsumer;

/**
 * Settings for the equipped-baubles HUD: on/off, row/column layout and position.
 * The position can be set with the sliders or by dragging the preview around the screen.
 */
@OnlyIn(Dist.CLIENT)
public class HudConfigScreen extends Screen {
    private static final int BUTTON_WIDTH = 200;
    private static final int ROW_HEIGHT = 24;

    private final @Nullable Screen parent;
    private PercentSlider xSlider;
    private PercentSlider ySlider;

    private boolean dragging;
    private double grabX;
    private double grabY;

    public HudConfigScreen(@Nullable Screen parent) {
        super(new TranslatableComponent("screen.baublesreborn.hud_config"));
        this.parent = parent;
    }

    /**
     * Makes the "Config" button of the Mods list open this screen. Client only.
     */
    public static void registerConfigScreen() {
        ModLoadingContext.get().registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory.class,
                () -> new ConfigGuiHandler.ConfigGuiFactory((mc, parent) -> new HudConfigScreen(parent)));
    }

    @Override
    protected void init() {
        int left = (this.width - BUTTON_WIDTH) / 2;
        int top = Math.max(32, this.height / 2 - 60);

        addRenderableWidget(CycleButton.onOffBuilder(ModConfigs.RENDER_BAUBLE_OVERLAY.get())
                .create(left, top, BUTTON_WIDTH, 20, new TranslatableComponent("option.baublesreborn.hud_enabled"),
                        (button, value) -> ModConfigs.RENDER_BAUBLE_OVERLAY.set(value)));

        addRenderableWidget(CycleButton.booleanBuilder(
                        new TranslatableComponent("option.baublesreborn.hud_layout.row"),
                        new TranslatableComponent("option.baublesreborn.hud_layout.column"))
                .withInitialValue(ModConfigs.OVERLAY_HORIZONTAL.get())
                .create(left, top + ROW_HEIGHT, BUTTON_WIDTH, 20, new TranslatableComponent("option.baublesreborn.hud_layout"),
                        (button, value) -> ModConfigs.OVERLAY_HORIZONTAL.set(value)));

        this.xSlider = addRenderableWidget(new PercentSlider(left, top + ROW_HEIGHT * 2, BUTTON_WIDTH, "option.baublesreborn.hud_x",
                ModConfigs.OVERLAY_X_PERCENT.get(), ModConfigs.OVERLAY_X_PERCENT::set));
        this.ySlider = addRenderableWidget(new PercentSlider(left, top + ROW_HEIGHT * 3, BUTTON_WIDTH, "option.baublesreborn.hud_y",
                ModConfigs.OVERLAY_Y_PERCENT.get(), ModConfigs.OVERLAY_Y_PERCENT::set));

        int half = (BUTTON_WIDTH - 4) / 2;
        addRenderableWidget(new Button(left, top + ROW_HEIGHT * 4, half, 20, new TranslatableComponent("option.baublesreborn.hud_reset"), button -> {
            ModConfigs.RENDER_BAUBLE_OVERLAY.set(true);
            ModConfigs.OVERLAY_HORIZONTAL.set(false);
            ModConfigs.OVERLAY_X_PERCENT.set(0);
            ModConfigs.OVERLAY_Y_PERCENT.set(100);
            assert this.minecraft != null;
            this.init(this.minecraft, this.width, this.height);
        }));
        addRenderableWidget(new Button(left + half + 4, top + ROW_HEIGHT * 4, half, 20, CommonComponents.GUI_DONE, button -> onClose()));
    }

    /**
     * What the preview shows: the baubles actually worn, or a sample set when there are none (or no world is loaded).
     */
    private List<ItemStack> previewStacks() {
        assert this.minecraft != null;
        List<ItemStack> equipped = BaublesOverlayRenderer.equippedBaubles(this.minecraft.player);
        if (!equipped.isEmpty()) return equipped;
        return List.of(
                new ItemStack(ItemRegistry.ITEM_CANISTER_RED.get()),
                new ItemStack(ItemRegistry.ITEM_CANISTER_YELLOW.get()),
                new ItemStack(ItemRegistry.ITEM_CANISTER_GREEN.get()),
                new ItemStack(ItemRegistry.ITEM_RING.get()));
    }

    private Bounds previewBounds(int itemCount) {
        return BaublesOverlayRenderer.layout(itemCount, this.width, this.height,
                ModConfigs.OVERLAY_X_PERCENT.get(), ModConfigs.OVERLAY_Y_PERCENT.get(), ModConfigs.OVERLAY_HORIZONTAL.get());
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        renderBackground(poseStack);
        drawCenteredString(poseStack, this.font, this.title, this.width / 2, 8, 0xFFFFFF);
        drawCenteredString(poseStack, this.font, new TranslatableComponent("screen.baublesreborn.hud_config.hint"), this.width / 2, 20, 0xA0A0A0);
        super.render(poseStack, mouseX, mouseY, partialTick);

        List<ItemStack> stacks = previewStacks();
        Bounds b = previewBounds(stacks.size());
        boolean enabled = ModConfigs.RENDER_BAUBLE_OVERLAY.get();
        int border = !enabled ? 0xFFAA3333 : (this.dragging || b.contains(mouseX, mouseY)) ? 0xFFFFFFFF : 0xFF909090;
        fill(poseStack, b.x() - 2, b.y() - 2, b.x() + b.width() + 2, b.y() + b.height() + 2, border);
        fill(poseStack, b.x() - 1, b.y() - 1, b.x() + b.width() + 1, b.y() + b.height() + 1, 0xFF202020);
        BaublesOverlayRenderer.renderItems(this.minecraft, stacks, b, ModConfigs.OVERLAY_HORIZONTAL.get());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) return true;
        if (button != 0) return false;

        Bounds b = previewBounds(previewStacks().size());
        if (!b.contains(mouseX, mouseY)) return false;
        this.dragging = true;
        this.grabX = mouseX - b.x();
        this.grabY = mouseY - b.y();
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (!this.dragging) return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);

        Bounds b = previewBounds(previewStacks().size());
        int xPercent = BaublesOverlayRenderer.toPercent((int) Math.round(mouseX - this.grabX), this.width, b.width());
        int yPercent = BaublesOverlayRenderer.toPercent((int) Math.round(mouseY - this.grabY), this.height, b.height());
        ModConfigs.OVERLAY_X_PERCENT.set(xPercent);
        ModConfigs.OVERLAY_Y_PERCENT.set(yPercent);
        this.xSlider.setPercent(xPercent);
        this.ySlider.setPercent(yPercent);
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.dragging && button == 0) {
            this.dragging = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void onClose() {
        assert this.minecraft != null;
        this.minecraft.setScreen(this.parent);
    }

    @Override
    public void removed() {
        ModConfigs.save();
    }

    private static class PercentSlider extends AbstractSliderButton {
        private final String translationKey;
        private final IntConsumer onChange;

        PercentSlider(int x, int y, int width, String translationKey, int percent, IntConsumer onChange) {
            super(x, y, width, 20, TextComponent.EMPTY, percent / 100.0);
            this.translationKey = translationKey;
            this.onChange = onChange;
            updateMessage();
        }

        private int percent() {
            return (int) Math.round(this.value * 100.0);
        }

        void setPercent(int percent) {
            this.value = percent / 100.0;
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            setMessage(new TranslatableComponent(this.translationKey, percent()));
        }

        @Override
        protected void applyValue() {
            this.onChange.accept(percent());
        }
    }
}
