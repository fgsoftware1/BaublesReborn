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
    private static final ForgeConfigSpec CLIENT;
    public static ForgeConfigSpec.BooleanValue ENABLED;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push(MOD_ID);
        RENDER_BAUBLE = builder.comment("When enabled baubles can render on player.").define("render_baubles", true);
        RENDER_BAUBLE_OVERLAY = builder.comment("Render bauble overlay").define("render_baubles_overlay", true);

        builder.pop();

        CLIENT = builder.build();
    }

    public static void registerAndLoadConfig() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT);
        CommentedFileConfig config = CommentedFileConfig.builder(FMLPaths.CONFIGDIR.get().resolve(MOD_ID.concat("-client.toml"))).sync().writingMode(WritingMode.REPLACE).build();
        config.load();
        CLIENT.setConfig(config);
    }
}
