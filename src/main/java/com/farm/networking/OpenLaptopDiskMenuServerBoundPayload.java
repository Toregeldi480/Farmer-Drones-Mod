package com.farm.networking;

import com.farm.FarmerDrones;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record OpenLaptopDiskMenuServerBoundPayload(BlockPos blockPos) implements CustomPacketPayload {
    public static final Identifier OPEN_LAPTOP_DISK_MENU_PAYLOAD_ID = FarmerDrones.id("open_laptop_disk_menu");
    public static final CustomPacketPayload.Type<OpenLaptopDiskMenuServerBoundPayload> TYPE = new CustomPacketPayload.Type<>(OPEN_LAPTOP_DISK_MENU_PAYLOAD_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenLaptopDiskMenuServerBoundPayload> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            OpenLaptopDiskMenuServerBoundPayload::blockPos,
            OpenLaptopDiskMenuServerBoundPayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
