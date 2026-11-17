package com.farm.init;

import com.farm.FarmerDrones;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;

public class ModComponents {
    public static final DataComponentType<String> DISK_SOURCE_CODE = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            FarmerDrones.id("disk_source_code"),
            DataComponentType.<String>builder().persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8).build()
    );

    public static void init() {

    }
}
