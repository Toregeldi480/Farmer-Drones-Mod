package com.farm.init;

import com.farm.FarmerDrones;
import com.farm.block.entity.LaptopBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntities {
    public static final BlockEntityType<LaptopBlockEntity> LAPTOP_BLOCK_ENTITY = register(
            "laptop",
            LaptopBlockEntity::new,
            ModBlocks.LAPTOP
    );

    private static <T extends BlockEntity> BlockEntityType<T> register(String name, FabricBlockEntityTypeBuilder.Factory<? extends T> factory, Block... blocks) {
        Identifier id = FarmerDrones.id(name);
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, FabricBlockEntityTypeBuilder.<T>create(factory, blocks).build());
    }

    public static void init() {
        FarmerDrones.LOGGER.info("Registering BlockEntities for " + FarmerDrones.MOD_ID);
    }
}
