package com.farm.util;

import com.farm.block.entity.LaptopBlockEntity;
import com.farm.block.entity.ServerBlockEntity;

public interface PlayerAccessor {
    void openLaptop(LaptopBlockEntity laptop);
    void openServerControlScreen(ServerBlockEntity server);
}
