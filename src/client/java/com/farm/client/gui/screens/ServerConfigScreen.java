package com.farm.client.gui.screens;

import com.farm.block.entity.ServerBlockEntity;
import com.farm.networking.SetServerAddressServerBoundPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ServerConfigScreen extends Screen {
    private ServerBlockEntity server;
    private EditBox addressBox;
    private Button saveButton;
    private String address;

    public ServerConfigScreen(ServerBlockEntity server) {
        super(Component.literal("Server Config"));
        this.server = server;
        this.address = server.getAddress();
    }

    @Override
    protected void init() {
        addressBox = new EditBox(this.font, this.width / 2 - 150, this.height / 2, 300, 30, Component.literal("Server Address"));
        saveButton = Button.builder(Component.literal("Save"), button -> this.onDone())
                .bounds(this.width / 2, this.height / 2 + 40, 100, 20).build();
        addressBox.setValue(address);
        this.addRenderableWidget(addressBox);
        this.addRenderableWidget(saveButton);
    }

    public void onDone() {
        ClientPlayNetworking.send(new SetServerAddressServerBoundPayload(this.server.getBlockPos(), this.addressBox.getValue()));
        this.onClose();
    }
}
