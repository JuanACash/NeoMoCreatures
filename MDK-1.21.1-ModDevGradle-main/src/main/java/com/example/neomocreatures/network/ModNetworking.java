package com.example.neomocreatures.network;

import com.example.neomocreatures.client.MoCNamingScreen;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModNetworking {

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToClient(
                OpenNamingScreenPayload.TYPE,
                OpenNamingScreenPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() ->
                        Minecraft.getInstance().setScreen(new MoCNamingScreen(payload.entityId()))));

        registrar.playToServer(
                SetPetNamePayload.TYPE,
                SetPetNamePayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    if (payload.name().isBlank() || payload.name().length() > 32) {
                        return;
                    }
                    Entity entity = context.player().level().getEntity(payload.entityId());
                    // Only the entity's own owner can (re)name it — checked
                    // generically via TamableAnimal/AbstractHorse, so this
                    // works for any future tameable creature too.
                    boolean isOwner = (entity instanceof net.minecraft.world.entity.TamableAnimal tamable
                                    && context.player().getUUID().equals(tamable.getOwnerUUID()))
                            || (entity instanceof net.minecraft.world.entity.animal.horse.AbstractHorse horse
                                    && context.player().getUUID().equals(horse.getOwnerUUID()));
                    // A tamed-but-ownerless mob (Scroll of Sale / Reset Owner) is
                    // adopted by whoever successfully renames it first.
                    boolean isAdoptableOwnerless = (entity instanceof net.minecraft.world.entity.TamableAnimal tamable2
                                    && tamable2.isTame() && tamable2.getOwnerUUID() == null)
                            || (entity instanceof net.minecraft.world.entity.animal.horse.AbstractHorse horse2
                                    && horse2.isTamed() && horse2.getOwnerUUID() == null);
                    if (entity != null && (isOwner || isAdoptableOwnerless)) {
                        if (isAdoptableOwnerless) {
                            if (entity instanceof net.minecraft.world.entity.TamableAnimal tamable3) {
                                tamable3.setOwnerUUID(context.player().getUUID());
                            } else if (entity instanceof net.minecraft.world.entity.animal.horse.AbstractHorse horse3) {
                                horse3.setOwnerUUID(context.player().getUUID());
                            }
                        }
                        entity.setCustomName(Component.literal(payload.name()));
                        entity.setCustomNameVisible(true);
                    }
                }));

        registrar.playToClient(OpenPlayerInventoryPayload.TYPE, OpenPlayerInventoryPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
                    if (mc.player != null) {
                        mc.setScreen(new net.minecraft.client.gui.screens.inventory.InventoryScreen(mc.player));
                    }
                }));
                
        registrar.playToServer(
            DescendInputPayload.TYPE,
            DescendInputPayload.STREAM_CODEC,
            (payload, context) -> context.enqueueWork(() -> {
                if (context.player().getVehicle() instanceof com.example.neomocreatures.entity.MoCHorseEntity horse) {
                    horse.setDescendHeld(payload.pressed());
                } else if (context.player().getVehicle() instanceof com.example.neomocreatures.entity.MoCWyvernEntity wyvern) {
                    wyvern.setDescendHeld(payload.pressed());
                } else if (context.player().getVehicle() instanceof com.example.neomocreatures.entity.MoCBigCatEntity bigCat) {
                    bigCat.setDescendHeld(payload.pressed());
                } else if (context.player().getVehicle() instanceof com.example.neomocreatures.entity.MoCManticoreEntity manticore) {
                    manticore.setDescendHeld(payload.pressed());
                } 
            }));

        registrar.playToServer(
            AscendInputPayload.TYPE,
            AscendInputPayload.STREAM_CODEC,
            (payload, context) -> context.enqueueWork(() -> {
                if (context.player().getVehicle() instanceof com.example.neomocreatures.entity.MoCHorseEntity horse) {
                    horse.setAscendHeld(payload.pressed());
                } else if (context.player().getVehicle() instanceof com.example.neomocreatures.entity.MoCWyvernEntity wyvern) {
                    wyvern.setAscendHeld(payload.pressed());
                } else if (context.player().getVehicle() instanceof com.example.neomocreatures.entity.MoCBigCatEntity bigCat) {
                    bigCat.setAscendHeld(payload.pressed());
                } else if (context.player().getVehicle() instanceof com.example.neomocreatures.entity.MoCManticoreEntity manticore) {
                    manticore.setAscendHeld(payload.pressed());
                } else if (context.player().getVehicle() instanceof com.example.neomocreatures.entity.MoCOstrichEntity ostrich) {
                    ostrich.setAscendHeld(payload.pressed());
                } 
            }));
    }
}