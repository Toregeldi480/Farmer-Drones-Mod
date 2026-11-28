package com.farm.entity;

import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.PlayState;
import com.geckolib.util.GeckoLibUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

public class FarmerDrone extends LivingEntity implements GeoEntity, InventoryCarrier {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation FLOAT = RawAnimation.begin().thenPlay("movement.float");
    private static final RawAnimation UP = RawAnimation.begin().thenPlay("movement.up");
    private static final RawAnimation DOWN = RawAnimation.begin().thenPlay("movement.down");
    private static final RawAnimation FORWARD = RawAnimation.begin().thenPlay("movement.forward");
    private static final RawAnimation BACKWARD = RawAnimation.begin().thenPlay("movement.backward");
    private static final RawAnimation RIGHT = RawAnimation.begin().thenPlay("movement.right");
    private static final RawAnimation LEFT = RawAnimation.begin().thenPlay("movement.left");
    private static final RawAnimation IDLE = RawAnimation.begin().thenPlay("movement.idle");

    private int delayTicks = 0;
    private int actionTicks = 0;
    private int rotationInput = 0;
    private Vec3 movementInput = Vec3.ZERO;
    private Action action = Action.IDLE;
    private Movement movement = Movement.NONE;
    private boolean isActive = false;

    // TODO: custom inventory
    private final SimpleContainer inventory = new SimpleContainer(1);

    public FarmerDrone(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
        this.setPos(new Vec3(this.blockPosition()));
    }

    @Override
    @NullMarked
    public InteractionResult interact(Player player, InteractionHand hand, Vec3 location) {
        ItemStack heldItem = player.getItemInHand(hand);
        if (heldItem.is(Items.LAPIS_LAZULI)) {
            Vec3 pos = this.position();
            this.setPos(Math.floor(pos.x) + 0.5f, Math.floor(pos.y), Math.floor(pos.z) + 0.5f);
        }
        if (heldItem.is(Items.DIAMOND)) {
            this.setActive(!this.isActive());
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("movement", test -> {
            if (this.action == Action.MOVE) {
                return switch (this.movement) {
                    case Movement.UP -> test.setAndContinue(UP);
                    case Movement.DOWN -> test.setAndContinue(DOWN);
                    case Movement.FORWARD -> test.setAndContinue(FORWARD);
                    case Movement.BACKWARD -> test.setAndContinue(BACKWARD);
                    case Movement.RIGHT -> test.setAndContinue(RIGHT);
                    case Movement.LEFT -> test.setAndContinue(LEFT);
                    default -> PlayState.STOP;
                };
            } else if (this.action == Action.IDLE) {
                return test.setAndContinue(IDLE);
            } else if (this.action == Action.FLOAT) {
                return test.setAndContinue(FLOAT);
            } else {
                return PlayState.STOP;
            }
        }));
    }

    @Override
    @NullMarked
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    // FIXME: no gravity logic needs fix
    @Override
    public void tick() {
        super.tick();
        if (this.isActive()) {
            this.setNoGravity(true);
            this.movementTick();
            this.rotationTick();
        } else {
            this.setNoGravity(false);
        }
    }

    private void movementTick() {
        if (this.action != Action.MOVE) return;
        if (this.actionTicks > 0) {
            Vec3 movement = getVectorRelativeToRot(movementInput);
            if (this.actionTicks % 4 == 0) {
                Vec3 pos = this.position().add(movement);
                BlockPos blockPos = new BlockPos((int) Math.floor(pos.x), (int) Math.floor(pos.y), (int)  Math.floor(pos.z));
                if (!this.level().getBlockState(blockPos).is(BlockTags.AIR)) {
                    this.stopMovement();
                    movement = Vec3.ZERO;
                }
            }
            this.setDeltaMovement(movement.scale(0.125f));
            this.move(MoverType.SELF, this.getDeltaMovement());
            --this.actionTicks;
        } else {
            this.stopMovement();
        }
    }

    private void rotationTick() {
        if (this.action != Action.ROTATE) return;
        if (this.actionTicks > 0) {
            this.setYRot(this.yRotO + 11.25f);
            this.setYBodyRot(this.yBodyRot + 11.25f);
            this.setYHeadRot(this.yHeadRot + 11.25f);
            --this.actionTicks;
        } else {
            this.stopAction();
        }
    }

    private void stopMovement() {
        this.stopAction();
        this.setDeltaMovement(Vec3.ZERO);
        this.movement = Movement.NONE;
    }

    private void stopAction() {
        this.action = (this.level().getBlockState(this.getOnPos().below()).is(Blocks.AIR)) ? Action.FLOAT : Action.IDLE;
        this.actionTicks = 0;
        this.delayTicks = 10;
    }

    private Vec3 getVectorRelativeToRot(Vec3 input) {
        float sin = Mth.sin(this.getYRot() * ((float)Math.PI / 180.0f));
        float cos = Mth.cos(this.getYRot() * ((float)Math.PI / 180.0f));
        return new Vec3(input.x * (double)cos - input.z * (double)sin, input.y, input.z * (double)cos + input.x * (double)sin);
    }

    @Override
    public boolean isFallFlying() {
        return false;
    }

    @Override
    protected float getMaxHeadRotationRelativeToBody() {
        return 0;
    }

    @Override
    public boolean canBeCollidedWith(final @Nullable Entity other) {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public HumanoidArm getMainArm() {
        return null;
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        this.delayTicks = input.getIntOr("DelayTicks", 0);
        this.actionTicks = input.getIntOr("ActionTicks", 0);
        this.rotationInput = input.getIntOr("RotationInput", 0);
        this.movementInput = new Vec3(input.getDoubleOr("MovementInputX", 0), input.getDoubleOr("MovementInputY", 0), input.getDoubleOr("MovementInputZ", 0));
        this.action = Action.valueOf(input.getStringOr("Action", "IDLE"));
        this.movement = Movement.valueOf(input.getStringOr("Movement", "NONE"));
        super.readAdditionalSaveData(input);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.putInt("DelayTicks", this.delayTicks);
        output.putInt("ActionTicks", this.actionTicks);
        output.putInt("RotationInput", this.rotationInput);
        output.putDouble("MovementInputX", this.movementInput.x);
        output.putDouble("MovementInputY", this.movementInput.y);
        output.putDouble("MovementInputZ", this.movementInput.z);
        output.putString("Action", this.action.name());
        output.putString("Movement", this.movement.name());
        super.addAdditionalSaveData(output);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 5.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.5f)
                .add(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE, 1.0f)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0f);
    }

    public boolean isActive() {
        return this.isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public void move(String direction, int distance) {
        switch (direction) {
            case "LEFT" -> this.move(1.0f, 0, 0, distance);
            case "RIGHT" -> this.move(-1.0f, 0, 0, distance);
            case "UP" -> this.move(0, 1.0f, 0, distance);
            case "DOWN" -> this.move(0, -1.0f, 0, distance);
            case "FORWARD" -> this.move(0, 0, 1.0f, distance);
            case "BACKWARD" -> this.move(0, 0, -1.0f, distance);
        }
    }

    public void move(float sideway, float vertical , float forward, int distance) {
        this.movementInput = new Vec3(sideway, vertical, forward);
        this.actionTicks = distance * 4;
        this.action = Action.MOVE;
        this.movement = (sideway != 0) ? (sideway > 0 ? Movement.LEFT : Movement.RIGHT) :
                (vertical != 0) ? (vertical > 0 ? Movement.UP : Movement.DOWN) :
                (forward != 0) ? (forward > 0 ? Movement.FORWARD : Movement.BACKWARD) : Movement.NONE;
    }

    public void rotate(int angle) {
        this.actionTicks = (int) (angle / 11.25f);
        this.action = Action.ROTATE;
    }

    @Override
    @NullMarked
    public SimpleContainer getInventory() {
        return this.inventory;
    }

    enum Action {
        FLOAT, IDLE, MOVE, ROTATE, COLLECT, WATER
    }

    enum Movement {
        NONE, UP, DOWN, FORWARD, BACKWARD, RIGHT, LEFT
    }
}
