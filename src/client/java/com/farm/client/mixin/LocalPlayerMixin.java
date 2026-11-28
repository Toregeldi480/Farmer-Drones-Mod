package com.farm.client.mixin;

import com.farm.block.entity.LaptopBlockEntity;
import com.farm.block.entity.ServerBlockEntity;
import com.farm.client.gui.screens.LaptopEditScreen;
import com.farm.client.gui.screens.ServerConfigScreen;
import com.farm.util.PlayerAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
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

    @Override
    public void openServerControlScreen(ServerBlockEntity server) {
        this.minecraft.gui.setScreen(new ServerConfigScreen(server));
    }
}
