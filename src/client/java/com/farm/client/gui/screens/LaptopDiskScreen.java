package com.farm.client.gui.screens;

import com.farm.block.entity.LaptopBlockEntity;
import com.farm.networking.CloseLaptopDiskMenuServerBoundPayload;
import com.farm.client.resources.FileManager;
import com.farm.client.util.MinecraftAccessor;
import com.farm.networking.SetLaptopDiskMenuSourceServerBoundPayload;
import com.farm.world.inventory.LaptopDiskMenu;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class LaptopDiskScreen extends AbstractContainerScreen<LaptopDiskMenu> implements ContainerListener {
    private static final Logger log = LoggerFactory.getLogger(LaptopDiskScreen.class);
    private LaptopDiskScreen.FileExplorerList explorer;
    private Button editTabButton;
    private StringWidget selectedPath;
    private final FileManager fileManager;

    public LaptopDiskScreen(LaptopDiskMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
        this.fileManager = ((MinecraftAccessor) this.minecraft).getFileManager();
    }

    protected void init() {
        super.init();
        drawBody();
        editTabButton = Button.builder(Component.literal("Editor Tab"), button -> this.openEditScreen())
                .bounds(10, 10, 100, 20).build();
        this.addRenderableWidget(editTabButton);
        menu.addSlotListener(this);
    }

    private void drawBody() {
        int explorerWidth = this.width / 4;
        int explorerHeight = this.height - 40;

        Path selected = fileManager.getSelected();
        explorer = new LaptopDiskScreen.FileExplorerList(this.minecraft, selected,
                (selected != null && Files.isDirectory(selected)) ? fileManager.extractFiles(selected) : fileManager.extractFiles(),
                explorerWidth,  explorerHeight, 0, 40, 12);

        if (selected != null && !Files.isDirectory(selected)) {
            selectedPath = new StringWidget(Component.literal(selected.toString()), this.font);
            selectedPath.setPosition(explorerWidth, 10);
            selectedPath.setMaxWidth(this.width - explorerWidth);
            this.addRenderableWidget(selectedPath);
        }

        this.addRenderableWidget(explorer);
    }

    private void openEditScreen() {
        ClientPlayNetworking.send(new CloseLaptopDiskMenuServerBoundPayload());
        this.minecraft.gui.setScreen(new LaptopEditScreen((LaptopBlockEntity) this.minecraft.player.level().getBlockEntity(menu.getBlockPos())));
    }

    @Override
    protected void slotClicked(final Slot slot, final int slotId, final int buttonNum, final ContainerInput containerInput) {
        super.slotClicked(slot, slotId, buttonNum, containerInput);
    }

    @Override
    public void extractSlot(final GuiGraphicsExtractor graphics, final Slot slot, final int mouseX, final int mouseY) {
        super.extractSlot(graphics, slot, mouseX, mouseY);
    }

    public void extractRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
    }

    public void extractBackground(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
    }

    @Override
    public void slotChanged(AbstractContainerMenu container, int slotIndex, ItemStack itemStack) {

    }

    @Override
    public void dataChanged(AbstractContainerMenu container, int id, int value) {

    }

    public class FileExplorerList extends ObjectSelectionList<LaptopDiskScreen.FileExplorerList.Entry> {
        private int width;
        private Path currentFilePath;

        public FileExplorerList(final Minecraft minecraft, Path currentFilePath, List<Path> files, int width, int height, int x, int y, int itemHeight) {
            super(minecraft, width, height, y, itemHeight);
            this.setX(x);
            this.width = width;
            this.currentFilePath = currentFilePath;

            if (currentFilePath != null && Files.isDirectory(currentFilePath) && !fileManager.isRootPath(currentFilePath)) {
                LaptopDiskScreen.FileExplorerList.Entry entry = new LaptopDiskScreen.FileExplorerList.Entry("./", currentFilePath.getParent());
                this.addEntry(entry);
            }

            if (files != null) {
                files.stream().filter(p -> Files.isDirectory(p)).forEach(resource -> {
                    LaptopDiskScreen.FileExplorerList.Entry entry = new LaptopDiskScreen.FileExplorerList.Entry(resource.getFileName().toString() + "/", resource);
                    this.addEntry(entry);
                    if (this.currentFilePath != null && this.currentFilePath.equals(resource)) {
                        this.setSelected(entry);
                    }
                });
                files.stream().filter(p -> !Files.isDirectory(p)).forEach(resource -> {
                    LaptopDiskScreen.FileExplorerList.Entry entry = new LaptopDiskScreen.FileExplorerList.Entry(resource.getFileName().toString(), resource);
                    this.addEntry(entry);
                    if (this.currentFilePath != null && this.currentFilePath.equals(resource)) {
                        this.setSelected(entry);
                    }
                });
            }

            if (this.getSelected() != null) {
                this.centerScrollOn(this.getSelected());
            }
        }

        public int getRowWidth() {
            return this.width;
        }

        class Entry extends ObjectSelectionList.Entry<LaptopDiskScreen.FileExplorerList.Entry> {
            private String filename;
            private Path path;

            public Entry(String filename, Path path) {
                super();
                this.filename = filename;
                this.path = path;
            }

            public void extractContent(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final boolean hovered, final float a) {
                Font font = LaptopDiskScreen.this.font;
                int y = this.getContentYMiddle();
                graphics.text(font, filename, this.getContentX(), y - 9 / 2, -1);
            }

            public boolean keyPressed(final KeyEvent event) {
                if (event.isSelection()) {
                    this.select();
                    return true;
                } else {
                    return super.keyPressed(event);
                }
            }

            public boolean mouseClicked(final MouseButtonEvent event, final boolean doubleClick) {
                if (doubleClick) {
                    this.select();
                }
                return super.mouseClicked(event, doubleClick);
            }

            private void select() {
                LaptopDiskScreen.FileExplorerList.this.setSelected(this);
                LaptopDiskScreen.this.fileManager.setSelected(path);

                if (!Files.isDirectory(path)) {
                    try {
                        ClientPlayNetworking.send(new SetLaptopDiskMenuSourceServerBoundPayload(Files.readString(path)));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                LaptopDiskScreen.this.rebuildWidgets();
            }

            @Override
            public Component getNarration() {
                return Component.literal("file");
            }
        }
    }
}
