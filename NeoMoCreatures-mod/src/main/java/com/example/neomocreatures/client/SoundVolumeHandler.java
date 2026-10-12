package com.example.neomocreatures.client;

import java.util.EnumSet;
import java.util.Set;

import com.example.neomocreatures.Config;
import com.example.neomocreatures.NeoMoCreatures;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.sound.PlaySoundEvent;

/** Applies the per-creature volume config to the sounds made by this mod's entities. */
@EventBusSubscriber(modid = NeoMoCreatures.MODID, value = Dist.CLIENT)
public final class SoundVolumeHandler {

    /** Entity sounds are played at the entity position, but the packet is quantized and arrives a few ticks late. */
    private static final double SEARCH_RADIUS = 1.5D;
    private static final Set<SoundSource> ENTITY_SOURCES =
            EnumSet.of(SoundSource.NEUTRAL, SoundSource.HOSTILE, SoundSource.AMBIENT);

    private SoundVolumeHandler() {
    }

    @SubscribeEvent
    public static void onPlaySound(PlaySoundEvent event) {
        SoundInstance sound = event.getSound();
        ClientLevel level = Minecraft.getInstance().level;
        if (sound == null || level == null || sound.isRelative() || !ENTITY_SOURCES.contains(sound.getSource())) {
            return;
        }

        Entity owner = findNearestEntity(level, sound);
        if (owner == null) {
            return;
        }

        double volume = Config.SOUNDS.getVolume(BuiltInRegistries.ENTITY_TYPE.getKey(owner.getType()));
        if (volume <= 0.0D) {
            event.setSound(null);
        } else if (volume != 1.0D) {
            event.setSound(ScaledSoundInstance.wrap(sound, (float) volume));
        }
    }

    /** The entity closest to the sound position, whatever its type, so sounds of other entities are not stolen. */
    private static Entity findNearestEntity(ClientLevel level, SoundInstance sound) {
        double x = sound.getX();
        double y = sound.getY();
        double z = sound.getZ();
        AABB area = new AABB(x, y, z, x, y, z).inflate(SEARCH_RADIUS);

        Entity nearest = null;
        double best = Double.MAX_VALUE;
        for (Entity entity : level.getEntities((Entity) null, area)) {
            double distance = entity.position().distanceToSqr(x, y, z);
            if (distance < best) {
                best = distance;
                nearest = entity;
            }
        }
        return nearest;
    }
}