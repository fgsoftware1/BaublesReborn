package io.babywolf.forge.client.gui.overlay;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.babywolf.forge.BaublesRebornMod;
import io.babywolf.forge.setup.ModConfigs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.ForgeIngameGui;

/**
 * Stacked health bar: health above the vanilla 20 is not drawn as extra rows of hearts but as coloured hearts
 * on top of the normal row, one colour per extra 20 health (so the canister hearts overlay the red ones).
 * <p>
 * It works in two halves, both driven by {@code ForgeIngameGuiMixin}: the max health vanilla lays the bar out with
 * is capped to one row, and after vanilla has drawn that row the over-health is painted over it here.
 */
@OnlyIn(Dist.CLIENT)
public class HealthBarOverlay {
    private static final float ROW_HEALTH = 20f;
    private static final int HEARTS_PER_ROW = 10;

    // Tint of each over-health layer: 21-40 health, 41-60, ... (repeats after the last one).
    private static final int[] LAYER_COLORS = {0xF5B82E, 0x4CD964, 0x4FC3F7, 0xB37FEB};

    // Heart sprites in gui/icons.png (9x9 each; the half heart is 9px to the right, hardcore variants 45px down).
    private static final int U_NORMAL = 52;
    private static final int U_POISONED = 88;
    private static final int U_WITHERED = 124;
    private static final int U_FROZEN = 178;

    /**
     * A white-ish copy of the vanilla icon sheet, so its heart can be tinted any colour (tinting the red heart
     * directly could only ever give darker reds). Built from whatever icons.png the resource packs provide.
     */
    private static final ResourceLocation TINTABLE_ICONS = new ResourceLocation(BaublesRebornMod.MOD_ID, "dynamic/tintable_icons");
    private static boolean tintableReady;
    private static boolean tintableFailed;

    private static boolean enabled() {
        return ModConfigs.STACK_HEALTH_BAR.get();
    }

    /**
     * The max health vanilla should lay the hearts out with: never more than one row while stacking is on.
     */
    public static float capMaxHealth(float maxHealth) {
        return enabled() ? Math.min(maxHealth, ROW_HEALTH) : maxHealth;
    }

    /**
     * Called when resource packs are reloaded: icons.png may have changed.
     */
    public static void invalidateTexture() {
        tintableReady = false;
        tintableFailed = false;
    }

    /**
     * Paints the over-health hearts over the row vanilla just drew at ({@code left}, {@code top}).
     */
    public static void render(ForgeIngameGui gui, PoseStack poseStack, int left, int top) {
        if (!enabled()) return;
        Minecraft mc = Minecraft.getInstance();
        if (!(mc.getCameraEntity() instanceof Player player)) return;

        int health = Mth.ceil(player.getHealth());
        if (health <= ROW_HEALTH) return;

        // Top layer = the partly filled one; the layer below it is completely full and shows through behind it.
        int topLayer = (health - 1) / (int) ROW_HEALTH;
        int topLayerHealth = health - topLayer * (int) ROW_HEALTH;

        int statusU = statusSpriteU(player);
        boolean tinted = statusU < 0 && ensureTintableTexture(mc);
        int u = statusU >= 0 ? statusU : U_NORMAL;
        int v = player.level.getLevelData().isHardcore() ? 45 : 0;

        // Same bobbing heart as vanilla while regenerating, so the overlay stays glued to the hearts under it.
        int regenHeart = player.hasEffect(MobEffects.REGENERATION) ? gui.getGuiTicks() % Mth.ceil(ROW_HEALTH + 5.0F) : -1;

        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, tinted ? TINTABLE_ICONS : GuiComponent.GUI_ICONS_LOCATION);

        for (int heart = 0; heart < HEARTS_PER_ROW; heart++) {
            int x = left + heart * 8;
            int y = heart == regenHeart ? top - 2 : top;
            int filled = Mth.clamp(topLayerHealth - heart * 2, 0, 2);

            // Hearts the top layer does not (fully) cover show the layer below; below layer 1 that is the vanilla red heart.
            if (filled < 2 && topLayer >= 2) {
                setColor(topLayer - 1, tinted);
                gui.blit(poseStack, x, y, u, v, 9, 9);
            }
            if (filled > 0) {
                setColor(topLayer, tinted);
                gui.blit(poseStack, x, y, filled == 2 ? u : u + 9, v, 9, 9);
            }
        }

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
        RenderSystem.disableBlend();
    }

    /**
     * Poison, wither and freezing recolour the vanilla hearts; the overlay then uses those vanilla sprites untinted
     * so the status stays visible. Returns -1 when no such status is active.
     */
    private static int statusSpriteU(Player player) {
        if (player.hasEffect(MobEffects.POISON)) return U_POISONED;
        if (player.hasEffect(MobEffects.WITHER)) return U_WITHERED;
        if (player.isFullyFrozen()) return U_FROZEN;
        return -1;
    }

    private static void setColor(int layer, boolean tinted) {
        if (!tinted) {
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            return;
        }
        int color = LAYER_COLORS[(layer - 1) % LAYER_COLORS.length];
        RenderSystem.setShaderColor((color >> 16 & 0xFF) / 255f, (color >> 8 & 0xFF) / 255f, (color & 0xFF) / 255f, 1f);
    }

    private static boolean ensureTintableTexture(Minecraft mc) {
        if (tintableReady) return true;
        if (tintableFailed) return false;

        try (var resource = mc.getResourceManager().getResource(GuiComponent.GUI_ICONS_LOCATION)) {
            NativeImage image = NativeImage.read(resource.getInputStream());
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    int abgr = image.getPixelRGBA(x, y);
                    // Brightest channel as grey: the heart's red becomes white and keeps its shading.
                    int value = Math.max(abgr & 0xFF, Math.max(abgr >> 8 & 0xFF, abgr >> 16 & 0xFF));
                    image.setPixelRGBA(x, y, (abgr & 0xFF000000) | value << 16 | value << 8 | value);
                }
            }
            // The texture manager takes ownership of the image and releases a previously registered one.
            mc.getTextureManager().register(TINTABLE_ICONS, new DynamicTexture(image));
            tintableReady = true;
        } catch (Exception e) {
            // Fall back to plain (red) vanilla hearts rather than retrying every frame.
            tintableFailed = true;
        }
        return tintableReady;
    }
}
