package io.babywolf.forge.compat.jei;

import io.babywolf.forge.BaublesRebornMod;
import io.babywolf.forge.registry.ItemRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

/**
 * Optional JEI integration. JEI finds this class through the annotation, so it is only ever loaded when JEI is
 * installed. The crafting recipes show up in JEI by themselves; this adds the "Information" pages that explain
 * what a recipe cannot: where the heart items drop and what the canisters do.
 */
@JeiPlugin
public class BaublesJeiPlugin implements IModPlugin {
    private static final ResourceLocation UID = new ResourceLocation(BaublesRebornMod.MOD_ID, "jei_plugin");

    private static void addInfo(IRecipeRegistration registration, RegistryObject<Item> item, String key) {
        registration.addIngredientInfo(new ItemStack(item.get()), VanillaTypes.ITEM_STACK, new TranslatableComponent("jei.baublesreborn.info." + key));
    }

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        addInfo(registration, ItemRegistry.ITEM_HEART_SHARD, "heart_shard");
        addInfo(registration, ItemRegistry.ITEM_HEART_CRIMSON, "heart_crimson");
        addInfo(registration, ItemRegistry.ITEM_HEART_GILDED, "heart_gilded");
        addInfo(registration, ItemRegistry.ITEM_HEART_VERDANT, "heart_verdant");
        addInfo(registration, ItemRegistry.ITEM_CANISTER, "canister_empty");
        addInfo(registration, ItemRegistry.ITEM_CANISTER_RED, "canister_filled");
        addInfo(registration, ItemRegistry.ITEM_CANISTER_YELLOW, "canister_filled");
        addInfo(registration, ItemRegistry.ITEM_CANISTER_GREEN, "canister_filled");
    }
}
