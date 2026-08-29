package com.example.examplemod.network;

import com.example.examplemod.ExampleMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** Client -> server: "the (vanilla) jump key is currently held down: true/false." */
public record AscendInputPayload(boolean pressed) implements CustomPacketPayload {

    public static final Type<AscendInputPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "ascend_input"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AscendInputPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, AscendInputPayload::pressed,
                    AscendInputPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}