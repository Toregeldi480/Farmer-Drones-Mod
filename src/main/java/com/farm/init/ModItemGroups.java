package com.farm.init;

import com.farm.FarmerDrones;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModItemGroups {
    public static final ResourceKey<CreativeModeTab> MISC_TAB_KEY = ResourceKey.create(
            BuiltInRegistries.CREATIVE_MODE_TAB.key(), FarmerDrones.id("misc_tab")
    );
    public static final CreativeModeTab MISC_TAB = FabricCreativeModeTab.builder()
            .icon(() -> new ItemStack(ModItems.FLOPPY_DISK_BLUE.asItem()))
            .title(Component.translatable("creative-tab.farmer-drones.misc_tab"))
            .displayItems((params, output) -> {
                output.accept(ModBlocks.LAPTOP.asItem());
                output.accept(ModBlocks.SERVER.asItem());
                output.accept(ModItems.FLOPPY_DISK_BLUE);
                output.accept(ModItems.FLOPPY_DISK_GRAY);
                output.accept(ModItems.FLOPPY_DISK_PINK);
            })
            .build();

    public static void init() {
        FarmerDrones.LOGGER.info("Registering BlockEntities for " + FarmerDrones.MOD_ID);
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, MISC_TAB_KEY, MISC_TAB);
    }
}
