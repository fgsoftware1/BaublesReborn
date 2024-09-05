package io.babywolf.forge.registry;

import io.babywolf.forge.BaublesRebornMod;
import io.babywolf.forge.registry.items.*;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BaublesRebornMod.MOD_ID);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    public static final CreativeModeTab BAUBLESREBORN_GROUP = new CreativeModeTab("baublesRebornModTab") {
        @Override
        public @NotNull ItemStack makeIcon() {
            return new ItemStack(ItemRegistry.ITEM_RING.get());
        }
    };

    public static RegistryObject<Item> ITEM_RING = ITEMS.register("item_ring",
            () -> new ItemRing(new Item.Properties().tab(
                    BAUBLESREBORN_GROUP
            ).stacksTo(1))
    );

    public static RegistryObject<Item> ITEM_CANISTER = ITEMS.register("item_canister",
            () -> new ItemCanister(new Item.Properties().tab(
                    BAUBLESREBORN_GROUP
            ).stacksTo(1))
    );

    public static RegistryObject<Item> ITEM_CANISTER_RED = ITEMS.register("item_canister_red",
            () -> new ItemCanisterRed(new Item.Properties().tab(
                    BAUBLESREBORN_GROUP
            ).stacksTo(1))
    );

    public static RegistryObject<Item> ITEM_CANISTER_GREEN = ITEMS.register("item_canister_green",
            () -> new ItemCanisterGreen(new Item.Properties().tab(
                    BAUBLESREBORN_GROUP
            ).stacksTo(1))
    );


    public static RegistryObject<Item> ITEM_CANISTER_YELLOW = ITEMS.register("item_canister_yellow",
            () -> new ItemCanisterYellow(new Item.Properties().tab(
                    BAUBLESREBORN_GROUP
            ).stacksTo(1))
    );
}
