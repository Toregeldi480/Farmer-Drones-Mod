package com.farm.client.gui.screens;

import com.farm.world.inventory.ServerInventoryMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ServerInventoryScreen extends AbstractContainerScreen<ServerInventoryMenu> {
    public ServerInventoryScreen(ServerInventoryMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }
}
