package io.babywolf.forge.registry;

import io.babywolf.forge.BaublesRebornMod;
import io.babywolf.forge.registry.items.*;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BaublesRebornMod.MOD_ID);
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
    // Base items of the remaining bauble types (the ring above is the base RING item).
    public static RegistryObject<Item> ITEM_AMULET = ITEMS.register("item_amulet",
            () -> new ItemAmulet(new Item.Properties().tab(
                    BAUBLESREBORN_GROUP
            ).stacksTo(1))
    );
    public static RegistryObject<Item> ITEM_BELT = ITEMS.register("item_belt",
            () -> new ItemBelt(new Item.Properties().tab(
                    BAUBLESREBORN_GROUP
            ).stacksTo(1))
    );
    public static RegistryObject<Item> ITEM_HEAD = ITEMS.register("item_head",
            () -> new ItemHead(new Item.Properties().tab(
                    BAUBLESREBORN_GROUP
            ).stacksTo(1))
    );
    public static RegistryObject<Item> ITEM_BODY = ITEMS.register("item_body",
            () -> new ItemBody(new Item.Properties().tab(
                    BAUBLESREBORN_GROUP
            ).stacksTo(1))
    );
    public static RegistryObject<Item> ITEM_CHARM = ITEMS.register("item_charm",
            () -> new ItemCharm(new Item.Properties().tab(
                    BAUBLESREBORN_GROUP
            ).stacksTo(1))
    );
    // Canister materials. Shards drop from hostile mobs and structure chests (see HeartLoot); four make a
    // Crimson Heart, which is upgraded to Gilded and then Verdant. A heart + an empty canister = a filled one.
    public static RegistryObject<Item> ITEM_HEART_SHARD = ITEMS.register("item_heart_shard",
            () -> new Item(new Item.Properties().tab(
                    BAUBLESREBORN_GROUP
            ))
    );
    public static RegistryObject<Item> ITEM_HEART_CRIMSON = ITEMS.register("item_heart_crimson",
            () -> new Item(new Item.Properties().tab(
                    BAUBLESREBORN_GROUP
            ).stacksTo(16))
    );
    public static RegistryObject<Item> ITEM_HEART_GILDED = ITEMS.register("item_heart_gilded",
            () -> new Item(new Item.Properties().tab(
                    BAUBLESREBORN_GROUP
            ).stacksTo(16).rarity(Rarity.UNCOMMON))
    );
    public static RegistryObject<Item> ITEM_HEART_VERDANT = ITEMS.register("item_heart_verdant",
            () -> new Item(new Item.Properties().tab(
                    BAUBLESREBORN_GROUP
            ).stacksTo(16).rarity(Rarity.RARE))
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

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
