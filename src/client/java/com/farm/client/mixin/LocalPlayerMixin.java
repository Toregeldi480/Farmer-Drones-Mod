package com.farm.client.mixin;

import com.farm.block.LaptopBlock;
import com.farm.block.entity.LaptopBlockEntity;
import com.farm.client.gui.screens.LaptopEditScreen;
import com.farm.client.resources.FileManager;
import com.farm.util.PlayerAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin implements PlayerAccessor {
    @Final
    @Shadow
    protected Minecraft minecraft;

    @Override
    public void openLaptop(LaptopBlockEntity laptop) {
        this.minecraft.gui.setScreen(new LaptopEditScreen(laptop));
    }
}
