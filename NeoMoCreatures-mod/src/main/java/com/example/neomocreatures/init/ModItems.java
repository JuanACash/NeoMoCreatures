package com.example.neomocreatures.init;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.bear.BearVariant;
import com.example.neomocreatures.entity.bigcat.BigCatVariant;
import com.example.neomocreatures.entity.smallfish.SmallFishVariant;
import com.example.neomocreatures.item.BearSpawnEggItem;
import com.example.neomocreatures.item.BigCatSpawnEggItem;
import com.example.neomocreatures.item.BirdSpawnEggItem;
import com.example.neomocreatures.item.BowlFoodItem;
import com.example.neomocreatures.item.BunnySpawnEggItem;
import com.example.neomocreatures.item.FilledAmuletItem;
import com.example.neomocreatures.item.FilledFishNetItem;
import com.example.neomocreatures.item.FishNetItem;
import com.example.neomocreatures.item.FoxSpawnEggItem;
import com.example.neomocreatures.item.KittyBedItem;
import com.example.neomocreatures.item.LitterBoxItem;
import com.example.neomocreatures.item.ManticoreSpawnEggItem;
import com.example.neomocreatures.item.MediumFishEggItem;
import com.example.neomocreatures.item.MoCEggItem;
import com.example.neomocreatures.item.OstrichSpawnEggItem;
import com.example.neomocreatures.item.ScorpionAxeItem;
import com.example.neomocreatures.item.ScorpionSpawnEggItem;
import com.example.neomocreatures.item.ScorpionSwordItem;
import com.example.neomocreatures.item.ScrollOfFreedomItem;
import com.example.neomocreatures.item.ScrollOfResetOwnerItem;
import com.example.neomocreatures.item.ScrollOfSaleItem;
import com.example.neomocreatures.item.SharkEggItem;
import com.example.neomocreatures.item.SharkSpawnEggItem;
import com.example.neomocreatures.item.SmallFishEggItem;
import com.example.neomocreatures.item.SmallFishSpawnEggItem;
import com.example.neomocreatures.item.SnakeSpawnEggItem;
import com.example.neomocreatures.item.StaffPortalItem;
import com.example.neomocreatures.item.WhipItem;
import com.example.neomocreatures.item.WildHorseSpawnEggItem;
import com.example.neomocreatures.item.WyvernSpawnEggItem;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SaddleItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;


public class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(NeoMoCreatures.MODID);

    /** Duration of the potion effect scorpion weapons apply on hit (5 seconds). */
    private static final int SCORPION_EFFECT_TICKS = 100;

    public static final DeferredItem<Item> WILD_HORSE_SPAWN_EGG = ITEMS.register("wild_horse_spawn_egg",
            () -> new WildHorseSpawnEggItem(ModEntities.MOC_HORSE, new Item.Properties()));

    public static final DeferredItem<Item> MOC_HORSE_MOB_SPAWN_EGG = spawnEgg("moc_horse_mob_spawn_egg", ModEntities.MOC_HORSE_MOB, 0x1A1A2E, 0x5B2C6F);
    public static final DeferredItem<Item> WYVERN_SPAWN_EGG = ITEMS.register("wyvern_spawn_egg",
        () -> new WyvernSpawnEggItem(new Item.Properties()));
    public static final DeferredItem<Item> MOC_ELEPHANT_SPAWN_EGG = spawnEgg("moc_elephant_spawn_egg", ModEntities.MOC_ELEPHANT, 0xE8DCC8, 0x5C4A3A);
    public static final DeferredItem<Item> LION_SPAWN_EGG = ITEMS.register("lion_spawn_egg",
        () -> new BigCatSpawnEggItem(
                BigCatVariant.SpawnFamily.LION, 0xC2A25C, 0x6B4A1F, new Item.Properties()));
    public static final DeferredItem<Item> TIGER_SPAWN_EGG = ITEMS.register("tiger_spawn_egg",
        () -> new BigCatSpawnEggItem(
                BigCatVariant.SpawnFamily.TIGER, 0xE0771C, 0x1A1A1A, new Item.Properties()));
    public static final DeferredItem<Item> LEOPARD_SPAWN_EGG = ITEMS.register("leopard_spawn_egg",
        () -> new BigCatSpawnEggItem(
                BigCatVariant.SpawnFamily.LEOPARD, 0xFFCE1B, 0x3A2A1A, new Item.Properties()));
    public static final DeferredItem<Item> PANTHER_SPAWN_EGG = ITEMS.register("panther_spawn_egg",
        () -> new BigCatSpawnEggItem(
                BigCatVariant.SpawnFamily.PANTHER, 0x1A1A1A, 0x2E2E2E, new Item.Properties()));
    public static final DeferredItem<Item> LIGER_SPAWN_EGG = ITEMS.register("liger_spawn_egg",
        () -> new BigCatSpawnEggItem(
                BigCatVariant.LIGER, 0xC97B2E, 0x5C4A3A, new Item.Properties()));
    public static final DeferredItem<Item> LIARD_SPAWN_EGG = ITEMS.register("liard_spawn_egg",
        () -> new BigCatSpawnEggItem(
                BigCatVariant.LIARD, 0xC2A25C, 0xFFCE1B, new Item.Properties()));
    public static final DeferredItem<Item> LEOGER_SPAWN_EGG = ITEMS.register("leoger_spawn_egg",
        () -> new BigCatSpawnEggItem(
                BigCatVariant.LEOGER, 0xFFCE1B, 0xC97B2E, new Item.Properties()));
    public static final DeferredItem<Item> LITHER_SPAWN_EGG = ITEMS.register("lither_spawn_egg",
        () -> new BigCatSpawnEggItem(
                BigCatVariant.LITHER, 0x1A1A1A, 0xC2A25C, new Item.Properties()));
    public static final DeferredItem<Item> PANTHARD_SPAWN_EGG = ITEMS.register("panthard_spawn_egg",
        () -> new BigCatSpawnEggItem(
                BigCatVariant.PANTHARD, 0x1A1A1A, 0xFFCE1B, new Item.Properties()));
    public static final DeferredItem<Item> PANTHGER_SPAWN_EGG = ITEMS.register("panthger_spawn_egg",
        () -> new BigCatSpawnEggItem(
                BigCatVariant.PANTHGER, 0x1A1A1A, 0xC97B2E, new Item.Properties()));
    public static final DeferredItem<Item> MANTICORE_SPAWN_EGG = ITEMS.register("manticore_spawn_egg",
        () -> new ManticoreSpawnEggItem(0x000000, 0x4A7A3A, new Item.Properties()));
    public static final DeferredItem<Item> SCORPION_SPAWN_EGG = ITEMS.register("scorpion_spawn_egg",
        () -> new ScorpionSpawnEggItem(0xC2914F, 0xFF0000, new Item.Properties()));
    public static final DeferredItem<Item> OSTRICH_SPAWN_EGG = ITEMS.register("ostrich_spawn_egg",
        () -> new OstrichSpawnEggItem(0xEDE8D0, 0x90D5FF, new Item.Properties()));
    public static final DeferredItem<Item> BLACK_BEAR_SPAWN_EGG = ITEMS.register("black_bear_spawn_egg",
        () -> new BearSpawnEggItem(
                BearVariant.BLACK, 0x3A2E24, 0x1A1410, new Item.Properties()));
    public static final DeferredItem<Item> GRIZZLY_BEAR_SPAWN_EGG = ITEMS.register("grizzly_bear_spawn_egg",
        () -> new BearSpawnEggItem(
                BearVariant.GRIZZLY, 0x8B5A2B, 0xC89B6A, new Item.Properties()));
    public static final DeferredItem<Item> POLAR_BEAR_SPAWN_EGG = ITEMS.register("polar_bear_spawn_egg",
        () -> new BearSpawnEggItem(
                BearVariant.POLAR, 0xF0F5F7, 0xA9C6D8, new Item.Properties()));
    public static final DeferredItem<Item> PANDA_BEAR_SPAWN_EGG = ITEMS.register("panda_bear_spawn_egg",
        () -> new BearSpawnEggItem(
                BearVariant.PANDA, 0xF5F5F0, 0x1A1A1A, new Item.Properties()));
    public static final DeferredItem<Item> FOX_SPAWN_EGG = ITEMS.register("fox_spawn_egg",
        () -> new FoxSpawnEggItem(0xC57726, 0xF2F2F2, new Item.Properties()));
    public static final DeferredItem<Item> RACCOON_SPAWN_EGG = spawnEgg("raccoon_spawn_egg", ModEntities.MOC_RACCOON, 0x6E6E6E, 0x2B2B2B);
    public static final DeferredItem<Item> TURKEY_SPAWN_EGG = spawnEgg("turkey_spawn_egg", ModEntities.MOC_TURKEY, 0x92C5FC, 0xF98B7C);
    public static final DeferredItem<Item> KOMODO_DRAGON_SPAWN_EGG = spawnEgg("komodo_dragon_spawn_egg", ModEntities.MOC_KOMODO_DRAGON, 0x8B8000, 0x00008B);
    public static final DeferredItem<Item> GOAT_SPAWN_EGG = spawnEgg("goat_spawn_egg", ModEntities.MOC_GOAT, 0xA89A91, 0xFD3DB5);
    public static final DeferredItem<Item> KITTY_SPAWN_EGG = spawnEgg("kitty_spawn_egg", ModEntities.MOC_KITTY, 0xD8B48C, 0x8B5A2B);
    public static final DeferredItem<Item> SNAKE_SPAWN_EGG = ITEMS.register("snake_spawn_egg",
        () -> new SnakeSpawnEggItem(0x2E4B1E, 0xC9A227, new Item.Properties()));
    public static final DeferredItem<Item> BUNNY_SPAWN_EGG = ITEMS.register("bunny_spawn_egg",
        () -> new BunnySpawnEggItem(0xFF66C4, 0xFFF066, new Item.Properties()));
    public static final DeferredItem<Item> BIRD_SPAWN_EGG = ITEMS.register("bird_spawn_egg",
        () -> new BirdSpawnEggItem(0x4A90D9, 0x32CD32, new Item.Properties()));
    public static final DeferredItem<Item> SHARK_SPAWN_EGG = ITEMS.register("shark_spawn_egg",
        () -> new SharkSpawnEggItem(0x5B6E7A, 0x2C4E6B, new Item.Properties()));
    public static final DeferredItem<Item> TURTLE_SPAWN_EGG = spawnEgg("turtle_spawn_egg", ModEntities.MOC_TURTLE, 0x634315, 0xA0991B);
    public static final DeferredItem<Item> STINGRAY_SPAWN_EGG = spawnEgg("stingray_spawn_egg", ModEntities.MOC_STINGRAY, 0xADEBB3, 0xDFB945);
    public static final DeferredItem<Item> DOLPHIN_SPAWN_EGG = spawnEgg("dolphin_spawn_egg", ModEntities.MOC_DOLPHIN, 0x3E5984, 0xABAEC4);
    public static final DeferredItem<Item> MANTA_RAY_SPAWN_EGG = spawnEgg("manta_ray_spawn_egg", ModEntities.MOC_MANTA_RAY, 0x007FFF, 0xE2CA76);
    public static final DeferredItem<Item> FISHY_SPAWN_EGG = spawnEgg("fishy_spawn_egg", ModEntities.MOC_FISHY, 0x5672FF, 0x1F17B0);
    public static final DeferredItem<Item> COD_FISH_SPAWN_EGG = spawnEgg("cod_fish_spawn_egg", ModEntities.MOC_COD, 0x5672FF, 0xFFD700);
    public static final DeferredItem<Item> SALMON_FISH_SPAWN_EGG = spawnEgg("salmon_fish_spawn_egg", ModEntities.MOC_SALMON, 0x5672FF, 0xFF6EC7);
    public static final DeferredItem<Item> BASS_FISH_SPAWN_EGG = spawnEgg("bass_fish_spawn_egg", ModEntities.MOC_BASS, 0x5672FF, 0x39FF14);
    public static final DeferredItem<Item> ANCHOVY_SPAWN_EGG = ITEMS.register("anchovy_spawn_egg",
        () -> new SmallFishSpawnEggItem(
                SmallFishVariant.ANCHOVY, 0x5672FF, 0xFF3B30, new Item.Properties()));
    public static final DeferredItem<Item> ANGELFISH_SPAWN_EGG = ITEMS.register("angelfish_spawn_egg",
        () -> new SmallFishSpawnEggItem(
                SmallFishVariant.ANGELFISH, 0x5672FF, 0x8E44AD, new Item.Properties()));
    public static final DeferredItem<Item> ANGLERFISH_SPAWN_EGG = ITEMS.register("anglerfish_spawn_egg",
        () -> new SmallFishSpawnEggItem(
                SmallFishVariant.ANGLER, 0x5672FF, 0x78909C, new Item.Properties()));
    public static final DeferredItem<Item> CLOWNFISH_SPAWN_EGG = ITEMS.register("clownfish_spawn_egg",
        () -> new SmallFishSpawnEggItem(
                SmallFishVariant.CLOWNFISH, 0x5672FF, 0xFF8C00, new Item.Properties()));
    public static final DeferredItem<Item> GOLDFISH_SPAWN_EGG = ITEMS.register("goldfish_spawn_egg",
        () -> new SmallFishSpawnEggItem(
                SmallFishVariant.GOLDFISH, 0x5672FF, 0x00E5FF, new Item.Properties()));
    public static final DeferredItem<Item> HIPPOTANG_SPAWN_EGG = ITEMS.register("hippotang_spawn_egg",
        () -> new SmallFishSpawnEggItem(
                SmallFishVariant.HIPPOTANG, 0x5672FF, 0x2979FF, new Item.Properties()));
    public static final DeferredItem<Item> MANDARINFISH_SPAWN_EGG = ITEMS.register("mandarinfish_spawn_egg",
        () -> new SmallFishSpawnEggItem(
                SmallFishVariant.MANDARIN, 0x5672FF, 0xFF00FF, new Item.Properties()));
    public static final DeferredItem<Item> PIRANHA_SPAWN_EGG = ITEMS.register("piranha_spawn_egg",
        () -> new SmallFishSpawnEggItem(
                SmallFishVariant.PIRANHA, 0x5672FF, 0x6D4C41, new Item.Properties()));
    public static final DeferredItem<Item> JELLYFISH_SPAWN_EGG = spawnEgg("jellyfish_spawn_egg", ModEntities.MOC_JELLYFISH, 0xC2ADBD, 0x906CBD);
    public static final DeferredItem<Item> CRAB_SPAWN_EGG = spawnEgg("crab_spawn_egg", ModEntities.MOC_CRAB, 0xB55B12, 0xEC97A5);
    public static final DeferredItem<Item> CROCODILE_SPAWN_EGG = spawnEgg("crocodile_spawn_egg", ModEntities.MOC_CROCODILE, 0x4A5D23, 0xD4C896);
    public static final DeferredItem<Item> GREEN_OGRE_SPAWN_EGG = spawnEgg("green_ogre_spawn_egg", ModEntities.MOC_GREEN_OGRE, 0x18874D, 0x1F0565);
    public static final DeferredItem<Item> FIRE_OGRE_SPAWN_EGG = spawnEgg("fire_ogre_spawn_egg", ModEntities.MOC_FIRE_OGRE, 0x690400, 0xFAB400);
    public static final DeferredItem<Item> CAVE_OGRE_SPAWN_EGG = spawnEgg("cave_ogre_spawn_egg", ModEntities.MOC_CAVE_OGRE, 0x4D81B8, 0xBFFAFF);
    public static final DeferredItem<Item> WEREWOLF_SPAWN_EGG = spawnEgg("werewolf_spawn_egg", ModEntities.MOC_WEREWOLF, 0x8B7355, 0x2F2F2F);
    public static final DeferredItem<Item> WILD_WOLF_SPAWN_EGG = spawnEgg("wild_wolf_spawn_egg", ModEntities.MOC_WILD_WOLF, 0x4A4A4A, 0x988718);
    public static final DeferredItem<Item> BOAR_SPAWN_EGG = spawnEgg("boar_spawn_egg", ModEntities.MOC_BOAR, 0x1F1817, 0x4C3B34);
    public static final DeferredItem<Item> DEER_SPAWN_EGG = spawnEgg("deer_spawn_egg", ModEntities.MOC_DEER, 0xB0966B, 0xD1D6D4);
    public static final DeferredItem<Item> RAT_SPAWN_EGG = spawnEgg("rat_spawn_egg", ModEntities.MOC_RAT, 0x383C3B, 0xF1ADA9);
    public static final DeferredItem<Item> HELL_RAT_SPAWN_EGG = spawnEgg("hellrat_spawn_egg", ModEntities.MOC_HELL_RAT, 0x4D4809, 0x9DFFC0);
    public static final DeferredItem<Item> MOUSE_SPAWN_EGG = spawnEgg("mouse_spawn_egg", ModEntities.MOC_MOUSE, 0x714704, 0xECAAAA);
    public static final DeferredItem<Item> MOLE_SPAWN_EGG = spawnEgg("mole_spawn_egg", ModEntities.MOC_MOLE, 0x040405, 0xA27E21);
    public static final DeferredItem<Item> DUCK_SPAWN_EGG = spawnEgg("duck_spawn_egg", ModEntities.MOC_DUCK, 0x303D09, 0xD5CCAD);
    public static final DeferredItem<Item> FLY_SPAWN_EGG = spawnEgg("fly_spawn_egg", ModEntities.MOC_FLY, 0x3A3F47, 0xA31F1F);
    public static final DeferredItem<Item> BUTTERFLY_SPAWN_EGG = spawnEgg("butterfly_spawn_egg", ModEntities.MOC_BUTTERFLY, 0xE8791C, 0x1E1410);
    public static final DeferredItem<Item> DRAGONFLY_SPAWN_EGG = spawnEgg("dragonfly_spawn_egg", ModEntities.MOC_DRAGONFLY, 0x1FA89A, 0x2A4FA0);
    public static final DeferredItem<Item> FIREFLY_SPAWN_EGG = spawnEgg("firefly_spawn_egg", ModEntities.MOC_FIREFLY, 0x2B2118, 0xD4E64A);
    public static final DeferredItem<Item> BEE_SPAWN_EGG = spawnEgg("bee_spawn_egg", ModEntities.MOC_BEE, 0xF0B31A, 0x2A1E0C);
    public static final DeferredItem<Item> SNAIL_SPAWN_EGG = spawnEgg("snail_spawn_egg", ModEntities.MOC_SNAIL, 0x8A5A30, 0xD8C8A0);
    public static final DeferredItem<Item> ROACH_SPAWN_EGG = spawnEgg("roach_spawn_egg", ModEntities.MOC_ROACH, 0x7A2E14, 0xC98A3F);
    public static final DeferredItem<Item> ANT_SPAWN_EGG = spawnEgg("ant_spawn_egg", ModEntities.MOC_ANT, 0x1C1C1C, 0x9A2B1A);
    public static final DeferredItem<Item> MAGGOT_SPAWN_EGG = spawnEgg("maggot_spawn_egg", ModEntities.MOC_MAGGOT, 0xE8E0B8, 0xB7A365);
    public static final DeferredItem<Item> CRICKET_SPAWN_EGG = spawnEgg("cricket_spawn_egg", ModEntities.MOC_CRICKET, 0x2A1B10, 0x86602F);
    public static final DeferredItem<Item> GRASSHOPPER_SPAWN_EGG = spawnEgg("grasshopper_spawn_egg", ModEntities.MOC_GRASSHOPPER, 0x7CB82F, 0x3F5A1B);
    public static final DeferredItem<Item> SILVER_SKELETON_SPAWN_EGG = spawnEgg("silverskeleton_spawn_egg", ModEntities.MOC_SILVER_SKELETON, 0xD3D3D3, 0xEEEEEE);
    public static final DeferredItem<Item> WRAITH_SPAWN_EGG = spawnEgg("wraith_spawn_egg", ModEntities.MOC_WRAITH, 0x2A2F26, 0xB0B0B0);
    public static final DeferredItem<Item> FLAME_WRAITH_SPAWN_EGG = spawnEgg("flamewraith_spawn_egg", ModEntities.MOC_FLAME_WRAITH, 0x000000, 0xFF7E80);
    public static final DeferredItem<Item> ENT_SPAWN_EGG = spawnEgg("ent_spawn_egg", ModEntities.MOC_ENT, 0x957546, 0x58823D);
    public static final DeferredItem<Item> MINI_GOLEM_SPAWN_EGG = spawnEgg("minigolem_spawn_egg", ModEntities.MOC_MINI_GOLEM, 0x787878, 0x81E4E5);
    public static final DeferredItem<Item> BIG_GOLEM_SPAWN_EGG = spawnEgg("biggolem_spawn_egg", ModEntities.MOC_BIG_GOLEM, 0x4A4A4A, 0x00CCBB);
    public static final DeferredItem<Item> HEART_OF_UNDEAD = ITEMS.registerSimpleItem("heart_of_undead", new Item.Properties());
    public static final DeferredItem<Item> HEART_OF_FIRE = ITEMS.registerSimpleItem("heart_of_fire", new Item.Properties());
    public static final DeferredItem<Item> HEART_OF_DARKNESS = ITEMS.registerSimpleItem("heart_of_darkness", new Item.Properties());
    public static final DeferredItem<Item> ESSENCE_OF_UNDEAD = ITEMS.registerSimpleItem("essence_of_undead", new Item.Properties());
    public static final DeferredItem<Item> ESSENCE_OF_FIRE = ITEMS.registerSimpleItem("essence_of_fire", new Item.Properties());
    public static final DeferredItem<Item> ESSENCE_OF_DARKNESS = ITEMS.registerSimpleItem("essence_of_darkness", new Item.Properties());
    public static final DeferredItem<Item> ESSENCE_OF_LIGHT = ITEMS.registerSimpleItem("essence_of_light", new Item.Properties());

    // ---- Amulets ----
    public static final DeferredItem<Item> AMULET_BONE = ITEMS.registerSimpleItem("amulet_bone", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> AMULET_BONE_FULL = filledAmulet("amulet_bone_full", false, AMULET_BONE);
    public static final DeferredItem<Item> AMULET_FAIRY = ITEMS.registerSimpleItem("amulet_fairy", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> AMULET_FAIRY_FULL = filledAmulet("amulet_fairy_full", true, AMULET_FAIRY);
    public static final DeferredItem<Item> AMULET_GHOST = ITEMS.registerSimpleItem("amulet_ghost", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> AMULET_GHOST_FULL = filledAmulet("amulet_ghost_full", false, AMULET_GHOST);
    public static final DeferredItem<Item> AMULET_PEGASUS = ITEMS.registerSimpleItem("amulet_pegasus", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> AMULET_PEGASUS_FULL = filledAmulet("amulet_pegasus_full", false, AMULET_PEGASUS);
    public static final DeferredItem<Item> PET_AMULET = ITEMS.registerSimpleItem("pet_amulet", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> PET_AMULET_FULL = filledAmulet("pet_amulet_full", false, PET_AMULET);
    // ---- Ancient Silver ----
    public static final DeferredItem<ArmorItem> ANCIENT_SILVER_HELMET = armor("ancient_silver_helmet", ModArmorMaterials.SILVER, ArmorItem.Type.HELMET, 77);
    public static final DeferredItem<ArmorItem> ANCIENT_SILVER_CHESTPLATE = armor("ancient_silver_chestplate", ModArmorMaterials.SILVER, ArmorItem.Type.CHESTPLATE, 112);
    public static final DeferredItem<ArmorItem> ANCIENT_SILVER_LEGGINGS = armor("ancient_silver_leggings", ModArmorMaterials.SILVER, ArmorItem.Type.LEGGINGS, 105);
    public static final DeferredItem<ArmorItem> ANCIENT_SILVER_BOOTS = armor("ancient_silver_boots", ModArmorMaterials.SILVER, ArmorItem.Type.BOOTS, 91);
    public static final DeferredItem<Item> ANCIENT_SILVER_INGOT = ITEMS.registerSimpleItem("ancient_silver_ingot", new Item.Properties());
    public static final DeferredItem<Item> ANCIENT_SILVER_NUGGET = ITEMS.registerSimpleItem("ancient_silver_nugget", new Item.Properties());
    public static final DeferredItem<Item> ANCIENT_SILVER_SCRAP = ITEMS.registerSimpleItem("ancient_silver_scrap", new Item.Properties());

    // ---- Fur / Hide / Reptile Armor ----
    public static final DeferredItem<Item> FUR = ITEMS.registerSimpleItem("fur", new Item.Properties());
    public static final DeferredItem<ArmorItem> FUR_HELMET = armor("fur_helmet", ModArmorMaterials.FUR, ArmorItem.Type.HELMET, 165);
    public static final DeferredItem<ArmorItem> FUR_CHEST = armor("fur_chest", ModArmorMaterials.FUR, ArmorItem.Type.CHESTPLATE, 240);
    public static final DeferredItem<ArmorItem> FUR_LEGS = armor("fur_legs", ModArmorMaterials.FUR, ArmorItem.Type.LEGGINGS, 225);
    public static final DeferredItem<ArmorItem> FUR_BOOTS = armor("fur_boots", ModArmorMaterials.FUR, ArmorItem.Type.BOOTS, 195);
    public static final DeferredItem<Item> HIDE = ITEMS.registerSimpleItem("hide", new Item.Properties());
    public static final DeferredItem<ArmorItem> HIDE_HELMET = armor("hide_helmet", ModArmorMaterials.HIDE, ArmorItem.Type.HELMET, 165);
    public static final DeferredItem<ArmorItem> HIDE_CHEST = armor("hide_chest", ModArmorMaterials.HIDE, ArmorItem.Type.CHESTPLATE, 240);
    public static final DeferredItem<ArmorItem> HIDE_LEGS = armor("hide_legs", ModArmorMaterials.HIDE, ArmorItem.Type.LEGGINGS, 225);
    public static final DeferredItem<ArmorItem> HIDE_BOOTS = armor("hide_boots", ModArmorMaterials.HIDE, ArmorItem.Type.BOOTS, 195);
    public static final DeferredItem<Item> REPTILE_HIDE = ITEMS.registerSimpleItem("reptile_hide", new Item.Properties());
    public static final DeferredItem<ArmorItem> REPTILE_HELMET = armor("reptile_helmet", ModArmorMaterials.REPTILE, ArmorItem.Type.HELMET, 165);
    public static final DeferredItem<ArmorItem> REPTILE_PLATE = armor("reptile_plate", ModArmorMaterials.REPTILE, ArmorItem.Type.CHESTPLATE, 240);
    public static final DeferredItem<ArmorItem> REPTILE_LEGS = armor("reptile_legs", ModArmorMaterials.REPTILE, ArmorItem.Type.LEGGINGS, 225);
    public static final DeferredItem<ArmorItem> REPTILE_BOOTS = armor("reptile_boots", ModArmorMaterials.REPTILE, ArmorItem.Type.BOOTS, 195);

    // ---- Scorpion Gear (5 biome variants) ----
    public static final DeferredItem<Item> SCORP_AXE_CAVE = scorpionAxe("scorp_axe_cave", MobEffects.CONFUSION);
    public static final DeferredItem<Item> SCORP_AXE_DIRT = scorpionAxe("scorp_axe_dirt", MobEffects.POISON);
    public static final DeferredItem<Item> SCORP_AXE_NETHER = fireScorpionAxe("scorp_axe_nether", 4);
    public static final DeferredItem<Item> SCORP_AXE_FROST = scorpionAxe("scorp_axe_frost", MobEffects.MOVEMENT_SLOWDOWN);
    public static final DeferredItem<Item> SCORP_AXE_UNDEAD = scorpionAxe("scorp_axe_undead", MobEffects.WEAKNESS);
    public static final DeferredItem<ArmorItem> SCORP_HELMET_CAVE = armor("scorp_helmet_cave", ModArmorMaterials.SCORP_CAVE, ArmorItem.Type.HELMET, 166);
    public static final DeferredItem<ArmorItem> SCORP_PLATE_CAVE = armor("scorp_plate_cave", ModArmorMaterials.SCORP_CAVE, ArmorItem.Type.CHESTPLATE, 241);
    public static final DeferredItem<ArmorItem> SCORP_LEGS_CAVE = armor("scorp_legs_cave", ModArmorMaterials.SCORP_CAVE, ArmorItem.Type.LEGGINGS, 226);
    public static final DeferredItem<ArmorItem> SCORP_BOOTS_CAVE = armor("scorp_boots_cave", ModArmorMaterials.SCORP_CAVE, ArmorItem.Type.BOOTS, 196);
    public static final DeferredItem<ArmorItem> SCORP_HELMET_DIRT = armor("scorp_helmet_dirt", ModArmorMaterials.SCORP_DIRT, ArmorItem.Type.HELMET, 166);
    public static final DeferredItem<ArmorItem> SCORP_PLATE_DIRT = armor("scorp_plate_dirt", ModArmorMaterials.SCORP_DIRT, ArmorItem.Type.CHESTPLATE, 241);
    public static final DeferredItem<ArmorItem> SCORP_LEGS_DIRT = armor("scorp_legs_dirt", ModArmorMaterials.SCORP_DIRT, ArmorItem.Type.LEGGINGS, 226);
    public static final DeferredItem<ArmorItem> SCORP_BOOTS_DIRT = armor("scorp_boots_dirt", ModArmorMaterials.SCORP_DIRT, ArmorItem.Type.BOOTS, 196);
    public static final DeferredItem<ArmorItem> SCORP_HELMET_NETHER = armor("scorp_helmet_nether", ModArmorMaterials.SCORP_NETHER, ArmorItem.Type.HELMET, 166);
    public static final DeferredItem<ArmorItem> SCORP_PLATE_NETHER = armor("scorp_plate_nether", ModArmorMaterials.SCORP_NETHER, ArmorItem.Type.CHESTPLATE, 241);
    public static final DeferredItem<ArmorItem> SCORP_LEGS_NETHER = armor("scorp_legs_nether", ModArmorMaterials.SCORP_NETHER, ArmorItem.Type.LEGGINGS, 226);
    public static final DeferredItem<ArmorItem> SCORP_BOOTS_NETHER = armor("scorp_boots_nether", ModArmorMaterials.SCORP_NETHER, ArmorItem.Type.BOOTS, 196);
    public static final DeferredItem<ArmorItem> SCORP_HELMET_FROST = armor("scorp_helmet_frost", ModArmorMaterials.SCORP_FROST, ArmorItem.Type.HELMET, 166);
    public static final DeferredItem<ArmorItem> SCORP_PLATE_FROST = armor("scorp_plate_frost", ModArmorMaterials.SCORP_FROST, ArmorItem.Type.CHESTPLATE, 241);
    public static final DeferredItem<ArmorItem> SCORP_LEGS_FROST = armor("scorp_legs_frost", ModArmorMaterials.SCORP_FROST, ArmorItem.Type.LEGGINGS, 226);
    public static final DeferredItem<ArmorItem> SCORP_BOOTS_FROST = armor("scorp_boots_frost", ModArmorMaterials.SCORP_FROST, ArmorItem.Type.BOOTS, 196);
    public static final DeferredItem<ArmorItem> SCORP_HELMET_UNDEAD = armor("scorp_helmet_undead", ModArmorMaterials.SCORP_UNDEAD, ArmorItem.Type.HELMET, 166);
    public static final DeferredItem<ArmorItem> SCORP_PLATE_UNDEAD = armor("scorp_plate_undead", ModArmorMaterials.SCORP_UNDEAD, ArmorItem.Type.CHESTPLATE, 241);
    public static final DeferredItem<ArmorItem> SCORP_LEGS_UNDEAD = armor("scorp_legs_undead", ModArmorMaterials.SCORP_UNDEAD, ArmorItem.Type.LEGGINGS, 226);
    public static final DeferredItem<ArmorItem> SCORP_BOOTS_UNDEAD = armor("scorp_boots_undead", ModArmorMaterials.SCORP_UNDEAD, ArmorItem.Type.BOOTS, 196);
    public static final DeferredItem<Item> SCORP_STING_CAVE = ITEMS.registerSimpleItem("scorp_sting_cave", new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> SCORP_STING_DIRT = ITEMS.registerSimpleItem("scorp_sting_dirt", new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> SCORP_STING_FROST = ITEMS.registerSimpleItem("scorp_sting_frost", new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> SCORP_STING_NETHER = ITEMS.registerSimpleItem("scorp_sting_nether", new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> SCORP_STING_UNDEAD = ITEMS.registerSimpleItem("scorp_sting_undead", new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> SCORP_SWORD_CAVE = scorpionSword("scorp_sword_cave", MobEffects.CONFUSION);
    public static final DeferredItem<Item> SCORP_SWORD_DIRT = scorpionSword("scorp_sword_dirt", MobEffects.POISON);
    public static final DeferredItem<Item> SCORP_SWORD_NETHER = fireScorpionSword("scorp_sword_nether", 4);
    public static final DeferredItem<Item> SCORP_SWORD_FROST = scorpionSword("scorp_sword_frost", MobEffects.MOVEMENT_SLOWDOWN);
    public static final DeferredItem<Item> SCORP_SWORD_UNDEAD = scorpionSword("scorp_sword_undead", MobEffects.WEAKNESS);

    // ---- Weapons ----
    public static final DeferredItem<Item> BIG_CAT_CLAW = ITEMS.registerSimpleItem("big_cat_claw", new Item.Properties());
    public static final DeferredItem<Item> BO = sword("bo", Tiers.IRON);
    public static final DeferredItem<Item> KATANA = sword("katana", Tiers.IRON);
    public static final DeferredItem<Item> NUNCHAKU = sword("nunchaku", Tiers.IRON);
    public static final DeferredItem<Item> SAI = sword("sai", Tiers.IRON);
    public static final DeferredItem<Item> SHARK_AXE = axe("shark_axe", Tiers.IRON, 6.0F, -3.1F);
    public static final DeferredItem<Item> SHARK_SWORD = sword("shark_sword", Tiers.IRON);
    public static final DeferredItem<Item> SHARK_TEETH = ITEMS.registerSimpleItem("shark_teeth", new Item.Properties());
    public static final DeferredItem<Item> SILVER_AXE = axe("silver_axe", Tiers.GOLD, 6.0F, -3.0F);
    public static final DeferredItem<Item> SILVER_SWORD = sword("silver_sword", Tiers.GOLD);
    public static final DeferredItem<Item> TUSKS_DIAMOND = ITEMS.registerSimpleItem("tusks_diamond", new Item.Properties().stacksTo(1).durability(1562));
    public static final DeferredItem<Item> TUSKS_IRON = ITEMS.registerSimpleItem("tusks_iron", new Item.Properties().stacksTo(1).durability(251));
    public static final DeferredItem<Item> TUSKS_WOOD = ITEMS.registerSimpleItem("tusks_wood", new Item.Properties().stacksTo(1).durability(60));

    // ---- Foods ----
    public static final DeferredItem<Item> CRAB_COOKED = food("crab_cooked", 8, 0.6F);
    public static final DeferredItem<Item> CRAB_RAW = food("crab_raw", 2, 0.3F);
    public static final DeferredItem<Item> DUCK_COOKED = food("duck_cooked", 6, 0.6F);
    public static final DeferredItem<Item> DUCK_RAW = rawMeat("duck_raw", 2);
    public static final DeferredItem<Item> OMELET = food("omelet", 4, 0.6F);
    public static final DeferredItem<Item> OSTRICH_COOKED = food("ostrich_cooked", 6, 0.6F);
    public static final DeferredItem<Item> OSTRICH_RAW = rawMeat("ostrich_raw", 2);
    public static final DeferredItem<Item> RAT_BURGER = food("rat_burger", 8, 0.6F);
    public static final DeferredItem<Item> RAT_COOKED = food("rat_cooked", 4, 0.6F);
    public static final DeferredItem<Item> RAT_RAW = rawMeat("rat_raw", 2);
    public static final DeferredItem<Item> TURKEY_COOKED = food("turkey_cooked", 8, 0.6F);
    public static final DeferredItem<Item> TURKEY_RAW = rawMeat("turkey_raw", 3);
    public static final DeferredItem<Item> TURTLE_COOKED = food("turtle_cooked", 4, 0.6F);
    public static final DeferredItem<Item> TURTLE_RAW = rawMeat("turtle_raw", 2);

    // When eaten, returns the empty bowl — same mechanism as vanilla's mushroom stew.
    public static final DeferredItem<Item> TURTLE_SOUP = ITEMS.register("turtle_soup",
    () -> new BowlFoodItem(new Item.Properties()
        .food(new FoodProperties.Builder().nutrition(5).saturationModifier(0.6F).build())
        .stacksTo(1)));

    public static final DeferredItem<Item> VENISON_COOKED = food("venison_cooked", 8, 0.6F);
    public static final DeferredItem<Item> VENISON_RAW = food("venison_raw", 3, 0.3F);


    // ---- Horse-related ----
    public static final DeferredItem<Item> HORSE_ARMOR_CRYSTAL = ITEMS.register("horse_armor_crystal",
        () -> new Item(new Item.Properties().stacksTo(1)
            .attributes(ItemAttributeModifiers.builder()
                .add(Attributes.ARMOR,
                     new AttributeModifier(
                         ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "horse_armor_crystal"),
                         19.0D,
                         AttributeModifier.Operation.ADD_VALUE),
                     EquipmentSlotGroup.BODY)
                .build())));
    public static final DeferredItem<Item> HORSE_SADDLE = ITEMS.register("horse_saddle",
        () -> new SaddleItem(new Item.Properties().stacksTo(16)));
    public static final DeferredItem<Item> UNICORN_HORN = ITEMS.registerSimpleItem("unicorn_horn", new Item.Properties());
    public static final DeferredItem<Item> WHIP = ITEMS.register("whip",
        () -> new WhipItem(new Item.Properties().stacksTo(1)));
    // ---- Elephant / Mammoth ----
    public static final DeferredItem<Item> ELEPHANT_CHEST = ITEMS.registerSimpleItem("elephant_chest", new Item.Properties());
    public static final DeferredItem<Item> ELEPHANT_GARMENT = ITEMS.registerSimpleItem("elephant_garment", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ELEPHANT_HARNESS = ITEMS.registerSimpleItem("elephant_harness", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ELEPHANT_HOWDAH = ITEMS.registerSimpleItem("elephant_howdah", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> MAMMOTH_PLATFORM = ITEMS.registerSimpleItem("mammoth_platform", new Item.Properties().stacksTo(1));

    // ---- Misc ----
    public static final DeferredItem<Item> CHITIN = ITEMS.registerSimpleItem("chitin", new Item.Properties());
    public static final DeferredItem<Item> CHITIN_BLACK = ITEMS.registerSimpleItem("chitin_black", new Item.Properties());
    public static final DeferredItem<Item> CHITIN_FROST = ITEMS.registerSimpleItem("chitin_frost", new Item.Properties());
    public static final DeferredItem<Item> CHITIN_NETHER = ITEMS.registerSimpleItem("chitin_nether", new Item.Properties());
    public static final DeferredItem<Item> CHITIN_UNDEAD = ITEMS.registerSimpleItem("chitin_undead", new Item.Properties());
    public static final DeferredItem<Item> BUILDER_HAMMER = ITEMS.registerSimpleItem("builder_hammer", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> FIRESTONE_CHUNK = ITEMS.registerSimpleItem("firestone_chunk", new Item.Properties());
    public static final DeferredItem<Item> FISH_NET = ITEMS.register("fish_net",
        () -> new FishNetItem(new Item.Properties().stacksTo(16)));
    public static final DeferredItem<Item> FISH_NET_FULL = ITEMS.register("fish_net_full",
        () -> new FilledFishNetItem(new Item.Properties().stacksTo(1), FISH_NET.get()));
    public static final DeferredItem<Item> HAYSTACK = ITEMS.registerSimpleItem("haystack", new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> KEY = ITEMS.registerSimpleItem("key", new Item.Properties());
    public static final DeferredItem<Item> KITTY_BED = ITEMS.registerSimpleItem("kitty_bed", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> KITTY_BED_BLACK = kittyBed("kitty_bed_black", DyeColor.BLACK);
    public static final DeferredItem<Item> KITTY_BED_BLUE = kittyBed("kitty_bed_blue", DyeColor.BLUE);
    public static final DeferredItem<Item> KITTY_BED_BROWN = kittyBed("kitty_bed_brown", DyeColor.BROWN);
    public static final DeferredItem<Item> KITTY_BED_CYAN = kittyBed("kitty_bed_cyan", DyeColor.CYAN);
    public static final DeferredItem<Item> KITTY_BED_GRAY = kittyBed("kitty_bed_gray", DyeColor.GRAY);
    public static final DeferredItem<Item> KITTY_BED_GREEN = kittyBed("kitty_bed_green", DyeColor.GREEN);
    public static final DeferredItem<Item> KITTY_BED_LIGHT_BLUE = kittyBed("kitty_bed_light_blue", DyeColor.LIGHT_BLUE);
    public static final DeferredItem<Item> KITTY_BED_LIME = kittyBed("kitty_bed_lime", DyeColor.LIME);
    public static final DeferredItem<Item> KITTY_BED_MAGENTA = kittyBed("kitty_bed_magenta", DyeColor.MAGENTA);
    public static final DeferredItem<Item> KITTY_BED_ORANGE = kittyBed("kitty_bed_orange", DyeColor.ORANGE);
    public static final DeferredItem<Item> KITTY_BED_PINK = kittyBed("kitty_bed_pink", DyeColor.PINK);
    public static final DeferredItem<Item> KITTY_BED_PURPLE = kittyBed("kitty_bed_purple", DyeColor.PURPLE);
    public static final DeferredItem<Item> KITTY_BED_RED = kittyBed("kitty_bed_red", DyeColor.RED);
    public static final DeferredItem<Item> KITTY_BED_SILVER = kittyBed("kitty_bed_silver", DyeColor.LIGHT_GRAY);
    public static final DeferredItem<Item> KITTY_BED_WHITE = kittyBed("kitty_bed_white", DyeColor.WHITE);
    public static final DeferredItem<Item> KITTY_BED_YELLOW = kittyBed("kitty_bed_yellow", DyeColor.YELLOW);
    public static final DeferredItem<Item> KITTY_LITTER = ITEMS.register("kitty_litter",
                () -> new LitterBoxItem(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> MEDALLION = ITEMS.registerSimpleItem("medallion", new Item.Properties());
    // Places a MoCEggEntity when used on a block — see MoCEggItem.
    public static final DeferredItem<Item> MOC_EGG = ITEMS.register("moc_egg",
        () -> new MoCEggItem(new Item.Properties()));

    // One egg per naturally-occurring wyvern variant (the 8 wild biome
    // textures + the plain mother) — each only ever hatches its own species.
    // The 8 wild ones have a 30% chance of hatching tier 2 instead of tier 1;
    // the mother egg always hatches the bigger, tamed-only mother form.
    public static final DeferredItem<Item> JUNGLE_WYVERN_EGG = wyvernEgg("jungle_wyvern_egg", "JUNGLE");
    public static final DeferredItem<Item> SWAMP_WYVERN_EGG = wyvernEgg("swamp_wyvern_egg", "SWAMP");
    public static final DeferredItem<Item> SAND_WYVERN_EGG = wyvernEgg("sand_wyvern_egg", "SAND");
    public static final DeferredItem<Item> SUN_WYVERN_EGG = wyvernEgg("sun_wyvern_egg", "SUN");
    public static final DeferredItem<Item> ARCTIC_WYVERN_EGG = wyvernEgg("arctic_wyvern_egg", "ARCTIC");
    public static final DeferredItem<Item> CAVE_WYVERN_EGG = wyvernEgg("cave_wyvern_egg", "CAVE");
    public static final DeferredItem<Item> MOUNTAIN_WYVERN_EGG = wyvernEgg("mountain_wyvern_egg", "MOUNTAIN");
    public static final DeferredItem<Item> SEA_WYVERN_EGG = wyvernEgg("sea_wyvern_egg", "SEA");
    public static final DeferredItem<Item> MOTHER_WYVERN_EGG = egg("mother_wyvern_egg", MoCEggItem::new, ModEntities.WYVERN_MOTHER_TAMED, "MOTHER");
    public static final DeferredItem<Item> DIRT_SCORPION_EGG = egg("dirt_scorpion_egg", MoCEggItem::new, ModEntities.MOC_SCORPION, "DIRT");
    public static final DeferredItem<Item> CAVE_SCORPION_EGG = egg("cave_scorpion_egg", MoCEggItem::new, ModEntities.MOC_SCORPION, "CAVE");
    public static final DeferredItem<Item> FROST_SCORPION_EGG = egg("frost_scorpion_egg", MoCEggItem::new, ModEntities.MOC_SCORPION, "FROST");
    public static final DeferredItem<Item> FIRE_SCORPION_EGG = egg("fire_scorpion_egg", MoCEggItem::new, ModEntities.MOC_SCORPION, "NETHER");
    public static final DeferredItem<Item> UNDEAD_SCORPION_EGG = egg("undead_scorpion_egg", MoCEggItem::new, ModEntities.MOC_SCORPION, "UNDEAD");
    public static final DeferredItem<Item> PLAIN_MANTICORE_EGG = egg("plain_manticore_egg", MoCEggItem::new, ModEntities.MOC_MANTICORE, "PLAIN");
    public static final DeferredItem<Item> DARK_MANTICORE_EGG = egg("dark_manticore_egg", MoCEggItem::new, ModEntities.MOC_MANTICORE, "DARK");
    public static final DeferredItem<Item> FROST_MANTICORE_EGG = egg("frost_manticore_egg", MoCEggItem::new, ModEntities.MOC_MANTICORE, "FROST");
    public static final DeferredItem<Item> FIRE_MANTICORE_EGG = egg("fire_manticore_egg", MoCEggItem::new, ModEntities.MOC_MANTICORE, "FIRE");
    public static final DeferredItem<Item> TOXIC_MANTICORE_EGG = egg("toxic_manticore_egg", MoCEggItem::new, ModEntities.MOC_MANTICORE, "TOXIC");
    public static final DeferredItem<Item> OSTRICH_EGG = egg("ostrich_egg", MoCEggItem::new, ModEntities.MOC_OSTRICH, null);
    public static final DeferredItem<Item> KOMODO_DRAGON_EGG = egg("komodo_dragon_egg", MoCEggItem::new, ModEntities.MOC_KOMODO_DRAGON, null);
    public static final DeferredItem<Item> SNAKE_EGG_GREEN_DARK = egg("snake_egg_green_dark", MoCEggItem::new, ModEntities.MOC_SNAKE, "GREEN_DARK");
    public static final DeferredItem<Item> SNAKE_EGG_WOLF = egg("snake_egg_wolf", MoCEggItem::new, ModEntities.MOC_SNAKE, "WOLF");
    public static final DeferredItem<Item> SNAKE_EGG_ORANGE = egg("snake_egg_orange", MoCEggItem::new, ModEntities.MOC_SNAKE, "ORANGE");
    public static final DeferredItem<Item> SNAKE_EGG_GREEN_BRIGHT = egg("snake_egg_green_bright", MoCEggItem::new, ModEntities.MOC_SNAKE, "GREEN_BRIGHT");
    public static final DeferredItem<Item> SNAKE_EGG_CORAL = egg("snake_egg_coral", MoCEggItem::new, ModEntities.MOC_SNAKE, "CORAL");
    public static final DeferredItem<Item> SNAKE_EGG_COBRA = egg("snake_egg_cobra", MoCEggItem::new, ModEntities.MOC_SNAKE, "COBRA");
    public static final DeferredItem<Item> SNAKE_EGG_RATTLE = egg("snake_egg_rattle", MoCEggItem::new, ModEntities.MOC_SNAKE, "RATTLE");
    public static final DeferredItem<Item> SNAKE_EGG_PYTHON = egg("snake_egg_python", MoCEggItem::new, ModEntities.MOC_SNAKE, "PYTHON");
    public static final DeferredItem<Item> SHARK_EGG = egg("shark_egg", SharkEggItem::new, ModEntities.MOC_SHARK, null);
    public static final DeferredItem<Item> COD_EGG = egg("cod_egg", MediumFishEggItem::new, ModEntities.MOC_COD, null);
    public static final DeferredItem<Item> SALMON_EGG = egg("salmon_egg", MediumFishEggItem::new, ModEntities.MOC_SALMON, null);
    public static final DeferredItem<Item> BASS_EGG = egg("bass_egg", MediumFishEggItem::new, ModEntities.MOC_BASS, null);
    public static final DeferredItem<Item> ANCHOVY_EGG = egg("anchovy_egg", SmallFishEggItem::new, ModEntities.MOC_SMALL_FISH, "ANCHOVY");
    public static final DeferredItem<Item> ANGELFISH_EGG = egg("angelfish_egg", SmallFishEggItem::new, ModEntities.MOC_SMALL_FISH, "ANGELFISH");
    public static final DeferredItem<Item> ANGLERFISH_EGG = egg("anglerfish_egg", SmallFishEggItem::new, ModEntities.MOC_SMALL_FISH, "ANGLER");
    public static final DeferredItem<Item> CLOWNFISH_EGG = egg("clownfish_egg", SmallFishEggItem::new, ModEntities.MOC_SMALL_FISH, "CLOWNFISH");
    public static final DeferredItem<Item> GOLDFISH_EGG = egg("goldfish_egg", SmallFishEggItem::new, ModEntities.MOC_SMALL_FISH, "GOLDFISH");
    public static final DeferredItem<Item> HIPPOTANG_EGG = egg("hippotang_egg", SmallFishEggItem::new, ModEntities.MOC_SMALL_FISH, "HIPPOTANG");
    public static final DeferredItem<Item> MANDARINFISH_EGG = egg("mandarinfish_egg", SmallFishEggItem::new, ModEntities.MOC_SMALL_FISH, "MANDARIN");
    public static final DeferredItem<Item> PIRANHA_EGG = egg("piranha_egg", SmallFishEggItem::new, ModEntities.MOC_SMALL_FISH, "PIRANHA");
    public static final DeferredItem<Item> MYSTIC_PEAR = ITEMS.registerSimpleItem("mystic_pear", new Item.Properties());
    public static final DeferredItem<Item> NETHER_CANNON = ITEMS.registerSimpleItem("nether_cannon", new Item.Properties());
    public static final DeferredItem<Item> PET_FOOD = ITEMS.registerSimpleItem("pet_food", new Item.Properties());
    public static final ResourceKey<JukeboxSong> SHUFFLING_SONG = ResourceKey.create(Registries.JUKEBOX_SONG, ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "shuffling"));
    public static final DeferredItem<Item> RECORD_SHUFFLE = ITEMS.registerSimpleItem("record_shuffle", new Item.Properties().stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(SHUFFLING_SONG));
    public static final DeferredItem<Item> ROPE = ITEMS.registerSimpleItem("rope", new Item.Properties());
    public static final DeferredItem<Item> SCROLL_OF_FREEDOM = ITEMS.register("scroll_of_freedom",
            () -> new ScrollOfFreedomItem(new Item.Properties()));
    public static final DeferredItem<Item> SCROLL_OF_OWNER = ITEMS.register("scroll_of_owner",
            () -> new ScrollOfResetOwnerItem(new Item.Properties()));
    public static final DeferredItem<Item> SCROLL_OF_SALE = ITEMS.register("scroll_of_sale",
            () -> new ScrollOfSaleItem(new Item.Properties()));
    public static final DeferredItem<Item> STAFF = ITEMS.registerSimpleItem("staff", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> STAFF2 = ITEMS.registerSimpleItem("staff2", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> STAFF3 = ITEMS.registerSimpleItem("staff3", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> STAFF_PORTAL = ITEMS.register("staff_portal",
            () -> new StaffPortalItem(new Item.Properties().stacksTo(1).durability(4)));
    public static final DeferredItem<Item> STAFF_TELEPORT = ITEMS.registerSimpleItem("staff_teleport", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> SUGAR_LUMP = ITEMS.registerSimpleItem("sugar_lump", new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationModifier(0.6F).build()).stacksTo(32));
    public static final DeferredItem<Item> WOOL_BALL = ITEMS.registerSimpleItem("wool_ball", new Item.Properties());

    // ==== Wyvern block set — BlockItems ====
    public static final DeferredItem<BlockItem> WYVSTONE_ITEM = ITEMS.registerSimpleBlockItem("wyvstone", ModBlocks.WYVSTONE);
    public static final DeferredItem<BlockItem> COBBLED_WYVSTONE_ITEM = ITEMS.registerSimpleBlockItem("cobbled_wyvstone", ModBlocks.COBBLED_WYVSTONE);
    public static final DeferredItem<BlockItem> DEEP_WYVSTONE_ITEM = ITEMS.registerSimpleBlockItem("deep_wyvstone", ModBlocks.DEEP_WYVSTONE);
    public static final DeferredItem<BlockItem> COBBLED_DEEP_WYVSTONE_ITEM = ITEMS.registerSimpleBlockItem("cobbled_deep_wyvstone", ModBlocks.COBBLED_DEEP_WYVSTONE);
    public static final DeferredItem<BlockItem> MOSSY_COBBLED_WYVSTONE_ITEM = ITEMS.registerSimpleBlockItem("mossy_cobbled_wyvstone", ModBlocks.MOSSY_COBBLED_WYVSTONE);
    public static final DeferredItem<BlockItem> MOSSY_COBBLED_DEEP_WYVSTONE_ITEM = ITEMS.registerSimpleBlockItem("mossy_cobbled_deep_wyvstone", ModBlocks.MOSSY_COBBLED_DEEP_WYVSTONE);
    public static final DeferredItem<BlockItem> WYVDIRT_ITEM = ITEMS.registerSimpleBlockItem("wyvdirt", ModBlocks.WYVDIRT);
    public static final DeferredItem<BlockItem> WYVGRASS_ITEM = ITEMS.registerSimpleBlockItem("wyvgrass", ModBlocks.WYVGRASS);
    public static final DeferredItem<BlockItem> TALL_WYVGRASS_ITEM = ITEMS.registerSimpleBlockItem("tall_wyvgrass", ModBlocks.TALL_WYVGRASS);
    public static final DeferredItem<BlockItem> WYVWOOD_LOG_ITEM = ITEMS.registerSimpleBlockItem("wyvwood_log", ModBlocks.WYVWOOD_LOG);
    public static final DeferredItem<BlockItem> WYVWOOD_LEAVES_ITEM = ITEMS.registerSimpleBlockItem("wyvwood_leaves", ModBlocks.WYVWOOD_LEAVES);
    public static final DeferredItem<BlockItem> WYVWOOD_PLANKS_ITEM = ITEMS.registerSimpleBlockItem("wyvwood_planks", ModBlocks.WYVWOOD_PLANKS);
    public static final DeferredItem<BlockItem> WYVWOOD_SAPLING_ITEM = ITEMS.registerSimpleBlockItem("wyvwood_sapling", ModBlocks.WYVWOOD_SAPLING);
    public static final DeferredItem<BlockItem> WYVERN_DIAMOND_ORE_ITEM = ITEMS.registerSimpleBlockItem("wyvern_diamond_ore", ModBlocks.WYVERN_DIAMOND_ORE);
    public static final DeferredItem<BlockItem> WYVERN_EMERALD_ORE_ITEM = ITEMS.registerSimpleBlockItem("wyvern_emerald_ore", ModBlocks.WYVERN_EMERALD_ORE);
    public static final DeferredItem<BlockItem> WYVERN_LAPIS_ORE_ITEM = ITEMS.registerSimpleBlockItem("wyvern_lapis_ore", ModBlocks.WYVERN_LAPIS_ORE);
    public static final DeferredItem<BlockItem> WYVERN_GOLD_ORE_ITEM = ITEMS.registerSimpleBlockItem("wyvern_gold_ore", ModBlocks.WYVERN_GOLD_ORE);
    public static final DeferredItem<BlockItem> WYVERN_IRON_ORE_ITEM = ITEMS.registerSimpleBlockItem("wyvern_iron_ore", ModBlocks.WYVERN_IRON_ORE);
    public static final DeferredItem<BlockItem> WYVERN_ANCIENT_ORE_ITEM = ITEMS.registerSimpleBlockItem("wyvern_ancient_ore", ModBlocks.WYVERN_ANCIENT_ORE);
    
    // ==== Ogre Lair block set — BlockItems ====
    public static final DeferredItem<BlockItem> DIRT_OGRE_LAIR_ITEM = ITEMS.registerSimpleBlockItem("dirt_ogre_lair", ModBlocks.DIRT_OGRE_LAIR);
    public static final DeferredItem<BlockItem> GRASS_OGRE_LAIR_ITEM = ITEMS.registerSimpleBlockItem("grass_ogre_lair", ModBlocks.GRASS_OGRE_LAIR);
    public static final DeferredItem<BlockItem> TALL_GRASS_OGRE_LAIR_ITEM = ITEMS.registerSimpleBlockItem("tall_grass_ogre_lair", ModBlocks.TALL_GRASS_OGRE_LAIR);
    public static final DeferredItem<BlockItem> LOG_OGRE_LAIR_ITEM = ITEMS.registerSimpleBlockItem("log_ogre_lair", ModBlocks.LOG_OGRE_LAIR);
    public static final DeferredItem<BlockItem> LEAVES_OGRE_LAIR_ITEM = ITEMS.registerSimpleBlockItem("leaves_ogre_lair", ModBlocks.LEAVES_OGRE_LAIR);
    public static final DeferredItem<BlockItem> WOOD_PLANKS_OGRE_LAIR_ITEM = ITEMS.registerSimpleBlockItem("wood_planks_ogre_lair", ModBlocks.WOOD_PLANKS_OGRE_LAIR);
    public static final DeferredItem<BlockItem> ANCIENT_SILVER_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("ancient_silver_block", ModBlocks.ANCIENT_SILVER_BLOCK);
    public static final DeferredItem<BlockItem> SILVER_SAND_ITEM = ITEMS.registerSimpleBlockItem("silver_sand", ModBlocks.SILVER_SAND);
    public static final DeferredItem<BlockItem> SILVER_SANDSTONE_ITEM = ITEMS.registerSimpleBlockItem("silver_sandstone", ModBlocks.SILVER_SANDSTONE);
    public static final DeferredItem<BlockItem> SILVER_SANDSTONE_CARVED_ITEM = ITEMS.registerSimpleBlockItem("silver_sandstone_carved", ModBlocks.SILVER_SANDSTONE_CARVED);
    public static final DeferredItem<BlockItem> SILVER_SANDSTONE_SMOOTH_ITEM = ITEMS.registerSimpleBlockItem("silver_sandstone_smooth", ModBlocks.SILVER_SANDSTONE_SMOOTH);

    // ==== Loose pieces — BlockItems ====
    public static final DeferredItem<BlockItem> ORE_FIRESTONE_ITEM = ITEMS.registerSimpleBlockItem("ore_firestone", ModBlocks.ORE_FIRESTONE);
    public static final DeferredItem<BlockItem> GLASS_GLEAMING_ITEM = ITEMS.registerSimpleBlockItem("glass_gleaming", ModBlocks.GLASS_GLEAMING);
    public static final DeferredItem<BlockItem> BLOCK_WYVERN_NEST_ITEM = ITEMS.registerSimpleBlockItem("block_wyvern_nest", ModBlocks.BLOCK_WYVERN_NEST);
    

    // ---------------------------------------------------------------------
    // Registration helpers (each one keeps the exact properties used before)
    // ---------------------------------------------------------------------

    private static DeferredItem<Item> spawnEgg(String name, Supplier<? extends EntityType<? extends Mob>> type,
                                               int backgroundColor, int highlightColor) {
        return ITEMS.register(name,
                () -> new DeferredSpawnEggItem(type, backgroundColor, highlightColor, new Item.Properties()));
    }

    private static DeferredItem<ArmorItem> armor(String name, Holder<ArmorMaterial> material,
                                                 ArmorItem.Type slot, int durability) {
        return ITEMS.register(name, () -> new ArmorItem(material, slot, new Item.Properties().durability(durability)));
    }

    private static DeferredItem<Item> sword(String name, Tier tier) {
        return ITEMS.register(name, () -> new SwordItem(tier, swordProperties(tier)));
    }

    private static DeferredItem<Item> axe(String name, Tier tier, float attackDamage, float attackSpeed) {
        return ITEMS.register(name, () -> new AxeItem(tier, new Item.Properties().stacksTo(1)
                .attributes(AxeItem.createAttributes(tier, attackDamage, attackSpeed))
                .durability(tier.getUses())));
    }

    /** Diamond-tier scorpion sword that applies a potion effect on hit. */
    private static DeferredItem<Item> scorpionSword(String name, Holder<MobEffect> effect) {
        return ITEMS.register(name, () -> new ScorpionSwordItem(Tiers.DIAMOND, swordProperties(Tiers.DIAMOND),
                () -> new MobEffectInstance(effect, SCORPION_EFFECT_TICKS, 0)));
    }

    /** Diamond-tier scorpion sword that sets the target on fire. */
    private static DeferredItem<Item> fireScorpionSword(String name, int fireSeconds) {
        return ITEMS.register(name, () -> new ScorpionSwordItem(Tiers.DIAMOND, swordProperties(Tiers.DIAMOND), fireSeconds));
    }

    /** Diamond-tier scorpion axe that applies a potion effect on hit. */
    private static DeferredItem<Item> scorpionAxe(String name, Holder<MobEffect> effect) {
        return ITEMS.register(name, () -> new ScorpionAxeItem(Tiers.DIAMOND, scorpionAxeProperties(),
                () -> new MobEffectInstance(effect, SCORPION_EFFECT_TICKS, 0)));
    }

    /** Diamond-tier scorpion axe that sets the target on fire. */
    private static DeferredItem<Item> fireScorpionAxe(String name, int fireSeconds) {
        return ITEMS.register(name, () -> new ScorpionAxeItem(Tiers.DIAMOND, scorpionAxeProperties(), fireSeconds));
    }

    private static Item.Properties swordProperties(Tier tier) {
        return new Item.Properties().stacksTo(1)
                .attributes(SwordItem.createAttributes(tier, 3, -2.4F))
                .durability(tier.getUses());
    }

    private static Item.Properties scorpionAxeProperties() {
        return new Item.Properties().stacksTo(1)
                .attributes(AxeItem.createAttributes(Tiers.DIAMOND, 5.0F, -3.0F))
                .durability(Tiers.DIAMOND.getUses());
    }

    private static DeferredItem<Item> food(String name, int nutrition, float saturation) {
        return ITEMS.registerSimpleItem(name, new Item.Properties().food(new FoodProperties.Builder()
                .nutrition(nutrition).saturationModifier(saturation).build()).stacksTo(64));
    }

    /** Raw meat: low saturation and a 30% chance of Hunger, like vanilla raw chicken. */
    private static DeferredItem<Item> rawMeat(String name, int nutrition) {
        return ITEMS.registerSimpleItem(name, new Item.Properties().food(new FoodProperties.Builder()
                .nutrition(nutrition).saturationModifier(0.3F)
                .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 600, 0), 0.3F)
                .build()).stacksTo(64));
    }

    private static DeferredItem<Item> filledAmulet(String name, boolean fairyVariant, DeferredItem<Item> emptyAmulet) {
        return ITEMS.register(name,
                () -> new FilledAmuletItem(new Item.Properties().stacksTo(1), fairyVariant, emptyAmulet.get()));
    }

    private static DeferredItem<Item> kittyBed(String name, DyeColor color) {
        return ITEMS.register(name, () -> new KittyBedItem(color, new Item.Properties().stacksTo(1)));
    }

    /** Egg that always hatches the same creature, optionally with a fixed variant. */
    private static DeferredItem<Item> egg(String name, BiFunction<Item.Properties, MoCEggItem.HatchSpec, Item> factory,
                                          Supplier<? extends EntityType<?>> type, @Nullable String variantId) {
        return ITEMS.register(name, () -> factory.apply(new Item.Properties(),
                new MoCEggItem.HatchSpec(type, null, 0.0D, variantId)));
    }

    /** Wild wyvern egg: 30% chance to hatch the tier 2 form instead of tier 1. */
    private static DeferredItem<Item> wyvernEgg(String name, String variantId) {
        return ITEMS.register(name, () -> new MoCEggItem(new Item.Properties(),
                new MoCEggItem.HatchSpec(ModEntities.WYVERN, ModEntities.WYVERN_TIER2, 0.3D, variantId)));
    }
}
