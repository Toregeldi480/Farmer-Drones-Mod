package com.farm.init;

import com.farm.FarmerDrones;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.Identifier;

public class ModBlockItemIds {
    public static final BlockItemId LAPTOP = create("laptop");

    private static BlockItemId create(String name) {
        Identifier id = FarmerDrones.id(name);
        return BlockItemId.create(id, id);
    }
}
