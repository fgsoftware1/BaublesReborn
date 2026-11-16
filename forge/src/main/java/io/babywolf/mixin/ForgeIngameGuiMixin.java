package io.babywolf.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import io.babywolf.forge.client.gui.overlay.HealthBarOverlay;
import net.minecraftforge.client.gui.ForgeIngameGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Hooks the health bar for {@link HealthBarOverlay}. {@code renderHealth} is a Forge method (not obfuscated),
 * hence {@code remap = false}.
 */
@Mixin(value = ForgeIngameGui.class, remap = false)
public abstract class ForgeIngameGuiMixin {
    @Unique
    private int baublesreborn$healthTop;

    // Where the health row is about to be drawn; left_height is advanced past it inside the method.
    @Inject(method = "renderHealth", at = @At("HEAD"))
    private void baublesreborn$beforeHealth(int width, int height, PoseStack poseStack, CallbackInfo ci) {
        this.baublesreborn$healthTop = height - ((ForgeIngameGui) (Object) this).left_height;
    }

    // healthMax is the only float local: capping it keeps the bar (and everything stacked above it) to one row.
    @ModifyVariable(method = "renderHealth", at = @At("STORE"), ordinal = 0)
    private float baublesreborn$capHealthMax(float healthMax) {
        return HealthBarOverlay.capMaxHealth(healthMax);
    }

    @Inject(method = "renderHealth", at = @At("TAIL"))
    private void baublesreborn$afterHealth(int width, int height, PoseStack poseStack, CallbackInfo ci) {
        HealthBarOverlay.render((ForgeIngameGui) (Object) this, poseStack, width / 2 - 91, this.baublesreborn$healthTop);
    }
}
