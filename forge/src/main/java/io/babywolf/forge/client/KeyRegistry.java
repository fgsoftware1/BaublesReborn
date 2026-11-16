package io.babywolf.forge.client;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ClientRegistry;
import org.lwjgl.glfw.GLFW;

/**
 * The mod's key mappings, listed under their own "Baubles Reborn" category in Options > Controls > Key Binds.
 */
@OnlyIn(Dist.CLIENT)
public class KeyRegistry {
    public static final String CATEGORY = "key.categories.baublesreborn";

    public static final KeyMapping KEY_BAUBLES = new KeyMapping("keybind.baublesinventory", GLFW.GLFW_KEY_B, CATEGORY);
    // Unbound by default; the same screen is reachable from Mods > BaublesReborn > Config.
    public static final KeyMapping KEY_HUD_CONFIG = new KeyMapping("keybind.baubleshudconfig", GLFW.GLFW_KEY_UNKNOWN, CATEGORY);

    /**
     * Call from FMLClientSetupEvent (inside enqueueWork).
     */
    public static void register() {
        ClientRegistry.registerKeyBinding(KEY_BAUBLES);
        ClientRegistry.registerKeyBinding(KEY_HUD_CONFIG);
    }
}
