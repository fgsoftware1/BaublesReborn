package io.babywolf.forge.registry.items;

import io.babywolf.forge.api.bauble.BaubleType;
import io.babywolf.forge.api.bauble.IBauble;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemCanisterYellow extends Item implements IBauble {
    public ItemCanisterYellow(Properties properties) {
        super(properties);
    }

    @Override
    public BaubleType getBaubleType(ItemStack stack) {
        return BaubleType.CANISTER;
    }
}