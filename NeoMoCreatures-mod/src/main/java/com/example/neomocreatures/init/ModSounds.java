package com.example.neomocreatures.init;

import com.example.neomocreatures.NeoMoCreatures;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, NeoMoCreatures.MODID);

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
    public static final DeferredHolder<SoundEvent, SoundEvent> HORSE_MAD = register("item.horse_mad");
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

    public static final DeferredHolder<SoundEvent, SoundEvent> WYVERN_GRUNT = register("moc_wyvern.grunt");
    public static final DeferredHolder<SoundEvent, SoundEvent> WYVERN_HURT = register("moc_wyvern.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> WYVERN_DEATH = register("moc_wyvern.death");
    public static final DeferredHolder<SoundEvent, SoundEvent> WYVERN_WING_FLAP = register("moc_wyvern.wing_flap");
    public static final DeferredHolder<SoundEvent, SoundEvent> WYVERN_POISON = register("moc_wyvern.poison");

    public static final DeferredHolder<SoundEvent, SoundEvent> ELEPHANT_AMBIENT = register("moc_elephant.ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> ELEPHANT_AMBIENT_BABY = register("moc_elephant.ambient_baby");
    public static final DeferredHolder<SoundEvent, SoundEvent> ELEPHANT_HURT = register("moc_elephant.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> ELEPHANT_DEATH = register("moc_elephant.death");

    public static final DeferredHolder<SoundEvent, SoundEvent> BIG_CAT_AMBIENT = register("moc_big_cat.ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> BIG_CAT_HURT = register("moc_big_cat.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> BIG_CAT_DEATH = register("moc_big_cat.death");
    public static final DeferredHolder<SoundEvent, SoundEvent> BIG_CAT_AMBIENT_BABY = register("moc_big_cat.ambient_baby");
    public static final DeferredHolder<SoundEvent, SoundEvent> BIG_CAT_HURT_BABY = register("moc_big_cat.hurt_baby");
    public static final DeferredHolder<SoundEvent, SoundEvent> BIG_CAT_DEATH_BABY = register("moc_big_cat.death_baby");

    public static final DeferredHolder<SoundEvent, SoundEvent> SCORPION_STING = register("moc_scorpion.sting");
    public static final DeferredHolder<SoundEvent, SoundEvent> SCORPION_CLAW = register("moc_scorpion.claw");
    public static final DeferredHolder<SoundEvent, SoundEvent> SCORPION_AMBIENT = register("moc_scorpion.ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> SCORPION_HURT = register("moc_scorpion.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> SCORPION_DEATH = register("moc_scorpion.death");

    public static final DeferredHolder<SoundEvent, SoundEvent> OSTRICH_AMBIENT = register("moc_ostrich.ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> OSTRICH_HURT = register("moc_ostrich.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> OSTRICH_DEATH = register("moc_ostrich.death");

    public static final DeferredHolder<SoundEvent, SoundEvent> BEAR_AMBIENT = register("moc_bear.ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> BEAR_HURT = register("moc_bear.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> BEAR_DEATH = register("moc_bear.death");

    public static final DeferredHolder<SoundEvent, SoundEvent> KOMODO_HISS_1 = register("moc_komodo.hiss1");
    public static final DeferredHolder<SoundEvent, SoundEvent> KOMODO_HISS_2 = register("moc_komodo.hiss2");
    public static final DeferredHolder<SoundEvent, SoundEvent> KOMODO_HURT = register("moc_komodo.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> KOMODO_DEATH = register("moc_komodo.death");

    public static final DeferredHolder<SoundEvent, SoundEvent> SNAKE_AMBIENT = register("moc_snake.ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> SNAKE_ANGRY = register("moc_snake.angry");
    public static final DeferredHolder<SoundEvent, SoundEvent> SNAKE_RATTLE = register("moc_snake.rattle");
    public static final DeferredHolder<SoundEvent, SoundEvent> SNAKE_SNAP = register("moc_snake.snap");
    public static final DeferredHolder<SoundEvent, SoundEvent> SNAKE_SWIM = register("moc_snake.swim");
    public static final DeferredHolder<SoundEvent, SoundEvent> SNAKE_HURT = register("moc_snake.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> SNAKE_DEATH = register("moc_snake.death");

    public static final DeferredHolder<SoundEvent, SoundEvent> FOX_AMBIENT = register("moc_fox.ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> FOX_HURT = register("moc_fox.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> FOX_DEATH = register("moc_fox.death");

    public static final DeferredHolder<SoundEvent, SoundEvent> RACCOON_AMBIENT = register("moc_raccoon.ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> RACCOON_HURT = register("moc_raccoon.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> RACCOON_DEATH = register("moc_raccoon.death");

    public static final DeferredHolder<SoundEvent, SoundEvent> TURKEY_AMBIENT = register("moc_turkey.ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> TURKEY_HURT = register("moc_turkey.hurt");

    public static final DeferredHolder<SoundEvent, SoundEvent> GOAT_AMBIENT_MALE = register("moc_goat.ambient_male");
    public static final DeferredHolder<SoundEvent, SoundEvent> GOAT_AMBIENT_FEMALE = register("moc_goat.ambient_female");
    public static final DeferredHolder<SoundEvent, SoundEvent> GOAT_AMBIENT_BABY = register("moc_goat.ambient_baby");
    public static final DeferredHolder<SoundEvent, SoundEvent> GOAT_HURT = register("moc_goat.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> GOAT_DEATH = register("moc_goat.death");
    public static final DeferredHolder<SoundEvent, SoundEvent> GOAT_EATING = register("moc_goat.eating");
    public static final DeferredHolder<SoundEvent, SoundEvent> GOAT_DIG = register("moc_goat.dig");
    public static final DeferredHolder<SoundEvent, SoundEvent> GOAT_SMACK = register("moc_goat.smack");

    public static final DeferredHolder<SoundEvent, SoundEvent> KITTY_AMBIENT = register("moc_kitty.ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> KITTY_AMBIENT_BABY = register("moc_kitty.ambient_baby");
    public static final DeferredHolder<SoundEvent, SoundEvent> KITTY_HURT = register("moc_kitty.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> KITTY_HURT_BABY = register("moc_kitty.hurt_baby");
    public static final DeferredHolder<SoundEvent, SoundEvent> KITTY_DEATH = register("moc_kitty.death");
    public static final DeferredHolder<SoundEvent, SoundEvent> KITTY_DEATH_BABY = register("moc_kitty.death_baby");
    public static final DeferredHolder<SoundEvent, SoundEvent> KITTY_EATING = register("moc_kitty.eating");
    public static final DeferredHolder<SoundEvent, SoundEvent> KITTY_EATING_FISH = register("moc_kitty.eating_fish");
    public static final DeferredHolder<SoundEvent, SoundEvent> KITTY_FOOD = register("moc_kitty.food");
    public static final DeferredHolder<SoundEvent, SoundEvent> KITTY_PURR = register("moc_kitty.purr");
    public static final DeferredHolder<SoundEvent, SoundEvent> KITTY_TRAPPED = register("moc_kitty.trapped");
    public static final DeferredHolder<SoundEvent, SoundEvent> KITTY_UPSET = register("moc_kitty.upset");

    public static final DeferredHolder<SoundEvent, SoundEvent> KITTY_BED_POURING_FOOD = register("moc_kitty.pouring_food");
    public static final DeferredHolder<SoundEvent, SoundEvent> KITTY_BED_POURING_MILK = register("moc_kitty.pouring_milk");

    private static DeferredHolder<SoundEvent, SoundEvent> register(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }
}