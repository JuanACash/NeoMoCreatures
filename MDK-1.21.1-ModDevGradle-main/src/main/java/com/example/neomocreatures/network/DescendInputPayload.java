package com.example.neomocreatures.network;

import com.example.neomocreatures.ExampleMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** Client -> server: "the descend key is currently held down: true/false." */
public record DescendInputPayload(boolean pressed) implements CustomPacketPayload {

    public static final Type<DescendInputPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "descend_input"));

    public static final StreamCodec<RegistryFriendlyByteBuf, DescendInputPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, DescendInputPayload::pressed,
                    DescendInputPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}