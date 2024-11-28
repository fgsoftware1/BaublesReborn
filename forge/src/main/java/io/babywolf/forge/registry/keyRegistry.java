package io.babywolf.forge.registry;

import io.babywolf.forge.BaublesRebornMod;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.Bindings;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class keyRegistry {
    public static final DeferredRegister<Bindings> ITEMS = DeferredRegister.create(ForgeRegistries.Keys, BaublesRebornMod.MOD_ID);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
