package com.farm.block.entity;

import com.farm.init.ModBlockEntities;
import com.farm.world.inventory.ServerInventoryMenu;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ServerBlockEntity extends BaseContainerBlockEntity {
//    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private NonNullList<ItemStack> items;
    private List<UUID> farmerDrones;
    private List<String> commands;
    private String address;

    public ServerBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.SERVER_BLOCK_ENTITY, pos, state);
    }

    public ServerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        this.farmerDrones = new ArrayList<>();
        this.commands = new ArrayList<>();
        this.address = "default";
    }

    @Override
    @NullMarked
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, this.items);

        this.farmerDrones = new ArrayList<>();
        ValueInput.TypedInputList<String> farmerDronesList = input.listOrEmpty("FarmerDrones", Codec.STRING);
        farmerDronesList.stream().forEach(uuid -> this.farmerDrones.add(UUID.fromString(uuid)));

        this.commands = new ArrayList<>();
        ValueInput.TypedInputList<String> commandsList = input.listOrEmpty("Commands", Codec.STRING);
        commandsList.stream().forEach(this.commands::add);

        this.address = input.getStringOr("ServerAddress", "default");
    }

    @Override
    @NullMarked
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);

        ContainerHelper.saveAllItems(output, this.items);

        ValueOutput.TypedOutputList<String> farmerDronesList = output.list("FarmerDrones", Codec.STRING);
        this.farmerDrones.forEach(uuid -> farmerDronesList.add(uuid.toString()));

        ValueOutput.TypedOutputList<String> commandsList = output.list("Commands", Codec.STRING);
        this.commands.forEach(commandsList::add);

        output.putString("ServerAddress", this.getAddress());
    }

    @Override
    @NullMarked
    protected Component getDefaultName() {
        return Component.literal("Server");
    }

    @Override
    @NullMarked
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    @NullMarked
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    @NullMarked
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new ServerInventoryMenu(containerId, inventory, this);
    }

    @Override
    public int getContainerSize() {
        return 9;
    }

    @Override
    @NullMarked
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
        this.setChanged();
        if (this.level != null && !this.level.isClientSide()) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    public void addFarmerDrone(UUID uuid) {
        this.farmerDrones.add(uuid);
        this.setChanged();
    }

    public void removeFarmerDrone(UUID uuid) {
        this.farmerDrones.remove(uuid);
        this.setChanged();
    }

//    @Override
//    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
//
//    }
//
//    @Override
//    public AnimatableInstanceCache getAnimatableInstanceCache() {
//        return this.geoCache;
//    }
}
