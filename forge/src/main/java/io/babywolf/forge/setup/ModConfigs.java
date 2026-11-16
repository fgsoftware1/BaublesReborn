package io.babywolf.forge.setup;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;

import static io.babywolf.forge.BaublesRebornMod.MOD_ID;

public class ModConfigs {
    public static final ForgeConfigSpec.BooleanValue RENDER_BAUBLE;
    public static final ForgeConfigSpec.BooleanValue RENDER_BAUBLE_OVERLAY;
    public static final ForgeConfigSpec.IntValue OVERLAY_X_PERCENT;
    public static final ForgeConfigSpec.IntValue OVERLAY_Y_PERCENT;
    public static final ForgeConfigSpec.BooleanValue OVERLAY_HORIZONTAL;
    public static final ForgeConfigSpec.BooleanValue STACK_HEALTH_BAR;
    private static final ForgeConfigSpec CLIENT;
    public static ForgeConfigSpec.BooleanValue ENABLED;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push(MOD_ID);
        RENDER_BAUBLE = builder.comment("When enabled baubles can render on player.").define("render_baubles", true);
        RENDER_BAUBLE_OVERLAY = builder.comment("Render bauble overlay").define("render_baubles_overlay", true);
        OVERLAY_X_PERCENT = builder.comment("Horizontal position of the bauble overlay: 0 = left edge of the screen, 100 = right edge.")
                .defineInRange("overlay_x_percent", 0, 0, 100);
        OVERLAY_Y_PERCENT = builder.comment("Vertical position of the bauble overlay: 0 = top edge of the screen, 100 = bottom edge.")
                .defineInRange("overlay_y_percent", 100, 0, 100);
        OVERLAY_HORIZONTAL = builder.comment("Lay the bauble overlay out as a row instead of a column.").define("overlay_horizontal", false);
        STACK_HEALTH_BAR = builder.comment("Draw health above 20 as coloured hearts on top of the normal health row instead of as extra rows.")
                .define("stack_health_bar", true);

        builder.pop();

        CLIENT = builder.build();
    }

    public static void registerAndLoadConfig() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT);
        CommentedFileConfig config = CommentedFileConfig.builder(FMLPaths.CONFIGDIR.get().resolve(MOD_ID.concat("-client.toml"))).sync().writingMode(WritingMode.REPLACE).build();
        config.load();
        CLIENT.setConfig(config);
    }

    /**
     * Writes values changed with {@code ConfigValue#set} back to the client config file.
     */
    public static void save() {
        CLIENT.save();
    }
}
