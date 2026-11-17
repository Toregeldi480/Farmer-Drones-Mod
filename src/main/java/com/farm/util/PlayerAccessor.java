package com.farm.util;

import com.farm.block.entity.LaptopBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;

public interface PlayerAccessor {
    void openLaptop(LaptopBlockEntity laptop);
}
