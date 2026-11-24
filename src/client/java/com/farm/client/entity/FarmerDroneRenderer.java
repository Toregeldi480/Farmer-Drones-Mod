package com.farm.client.entity;

import com.farm.entity.FarmerDrone;
import com.geckolib.loading.math.MathParser;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class FarmerDroneRenderer<R extends EntityRenderState & GeoRenderState> extends GeoEntityRenderer<FarmerDrone, R> {
    public FarmerDroneRenderer(EntityRendererProvider.Context context, EntityType<FarmerDrone> entityType) {
        super(context, entityType);
    }
}
