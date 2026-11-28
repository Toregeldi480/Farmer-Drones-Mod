package com.farm.networking;

import com.farm.FarmerDrones;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NullMarked;

public record ConnectToServerFarmerDroneServerBoundPayload(String serverAddress, String farmerDroneUUID) implements CustomPacketPayload {
    public static final Identifier CONNECT_TO_SERVER_FARMER_DRONE_PAYLOAD_ID = FarmerDrones.id("connect_to_server_farmer_drone");
    public static final CustomPacketPayload.Type<ConnectToServerFarmerDroneServerBoundPayload> TYPE = new CustomPacketPayload.Type<>(CONNECT_TO_SERVER_FARMER_DRONE_PAYLOAD_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, ConnectToServerFarmerDroneServerBoundPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            ConnectToServerFarmerDroneServerBoundPayload::serverAddress,
            ByteBufCodecs.STRING_UTF8,
            ConnectToServerFarmerDroneServerBoundPayload::farmerDroneUUID,
            ConnectToServerFarmerDroneServerBoundPayload::new
    );

    @Override
    @NullMarked
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
