package com.farm.client;

import com.farm.client.gui.screens.LaptopDiskScreen;
import com.farm.init.ModEntityTypes;
import com.farm.init.ModMenuTypes;
import com.geckolib.renderer.GeoEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.EntityRenderers;

public class FarmerDronesClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		EntityRenderers.register(ModEntityTypes.FARMER_DRONE, context -> new GeoEntityRenderer<>(context, ModEntityTypes.FARMER_DRONE));
		MenuScreens.register(ModMenuTypes.LAPTOP_DISK, LaptopDiskScreen::new);
	}
}