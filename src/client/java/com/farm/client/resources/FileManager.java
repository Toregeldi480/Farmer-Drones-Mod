package com.farm.client.resources;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.FileUtil;
import org.jspecify.annotations.NullMarked;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class FileManager implements ResourceManager {
    private static Path rootDir;
    private Path currentFile;

    public FileManager(final Minecraft minecraft) {
        FileManager.rootDir = Path.of(minecraft.gameDirectory.getAbsolutePath() + "/" + "farmer-drones/files/");
        try {
            FileUtil.createDirectoriesSafe(rootDir);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isRootPath(Path path) {
        try {
            return Files.isSameFile(path, rootDir);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Path> extractFiles() {
        try {
            return Files.list(rootDir).toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Path> extractFiles(Path path) {
        try {
            return Files.list(path).toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Path getSelected() {
        return this.currentFile;
    }

    public void setSelected(final Path path) {
        this.currentFile = path;
    }

    @Override
    @NullMarked
    public Set<String> getNamespaces() {
        return Set.of();
    }

    @Override
    @NullMarked
    public List<Resource> getResourceStack(Identifier location) {
        return List.of();
    }

    @Override
    @NullMarked
    public Map<Identifier, Resource> listResources(String directory, Selector selector) {
        return Map.of();
    }

    @Override
    @NullMarked
    public Map<Identifier, List<Resource>> listResourceStacks(String directory, Selector selector) {
        return Map.of();
    }

    @Override
    @NullMarked
    public Stream<PackResources> listPacks() {
        return Stream.empty();
    }

    @Override
    @NullMarked
    public Optional<Resource> getResource(Identifier location) {
        return Optional.empty();
    }
}
