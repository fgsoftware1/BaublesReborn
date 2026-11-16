package io.babywolf.forge.compat.jer;

import io.babywolf.forge.event.HeartLoot;
import jeresources.api.IDungeonRegistry;
import jeresources.api.IJERAPI;
import jeresources.compatibility.JERAPI;

/**
 * Optional Just Enough Resources integration: lists this mod's chest loot under its own dungeon categories.
 * Only touched when JER is installed (see {@code BaublesRebornMod}), so its classes are never needed otherwise.
 * <p>
 * The API is taken from JER's {@code JERAPI.getInstance()} rather than through the {@code @JERPlugin} field
 * injection its API describes: that works in JER 0.14.1, but in 0.14.2 for 1.18.2 the annotation only compiles
 * on classes while the injector still looks for an annotated field. getInstance() works in both.
 * <p>
 * The Heart Shard mob drop is not listed here: JER's API can add new mobs but not add a drop to the vanilla ones.
 * That drop is described on the shard's JEI information page instead.
 */
public class BaublesJerPlugin {

    /**
     * JER queues these and builds its pages later, so any time during mod loading is fine.
     */
    public static void register() {
        IJERAPI api = JERAPI.getInstance();
        if (api == null) return;

        IDungeonRegistry dungeons = api.getDungeonRegistry();
        registerChest(dungeons, "overworld");
        registerChest(dungeons, "nether");
        registerChest(dungeons, "end");
    }

    private static void registerChest(IDungeonRegistry dungeons, String name) {
        String category = "baublesreborn/hearts_" + name;
        dungeons.registerCategory(category, "jer.baublesreborn.hearts_" + name);
        dungeons.registerChest(category, HeartLoot.chestTable(name));
    }
}
