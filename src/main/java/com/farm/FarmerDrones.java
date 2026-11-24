package com.farm;

import com.farm.init.*;
import com.farm.networking.CloseLaptopDiskMenuServerBoundPayload;
import com.farm.networking.OpenLaptopDiskMenuServerBoundPayload;
import com.farm.init.ModItemTags;
import com.farm.networking.SetLaptopDiskMenuSourceServerBoundPayload;
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
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

		ServerPlayNetworking.registerGlobalReceiver(OpenLaptopDiskMenuServerBoundPayload.TYPE, ((payload, context) -> {
			ServerPlayer player = context.player();
			BlockPos blockPos = payload.blockPos();
			ServerLevel level = player.level();
			player.openMenu(new ExtendedMenuProvider<BlockPos>() {
				@Override
				public BlockPos getScreenOpeningData(ServerPlayer player) {
					return blockPos;
				}

				@Override
				public Component getDisplayName() {
					return Component.empty();
				}

				@Override
				public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
					return new LaptopDiskMenu(containerId, inventory, blockPos, ContainerLevelAccess.create(level, blockPos));
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
			ServerPlayer player = context.player();
			if (player.containerMenu instanceof LaptopDiskMenu menu) {
				menu.syncSource(payload.source());
			}
		}));
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
