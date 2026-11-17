package com.farm.client.entity;

import com.farm.FarmerDrones;
import com.farm.entity.FarmerDrone;
import com.geckolib.loading.math.MathParser;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.AnimationState;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

public class FarmerDroneModel extends GeoModel<FarmerDrone> {
    private final Identifier modelPath = Identifier.fromNamespaceAndPath(FarmerDrones.MOD_ID, "farmer_drone");
    private final Identifier animationsPath = Identifier.fromNamespaceAndPath(FarmerDrones.MOD_ID, "farmer_drone");
    private final Identifier texturePath = Identifier.fromNamespaceAndPath(FarmerDrones.MOD_ID, "textures/farmer_drone.png");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return this.modelPath;
    }

    @Override
    public Identifier getAnimationResource(FarmerDrone animatable) {
        return this.animationsPath;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return this.texturePath;
    }
}