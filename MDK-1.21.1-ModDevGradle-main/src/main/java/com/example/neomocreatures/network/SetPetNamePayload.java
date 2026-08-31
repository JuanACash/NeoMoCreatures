package com.example.neomocreatures.network;

import com.example.neomocreatures.ExampleMod;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** Client -> server: "name entity id X this string." */
public record SetPetNamePayload(int entityId, String name) implements CustomPacketPayload {

    public static final Type<SetPetNamePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "set_pet_name"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SetPetNamePayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, SetPetNamePayload::entityId,
                    ByteBufCodecs.STRING_UTF8, SetPetNamePayload::name,
                    SetPetNamePayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}