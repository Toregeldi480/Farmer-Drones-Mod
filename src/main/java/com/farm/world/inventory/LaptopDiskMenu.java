package com.farm.world.inventory;

import com.farm.init.ModBlocks;
import com.farm.init.ModComponents;
import com.farm.init.ModItemTags;
import com.farm.init.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NullMarked;

public class LaptopDiskMenu extends ItemCombinerMenu {
    private BlockPos blockPos;
    private String source = "";

    public LaptopDiskMenu(int containerId, Inventory inventory, BlockPos blockPos) {
        this(containerId, inventory, blockPos, ContainerLevelAccess.NULL);
        this.blockPos = blockPos;
    }

    public LaptopDiskMenu(int containerId, Inventory inventory, BlockPos blockPos, ContainerLevelAccess access) {
        super(ModMenuTypes.LAPTOP_DISK, containerId, inventory, access, createInputSlotDefinitions());
        this.blockPos = blockPos;
    }

    private static ItemCombinerMenuSlotDefinition createInputSlotDefinitions() {
        return ItemCombinerMenuSlotDefinition.create().withSlot(0, 79, 12, (itemStack) -> itemStack.is(ModItemTags.DISK)).withResultSlot(1, 79, 36).build();
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public void syncSource(String source) {
        this.source = source;
        createResult();
    }

    @Override
    @NullMarked
    public void slotsChanged(Container container) {
        super.slotsChanged(container);
        createResult();
    }

    @Override
    @NullMarked
    protected void onTake(Player player, ItemStack carried) {
        this.inputSlots.setItem(0, ItemStack.EMPTY);
    }

    @Override
    @NullMarked
    protected boolean isValidBlock(BlockState state) {
        return state.is(ModBlocks.LAPTOP);
    }

    @Override
    public void createResult() {
        ItemStack input = this.inputSlots.getItem(0);

        if (!input.isEmpty()) {
            ItemStack result = input.copy();
            result.set(ModComponents.DISK_SOURCE_CODE, source);
            result.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
            this.resultSlots.setItem(1, result);
            this.broadcastChanges();
        } else {
            this.resultSlots.setItem(1, ItemStack.EMPTY);
        }
    }
}