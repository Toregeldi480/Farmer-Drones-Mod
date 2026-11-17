package com.farm.networking;

import com.farm.FarmerDrones;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record SetLaptopDiskMenuSourceServerBoundPayload(String source) implements CustomPacketPayload {
    public static final Identifier OPEN_LAPTOP_DISK_MENU_PAYLOAD_ID = FarmerDrones.id("set_laptop_disk_menu_source");
    public static final CustomPacketPayload.Type<SetLaptopDiskMenuSourceServerBoundPayload> TYPE = new CustomPacketPayload.Type<>(OPEN_LAPTOP_DISK_MENU_PAYLOAD_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, SetLaptopDiskMenuSourceServerBoundPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            SetLaptopDiskMenuSourceServerBoundPayload::source,
            SetLaptopDiskMenuSourceServerBoundPayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
