package com.farm.mixin;

import com.farm.block.entity.LaptopBlockEntity;
import com.farm.block.entity.ServerBlockEntity;
import com.farm.util.PlayerAccessor;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin implements PlayerAccessor {
    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    private void tick(CallbackInfo ci) {
    }

    @Override
    public void openLaptop(LaptopBlockEntity laptop) {
    }

    @Override
    public void openServerControlScreen(ServerBlockEntity server) {
    }
}