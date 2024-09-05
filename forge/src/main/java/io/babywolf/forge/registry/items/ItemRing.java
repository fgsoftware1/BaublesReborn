package io.babywolf.forge.registry.items;

import io.babywolf.forge.api.BaublesAPI;
import io.babywolf.forge.api.bauble.BaubleType;
import io.babywolf.forge.api.bauble.IBauble;
import io.babywolf.forge.api.bauble.IBaublesItemHandler;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemRing extends Item implements IBauble {
    public ItemRing(Properties properties) {
        super(properties);
    }

    @Override
    public BaubleType getBaubleType(ItemStack stack) {
        return BaubleType.RING;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        if (world.isClientSide) {
            IBaublesItemHandler baubles = (IBaublesItemHandler) BaublesAPI.getBaublesHandler(player);
            for (int i = 0; i < baubles.getSlots(); i++) {
                baubles.getStackInSlot(i);
                if (baubles.getStackInSlot(i).isEmpty() && baubles.isItemValidForSlot(i, player.getItemInHand(hand))) {
                    baubles.setStackInSlot(i, player.getItemInHand(hand).copy());
                    if (!player.isCreative()) {
                        player.getInventory().canPlaceItem(player.getInventory().selected, ItemStack.EMPTY);
                    }
                    onEquipped(player, player.getItemInHand(hand));
                    break;
                }
            }
        }
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }
}
