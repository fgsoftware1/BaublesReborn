package io.babywolf.forge.container.slots;

import io.babywolf.forge.api.bauble.IBauble;
import io.babywolf.forge.api.bauble.IBaublesItemHandler;
import io.babywolf.forge.api.cap.CapabilityBaubles;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class SlotBauble extends SlotItemHandler {
    int baubleSlot;
    Player player;

    public SlotBauble(Player player, IBaublesItemHandler itemHandler, int slot, int par4, int par5) {
        super(itemHandler, slot, par4, par5);
        this.baubleSlot = slot;
        this.player = player;
    }

    @Override
    public boolean mayPickup(Player player) {
        ItemStack stack = getItem();
        if (stack.isEmpty())
            return false;

        IBauble bauble = stack.getCapability(CapabilityBaubles.ITEM_BAUBLE).orElseThrow(NullPointerException::new);
        return bauble.canUnequip(player);
    }

    @Override
    public void onTake(Player playerIn, ItemStack stack) {
        if (!hasItem() && !((IBaublesItemHandler) getItemHandler()).isEventBlocked() && stack.getCapability(CapabilityBaubles.ITEM_BAUBLE).isPresent()) {
            stack.getCapability(CapabilityBaubles.ITEM_BAUBLE, null).ifPresent((iBauble) -> iBauble.onUnequipped(playerIn, stack));
        }
        super.onTake(playerIn, stack);
    }

    @Override
    public void set(ItemStack stack) {
        if (hasItem() && !ItemStack.isSame(stack, getItem()) && !((IBaublesItemHandler) getItemHandler()).isEventBlocked() && getItem().getCapability(CapabilityBaubles.ITEM_BAUBLE, null).isPresent()) {
            getItem().getCapability(CapabilityBaubles.ITEM_BAUBLE, null).ifPresent((iBauble) -> iBauble.onUnequipped(player, stack));
        }

        ItemStack oldstack = getItem().copy();
        super.set(stack);

        if (hasItem() && !ItemStack.isSame(oldstack, getItem()) && !((IBaublesItemHandler) getItemHandler()).isEventBlocked() && getItem().getCapability(CapabilityBaubles.ITEM_BAUBLE, null).isPresent()) {
            getItem().getCapability(CapabilityBaubles.ITEM_BAUBLE, null).ifPresent((iBauble) -> iBauble.onEquipped(player, stack));
        }
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}