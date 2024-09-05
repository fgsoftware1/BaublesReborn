package io.babywolf.forge.registry.items;

import io.babywolf.forge.api.BaublesAPI;
import io.babywolf.forge.api.bauble.BaubleType;
import io.babywolf.forge.api.bauble.IBauble;
import io.babywolf.forge.api.bauble.IBaublesItemHandler;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public class ItemCanisterRed extends Item implements IBauble {
    private static final UUID HEALTH_BOOST_ID = UUID.fromString("d5d0d878-b3c2-4194-a621-1a3d957c4150");
    private static final double HEALTH_BOOST_AMOUNT = 4.0; // 2 hearts

    public ItemCanisterRed(Properties properties) {
        super(properties);
    }

    @Override
    public BaubleType getBaubleType(ItemStack stack) {
        return BaubleType.CANISTER;
    }

    @Override
    public void onEquipped(LivingEntity player, ItemStack stack) {
        if (player instanceof Player) {
            addHealthBoost((Player) player);
        }
    }

    @Override
    public void onUnequipped(LivingEntity player, ItemStack stack) {
        if (player instanceof Player) {
            removeHealthBoost((Player) player);
        }
    }

    private void addHealthBoost(Player player) {
        Objects.requireNonNull(player.getAttribute(Attributes.MAX_HEALTH)).addTransientModifier(
                new AttributeModifier(HEALTH_BOOST_ID, "Red Canister Health Boost", HEALTH_BOOST_AMOUNT, AttributeModifier.Operation.ADDITION)
        );
    }

    private void removeHealthBoost(Player player) {
        Objects.requireNonNull(player.getAttribute(Attributes.MAX_HEALTH)).removeModifier(HEALTH_BOOST_ID);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level world, @NotNull Player player, @NotNull InteractionHand hand) {
        if (world.isClientSide) {
            IBaublesItemHandler baubles = (IBaublesItemHandler) BaublesAPI.getBaublesHandler(player);
            int targetSlot = 2;
            baubles.setStackInSlot(targetSlot, player.getItemInHand(hand).copy());
            if (!player.isCreative()) {
                player.getInventory().canPlaceItem(player.getInventory().selected, ItemStack.EMPTY);
            }
            onEquipped(player, player.getItemInHand(hand));
        }
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }
}
