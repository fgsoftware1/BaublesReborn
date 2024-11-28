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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
    @OnlyIn(Dist.CLIENT)
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level world, @NotNull Player player, @NotNull InteractionHand hand) {
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

        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }
}
