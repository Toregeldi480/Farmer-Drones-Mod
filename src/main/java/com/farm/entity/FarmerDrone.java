package com.farm.entity;

import com.farm.init.ModComponents;
import com.farm.item.FloppyDisk;
import com.farm.programming.Runner;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.PlayState;
import com.geckolib.util.GeckoLibUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
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

    Runner runner = new Runner(this, "");
    private String sourceCode = "";
    private int line = 0;
    private int delayTicks = 0;
    private int actionTicks = 0;
    private int rotationInput = 0;
    private Vec3 movementInput = Vec3.ZERO;
    private Action action = Action.IDLE;
    private Movement movement = Movement.NONE;

    // TODO: custom inventory
    private SimpleContainer inventory = new SimpleContainer(1);

    public FarmerDrone(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
        this.setPos(new Vec3(this.blockPosition()));
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand, Vec3 location) {
        ItemStack heldItem = player.getItemInHand(hand);

        if (heldItem.getItem() instanceof FloppyDisk) {
            String source = heldItem.getOrDefault(ModComponents.DISK_SOURCE_CODE, "");
            this.setSource(source);
            this.line = 0;
        }

        // TODO: temporary triggers
        if (heldItem.is(Items.GOLD_INGOT)) {
            this.runner.setLoop(!this.runner.isLoop());
        }

        if (heldItem.is(Items.LAPIS_LAZULI)) {
            Vec3 pos = this.position();
            this.setPos(Math.floor(pos.x) + 0.5f, Math.floor(pos.y), Math.floor(pos.z) + 0.5f);
        }

        if (heldItem.is(Items.DIAMOND)) {
            boolean isRunning = this.runner.isRunning();
            this.runner.setRunning(!isRunning);
            this.delayTicks = 0;
            if (!this.runner.isRunning()) this.stopAction();
            player.sendOverlayMessage(Component.literal(isRunning ? "Stopping Code Execution" :
                    (this.line == 0 ? "Starting Code Execution" : "Continue Code Execution From Line: " + this.line)));
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("movement", test -> {
            if (this.action == Action.MOVE) {
                switch (this.movement) {
                    case Movement.UP: return test.setAndContinue(UP);
                    case Movement.DOWN: return test.setAndContinue(DOWN);
                    case Movement.FORWARD: return test.setAndContinue(FORWARD);
                    case Movement.BACKWARD: return test.setAndContinue(BACKWARD);
                    case Movement.RIGHT: return test.setAndContinue(RIGHT);
                    case Movement.LEFT: return test.setAndContinue(LEFT);
                    default: return PlayState.STOP;
                }
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
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.runner.isRunning()) {
            this.setNoGravity(true);
            this.runnerTick();
            this.movementTick();
            this.rotationTick();
        } else {
            this.setNoGravity(false);
        }
    }

    private void runnerTick() {
        if (this.actionTicks <= 0 && this.action == Action.IDLE && this.line < this.runner.getLineCount()) {
            if (this.delayTicks-- <= 0) this.runner.execLine(this.line++);
        }
        if (this.actionTicks <= 0) {
            if (this.line == this.runner.getLineCount()) {
                this.runner.setRunning(this.runner.isLoop());
                this.line = 0;
            }
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
        float sin = Mth.sin((double)(this.getYRot() * ((float)Math.PI / 180F)));
        float cos = Mth.cos((double)(this.getYRot() * ((float)Math.PI / 180F)));
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
        this.sourceCode = input.getStringOr("SourceCode", "");
        this.line = input.getIntOr("Line", 0);
        this.delayTicks = input.getIntOr("DelayTicks", 0);
        this.actionTicks = input.getIntOr("ActionTicks", 0);
        this.rotationInput = input.getIntOr("RotationInput", 0);
        this.movementInput = new Vec3(input.getDoubleOr("MovementInputX", 0), input.getDoubleOr("MovementInputY", 0), input.getDoubleOr("MovementInputZ", 0));
        this.runner = new Runner(this, sourceCode);
        this.runner.setRunning(input.getBooleanOr("IsRunning", false));
        this.action = Action.valueOf(input.getStringOr("Action", "IDLE"));
        this.movement = Movement.valueOf(input.getStringOr("Movement", "NONE"));
        super.readAdditionalSaveData(input);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.putBoolean("IsRunning", this.runner.isRunning());
        output.putString("SourceCode", this.sourceCode);
        output.putInt("Line", this.line);
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

    public void setSource(String source) {
        this.sourceCode = source;
        this.runner.setSource(source);
    }

    public void move(float sideway, float vertical , float forward, int distance) {
        sideway *= -1;
        this.movementInput = new Vec3(sideway, vertical, forward).normalize();
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
    public SimpleContainer getInventory() {
        return this.inventory;
    }

    enum Action {
        FLOAT, IDLE, MOVE, ROTATE, COLLECT, WATER
    }

    enum Movement {
        NONE, UP, DOWN, FORWARD, BACKWARD, RIGHT, LEFT;
    }
}
