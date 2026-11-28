package com.farm;

import com.farm.block.entity.ServerBlockEntity;
import com.farm.init.*;
import com.farm.networking.*;
import com.farm.init.ModItemTags;
import com.farm.world.inventory.LaptopDiskMenu;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class FarmerDrones implements ModInitializer {
	public static final String MOD_ID = "farmer-drones";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModBlockEntities.init();
		ModEntityTypes.init();
		ModComponents.init();
		ModItems.init();
		ModBlocks.init();
		ModItemGroups.init();
		ModItemTags.init();
		ModMenuTypes.init();

		PayloadTypeRegistry.serverboundPlay().register(OpenLaptopDiskMenuServerBoundPayload.TYPE, OpenLaptopDiskMenuServerBoundPayload.CODEC);
		PayloadTypeRegistry.serverboundPlay().register(CloseLaptopDiskMenuServerBoundPayload.TYPE, CloseLaptopDiskMenuServerBoundPayload.CODEC);
		PayloadTypeRegistry.serverboundPlay().register(SetLaptopDiskMenuSourceServerBoundPayload.TYPE, SetLaptopDiskMenuSourceServerBoundPayload.CODEC);
		PayloadTypeRegistry.serverboundPlay().register(ConnectToServerFarmerDroneServerBoundPayload.TYPE, ConnectToServerFarmerDroneServerBoundPayload.CODEC);
		PayloadTypeRegistry.serverboundPlay().register(SetServerAddressServerBoundPayload.TYPE, SetServerAddressServerBoundPayload.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(OpenLaptopDiskMenuServerBoundPayload.TYPE, ((payload, context) -> {
			BlockPos blockPos = payload.blockPos();
			context.player().openMenu(new ExtendedMenuProvider<BlockPos>() {
				@Override
				@NullMarked
				public BlockPos getScreenOpeningData(ServerPlayer player) {
					return blockPos;
				}

				@Override
				@NullMarked
				public Component getDisplayName() {
					return Component.empty();
				}

				@Override
				@NullMarked
				public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
					return new LaptopDiskMenu(containerId, inventory, blockPos, ContainerLevelAccess.create(player.level(), blockPos));
				}
			});
		}));

		ServerPlayNetworking.registerGlobalReceiver(CloseLaptopDiskMenuServerBoundPayload.TYPE, ((payload, context) -> {
			ServerPlayer player = context.player();
			if (player.containerMenu instanceof LaptopDiskMenu menu) {
				menu.removed(player);
			}
		}));

		ServerPlayNetworking.registerGlobalReceiver(SetLaptopDiskMenuSourceServerBoundPayload.TYPE, ((payload, context) -> {
			if (context.player().containerMenu instanceof LaptopDiskMenu menu) {
				menu.syncSource(payload.source());
			}
		}));

		ServerPlayNetworking.registerGlobalReceiver(ConnectToServerFarmerDroneServerBoundPayload.TYPE, ((payload, context) -> {
			ServerPlayer player = context.player();
			ServerLevel level = player.level();
			level.findBlocksIn(AABB.ofSize(player.position(), 4, 4, 4))
					.filterState(state -> state.is(ModBlocks.SERVER))
					.forEach(((blockPos, blockState) -> {
						if (level.getBlockEntity(blockPos) instanceof ServerBlockEntity server) {
							if (server.getAddress().equals(payload.serverAddress())) {
								server.addFarmerDrone(UUID.fromString(payload.farmerDroneUUID()));
							}
						}
					}));
		}));

		ServerPlayNetworking.registerGlobalReceiver(SetServerAddressServerBoundPayload.TYPE, ((payload, context) -> {
			ServerPlayer player = context.player();
			if (player.level().getBlockEntity(payload.blockPos()) instanceof ServerBlockEntity server) {
				server.setAddress(payload.serverAddress());
			}
		}));
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
