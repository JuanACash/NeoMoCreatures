package com.example.neomocreatures.network;

import com.example.neomocreatures.NeoMoCreatures;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** Server -> client: "open the naming screen for entity id X." */
public record OpenNamingScreenPayload(int entityId) implements CustomPacketPayload {

    public static final Type<OpenNamingScreenPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "open_naming_screen"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenNamingScreenPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    OpenNamingScreenPayload::entityId,
                    OpenNamingScreenPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}