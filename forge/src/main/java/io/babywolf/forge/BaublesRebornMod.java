package io.babywolf.forge;

import io.babywolf.forge.client.ClientSetup;
import io.babywolf.forge.network.PacketHandler;
import io.babywolf.forge.registry.ItemRegistry;
import io.babywolf.forge.setup.ModItems;
import io.babywolf.forge.setup.ModMenus;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Common entry point: runs on both the client and the dedicated server, so it must not touch client classes
 * directly. Client-only setup lives in {@link ClientSetup}.
 */
@Mod(BaublesRebornMod.MOD_ID)
public class BaublesRebornMod {
    public static final String MOD_ID = "baublesreborn";

    public BaublesRebornMod() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.init();
        ModMenus.init();

        ItemRegistry.register(eventBus);

        eventBus.addListener(this::setupCommon);
        eventBus.addListener(this::enqueueIMC);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientSetup::init);
    }

    private void setupCommon(FMLCommonSetupEvent event) {
        PacketHandler.registerMessages();
    }

    // Optional integrations that need an explicit call (JEI finds its plugin by annotation instead).
    private void enqueueIMC(InterModEnqueueEvent event) {
        // The plugin class references JER's API, so it must only be touched when JER is installed.
        if (ModList.get().isLoaded(BaublesJerCompat.MOD_ID)) {
            BaublesJerCompat.register();
        }
    }

    /**
     * Indirection so the JER plugin class (and with it JER's API classes) is only loaded on demand.
     */
    private static final class BaublesJerCompat {
        static final String MOD_ID = "jeresources";

        static void register() {
            io.babywolf.forge.compat.jer.BaublesJerPlugin.register();
        }
    }
}
