package io.babywolf.forge.client;

import io.babywolf.forge.BaublesRebornMod;
import io.babywolf.forge.client.gui.HudConfigScreen;
import io.babywolf.forge.client.gui.PlayerExpandedScreen;
import io.babywolf.forge.client.gui.overlay.BaublesOverlayRenderer;
import io.babywolf.forge.client.gui.overlay.HealthBarOverlay;
import io.babywolf.forge.setup.ModConfigs;
import io.babywolf.forge.setup.ModMenus;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * Everything that only exists on the physical client. The common mod class reaches this through DistExecutor
 * only, so none of it (or the client classes it references) is loaded on a dedicated server.
 */
@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = BaublesRebornMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    private static final String CONFIGURED_MOD_ID = "configured";

    /**
     * Called from the mod constructor, on the client only.
     */
    public static void init() {
        // The config only holds rendering/HUD options, so a dedicated server has no use for it.
        ModConfigs.registerAndLoadConfig();

        // Optional integration with MrCrayfish's Configured: it builds a full config page (every option, not just
        // the HUD ones) for any mod that has a Forge config but no config screen of its own. So when it is
        // installed we leave the Mods-list "Config" button to it; our HUD screen stays reachable by its key bind.
        if (!ModList.get().isLoaded(CONFIGURED_MOD_ID)) {
            HudConfigScreen.registerConfigScreen();
        }
    }

    @SubscribeEvent
    public static void onRegisterReloadListeners(RegisterClientReloadListenersEvent event) {
        // The tinted hearts are derived from the active resource packs' icons.png.
        event.registerReloadListener((ResourceManagerReloadListener) resourceManager -> HealthBarOverlay.invalidateTexture());
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenus.PLAYER_BAUBLES.get(), PlayerExpandedScreen::new);
            KeyRegistry.register();
            OverlayRegistry.registerOverlayAbove(ForgeIngameGui.HOTBAR_ELEMENT, "baubles_overlay", new BaublesOverlayRenderer());
        });
    }
}
