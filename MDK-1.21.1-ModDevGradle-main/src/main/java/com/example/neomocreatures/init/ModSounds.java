package com.example.neomocreatures.init;

import com.example.neomocreatures.ExampleMod;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, ExampleMod.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> HORSE_GRUNT = register("moc_horse.grunt");
    public static final DeferredHolder<SoundEvent, SoundEvent> HORSE_HURT = register("moc_horse.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> HORSE_DEATH = register("moc_horse.death");
    public static final DeferredHolder<SoundEvent, SoundEvent> HORSE_EATING = register("moc_horse.eating");
    public static final DeferredHolder<SoundEvent, SoundEvent> DONKEY_GRUNT = register("moc_horse.donkey_grunt");
    public static final DeferredHolder<SoundEvent, SoundEvent> DONKEY_HURT = register("moc_horse.donkey_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> DONKEY_DEATH = register("moc_horse.donkey_death");
    public static final DeferredHolder<SoundEvent, SoundEvent> ZEBRA_GRUNT = register("moc_horse.zebra_grunt");
    public static final DeferredHolder<SoundEvent, SoundEvent> ZEBRA_HURT = register("moc_horse.zebra_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> HORSE_DRINKING = register("moc_horse.drinking");
    public static final DeferredHolder<SoundEvent, SoundEvent> HORSE_TRANSFORM = register("moc_horse.transform");
    public static final DeferredHolder<SoundEvent, SoundEvent> HORSE_WING_FLAP = register("moc_horse.wing_flap");
    public static final DeferredHolder<SoundEvent, SoundEvent> HORSE_ARMOR_PUT = register("moc_horse.armor_put");
    public static final DeferredHolder<SoundEvent, SoundEvent> HORSE_ARMOR_OFF = register("moc_horse.armor_off");
    public static final DeferredHolder<SoundEvent, SoundEvent> HORSE_MOB_GRUNT1 = register("moc_horse_mob.grunt1");
    public static final DeferredHolder<SoundEvent, SoundEvent> HORSE_MOB_GRUNT2 = register("moc_horse_mob.grunt2");
    public static final DeferredHolder<SoundEvent, SoundEvent> HORSE_MOB_HURT = register("moc_horse_mob.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> HORSE_MOB_DEATH = register("moc_horse_mob.death");
    public static final DeferredHolder<SoundEvent, SoundEvent> HORSE_MOB_AGGRESSIVE = register("moc_horse_mob.aggressive");
    public static final DeferredHolder<SoundEvent, SoundEvent> HORSE_UNDEAD_GRUNT1 = register("item.horse_grunt_undead1");
    public static final DeferredHolder<SoundEvent, SoundEvent> HORSE_UNDEAD_GRUNT2 = register("item.horse_grunt_undead2");
    public static final DeferredHolder<SoundEvent, SoundEvent> HORSE_UNDEAD_HURT = register("item.horse_hurt_undead");
    public static final DeferredHolder<SoundEvent, SoundEvent> HORSE_UNDEAD_DEATH = register("item.horse_dying_undead");
    public static final DeferredHolder<SoundEvent, SoundEvent> HORSE_MAD_UNDEAD = register("item.horse_mad_undead");
    public static final DeferredHolder<SoundEvent, SoundEvent> HORSE_GHOST_GRUNT1 = register("item.horse_grunt_ghost1");
    public static final DeferredHolder<SoundEvent, SoundEvent> HORSE_GHOST_GRUNT2 = register("item.horse_grunt_ghost2");
    public static final DeferredHolder<SoundEvent, SoundEvent> HORSE_GHOST_GRUNT3 = register("item.horse_grunt_ghost3");
    public static final DeferredHolder<SoundEvent, SoundEvent> HORSE_GHOST_HURT = register("item.horse_hurt_ghost");
    public static final DeferredHolder<SoundEvent, SoundEvent> HORSE_GHOST_DEATH = register("item.horse_dying_ghost");
    public static final DeferredHolder<SoundEvent, SoundEvent> HORSE_GHOST_MAD = register("item.horse_mad_ghost");
    public static final DeferredHolder<SoundEvent, SoundEvent> AMULET_APPEAR = register("moc_horse.amulet_appear");
    public static final DeferredHolder<SoundEvent, SoundEvent> AMULET_APPEAR_MAGIC = register("moc_horse.amulet_appear_magic");
    public static final DeferredHolder<SoundEvent, SoundEvent> AMULET_VANISH = register("moc_horse.amulet_vanish");
    public static final DeferredHolder<SoundEvent, SoundEvent> WHIP = register("item.whip");
    public static final DeferredHolder<SoundEvent, SoundEvent> RECORD_SHUFFLING = register("records.shuffling");

    private static DeferredHolder<SoundEvent, SoundEvent> register(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }
}