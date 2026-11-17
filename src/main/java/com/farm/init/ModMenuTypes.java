package com.farm.init;

import com.farm.FarmerDrones;
import com.farm.world.inventory.LaptopDiskMenu;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public class ModMenuTypes {
    public static final MenuType<LaptopDiskMenu> LAPTOP_DISK = Registry.register(
            BuiltInRegistries.MENU,
            FarmerDrones.id("laptop_disk"),
            new ExtendedMenuType<>(LaptopDiskMenu::new, BlockPos.STREAM_CODEC)
    );

    public static void init() {}
}
