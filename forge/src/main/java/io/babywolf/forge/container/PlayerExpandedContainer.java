package io.babywolf.forge.container;

import com.mojang.datafixers.util.Pair;
import io.babywolf.forge.api.bauble.IBauble;
import io.babywolf.forge.api.bauble.IBaublesItemHandler;
import io.babywolf.forge.api.cap.CapabilityBaubles;
import io.babywolf.forge.container.slots.SlotBauble;
import io.babywolf.forge.registry.items.ItemCanisterGreen;
import io.babywolf.forge.registry.items.ItemCanisterRed;
import io.babywolf.forge.registry.items.ItemCanisterYellow;
import io.babywolf.forge.setup.ModMenus;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PlayerExpandedContainer extends AbstractContainerMenu {
    public static final ResourceLocation[] ARMOR_SLOT_TEXTURES = new ResourceLocation[]{InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS, InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS, InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE, InventoryMenu.EMPTY_ARMOR_SLOT_HELMET};
    public final boolean isLocalWorld;
    private final CraftingContainer craftMatrix = new CraftingContainer(this, 2, 2);
    private final ResultContainer craftResult = new ResultContainer();
    private final Player player;

    public IBaublesItemHandler baubles;

    public PlayerExpandedContainer(int id, Inventory playerInventory, boolean localWorld) {
        super(ModMenus.PLAYER_BAUBLES.get(), id);
        this.isLocalWorld = localWorld;
        this.player = playerInventory.player;

        this.baubles = this.player.getCapability(CapabilityBaubles.BAUBLES).orElseThrow(NullPointerException::new);

        // crafting matrix slots
        int[][] slotPositions = {
                {114, 18},  // Top-left
                {132, 18},  // Top-right
                {114, 36},  // Bottom-left
                {132, 36}   // Bottom-right
        };

        //populate crafting matrix slots
        for (int i = 0; i < 4; ++i) {
            int x = slotPositions[i][0];
            int y = slotPositions[i][1];
            this.addSlot(new Slot(this.craftMatrix, i, x + 1, y));
        }
        // populate crafting matrix output slot
        this.addSlot(new ResultSlot(playerInventory.player, this.craftMatrix, this.craftResult, 0, 154, 28) {
            @Override
            public boolean mayPlace(@Nonnull ItemStack stack) {
                return false;
            }
        });

        //populate offhand slot
        this.addSlot(new Slot(playerInventory, 40, 95, 62) {
            @Override
            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(InventoryMenu.BLOCK_ATLAS, InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD);
            }
        });

        // armor slots
        int[][] armorSlotPositions = {
                {6, 61},  // Boots
                {6, 44},  // Leggings
                {6, 26},  // Chestplate
                {6, 8}    // Helmet
        };

        // armor slots
        final EquipmentSlot[] ARMOR_SLOTS = {
                EquipmentSlot.HEAD, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.FEET
        };

        // populate armor slots
        for (int i = 0; i < 4; ++i) {
            final EquipmentSlot equipmentslot = ARMOR_SLOTS[i];
            this.addSlot(new Slot(playerInventory, 39 - i, armorSlotPositions[i][0], armorSlotPositions[i][1]) {
                @Override
                public int getMaxStackSize() {
                    return 1;
                }

                @Override
                public boolean mayPlace(ItemStack stack) {
                    return stack.canEquip(equipmentslot, player);
                }

                @Override
                public boolean mayPickup(Player playerIn) {
                    ItemStack itemstack = this.getItem();
                    return (itemstack.isEmpty() || playerIn.isCreative() || !EnchantmentHelper.hasBindingCurse(itemstack)) && super.mayPickup(playerIn);
                }

                @Override
                public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                    return Pair.of(InventoryMenu.BLOCK_ATLAS, ARMOR_SLOT_TEXTURES[equipmentslot.getIndex()]);
                }
            });
        }

        //populate bauble slots
        this.addBaubleSlots();

        //player inventory
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 9; ++x) {
                this.addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18 - 1, 84 + y * 18));
            }
        }

        //player hotbar
        for (int x = 0; x < 9; ++x) {
            this.addSlot(new Slot(playerInventory, x, 8 + x * 18, 142));
        }
    }

    @Override
    public void slotsChanged(@Nonnull Container container) {
        try {
            Method onCraftChange = ObfuscationReflectionHelper.findMethod(CraftingMenu.class, "slotChangedCraftingGrid", AbstractContainerMenu.class, Level.class, Player.class, CraftingContainer.class, ResultContainer.class);
            onCraftChange.invoke(null, this, this.player.level, this.player, this.craftMatrix, this.craftResult);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removed(@Nonnull Player player) {
        super.removed(player);
        this.craftResult.clearContent();

        if (!player.level.isClientSide) {
            this.clearContainer(player, this.craftMatrix);
        }
    }

    @Override
    public boolean stillValid(@Nonnull Player par1PlayerEntity) {
        return true;
    }

    @Override
    @Nonnull
    public ItemStack quickMoveStack(@Nonnull Player playerIn, int index) {
        var itemStack = ItemStack.EMPTY;
        var slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            var stackInSlot = slot.getItem();
            itemStack = stackInSlot.copy();

            var entityEquipmentSlot = Mob.getEquipmentSlotForItem(itemStack);

            int slotShift = baubles.getSlots();

            if (index == 0) {
                if (!this.moveItemStackTo(stackInSlot, 9 + slotShift, 45 + slotShift, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(stackInSlot, itemStack);
            } else if (index >= 1 && index < 5) {
                if (!this.moveItemStackTo(stackInSlot, 9 + slotShift, 45 + slotShift, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 5 && index < 9) {
                if (!this.moveItemStackTo(stackInSlot, 9 + slotShift, 45 + slotShift, false)) {
                    return ItemStack.EMPTY;
                }
            }

            // baubles -> inv
            else if (index >= 9 && index < 9 + slotShift) {
                if (!this.moveItemStackTo(stackInSlot, 9 + slotShift, 45 + slotShift, false)) {
                    return ItemStack.EMPTY;
                }
            }

            // inv -> armor
            else if (entityEquipmentSlot.getType() == EquipmentSlot.Type.ARMOR && !(this.slots.get(8 - entityEquipmentSlot.getIndex())).hasItem()) {
                int i = 8 - entityEquipmentSlot.getIndex();

                if (!this.moveItemStackTo(stackInSlot, i, i + 1, false)) {
                    return ItemStack.EMPTY;
                }
            }

            // inv -> offhand
            else if (entityEquipmentSlot == EquipmentSlot.OFFHAND && !(this.slots.get(45 + slotShift)).hasItem()) {
                if (!this.moveItemStackTo(stackInSlot, 45 + slotShift, 46 + slotShift, false)) {
                    return ItemStack.EMPTY;
                }
            }
            // inv -> bauble
            else if (itemStack.getCapability(CapabilityBaubles.ITEM_BAUBLE, null).isPresent()) {
                var bauble = itemStack.getCapability(CapabilityBaubles.ITEM_BAUBLE, null).orElseThrow(NullPointerException::new);
                for (int baubleSlot : bauble.getBaubleType(itemStack).getValidSlots()) {
                    if (bauble.canEquip(this.player) && !(this.slots.get(baubleSlot + 9)).hasItem() &&
                            !this.moveItemStackTo(stackInSlot, baubleSlot + 9, baubleSlot + 10, false)) {
                        return ItemStack.EMPTY;
                    }
                    if (stackInSlot.getCount() == 0) break;
                }
            } else if (index >= 9 + slotShift && index < 36 + slotShift) {
                if (!this.moveItemStackTo(stackInSlot, 36 + slotShift, 45 + slotShift, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 36 + slotShift && index < 45 + slotShift) {
                if (!this.moveItemStackTo(stackInSlot, 9 + slotShift, 36 + slotShift, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stackInSlot, 9 + slotShift, 45 + slotShift, false)) {
                return ItemStack.EMPTY;
            }

            if (stackInSlot.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stackInSlot.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            if (stackInSlot.isEmpty() && !baubles.isEventBlocked() && slot instanceof SlotBauble && itemStack.getCapability(CapabilityBaubles.ITEM_BAUBLE, null).isPresent()) {
                var finalItemStack = itemStack;
                itemStack.getCapability(CapabilityBaubles.ITEM_BAUBLE, null).ifPresent((iBauble -> iBauble.onEquipped(playerIn, finalItemStack)));
            }
        }

        return itemStack;
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return slot.container != this.craftResult && super.canTakeItemForPickAll(stack, slot);
    }

    private void addBaubleSlots() {
        // Canister slots
        int[][] canisterPositions = {{-14, 9}, {-14, 27}, {-14, 44}};
        for (int i = 0; i < canisterPositions.length; i++) {
            switch (i) {
                case 0:
                    this.addSlot(new SlotBauble(player, baubles, i, canisterPositions[i][0], canisterPositions[i][1]) {
                        @Override
                        public boolean mayPlace(ItemStack stack) {
                            return stack.getItem() instanceof ItemCanisterGreen;
                        }
                    });
                    break;

                case 1:
                    this.addSlot(new SlotBauble(player, baubles, i, canisterPositions[i][0], canisterPositions[i][1]) {
                        @Override
                        public boolean mayPlace(ItemStack stack) {
                            return stack.getItem() instanceof ItemCanisterYellow;
                        }
                    });

                case 2:
                    this.addSlot(new SlotBauble(player, baubles, i, canisterPositions[i][0], canisterPositions[i][1]) {
                        @Override
                        public boolean mayPlace(ItemStack stack) {
                            return stack.getItem() instanceof ItemCanisterRed;
                        }
                    });
            }
        }

        // Bauble slots
        int[][] baublePositions = {
                {76, 8}, {76, 26}, {76, 44}, {76, 62},
                {95, 8}, {95, 26}, {95, 44}
        };
        for (int i = 0; i < baublePositions.length; i++) {
            this.addSlot(new SlotBauble(player, baubles, i + 3, baublePositions[i][0], baublePositions[i][1]) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return stack.getItem() instanceof IBauble;
                }
            });
        }
    }
}