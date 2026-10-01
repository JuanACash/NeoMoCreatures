package com.example.neomocreatures.network;

import com.example.neomocreatures.NeoMoCreatures;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record OpenPlayerInventoryPayload() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<OpenPlayerInventoryPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "open_player_inventory"));

    public static final StreamCodec<io.netty.buffer.ByteBuf, OpenPlayerInventoryPayload> STREAM_CODEC =
            StreamCodec.unit(new OpenPlayerInventoryPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}