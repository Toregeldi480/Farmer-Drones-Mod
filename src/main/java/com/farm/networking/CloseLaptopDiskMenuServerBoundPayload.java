package com.farm.networking;

import com.farm.FarmerDrones;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NullMarked;

public record CloseLaptopDiskMenuServerBoundPayload() implements CustomPacketPayload {
    public static final Identifier CLOSE_LAPTOP_DISK_MENU_PAYLOAD_ID = FarmerDrones.id("close_laptop_disk_menu");
    public static final CustomPacketPayload.Type<CloseLaptopDiskMenuServerBoundPayload> TYPE = new CustomPacketPayload.Type<>(CLOSE_LAPTOP_DISK_MENU_PAYLOAD_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, CloseLaptopDiskMenuServerBoundPayload> CODEC = StreamCodec.unit(
            new CloseLaptopDiskMenuServerBoundPayload()
    );

    @Override
    @NullMarked
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
