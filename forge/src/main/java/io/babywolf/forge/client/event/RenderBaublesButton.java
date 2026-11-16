package io.babywolf.forge.client.event;

import io.babywolf.forge.BaublesRebornMod;
import io.babywolf.forge.client.gui.PlayerExpandedScreen;
import io.babywolf.forge.client.gui.widget.BaublesButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BaublesRebornMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class RenderBaublesButton {

    @SuppressWarnings("rawtypes")
    @SubscribeEvent
    public static void onGuiPostInit(ScreenEvent.InitScreenEvent.Post event) {
        Screen screen = event.getScreen();
        // Not the creative inventory: it is also an EffectRenderingInventoryScreen but has no baubles tab.
        if (screen instanceof InventoryScreen) {
            // Inside the top-right corner of the vanilla paper-doll box (black area x 26..74, y 8..77, measured
            // from inventory.png), 2px in from both its top and right edges.
            // Same 10px ring as the back button on the expanded screen.
            event.addListener(new BaublesButton((AbstractContainerScreen) screen, 63, 10, PlayerExpandedScreen.BUTTON_SIZE - 2, PlayerExpandedScreen.BUTTON_SIZE - 2));
        } else if (screen instanceof PlayerExpandedScreen) {
            // Grey/gold back ring in the paper-doll's top-right corner: drawn at 10px (the sprite is 12px),
            // keeping the top and right edges where the full-size ring had them.
            event.addListener(new BaublesButton((AbstractContainerScreen) screen, 82, 28, PlayerExpandedScreen.BUTTON_SIZE - 2, PlayerExpandedScreen.BUTTON_SIZE - 2));
        }
    }
}