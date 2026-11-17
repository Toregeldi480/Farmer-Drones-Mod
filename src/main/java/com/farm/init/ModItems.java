package com.farm.init;

import com.farm.FarmerDrones;
import com.farm.item.FloppyDisk;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public class ModItems {
//    public static final Item FARMER_DRONE = register(ModItemIds.FARMER_DRONE, Item::new, new Item(new Item.Properties()));
//    public static final Item REMOTE = register(ModItemIds.REMOTE, Item::new, new Remote(new Item.Properties()));

    public static final Item FLOPPY_DISK_BLUE = register(ModItemIds.FLOPPY_DISK_BLUE, FloppyDisk::new, new Item.Properties().stacksTo(1).durability(1024));
    public static final Item FLOPPY_DISK_GRAY = register(ModItemIds.FLOPPY_DISK_GRAY, FloppyDisk::new, new Item.Properties().stacksTo(1).durability(2048));
    public static final Item FLOPPY_DISK_PINK = register(ModItemIds.FLOPPY_DISK_PINK, FloppyDisk::new, new Item.Properties().stacksTo(1).durability(5096));

    public static Item register(ResourceKey<Item> itemKey, Function<Item.Properties, Item> itemFactory, Item.Properties settings) {
        Item item = itemFactory.apply(settings.setId(itemKey));
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);
        return item;
    }

    public static void init() {
        FarmerDrones.LOGGER.info("Registering EntityTypes for " + FarmerDrones.MOD_ID);
    }
}
