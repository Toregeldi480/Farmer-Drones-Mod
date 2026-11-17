package com.farm.block.entity;

import com.farm.init.ModBlockEntities;
import com.farm.world.inventory.LaptopDiskMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class LaptopBlockEntity extends BlockEntity implements MenuProvider {
    private Path editorPath;
    private String editorText = "";

    public LaptopBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.LAPTOP_BLOCK_ENTITY, pos, state);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new LaptopDiskMenu(containerId, inventory, getBlockPos(),ContainerLevelAccess.create(level, getBlockPos()));
    }

    public Path getEditorPath() {
        return editorPath;
    }

    public void setEditorPath(Path editorPath) {
        this.editorPath = editorPath;
    }

    public void openEditorFile() {
        try {
            editorText = Files.readString(editorPath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void saveEditorFile() {
        try {
            if (editorPath == null || Files.isDirectory(editorPath)) return;
            Files.deleteIfExists(editorPath);
            Files.writeString(editorPath, editorText, StandardOpenOption.CREATE_NEW);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String readEditorText() {
        if (editorPath == null || Files.isDirectory(editorPath)) return "";
        return editorText;
    }

    public void writeEditorText(String editorText) {
        if (editorPath == null || Files.isDirectory(editorPath)) return;
        this.editorText = editorText;
    }

    @Override
    public Component getDisplayName() {
        return Component.empty();
    }
}
