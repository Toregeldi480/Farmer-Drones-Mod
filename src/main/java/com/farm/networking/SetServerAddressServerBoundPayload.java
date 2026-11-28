package com.farm.networking;

import com.farm.FarmerDrones;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NullMarked;

public record SetServerAddressServerBoundPayload(BlockPos blockPos, String serverAddress) implements CustomPacketPayload {
    public static final Identifier SET_SERVER_ADDRESS_SERVER_PAYLOAD_ID = FarmerDrones.id("set_server_address_server");
    public static final Type<SetServerAddressServerBoundPayload> TYPE = new Type<>(SET_SERVER_ADDRESS_SERVER_PAYLOAD_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, SetServerAddressServerBoundPayload> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            SetServerAddressServerBoundPayload::blockPos,
            ByteBufCodecs.STRING_UTF8,
            SetServerAddressServerBoundPayload::serverAddress,
            SetServerAddressServerBoundPayload::new
    );

    @Override
    @NullMarked
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
