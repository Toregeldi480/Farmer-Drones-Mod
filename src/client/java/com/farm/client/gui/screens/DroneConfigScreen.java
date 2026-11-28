package com.farm.client.gui.screens;

import com.farm.networking.ConnectToServerFarmerDroneServerBoundPayload;
import com.farm.entity.FarmerDrone;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class DroneConfigScreen extends Screen {
    private final FarmerDrone farmerDrone;
    private EditBox serverAddress;
    private Button connectButton;

    public DroneConfigScreen(FarmerDrone farmerDrone) {
        super(Component.literal("Farmer Drone Config"));
        this.farmerDrone = farmerDrone;
    }

    @Override
    protected void init() {
        serverAddress = new EditBox(this.font, this.width / 2, this.height / 2, 500, 30, Component.literal("Server Address"));
        connectButton = Button.builder(Component.literal("Connect"), button -> onConnect())
                .bounds(this.width / 2, this.height / 2 + 40, 100, 20).build();

        this.addRenderableWidget(serverAddress);
        this.addRenderableWidget(connectButton);
    }

    public void onConnect() {
        ClientPlayNetworking.send(new ConnectToServerFarmerDroneServerBoundPayload(this.serverAddress.getValue(), this.farmerDrone.getUUID().toString()));
        this.onClose();
    }
}
