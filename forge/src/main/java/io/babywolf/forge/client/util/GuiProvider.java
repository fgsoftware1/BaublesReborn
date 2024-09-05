package io.babywolf.forge.client.util;

import io.babywolf.forge.container.PlayerExpandedContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GuiProvider implements MenuProvider {

    @Override
    @Nonnull
    public Component getDisplayName() {
        return new TextComponent("PlayerBaublesInv");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @Nonnull Inventory playerInventory, Player playerEntity) {
        return new PlayerExpandedContainer(id, playerInventory, !playerEntity.level.isClientSide);
    }
}
