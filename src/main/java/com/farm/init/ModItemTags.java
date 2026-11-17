package com.farm.init;

import com.farm.FarmerDrones;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModItemTags {
    public static final TagKey<Item> DISK = TagKey.create(Registries.ITEM, FarmerDrones.id("disk"));

    public static void init() {}
}
