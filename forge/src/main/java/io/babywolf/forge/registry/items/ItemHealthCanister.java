package io.babywolf.forge.registry.items;

import io.babywolf.forge.api.BaublesAPI;
import io.babywolf.forge.api.bauble.BaubleType;
import io.babywolf.forge.api.bauble.IBauble;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Base for the coloured canisters: each one worn in a canister slot adds {@link #HEALTH_PER_CANISTER} max health.
 */
public abstract class ItemHealthCanister extends Item implements IBauble {
    public static final double HEALTH_PER_CANISTER = 10.0; // 5 hearts

    private static final UUID HEALTH_BOOST_ID = UUID.fromString("d5d0d878-b3c2-4194-a621-1a3d957c4150");

    protected ItemHealthCanister(Properties properties) {
        super(properties);
    }

    /**
     * Makes the player's max-health bonus match the number of canisters they are wearing.
     * Called every server tick rather than from onEquipped/onUnequipped, so the bonus is right no matter how the
     * slots changed (GUI, right-click, death drops) and is restored after a relog or respawn, where the transient
     * modifier is lost. One combined modifier is used because the same modifier id cannot be applied twice.
     */
    public static void updateHealthBoost(Player player) {
        AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth == null) return;

        int worn = BaublesAPI.getBaublesHandler(player).map(baubles -> {
            int count = 0;
            for (int slot = 0; slot < baubles.getSlots(); slot++) {
                if (baubles.getStackInSlot(slot).getItem() instanceof ItemHealthCanister) count++;
            }
            return count;
        }).orElse(0);

        double target = worn * HEALTH_PER_CANISTER;
        AttributeModifier existing = maxHealth.getModifier(HEALTH_BOOST_ID);
        if ((existing == null ? 0.0 : existing.getAmount()) == target) return;

        if (existing != null) maxHealth.removeModifier(HEALTH_BOOST_ID);
        if (target > 0) {
            maxHealth.addTransientModifier(new AttributeModifier(HEALTH_BOOST_ID, "Canister health boost", target, AttributeModifier.Operation.ADDITION));
        }
        if (player.getHealth() > player.getMaxHealth()) player.setHealth(player.getMaxHealth());
    }

    @Override
    public BaubleType getBaubleType(ItemStack stack) {
        return BaubleType.CANISTER;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level world, @NotNull Player player, @NotNull InteractionHand hand) {
        return BaublesAPI.equipFromHand(player, hand);
    }
}
