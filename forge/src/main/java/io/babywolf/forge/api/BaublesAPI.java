package io.babywolf.forge.api;

import io.babywolf.forge.api.bauble.BaubleType;
import io.babywolf.forge.api.bauble.IBaublesItemHandler;
import io.babywolf.forge.api.cap.CapabilityBaubles;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
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
}