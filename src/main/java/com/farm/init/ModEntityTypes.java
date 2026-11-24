package com.farm.init;

import com.farm.FarmerDrones;
import com.farm.entity.FarmerDrone;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class ModEntityTypes {
    public static final EntityType<FarmerDrone> FARMER_DRONE = register(
            ResourceKey.create(Registries.ENTITY_TYPE, FarmerDrones.id("farmer_drone")),
            EntityType.Builder.of(FarmerDrone::new, MobCategory.MISC).sized(0.9f, 0.9f).spawnDimensionsScale(1.0f).eyeHeight(0.5f)
    );

    private static <T extends Entity> EntityType<T> register(ResourceKey<EntityType<?>> key, EntityType.Builder<T> builder) {
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, key, builder.build(key));
    }

    public static void registerAttributes() {
        FarmerDrones.LOGGER.info("Registering Entity Attributes for " + FarmerDrones.MOD_ID);
        FabricDefaultAttributeRegistry.register(FARMER_DRONE, FarmerDrone.createAttributes());
    }

    public static void init() {
        FarmerDrones.LOGGER.info("Registering Entities for " + FarmerDrones.MOD_ID);
        registerAttributes();
    }
}
