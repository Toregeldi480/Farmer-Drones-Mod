package com.farm.client.gui.screens;

import com.farm.block.entity.LaptopBlockEntity;
import com.farm.client.resources.FileManager;
import com.farm.client.util.MinecraftAccessor;
import com.farm.networking.OpenLaptopDiskMenuServerBoundPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class LaptopEditScreen extends Screen {
    private static final Logger log = LoggerFactory.getLogger(LaptopEditScreen.class);
    private Component EDITOR_FILENAME = Component.empty();
    private Layout layout = Layout.NORMAL;
    private MultiLineEditBox editor;
    private FileExplorerList explorer;
    private Button diskTabButton;
    private Button cycleLayoutButton;
    private Button saveButton;
    private Button closeButton;
    private final LaptopBlockEntity laptop;
    private final BlockPos blockPos;
    private final FileManager fileManager;
    int explorerWidth, editorX, editorWidth;

    public LaptopEditScreen(LaptopBlockEntity laptop) {
        super(Component.literal("Laptop Screen"));
        this.laptop = laptop;
        this.blockPos = laptop.getBlockPos();
        this.fileManager = ((MinecraftAccessor) this.minecraft).getFileManager();
    }

    @Override
    protected void init() {
        drawTopbar();
        drawBody();
    }

    public void extractRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        if (editorWidth > 0) {
            graphics.text(this.font, EDITOR_FILENAME, editorX, 10, -1);
        }
    }

    private void drawTopbar() {
        int top = 8;
        int left = 10;
        int right = 10;

        diskTabButton = Button.builder(Component.literal("Disks Tab"), button -> this.openDiskScreen())
                .bounds(left, top, 100, 20).build();

        closeButton = Button.builder(Component.literal("X"), button -> this.onClose())
                .bounds(this.width - right - 20, top, 20, 20).build();

        this.addRenderableWidget(diskTabButton);
        this.addRenderableWidget(closeButton);
    }

    private void drawBody() {
        int top = 30;
        int bottom = 10;
        int left = 10;
        int right = 10;

        switch (layout) {
            case MAX_EXPLORER -> {
                explorerWidth = this.width - left - right;
                editorX = this.width;
                editorWidth = 0;
            }
            case MAX_EDITOR -> {
                explorerWidth = 0;
                editorX = left;
                editorWidth = this.width - left - right;
            }
            default -> {
                explorerWidth = this.width / 3;
                editorX = explorerWidth + left;
                editorWidth = this.width - explorerWidth - left - right;
            }
        }

        if (explorerWidth > 0) {
            Path selected = fileManager.getSelected();
            explorer = new FileExplorerList(this.minecraft, selected,
                    (selected != null && Files.isDirectory(selected)) ? fileManager.extractFiles(selected) : fileManager.extractFiles(),
                    explorerWidth,  this.height - top - bottom, left, top, 12);
            this.addRenderableWidget(explorer);
        }

        if (editorWidth > 0) {
            editor = MultiLineEditBox.builder().build(this.font, editorWidth, this.height - top - bottom, Component.literal("Code Editor"));
            editor.setPosition(editorX, top);
            editor.setValue(laptop.readEditorText());
            editor.setValueListener(laptop::writeEditorText);
            Path path = laptop.getEditorPath();
            if (path != null) {
                boolean bl = Files.isDirectory(path);
                editor.visible = !bl;
                String filename = bl ? path.getFileName().toString() + "/" : path.getFileName().toString();
                EDITOR_FILENAME = Component.literal(filename);
            }
            this.addRenderableWidget(editor);
        }

        cycleLayoutButton = Button.builder(Component.literal("Layout: " + layout.name()), button -> {
            layout = Layout.values()[(layout.ordinal() + 1) % Layout.values().length];
            this.rebuildWidgets();
        }).bounds(this.width - right - closeButton.getWidth() - 100 - 5, closeButton.getY(), 100, 20).build();

        saveButton = Button.builder(Component.literal("Save"), button -> this.onDone())
                .bounds(this.width - this.closeButton.getWidth() - this.cycleLayoutButton.getWidth() - right - 100 - 10, this.cycleLayoutButton.getY(), 100, 20).build();

        this.addRenderableWidget(cycleLayoutButton);
        this.addRenderableWidget(saveButton);
    }

    private void openDiskScreen() {
        ClientPlayNetworking.send(new OpenLaptopDiskMenuServerBoundPayload(this.blockPos));
    }

    private void onDone() {
        laptop.saveEditorFile();
    }

//    @Override
//    public void tick() {
//        if (i % 20 == 0) {
//            log.info(laptop.readFile());
//        }
//    }

    private class FileExplorerList extends ObjectSelectionList<FileExplorerList.Entry> {
        private int width;
        private Path currentFilePath;

        public FileExplorerList(final Minecraft minecraft, Path currentFilePath, List<Path> files, int width, int height, int x, int y, int itemHeight) {
            super(minecraft, width, height, y, itemHeight);
            this.setX(x);
            this.width = width;
            this.currentFilePath = currentFilePath;

            if (currentFilePath != null && Files.isDirectory(currentFilePath) && !fileManager.isRootPath(currentFilePath)) {
                FileExplorerList.Entry entry = new FileExplorerList.Entry("./", currentFilePath.getParent());
                this.addEntry(entry);
            }

            if (files != null) {
                files.stream().filter(p -> Files.isDirectory(p)).forEach(resource -> {
                    FileExplorerList.Entry entry = new FileExplorerList.Entry(resource.getFileName().toString() + "/", resource);
                    this.addEntry(entry);
                    if (this.currentFilePath != null && this.currentFilePath.equals(resource)) {
                        this.setSelected(entry);
                    }
                });
                files.stream().filter(p -> !Files.isDirectory(p)).forEach(resource -> {
                    FileExplorerList.Entry entry = new FileExplorerList.Entry(resource.getFileName().toString(), resource);
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

        class Entry extends ObjectSelectionList.Entry<FileExplorerList.Entry> {
            private String filename;
            private Path path;

            public Entry(String filename, Path path) {
                super();
                this.filename = filename;
                this.path = path;
            }

            public void extractContent(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final boolean hovered, final float a) {
                Font font = LaptopEditScreen.this.font;
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
                LaptopEditScreen.FileExplorerList.this.setSelected(this);
                LaptopEditScreen.this.fileManager.setSelected(path);
                LaptopEditScreen.this.laptop.setEditorPath(path);
                if (!path.toFile().isDirectory()) {
                    LaptopEditScreen.this.laptop.openEditorFile();
                    LaptopEditScreen.this.editor.setValue(LaptopEditScreen.this.laptop.readEditorText());
                    LaptopEditScreen.this.EDITOR_FILENAME = Component.literal(filename);
                    LaptopEditScreen.this.editor.visible = true;
                } else {
                    LaptopEditScreen.this.rebuildWidgets();
                }
            }

            @Override
            public Component getNarration() {
                return Component.literal("file");
            }
        }
    }

    private enum Layout {
        NORMAL, MAX_EXPLORER, MAX_EDITOR
    }
}
