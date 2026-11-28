package com.farm.world.inventory;

import com.farm.init.ModItemTags;
import com.farm.init.ModMenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NullMarked;

public class ServerInventoryMenu extends AbstractContainerMenu {
    private final Container container;

    public ServerInventoryMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new SimpleContainer(9));
    }

    public ServerInventoryMenu(int containerId, Inventory inventory, Container container) {
        super(ModMenuTypes.SERVER_INVENTORY, containerId);
        this.container = container;
        for(int x = 0; x < 9; ++x) {
            this.addSlot(new DiskSlot(this.container, x, 8 + x * 18, 18));
        }
        this.addStandardInventorySlots(inventory, 8, 84);
    }

    @Override
    @NullMarked
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        return null;
    }

    @Override
    @NullMarked
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    @NullMarked
    public void removed(Player player) {
        super.removed(player);
        this.container.stopOpen(player);
    }

    private static class DiskSlot extends Slot {
        public DiskSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        public boolean mayPlace(final ItemStack itemStack) {
            return itemStack.is(ModItemTags.DISK);
        }

        public int getMaxStackSize() {
            return 1;
        }
    }
}
