package io.babywolf.forge.registry.items;

import io.babywolf.forge.api.BaublesAPI;
import io.babywolf.forge.api.bauble.BaubleType;
import io.babywolf.forge.api.bauble.IBauble;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ItemRing extends Item implements IBauble {
    public ItemRing(Properties properties) {
        super(properties);
    }

    @Override
    public BaubleType getBaubleType(ItemStack stack) {
        return BaubleType.RING;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level world, @NotNull Player player, @NotNull InteractionHand hand) {
        return BaublesAPI.equipFromHand(player, hand);
    }
}
