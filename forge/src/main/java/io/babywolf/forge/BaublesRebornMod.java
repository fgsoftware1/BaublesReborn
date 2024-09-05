package io.babywolf.forge;

import io.babywolf.forge.client.gui.PlayerExpandedScreen;
import io.babywolf.forge.client.gui.overlay.BaublesOverlayRenderer;
import io.babywolf.forge.network.PacketHandler;
import io.babywolf.forge.registry.ItemRegistry;
import io.babywolf.forge.setup.ModConfigs;
import io.babywolf.forge.setup.ModItems;
import io.babywolf.forge.setup.ModMenus;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.lwjgl.glfw.GLFW;

@Mod(BaublesRebornMod.MOD_ID)
public class BaublesRebornMod {
    public static final String MOD_ID = "baublesreborn";
    public static KeyMapping KEY_BAUBLES = null;

    public BaublesRebornMod() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModConfigs.registerAndLoadConfig();

        ModItems.init();
        ModMenus.init();

        ItemRegistry.register(eventBus);

        eventBus.addListener(this::setupCommon);
        eventBus.addListener(this::setupClient);
    }

    private void setupCommon(FMLCommonSetupEvent event) {
        PacketHandler.registerMessages();
    }

    private void setupClient(FMLClientSetupEvent event) {
        KEY_BAUBLES = new KeyMapping("keybind.baublesinventory", GLFW.GLFW_KEY_B, "key.categories.inventory");

        event.enqueueWork(() -> {
            MenuScreens.register(ModMenus.PLAYER_BAUBLES.get(), PlayerExpandedScreen::new);
            ClientRegistry.registerKeyBinding(KEY_BAUBLES);
            OverlayRegistry.registerOverlayAbove(ForgeIngameGui.HOTBAR_ELEMENT, "baubles_overlay", new BaublesOverlayRenderer());
        });
    }
}
