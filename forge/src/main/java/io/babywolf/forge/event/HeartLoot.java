package io.babywolf.forge.event;

import io.babywolf.forge.BaublesRebornMod;
import io.babywolf.forge.registry.ItemRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

/**
 * Where the heart canister materials come from in the world:
 * <ul>
 *   <li>Heart Shards drop rarely from hostile mobs killed by a player.</li>
 *   <li>Structure chests get one extra pool, taken from this mod's own loot tables
 *       ({@code data/baublesreborn/loot_tables/inject/chests/*.json}), so the odds can be changed by a datapack.</li>
 * </ul>
 */
@Mod.EventBusSubscriber(modid = BaublesRebornMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HeartLoot {
    // Chance for a hostile mob to drop one Heart Shard, plus a bonus per level of Looting.
    private static final float SHARD_DROP_CHANCE = 0.03f;
    private static final float SHARD_DROP_CHANCE_PER_LOOTING = 0.01f;

    private static final ResourceLocation OVERWORLD_CHESTS = chestTable("overworld");
    private static final ResourceLocation NETHER_CHESTS = chestTable("nether");
    private static final ResourceLocation END_CHESTS = chestTable("end");

    // Vanilla chest loot table -> the table of ours that is added to it.
    private static final Map<ResourceLocation, ResourceLocation> CHEST_INJECTIONS = new HashMap<>();

    static {
        CHEST_INJECTIONS.put(BuiltInLootTables.SIMPLE_DUNGEON, OVERWORLD_CHESTS);
        CHEST_INJECTIONS.put(BuiltInLootTables.ABANDONED_MINESHAFT, OVERWORLD_CHESTS);
        CHEST_INJECTIONS.put(BuiltInLootTables.DESERT_PYRAMID, OVERWORLD_CHESTS);
        CHEST_INJECTIONS.put(BuiltInLootTables.JUNGLE_TEMPLE, OVERWORLD_CHESTS);
        CHEST_INJECTIONS.put(BuiltInLootTables.STRONGHOLD_CORRIDOR, OVERWORLD_CHESTS);
        CHEST_INJECTIONS.put(BuiltInLootTables.WOODLAND_MANSION, OVERWORLD_CHESTS);
        CHEST_INJECTIONS.put(BuiltInLootTables.NETHER_BRIDGE, NETHER_CHESTS);
        CHEST_INJECTIONS.put(BuiltInLootTables.BASTION_TREASURE, NETHER_CHESTS);
        CHEST_INJECTIONS.put(BuiltInLootTables.BASTION_OTHER, NETHER_CHESTS);
        CHEST_INJECTIONS.put(BuiltInLootTables.END_CITY_TREASURE, END_CHESTS);
    }

    /**
     * This mod's own loot table that is added to a group of vanilla chests: "overworld", "nether" or "end".
     */
    public static ResourceLocation chestTable(String name) {
        return new ResourceLocation(BaublesRebornMod.MOD_ID, "inject/chests/" + name);
    }

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        ResourceLocation injected = CHEST_INJECTIONS.get(event.getName());
        if (injected == null) return;

        event.getTable().addPool(LootPool.lootPool()
                .name(BaublesRebornMod.MOD_ID + "_hearts")
                .add(LootTableReference.lootTableReference(injected))
                .build());
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (entity.level.isClientSide || !(entity instanceof Enemy)) return;
        // Player kills only, so mob farms that kill by fall damage or suffocation do not rain shards.
        if (!event.isRecentlyHit() || !(event.getSource().getEntity() instanceof Player)) return;

        float chance = SHARD_DROP_CHANCE + SHARD_DROP_CHANCE_PER_LOOTING * event.getLootingLevel();
        if (entity.getRandom().nextFloat() >= chance) return;

        ItemEntity drop = new ItemEntity(entity.level, entity.getX(), entity.getY(), entity.getZ(), new ItemStack(ItemRegistry.ITEM_HEART_SHARD.get()));
        drop.setDefaultPickUpDelay();
        event.getDrops().add(drop);
    }
}
