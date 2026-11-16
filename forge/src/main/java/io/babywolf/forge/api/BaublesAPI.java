package io.babywolf.forge.api;

import io.babywolf.forge.api.bauble.BaubleType;
import io.babywolf.forge.api.bauble.IBaublesItemHandler;
import io.babywolf.forge.api.cap.CapabilityBaubles;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;

/**
 * @author Azanor
 * @author lazynessmind - porter
 */
@SuppressWarnings({"ConstantConditions", "unused"})
public class BaublesAPI {
    //Retrieves the baubles inventory capability handler for the supplied player
    public static LazyOptional<IBaublesItemHandler> getBaublesHandler(Player player) {
        return player.getCapability(CapabilityBaubles.BAUBLES);
    }

    //Returns if the passed in item is equipped in a bauble slot. Will return the first slot found
    //@return -1 if not found and slot number if it is found
    public static int isBaubleEquipped(Player player, Item bauble) {
        return getBaublesHandler(player).map(handler -> {
            for (int a = 0; a < handler.getSlots(); a++) {
                if (!handler.getStackInSlot(a).isEmpty() && handler.getStackInSlot(a).getItem() == bauble)
                    return a;
            }
            return -1;
        }).orElse(-1);
    }

    public static int getEmptySlotForBaubleType(Player playerEntity, BaubleType type) {
        IBaublesItemHandler itemHandler = getBaublesHandler(playerEntity).orElseThrow(NullPointerException::new);

        for (int i : type.getValidSlots()) {
            if (itemHandler.getStackInSlot(i).isEmpty()) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Right-click equip: moves the bauble held in {@code hand} into the first empty bauble slot that accepts it.
     * The inventory is only changed on the server; the baubles container syncs the slot to clients on its next tick.
     *
     * @return success if a free slot was found, pass (nothing changed) otherwise
     */
    public static InteractionResultHolder<ItemStack> equipFromHand(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        IBaublesItemHandler baubles = getBaublesHandler(player).orElse(null);
        if (baubles == null || held.isEmpty()) return InteractionResultHolder.pass(held);

        for (int slot = 0; slot < baubles.getSlots(); slot++) {
            if (!baubles.getStackInSlot(slot).isEmpty() || !baubles.isItemValidForSlot(slot, held)) continue;

            if (!player.level.isClientSide) {
                ItemStack equipped = held.split(1);
                baubles.setStackInSlot(slot, equipped);
                equipped.getCapability(CapabilityBaubles.ITEM_BAUBLE).ifPresent(b -> b.onEquipped(player, equipped));
            }
            return InteractionResultHolder.sidedSuccess(held, player.level.isClientSide);
        }

        return InteractionResultHolder.pass(held);
    }
}