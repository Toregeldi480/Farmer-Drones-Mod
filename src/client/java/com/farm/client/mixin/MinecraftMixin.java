package com.farm.client.mixin;

import com.farm.client.resources.FileManager;
import com.farm.client.util.MinecraftAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin implements MinecraftAccessor {
    @Unique
    private FileManager fileManager;

    @Unique
    private Minecraft minecraft;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(GameConfig gameConfig, CallbackInfo ci) {
        this.fileManager = new FileManager((Minecraft) (Object) this);
    }

    public FileManager getFileManager() {
        return this.fileManager;
    }
}
