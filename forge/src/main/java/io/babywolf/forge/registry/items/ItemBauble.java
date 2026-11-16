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

/**
 * A plain bauble of a fixed type with no effect of its own: the base amulet, belt, head, body and charm items.
 * Right-click equips it into the first free slot of its type.
 */
public class ItemBauble extends Item implements IBauble {
    private final BaubleType type;

    public ItemBauble(Properties properties, BaubleType type) {
        super(properties);
        this.type = type;
    }

    @Override
    public BaubleType getBaubleType(ItemStack stack) {
        return this.type;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level world, @NotNull Player player, @NotNull InteractionHand hand) {
        return BaublesAPI.equipFromHand(player, hand);
    }
}
