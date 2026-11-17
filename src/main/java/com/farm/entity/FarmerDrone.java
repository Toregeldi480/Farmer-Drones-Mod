package com.farm.entity;

import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.PlayState;
import com.geckolib.util.GeckoLibUtil;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class FarmerDrone extends LivingEntity implements GeoEntity {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation IDLE_AIR = RawAnimation.begin().thenPlay("movement.idle.air");
    private static final RawAnimation MOVE_AIR = RawAnimation.begin().thenPlay("movement.move.air");

    private static EntityDataAccessor<Boolean> IS_FLYING_ID = SynchedEntityData.defineId(FarmerDrone.class, EntityDataSerializers.BOOLEAN);
    private static EntityDataAccessor<String> SOURCE_CODE_ID = SynchedEntityData.defineId(FarmerDrone.class, EntityDataSerializers.STRING);
    private static EntityDataAccessor<Integer> LINE_ID = SynchedEntityData.defineId(FarmerDrone.class, EntityDataSerializers.INT);

    public FarmerDrone(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
    }

    private boolean isFlying = false;
    private String sourceCode = "";
    private int line = 0;

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("movement.air", test -> {
            if (this.getInBlockState().isAir()) {
                if (test.isMoving()) {
                    return test.setAndContinue(MOVE_AIR);
                }
                return test.setAndContinue(IDLE_AIR);
            }
            return PlayState.STOP;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    @Override
    public void tick() {
        super.tick();
        this.setNoGravity(this.isFlying());
    }

    @Override
    protected float getMaxHeadRotationRelativeToBody() {
        return 0;
    }

    @Override
    public boolean canBeCollidedWith(final @Nullable Entity other) {
        return true;
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public boolean isPushedByFluid() {
        return true;
    }

    @Override
    public HumanoidArm getMainArm() {
        return null;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder entityData) {
        super.defineSynchedData(entityData);
        entityData.define(IS_FLYING_ID, false);
        entityData.define(SOURCE_CODE_ID, "");
        entityData.define(LINE_ID, 0);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        isFlying = input.getBooleanOr("IsFlying", false);
        sourceCode = input.getStringOr("SourceCode", "");
        line = input.getIntOr("Line", 0);
        super.readAdditionalSaveData(input);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.putBoolean("IsFlying", this.isFlying());
        output.putString("SourceCode", sourceCode);
        output.putInt("Line", line);
        super.addAdditionalSaveData(output);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 5.0f)
                .add(Attributes.MOVEMENT_SPEED, 1.0f)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8);
    }

    public void move(float forward, float sideways) {
        double yawRad = Math.toRadians(this.getYRot());

        double forwardX = -Math.sin(yawRad);
        double forwardZ =  Math.cos(yawRad);
        double rightX   =  Math.cos(yawRad);
        double rightZ   =  Math.sin(yawRad);

        double dx = forward * forwardX + sideways * rightX;
        double dz = forward * forwardZ + sideways * rightZ;

        this.move(MoverType.SELF, new Vec3(dx, 0, dz));
    }

    public void rotate(float angle) {
        this.setYRot(this.getYRot() + angle);
    }

    public boolean isFlying() {
        return this.entityData.get(IS_FLYING_ID);
    }

    public void setFlying(boolean isFlying) {
        this.entityData.set(IS_FLYING_ID, isFlying);
    }

    public Vec3 getLocalVelocity() {
        double dx = this.getX() - this.xOld;
        double dz = this.getZ() - this.zOld;

        double yawRad = Math.toRadians(this.getYRot());

        double forwardX = -Math.sin(yawRad);
        double forwardZ =  Math.cos(yawRad);
        double rightX   =  Math.cos(yawRad);
        double rightZ   =  Math.sin(yawRad);

        double forward  = dx * forwardX + dz * forwardZ;
        double sideways = dx * rightX   + dz * rightZ;

        return new Vec3(forward, 0, sideways);
    }
}
