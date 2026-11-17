package com.farm.init;

import com.farm.FarmerDrones;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public class ModItemIds {
    public static final ResourceKey<Item> FLOPPY_DISK_BLUE = create("floppy_disk_blue");
    public static final ResourceKey<Item> FLOPPY_DISK_GRAY = create("floppy_disk_gray");
    public static final ResourceKey<Item> FLOPPY_DISK_PINK = create("floppy_disk_pink");

    public static ResourceKey<Item> create(String name) {
        return ResourceKey.create(Registries.ITEM, FarmerDrones.id(name));
    }
}
