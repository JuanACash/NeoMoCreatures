package com.example.neomocreatures.init;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.item.ScorpionSwordItem;
import com.example.neomocreatures.item.WildHorseSpawnEggItem;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;


public class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(NeoMoCreatures.MODID);

    public static final DeferredItem<Item> WILD_HORSE_SPAWN_EGG = ITEMS.register("wild_horse_spawn_egg",
            () -> new WildHorseSpawnEggItem(ModEntities.MOC_HORSE, new Item.Properties()));

    public static final DeferredItem<Item> MOC_HORSE_MOB_SPAWN_EGG = ITEMS.register("moc_horse_mob_spawn_egg",
        () -> new net.neoforged.neoforge.common.DeferredSpawnEggItem(ModEntities.MOC_HORSE_MOB, 0x1A1A2E, 0x5B2C6F, new Item.Properties()));
    public static final DeferredItem<Item> WYVERN_SPAWN_EGG = ITEMS.register("wyvern_spawn_egg",
        () -> new com.example.neomocreatures.item.WyvernSpawnEggItem(new Item.Properties()));
    public static final DeferredItem<Item> MOC_ELEPHANT_SPAWN_EGG = ITEMS.register("moc_elephant_spawn_egg",
        () -> new net.neoforged.neoforge.common.DeferredSpawnEggItem(ModEntities.MOC_ELEPHANT, 0xE8DCC8, 0x5C4A3A, new Item.Properties()));
    public static final DeferredItem<Item> LION_SPAWN_EGG = ITEMS.register("lion_spawn_egg",
        () -> new com.example.neomocreatures.item.BigCatSpawnEggItem(
                com.example.neomocreatures.entity.bigcat.BigCatVariant.SpawnFamily.LION, 0xC2A25C, 0x6B4A1F, new Item.Properties()));
    public static final DeferredItem<Item> TIGER_SPAWN_EGG = ITEMS.register("tiger_spawn_egg",
        () -> new com.example.neomocreatures.item.BigCatSpawnEggItem(
                com.example.neomocreatures.entity.bigcat.BigCatVariant.SpawnFamily.TIGER, 0xE0771C, 0x1A1A1A, new Item.Properties()));
    public static final DeferredItem<Item> LEOPARD_SPAWN_EGG = ITEMS.register("leopard_spawn_egg",
        () -> new com.example.neomocreatures.item.BigCatSpawnEggItem(
                com.example.neomocreatures.entity.bigcat.BigCatVariant.SpawnFamily.LEOPARD, 0xFFCE1B, 0x3A2A1A, new Item.Properties()));
    public static final DeferredItem<Item> PANTHER_SPAWN_EGG = ITEMS.register("panther_spawn_egg",
        () -> new com.example.neomocreatures.item.BigCatSpawnEggItem(
                com.example.neomocreatures.entity.bigcat.BigCatVariant.SpawnFamily.PANTHER, 0x1A1A1A, 0x2E2E2E, new Item.Properties()));
    public static final DeferredItem<Item> LIGER_SPAWN_EGG = ITEMS.register("liger_spawn_egg",
        () -> new com.example.neomocreatures.item.BigCatSpawnEggItem(
                com.example.neomocreatures.entity.bigcat.BigCatVariant.LIGER, 0xC97B2E, 0x5C4A3A, new Item.Properties()));
    public static final DeferredItem<Item> LIARD_SPAWN_EGG = ITEMS.register("liard_spawn_egg",
        () -> new com.example.neomocreatures.item.BigCatSpawnEggItem(
                com.example.neomocreatures.entity.bigcat.BigCatVariant.LIARD, 0xC2A25C, 0xFFCE1B, new Item.Properties()));
    public static final DeferredItem<Item> LEOGER_SPAWN_EGG = ITEMS.register("leoger_spawn_egg",
        () -> new com.example.neomocreatures.item.BigCatSpawnEggItem(
                com.example.neomocreatures.entity.bigcat.BigCatVariant.LEOGER, 0xFFCE1B, 0xC97B2E, new Item.Properties()));
    public static final DeferredItem<Item> LITHER_SPAWN_EGG = ITEMS.register("lither_spawn_egg",
        () -> new com.example.neomocreatures.item.BigCatSpawnEggItem(
                com.example.neomocreatures.entity.bigcat.BigCatVariant.LITHER, 0x1A1A1A, 0xC2A25C, new Item.Properties()));
    public static final DeferredItem<Item> PANTHARD_SPAWN_EGG = ITEMS.register("panthard_spawn_egg",
        () -> new com.example.neomocreatures.item.BigCatSpawnEggItem(
                com.example.neomocreatures.entity.bigcat.BigCatVariant.PANTHARD, 0x1A1A1A, 0xFFCE1B, new Item.Properties()));
    public static final DeferredItem<Item> PANTHGER_SPAWN_EGG = ITEMS.register("panthger_spawn_egg",
        () -> new com.example.neomocreatures.item.BigCatSpawnEggItem(
                com.example.neomocreatures.entity.bigcat.BigCatVariant.PANTHGER, 0x1A1A1A, 0xC97B2E, new Item.Properties()));
    public static final DeferredItem<Item> MANTICORE_SPAWN_EGG = ITEMS.register("manticore_spawn_egg",
        () -> new com.example.neomocreatures.item.ManticoreSpawnEggItem(0x000000, 0x4A7A3A, new Item.Properties()));
    public static final DeferredItem<Item> SCORPION_SPAWN_EGG = ITEMS.register("scorpion_spawn_egg",
        () -> new com.example.neomocreatures.item.ScorpionSpawnEggItem(0xC2914F, 0xFF0000, new Item.Properties()));
    public static final DeferredItem<Item> OSTRICH_SPAWN_EGG = ITEMS.register("ostrich_spawn_egg",
        () -> new com.example.neomocreatures.item.OstrichSpawnEggItem(0xEDE8D0, 0x90D5FF, new Item.Properties()));
    public static final DeferredItem<Item> BLACK_BEAR_SPAWN_EGG = ITEMS.register("black_bear_spawn_egg",
        () -> new com.example.neomocreatures.item.BearSpawnEggItem(
                com.example.neomocreatures.entity.bear.BearVariant.BLACK, 0x3A2E24, 0x1A1410, new Item.Properties()));
        public static final DeferredItem<Item> GRIZZLY_BEAR_SPAWN_EGG = ITEMS.register("grizzly_bear_spawn_egg",
        () -> new com.example.neomocreatures.item.BearSpawnEggItem(
                com.example.neomocreatures.entity.bear.BearVariant.GRIZZLY, 0x8B5A2B, 0xC89B6A, new Item.Properties()));
        public static final DeferredItem<Item> POLAR_BEAR_SPAWN_EGG = ITEMS.register("polar_bear_spawn_egg",
        () -> new com.example.neomocreatures.item.BearSpawnEggItem(
                com.example.neomocreatures.entity.bear.BearVariant.POLAR, 0xF0F5F7, 0xA9C6D8, new Item.Properties()));
        public static final DeferredItem<Item> PANDA_BEAR_SPAWN_EGG = ITEMS.register("panda_bear_spawn_egg",
        () -> new com.example.neomocreatures.item.BearSpawnEggItem(
                com.example.neomocreatures.entity.bear.BearVariant.PANDA, 0xF5F5F0, 0x1A1A1A, new Item.Properties()));
        public static final DeferredItem<Item> FOX_SPAWN_EGG = ITEMS.register("fox_spawn_egg",
        () -> new com.example.neomocreatures.item.FoxSpawnEggItem(0xC57726, 0xF2F2F2, new Item.Properties()));
        public static final DeferredItem<Item> RACCOON_SPAWN_EGG = ITEMS.register("raccoon_spawn_egg",
        () -> new net.neoforged.neoforge.common.DeferredSpawnEggItem(
                com.example.neomocreatures.init.ModEntities.MOC_RACCOON, 0x6E6E6E, 0x2B2B2B, new Item.Properties()));
        public static final DeferredItem<Item> TURKEY_SPAWN_EGG = ITEMS.register("turkey_spawn_egg",
        () -> new net.neoforged.neoforge.common.DeferredSpawnEggItem(
                com.example.neomocreatures.init.ModEntities.MOC_TURKEY, 0x92C5FC, 0xF98B7C, new Item.Properties()));
        public static final DeferredItem<Item> KOMODO_DRAGON_SPAWN_EGG = ITEMS.register("komodo_dragon_spawn_egg",
        () -> new net.neoforged.neoforge.common.DeferredSpawnEggItem(
                com.example.neomocreatures.init.ModEntities.MOC_KOMODO_DRAGON, 0x8B8000, 0x00008B, new Item.Properties()));
        public static final DeferredItem<Item> GOAT_SPAWN_EGG = ITEMS.register("goat_spawn_egg",
        () -> new net.neoforged.neoforge.common.DeferredSpawnEggItem(
                com.example.neomocreatures.init.ModEntities.MOC_GOAT, 0xA89A91, 0xFD3DB5, new Item.Properties()));
        public static final DeferredItem<Item> KITTY_SPAWN_EGG = ITEMS.register("kitty_spawn_egg",
        () -> new net.neoforged.neoforge.common.DeferredSpawnEggItem(
                com.example.neomocreatures.init.ModEntities.MOC_KITTY, 0xD8B48C, 0x8B5A2B, new Item.Properties()));
        public static final DeferredItem<Item> SNAKE_SPAWN_EGG = ITEMS.register("snake_spawn_egg",
        () -> new com.example.neomocreatures.item.SnakeSpawnEggItem(0x2E4B1E, 0xC9A227, new Item.Properties()));
        public static final DeferredItem<Item> BUNNY_SPAWN_EGG = ITEMS.register("bunny_spawn_egg",
        () -> new com.example.neomocreatures.item.BunnySpawnEggItem(0xFF66C4, 0xFFF066, new Item.Properties()));
        public static final DeferredItem<Item> BIRD_SPAWN_EGG = ITEMS.register("bird_spawn_egg",
        () -> new com.example.neomocreatures.item.BirdSpawnEggItem(0x4A90D9, 0x32CD32, new Item.Properties()));
        public static final DeferredItem<Item> SHARK_SPAWN_EGG = ITEMS.register("shark_spawn_egg",
        () -> new com.example.neomocreatures.item.SharkSpawnEggItem(0x5B6E7A, 0x2C4E6B, new Item.Properties()));
        public static final DeferredItem<Item> TURTLE_SPAWN_EGG = ITEMS.register("turtle_spawn_egg",
        () -> new net.neoforged.neoforge.common.DeferredSpawnEggItem(
                com.example.neomocreatures.init.ModEntities.MOC_TURTLE, 0x634315, 0xA0991B, new Item.Properties()));
        public static final DeferredItem<Item> STINGRAY_SPAWN_EGG = ITEMS.register("stingray_spawn_egg",
        () -> new net.neoforged.neoforge.common.DeferredSpawnEggItem(
                com.example.neomocreatures.init.ModEntities.MOC_STINGRAY, 0xADEBB3, 0xDFB945, new Item.Properties()));
        public static final DeferredItem<Item> DOLPHIN_SPAWN_EGG = ITEMS.register("dolphin_spawn_egg",
        () -> new net.neoforged.neoforge.common.DeferredSpawnEggItem(
                com.example.neomocreatures.init.ModEntities.MOC_DOLPHIN, 0x3E5984, 0xABAEC4, new Item.Properties()));
        public static final DeferredItem<Item> MANTA_RAY_SPAWN_EGG = ITEMS.register("manta_ray_spawn_egg",
        () -> new net.neoforged.neoforge.common.DeferredSpawnEggItem(
                com.example.neomocreatures.init.ModEntities.MOC_MANTA_RAY, 0x007FFF, 0xE2CA76, new Item.Properties()));
        public static final DeferredItem<Item> FISHY_SPAWN_EGG = ITEMS.register("fishy_spawn_egg",
        () -> new net.neoforged.neoforge.common.DeferredSpawnEggItem(
                com.example.neomocreatures.init.ModEntities.MOC_FISHY, 0x5672FF, 0x1F17B0, new Item.Properties()));
        public static final DeferredItem<Item> COD_FISH_SPAWN_EGG = ITEMS.register("cod_fish_spawn_egg",
        () -> new net.neoforged.neoforge.common.DeferredSpawnEggItem(
                com.example.neomocreatures.init.ModEntities.MOC_COD, 0x5672FF, 0xFFD700, new Item.Properties()));
        public static final DeferredItem<Item> SALMON_FISH_SPAWN_EGG = ITEMS.register("salmon_fish_spawn_egg",
        () -> new net.neoforged.neoforge.common.DeferredSpawnEggItem(
                com.example.neomocreatures.init.ModEntities.MOC_SALMON, 0x5672FF, 0xFF6EC7, new Item.Properties()));
        public static final DeferredItem<Item> BASS_FISH_SPAWN_EGG = ITEMS.register("bass_fish_spawn_egg",
        () -> new net.neoforged.neoforge.common.DeferredSpawnEggItem(
                com.example.neomocreatures.init.ModEntities.MOC_BASS, 0x5672FF, 0x39FF14, new Item.Properties()));
        public static final DeferredItem<Item> ANCHOVY_SPAWN_EGG = ITEMS.register("anchovy_spawn_egg",
        () -> new com.example.neomocreatures.item.SmallFishSpawnEggItem(
                com.example.neomocreatures.entity.smallfish.SmallFishVariant.ANCHOVY, 0x5672FF, 0xFF3B30, new Item.Properties()));
        public static final DeferredItem<Item> ANGELFISH_SPAWN_EGG = ITEMS.register("angelfish_spawn_egg",
        () -> new com.example.neomocreatures.item.SmallFishSpawnEggItem(
                com.example.neomocreatures.entity.smallfish.SmallFishVariant.ANGELFISH, 0x5672FF, 0x8E44AD, new Item.Properties()));
        public static final DeferredItem<Item> ANGLERFISH_SPAWN_EGG = ITEMS.register("anglerfish_spawn_egg",
        () -> new com.example.neomocreatures.item.SmallFishSpawnEggItem(
                com.example.neomocreatures.entity.smallfish.SmallFishVariant.ANGLER, 0x5672FF, 0x78909C, new Item.Properties()));
        public static final DeferredItem<Item> CLOWNFISH_SPAWN_EGG = ITEMS.register("clownfish_spawn_egg",
        () -> new com.example.neomocreatures.item.SmallFishSpawnEggItem(
                com.example.neomocreatures.entity.smallfish.SmallFishVariant.CLOWNFISH, 0x5672FF, 0xFF8C00, new Item.Properties()));
        public static final DeferredItem<Item> GOLDFISH_SPAWN_EGG = ITEMS.register("goldfish_spawn_egg",
        () -> new com.example.neomocreatures.item.SmallFishSpawnEggItem(
                com.example.neomocreatures.entity.smallfish.SmallFishVariant.GOLDFISH, 0x5672FF, 0x00E5FF, new Item.Properties()));
        public static final DeferredItem<Item> HIPPOTANG_SPAWN_EGG = ITEMS.register("hippotang_spawn_egg",
        () -> new com.example.neomocreatures.item.SmallFishSpawnEggItem(
                com.example.neomocreatures.entity.smallfish.SmallFishVariant.HIPPOTANG, 0x5672FF, 0x2979FF, new Item.Properties()));
        public static final DeferredItem<Item> MANDARINFISH_SPAWN_EGG = ITEMS.register("mandarinfish_spawn_egg",
        () -> new com.example.neomocreatures.item.SmallFishSpawnEggItem(
                com.example.neomocreatures.entity.smallfish.SmallFishVariant.MANDARIN, 0x5672FF, 0xFF00FF, new Item.Properties()));
        public static final DeferredItem<Item> PIRANHA_SPAWN_EGG = ITEMS.register("piranha_spawn_egg",
        () -> new com.example.neomocreatures.item.SmallFishSpawnEggItem(
                com.example.neomocreatures.entity.smallfish.SmallFishVariant.PIRANHA, 0x5672FF, 0x6D4C41, new Item.Properties()));
        public static final DeferredItem<Item> JELLYFISH_SPAWN_EGG = ITEMS.register("jellyfish_spawn_egg",
        () -> new net.neoforged.neoforge.common.DeferredSpawnEggItem(
                com.example.neomocreatures.init.ModEntities.MOC_JELLYFISH, 0xC2ADBD, 0x906CBD, new Item.Properties()));

    public static final DeferredItem<Item> HEART_OF_UNDEAD = ITEMS.registerSimpleItem("heart_of_undead", new Item.Properties());
    public static final DeferredItem<Item> HEART_OF_FIRE = ITEMS.registerSimpleItem("heart_of_fire", new Item.Properties());
    public static final DeferredItem<Item> HEART_OF_DARKNESS = ITEMS.registerSimpleItem("heart_of_darkness", new Item.Properties());
    public static final DeferredItem<Item> ESSENCE_OF_UNDEAD = ITEMS.registerSimpleItem("essence_of_undead", new Item.Properties());
    public static final DeferredItem<Item> ESSENCE_OF_FIRE = ITEMS.registerSimpleItem("essence_of_fire", new Item.Properties());
    public static final DeferredItem<Item> ESSENCE_OF_DARKNESS = ITEMS.registerSimpleItem("essence_of_darkness", new Item.Properties());
    public static final DeferredItem<Item> ESSENCE_OF_LIGHT = ITEMS.registerSimpleItem("essence_of_light", new Item.Properties());

    // ---- Amulets ----
    public static final DeferredItem<Item> AMULET_BONE = ITEMS.registerSimpleItem("amulet_bone", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> AMULET_BONE_FULL = ITEMS.register("amulet_bone_full",
            () -> new com.example.neomocreatures.item.FilledAmuletItem(new Item.Properties().stacksTo(1), false, AMULET_BONE.get()));
    public static final DeferredItem<Item> AMULET_FAIRY = ITEMS.registerSimpleItem("amulet_fairy", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> AMULET_FAIRY_FULL = ITEMS.register("amulet_fairy_full",
            () -> new com.example.neomocreatures.item.FilledAmuletItem(new Item.Properties().stacksTo(1), true, AMULET_FAIRY.get()));
    public static final DeferredItem<Item> AMULET_GHOST = ITEMS.registerSimpleItem("amulet_ghost", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> AMULET_GHOST_FULL = ITEMS.register("amulet_ghost_full",
            () -> new com.example.neomocreatures.item.FilledAmuletItem(new Item.Properties().stacksTo(1), false, AMULET_GHOST.get()));
    public static final DeferredItem<Item> AMULET_PEGASUS = ITEMS.registerSimpleItem("amulet_pegasus", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> AMULET_PEGASUS_FULL = ITEMS.register("amulet_pegasus_full",
            () -> new com.example.neomocreatures.item.FilledAmuletItem(new Item.Properties().stacksTo(1), false, AMULET_PEGASUS.get()));
    public static final DeferredItem<Item> PET_AMULET = ITEMS.registerSimpleItem("pet_amulet", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> PET_AMULET_FULL = ITEMS.register("pet_amulet_full",
            () -> new com.example.neomocreatures.item.FilledAmuletItem(new Item.Properties().stacksTo(1), false, PET_AMULET.get()));
    // ---- Ancient Silver ----
    public static final DeferredItem<ArmorItem> ANCIENT_SILVER_HELMET = ITEMS.register("ancient_silver_helmet",
            () -> new ArmorItem(ModArmorMaterials.SILVER, ArmorItem.Type.HELMET, new Item.Properties().durability(77)));
    public static final DeferredItem<ArmorItem> ANCIENT_SILVER_CHESTPLATE = ITEMS.register("ancient_silver_chestplate",
            () -> new ArmorItem(ModArmorMaterials.SILVER, ArmorItem.Type.CHESTPLATE, new Item.Properties().durability(112)));
    public static final DeferredItem<ArmorItem> ANCIENT_SILVER_LEGGINGS = ITEMS.register("ancient_silver_leggings",
            () -> new ArmorItem(ModArmorMaterials.SILVER, ArmorItem.Type.LEGGINGS, new Item.Properties().durability(105)));
    public static final DeferredItem<ArmorItem> ANCIENT_SILVER_BOOTS = ITEMS.register("ancient_silver_boots",
            () -> new ArmorItem(ModArmorMaterials.SILVER, ArmorItem.Type.BOOTS, new Item.Properties().durability(91)));
    public static final DeferredItem<Item> ANCIENT_SILVER_INGOT = ITEMS.registerSimpleItem("ancient_silver_ingot", new Item.Properties());
    public static final DeferredItem<Item> ANCIENT_SILVER_NUGGET = ITEMS.registerSimpleItem("ancient_silver_nugget", new Item.Properties());
    public static final DeferredItem<Item> ANCIENT_SILVER_SCRAP = ITEMS.registerSimpleItem("ancient_silver_scrap", new Item.Properties());

    // ---- Fur / Hide / Reptile Armor ----
    public static final DeferredItem<Item> FUR = ITEMS.registerSimpleItem("fur", new Item.Properties());
    public static final DeferredItem<ArmorItem> FUR_HELMET = ITEMS.register("fur_helmet",
            () -> new ArmorItem(ModArmorMaterials.FUR, ArmorItem.Type.HELMET, new Item.Properties().durability(165)));
    public static final DeferredItem<ArmorItem> FUR_CHEST = ITEMS.register("fur_chest",
            () -> new ArmorItem(ModArmorMaterials.FUR, ArmorItem.Type.CHESTPLATE, new Item.Properties().durability(240)));
    public static final DeferredItem<ArmorItem> FUR_LEGS = ITEMS.register("fur_legs",
            () -> new ArmorItem(ModArmorMaterials.FUR, ArmorItem.Type.LEGGINGS, new Item.Properties().durability(225)));
    public static final DeferredItem<ArmorItem> FUR_BOOTS = ITEMS.register("fur_boots",
            () -> new ArmorItem(ModArmorMaterials.FUR, ArmorItem.Type.BOOTS, new Item.Properties().durability(195)));
    public static final DeferredItem<Item> HIDE = ITEMS.registerSimpleItem("hide", new Item.Properties());
    public static final DeferredItem<ArmorItem> HIDE_HELMET = ITEMS.register("hide_helmet",
            () -> new ArmorItem(ModArmorMaterials.HIDE, ArmorItem.Type.HELMET, new Item.Properties().durability(165)));
    public static final DeferredItem<ArmorItem> HIDE_CHEST = ITEMS.register("hide_chest",
            () -> new ArmorItem(ModArmorMaterials.HIDE, ArmorItem.Type.CHESTPLATE, new Item.Properties().durability(240)));
    public static final DeferredItem<ArmorItem> HIDE_LEGS = ITEMS.register("hide_legs",
            () -> new ArmorItem(ModArmorMaterials.HIDE, ArmorItem.Type.LEGGINGS, new Item.Properties().durability(225)));
    public static final DeferredItem<ArmorItem> HIDE_BOOTS = ITEMS.register("hide_boots",
            () -> new ArmorItem(ModArmorMaterials.HIDE, ArmorItem.Type.BOOTS, new Item.Properties().durability(195)));
    public static final DeferredItem<Item> REPTILE_HIDE = ITEMS.registerSimpleItem("reptile_hide", new Item.Properties());
    public static final DeferredItem<ArmorItem> REPTILE_HELMET = ITEMS.register("reptile_helmet",
            () -> new ArmorItem(ModArmorMaterials.REPTILE, ArmorItem.Type.HELMET, new Item.Properties().durability(165)));
    public static final DeferredItem<ArmorItem> REPTILE_PLATE = ITEMS.register("reptile_plate",
            () -> new ArmorItem(ModArmorMaterials.REPTILE, ArmorItem.Type.CHESTPLATE, new Item.Properties().durability(240)));
    public static final DeferredItem<ArmorItem> REPTILE_LEGS = ITEMS.register("reptile_legs",
            () -> new ArmorItem(ModArmorMaterials.REPTILE, ArmorItem.Type.LEGGINGS, new Item.Properties().durability(225)));
    public static final DeferredItem<ArmorItem> REPTILE_BOOTS = ITEMS.register("reptile_boots",
            () -> new ArmorItem(ModArmorMaterials.REPTILE, ArmorItem.Type.BOOTS, new Item.Properties().durability(195)));

    // ---- Scorpion Gear (5 biome variants) ----
    public static final DeferredItem<Item> SCORP_AXE_CAVE = ITEMS.register("scorp_axe_cave",
        () -> new com.example.neomocreatures.item.ScorpionAxeItem(net.minecraft.world.item.Tiers.DIAMOND,
            new Item.Properties().stacksTo(1)
                .attributes(net.minecraft.world.item.AxeItem.createAttributes(net.minecraft.world.item.Tiers.DIAMOND, 5.0F, -3.0F))
                .durability(net.minecraft.world.item.Tiers.DIAMOND.getUses()),
            () -> new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.CONFUSION, 100, 0)));

    public static final DeferredItem<Item> SCORP_AXE_DIRT = ITEMS.register("scorp_axe_dirt",
        () -> new com.example.neomocreatures.item.ScorpionAxeItem(net.minecraft.world.item.Tiers.DIAMOND,
            new Item.Properties().stacksTo(1)
                .attributes(net.minecraft.world.item.AxeItem.createAttributes(net.minecraft.world.item.Tiers.DIAMOND, 5.0F, -3.0F))
                .durability(net.minecraft.world.item.Tiers.DIAMOND.getUses()),
            () -> new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.POISON, 100, 0)));

    public static final DeferredItem<Item> SCORP_AXE_NETHER = ITEMS.register("scorp_axe_nether",
        () -> new com.example.neomocreatures.item.ScorpionAxeItem(net.minecraft.world.item.Tiers.DIAMOND,
            new Item.Properties().stacksTo(1)
                .attributes(net.minecraft.world.item.AxeItem.createAttributes(net.minecraft.world.item.Tiers.DIAMOND, 5.0F, -3.0F))
                .durability(net.minecraft.world.item.Tiers.DIAMOND.getUses()),
            4));

    public static final DeferredItem<Item> SCORP_AXE_FROST = ITEMS.register("scorp_axe_frost",
        () -> new com.example.neomocreatures.item.ScorpionAxeItem(net.minecraft.world.item.Tiers.DIAMOND,
            new Item.Properties().stacksTo(1)
                .attributes(net.minecraft.world.item.AxeItem.createAttributes(net.minecraft.world.item.Tiers.DIAMOND, 5.0F, -3.0F))
                .durability(net.minecraft.world.item.Tiers.DIAMOND.getUses()),
            () -> new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 100, 0)));

    public static final DeferredItem<Item> SCORP_AXE_UNDEAD = ITEMS.register("scorp_axe_undead",
        () -> new com.example.neomocreatures.item.ScorpionAxeItem(net.minecraft.world.item.Tiers.DIAMOND,
            new Item.Properties().stacksTo(1)
                .attributes(net.minecraft.world.item.AxeItem.createAttributes(net.minecraft.world.item.Tiers.DIAMOND, 5.0F, -3.0F))
                .durability(net.minecraft.world.item.Tiers.DIAMOND.getUses()),
            () -> new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.WEAKNESS, 100, 0)));
    public static final DeferredItem<ArmorItem> SCORP_HELMET_CAVE = ITEMS.register("scorp_helmet_cave",
            () -> new ArmorItem(ModArmorMaterials.SCORP_CAVE, ArmorItem.Type.HELMET, new Item.Properties().durability(166)));
    public static final DeferredItem<ArmorItem> SCORP_PLATE_CAVE = ITEMS.register("scorp_plate_cave",
            () -> new ArmorItem(ModArmorMaterials.SCORP_CAVE, ArmorItem.Type.CHESTPLATE, new Item.Properties().durability(241)));
    public static final DeferredItem<ArmorItem> SCORP_LEGS_CAVE = ITEMS.register("scorp_legs_cave",
            () -> new ArmorItem(ModArmorMaterials.SCORP_CAVE, ArmorItem.Type.LEGGINGS, new Item.Properties().durability(226)));
    public static final DeferredItem<ArmorItem> SCORP_BOOTS_CAVE = ITEMS.register("scorp_boots_cave",
            () -> new ArmorItem(ModArmorMaterials.SCORP_CAVE, ArmorItem.Type.BOOTS, new Item.Properties().durability(196)));

    public static final DeferredItem<ArmorItem> SCORP_HELMET_DIRT = ITEMS.register("scorp_helmet_dirt",
            () -> new ArmorItem(ModArmorMaterials.SCORP_DIRT, ArmorItem.Type.HELMET, new Item.Properties().durability(166)));
    public static final DeferredItem<ArmorItem> SCORP_PLATE_DIRT = ITEMS.register("scorp_plate_dirt",
            () -> new ArmorItem(ModArmorMaterials.SCORP_DIRT, ArmorItem.Type.CHESTPLATE, new Item.Properties().durability(241)));
    public static final DeferredItem<ArmorItem> SCORP_LEGS_DIRT = ITEMS.register("scorp_legs_dirt",
            () -> new ArmorItem(ModArmorMaterials.SCORP_DIRT, ArmorItem.Type.LEGGINGS, new Item.Properties().durability(226)));
    public static final DeferredItem<ArmorItem> SCORP_BOOTS_DIRT = ITEMS.register("scorp_boots_dirt",
            () -> new ArmorItem(ModArmorMaterials.SCORP_DIRT, ArmorItem.Type.BOOTS, new Item.Properties().durability(196)));

    public static final DeferredItem<ArmorItem> SCORP_HELMET_NETHER = ITEMS.register("scorp_helmet_nether",
            () -> new ArmorItem(ModArmorMaterials.SCORP_NETHER, ArmorItem.Type.HELMET, new Item.Properties().durability(166)));
    public static final DeferredItem<ArmorItem> SCORP_PLATE_NETHER = ITEMS.register("scorp_plate_nether",
            () -> new ArmorItem(ModArmorMaterials.SCORP_NETHER, ArmorItem.Type.CHESTPLATE, new Item.Properties().durability(241)));
    public static final DeferredItem<ArmorItem> SCORP_LEGS_NETHER = ITEMS.register("scorp_legs_nether",
            () -> new ArmorItem(ModArmorMaterials.SCORP_NETHER, ArmorItem.Type.LEGGINGS, new Item.Properties().durability(226)));
    public static final DeferredItem<ArmorItem> SCORP_BOOTS_NETHER = ITEMS.register("scorp_boots_nether",
            () -> new ArmorItem(ModArmorMaterials.SCORP_NETHER, ArmorItem.Type.BOOTS, new Item.Properties().durability(196)));

    public static final DeferredItem<ArmorItem> SCORP_HELMET_FROST = ITEMS.register("scorp_helmet_frost",
            () -> new ArmorItem(ModArmorMaterials.SCORP_FROST, ArmorItem.Type.HELMET, new Item.Properties().durability(166)));
    public static final DeferredItem<ArmorItem> SCORP_PLATE_FROST = ITEMS.register("scorp_plate_frost",
            () -> new ArmorItem(ModArmorMaterials.SCORP_FROST, ArmorItem.Type.CHESTPLATE, new Item.Properties().durability(241)));
    public static final DeferredItem<ArmorItem> SCORP_LEGS_FROST = ITEMS.register("scorp_legs_frost",
            () -> new ArmorItem(ModArmorMaterials.SCORP_FROST, ArmorItem.Type.LEGGINGS, new Item.Properties().durability(226)));
    public static final DeferredItem<ArmorItem> SCORP_BOOTS_FROST = ITEMS.register("scorp_boots_frost",
            () -> new ArmorItem(ModArmorMaterials.SCORP_FROST, ArmorItem.Type.BOOTS, new Item.Properties().durability(196)));

    public static final DeferredItem<ArmorItem> SCORP_HELMET_UNDEAD = ITEMS.register("scorp_helmet_undead",
            () -> new ArmorItem(ModArmorMaterials.SCORP_UNDEAD, ArmorItem.Type.HELMET, new Item.Properties().durability(166)));
    public static final DeferredItem<ArmorItem> SCORP_PLATE_UNDEAD = ITEMS.register("scorp_plate_undead",
            () -> new ArmorItem(ModArmorMaterials.SCORP_UNDEAD, ArmorItem.Type.CHESTPLATE, new Item.Properties().durability(241)));
    public static final DeferredItem<ArmorItem> SCORP_LEGS_UNDEAD = ITEMS.register("scorp_legs_undead",
            () -> new ArmorItem(ModArmorMaterials.SCORP_UNDEAD, ArmorItem.Type.LEGGINGS, new Item.Properties().durability(226)));
    public static final DeferredItem<ArmorItem> SCORP_BOOTS_UNDEAD = ITEMS.register("scorp_boots_undead",
            () -> new ArmorItem(ModArmorMaterials.SCORP_UNDEAD, ArmorItem.Type.BOOTS, new Item.Properties().durability(196)));

    public static final DeferredItem<Item> SCORP_STING_CAVE = ITEMS.registerSimpleItem("scorp_sting_cave", new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> SCORP_STING_DIRT = ITEMS.registerSimpleItem("scorp_sting_dirt", new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> SCORP_STING_FROST = ITEMS.registerSimpleItem("scorp_sting_frost", new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> SCORP_STING_NETHER = ITEMS.registerSimpleItem("scorp_sting_nether", new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> SCORP_STING_UNDEAD = ITEMS.registerSimpleItem("scorp_sting_undead", new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> SCORP_SWORD_CAVE = ITEMS.register("scorp_sword_cave",
        () -> new ScorpionSwordItem(
            Tiers.DIAMOND,
            new Item.Properties()
                .stacksTo(1)
                .attributes(SwordItem.createAttributes(Tiers.DIAMOND, 3, -2.4F))
                .durability(Tiers.DIAMOND.getUses()),
            () -> new MobEffectInstance(
                MobEffects.CONFUSION,
                100,
                0
            )));
    public static final DeferredItem<Item> SCORP_SWORD_DIRT = ITEMS.register("scorp_sword_dirt",
        () -> new ScorpionSwordItem(
            Tiers.DIAMOND,
            new Item.Properties()
                .stacksTo(1)
                .attributes(SwordItem.createAttributes(Tiers.DIAMOND, 3, -2.4F))
                .durability(Tiers.DIAMOND.getUses()),
            () -> new MobEffectInstance(
                MobEffects.POISON,
                100,
                0
            )));
    public static final DeferredItem<Item> SCORP_SWORD_NETHER = ITEMS.register("scorp_sword_nether",
        () -> new ScorpionSwordItem(
            Tiers.DIAMOND,
            new Item.Properties()
                .stacksTo(1)
                .attributes(SwordItem.createAttributes(Tiers.DIAMOND, 3, -2.4F))
                .durability(Tiers.DIAMOND.getUses()),
            4));
    public static final DeferredItem<Item> SCORP_SWORD_FROST = ITEMS.register("scorp_sword_frost",
        () -> new ScorpionSwordItem(
            Tiers.DIAMOND,
            new Item.Properties()
                .stacksTo(1)
                .attributes(SwordItem.createAttributes(Tiers.DIAMOND, 3, -2.4F))
                .durability(Tiers.DIAMOND.getUses()),
            () -> new MobEffectInstance(
                MobEffects.MOVEMENT_SLOWDOWN,
                100,
                0
            )));
    public static final DeferredItem<Item> SCORP_SWORD_UNDEAD = ITEMS.register("scorp_sword_undead",
        () -> new ScorpionSwordItem(
            Tiers.DIAMOND,
            new Item.Properties()
                .stacksTo(1)
                .attributes(SwordItem.createAttributes(Tiers.DIAMOND, 3, -2.4F))
                .durability(Tiers.DIAMOND.getUses()),
            () -> new MobEffectInstance(
                MobEffects.WEAKNESS,
                100,
                0
            )));

    // ---- Weapons ----
    public static final DeferredItem<Item> BIG_CAT_CLAW = ITEMS.registerSimpleItem("big_cat_claw", new Item.Properties());
    public static final DeferredItem<Item> BO = ITEMS.register("bo",
        () -> new SwordItem(
            Tiers.IRON,
            new Item.Properties()
                .stacksTo(1)
                .attributes(SwordItem.createAttributes(Tiers.IRON, 3, -2.4F))
                .durability(Tiers.IRON.getUses())));
    public static final DeferredItem<Item> KATANA = ITEMS.register("katana",
        () -> new SwordItem(
            Tiers.IRON,
            new Item.Properties()
                .stacksTo(1)
                .attributes(SwordItem.createAttributes(Tiers.IRON, 3, -2.4F))
                .durability(Tiers.IRON.getUses())));
    public static final DeferredItem<Item> NUNCHAKU = ITEMS.register("nunchaku",
        () -> new SwordItem(
            Tiers.IRON,
            new Item.Properties()
                .stacksTo(1)
                .attributes(SwordItem.createAttributes(Tiers.IRON, 3, -2.4F))
                .durability(Tiers.IRON.getUses())));
    public static final DeferredItem<Item> SAI = ITEMS.register("sai",
        () -> new SwordItem(
            Tiers.IRON,
            new Item.Properties()
                .stacksTo(1)
                .attributes(SwordItem.createAttributes(Tiers.IRON, 3, -2.4F))
                .durability(Tiers.IRON.getUses())));
    public static final DeferredItem<Item> SHARK_AXE = ITEMS.register("shark_axe",
        () -> new net.minecraft.world.item.AxeItem(net.minecraft.world.item.Tiers.IRON,
            new Item.Properties().stacksTo(1)
                .attributes(net.minecraft.world.item.AxeItem.createAttributes(net.minecraft.world.item.Tiers.IRON, 6.0F, -3.1F))
                .durability(net.minecraft.world.item.Tiers.IRON.getUses())));
    public static final DeferredItem<Item> SHARK_SWORD = ITEMS.register("shark_sword",
        () -> new SwordItem(
            Tiers.IRON,
            new Item.Properties()
                .stacksTo(1)
                .attributes(SwordItem.createAttributes(Tiers.IRON, 3, -2.4F))
                .durability(Tiers.IRON.getUses())));
    public static final DeferredItem<Item> SHARK_TEETH = ITEMS.registerSimpleItem("shark_teeth", new Item.Properties());
    public static final DeferredItem<Item> SILVER_AXE = ITEMS.register("silver_axe",
        () -> new net.minecraft.world.item.AxeItem(net.minecraft.world.item.Tiers.GOLD,
            new Item.Properties().stacksTo(1)
                .attributes(net.minecraft.world.item.AxeItem.createAttributes(net.minecraft.world.item.Tiers.GOLD, 6.0F, -3.0F))
                .durability(net.minecraft.world.item.Tiers.GOLD.getUses())));
    public static final DeferredItem<Item> SILVER_SWORD = ITEMS.register("silver_sword",
        () -> new SwordItem(
            Tiers.GOLD,
            new Item.Properties()
                .stacksTo(1)
                .attributes(SwordItem.createAttributes(Tiers.GOLD, 3, -2.4F))
                .durability(Tiers.GOLD.getUses())));
    public static final DeferredItem<Item> TUSKS_DIAMOND = ITEMS.registerSimpleItem("tusks_diamond", new Item.Properties().stacksTo(1).durability(1562));
    public static final DeferredItem<Item> TUSKS_IRON = ITEMS.registerSimpleItem("tusks_iron", new Item.Properties().stacksTo(1).durability(251));
    public static final DeferredItem<Item> TUSKS_WOOD = ITEMS.registerSimpleItem("tusks_wood", new Item.Properties().stacksTo(1).durability(60));

    // ---- Foods ----
    public static final DeferredItem<Item> CRAB_COOKED = ITEMS.registerSimpleItem("crab_cooked",
        new Item.Properties().food(new FoodProperties.Builder()
            .nutrition(8).saturationModifier(0.6F).build()).stacksTo(64));

    public static final DeferredItem<Item> CRAB_RAW = ITEMS.registerSimpleItem("crab_raw",
        new Item.Properties().food(new FoodProperties.Builder()
            .nutrition(2).saturationModifier(0.3F).build()).stacksTo(64));

    public static final DeferredItem<Item> DUCK_COOKED = ITEMS.registerSimpleItem("duck_cooked",
        new Item.Properties().food(new FoodProperties.Builder()
            .nutrition(6).saturationModifier(0.6F).build()).stacksTo(64));

    public static final DeferredItem<Item> DUCK_RAW = ITEMS.registerSimpleItem("duck_raw",
        new Item.Properties().food(new FoodProperties.Builder()
            .nutrition(2).saturationModifier(0.3F)
            .effect(() -> new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.HUNGER, 600, 0), 0.3F)
            .build()).stacksTo(64));

    public static final DeferredItem<Item> OMELET = ITEMS.registerSimpleItem("omelet",
        new Item.Properties().food(new FoodProperties.Builder()
            .nutrition(4).saturationModifier(0.6F).build()).stacksTo(64));

    public static final DeferredItem<Item> OSTRICH_COOKED = ITEMS.registerSimpleItem("ostrich_cooked",
        new Item.Properties().food(new FoodProperties.Builder()
            .nutrition(6).saturationModifier(0.6F).build()).stacksTo(64));

    public static final DeferredItem<Item> OSTRICH_RAW = ITEMS.registerSimpleItem("ostrich_raw",
        new Item.Properties().food(new FoodProperties.Builder()
            .nutrition(2).saturationModifier(0.3F)
            .effect(() -> new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.HUNGER, 600, 0), 0.3F)
            .build()).stacksTo(64));

    public static final DeferredItem<Item> RAT_BURGER = ITEMS.registerSimpleItem("rat_burger",
        new Item.Properties().food(new FoodProperties.Builder()
            .nutrition(8).saturationModifier(0.6F).build()).stacksTo(64));

    public static final DeferredItem<Item> RAT_COOKED = ITEMS.registerSimpleItem("rat_cooked",
        new Item.Properties().food(new FoodProperties.Builder()
            .nutrition(4).saturationModifier(0.6F).build()).stacksTo(64));

    public static final DeferredItem<Item> RAT_RAW = ITEMS.registerSimpleItem("rat_raw",
        new Item.Properties().food(new FoodProperties.Builder()
            .nutrition(2).saturationModifier(0.3F)
            .effect(() -> new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.HUNGER, 600, 0), 0.3F)
            .build()).stacksTo(64));

    public static final DeferredItem<Item> TURKEY_COOKED = ITEMS.registerSimpleItem("turkey_cooked",
        new Item.Properties().food(new FoodProperties.Builder()
            .nutrition(8).saturationModifier(0.6F).build()).stacksTo(64));

    public static final DeferredItem<Item> TURKEY_RAW = ITEMS.registerSimpleItem("turkey_raw",
        new Item.Properties().food(new FoodProperties.Builder()
            .nutrition(3).saturationModifier(0.3F)
            .effect(() -> new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.HUNGER, 600, 0), 0.3F)
            .build()).stacksTo(64));

    public static final DeferredItem<Item> TURTLE_COOKED = ITEMS.registerSimpleItem("turtle_cooked",
        new Item.Properties().food(new FoodProperties.Builder()
            .nutrition(4).saturationModifier(0.6F).build()).stacksTo(64));

    public static final DeferredItem<Item> TURTLE_RAW = ITEMS.registerSimpleItem("turtle_raw",
        new Item.Properties().food(new FoodProperties.Builder()
            .nutrition(2).saturationModifier(0.3F)
            .effect(() -> new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.HUNGER, 600, 0), 0.3F)
            .build()).stacksTo(64));

    // When eaten, returns the empty bowl — same mechanism as vanilla's mushroom stew.
    public static final DeferredItem<Item> TURTLE_SOUP = ITEMS.register("turtle_soup",
    () -> new com.example.neomocreatures.item.BowlFoodItem(new Item.Properties()
        .food(new FoodProperties.Builder().nutrition(5).saturationModifier(0.6F).build())
        .stacksTo(1)));

    public static final DeferredItem<Item> VENISON_COOKED = ITEMS.registerSimpleItem("venison_cooked",
        new Item.Properties().food(new FoodProperties.Builder()
            .nutrition(8).saturationModifier(0.6F).build()).stacksTo(64));

    public static final DeferredItem<Item> VENISON_RAW = ITEMS.registerSimpleItem("venison_raw",
        new Item.Properties().food(new FoodProperties.Builder()
            .nutrition(3).saturationModifier(0.3F).build()).stacksTo(64));


    // ---- Horse-related ----
    public static final DeferredItem<Item> HORSE_ARMOR_CRYSTAL = ITEMS.register("horse_armor_crystal",
        () -> new Item(new Item.Properties().stacksTo(1)
            .attributes(net.minecraft.world.item.component.ItemAttributeModifiers.builder()
                .add(net.minecraft.world.entity.ai.attributes.Attributes.ARMOR,
                     new net.minecraft.world.entity.ai.attributes.AttributeModifier(
                         net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "horse_armor_crystal"),
                         19.0D,
                         net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE),
                     net.minecraft.world.entity.EquipmentSlotGroup.BODY)
                .build())));    public static final DeferredItem<Item> HORSE_SADDLE = ITEMS.register("horse_saddle",
        () -> new net.minecraft.world.item.SaddleItem(new Item.Properties().stacksTo(16)));
    public static final DeferredItem<Item> UNICORN_HORN = ITEMS.registerSimpleItem("unicorn_horn", new Item.Properties());
    public static final DeferredItem<Item> WHIP = ITEMS.register("whip",
        () -> new com.example.neomocreatures.item.WhipItem(new Item.Properties().stacksTo(1)));
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
        () -> new com.example.neomocreatures.item.FishNetItem(new Item.Properties().stacksTo(16)));
    public static final DeferredItem<Item> FISH_NET_FULL = ITEMS.register("fish_net_full",
        () -> new com.example.neomocreatures.item.FilledFishNetItem(new Item.Properties().stacksTo(1), FISH_NET.get()));
    public static final DeferredItem<Item> HAYSTACK = ITEMS.registerSimpleItem("haystack", new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> KEY = ITEMS.registerSimpleItem("key", new Item.Properties());
    public static final DeferredItem<Item> KITTY_BED = ITEMS.registerSimpleItem("kitty_bed", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> KITTY_BED_BLACK = ITEMS.register("kitty_bed_black",
                () -> new com.example.neomocreatures.item.KittyBedItem(net.minecraft.world.item.DyeColor.BLACK, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> KITTY_BED_BLUE = ITEMS.register("kitty_bed_blue",
                () -> new com.example.neomocreatures.item.KittyBedItem(net.minecraft.world.item.DyeColor.BLUE, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> KITTY_BED_BROWN = ITEMS.register("kitty_bed_brown",
                () -> new com.example.neomocreatures.item.KittyBedItem(net.minecraft.world.item.DyeColor.BROWN, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> KITTY_BED_CYAN = ITEMS.register("kitty_bed_cyan",
                () -> new com.example.neomocreatures.item.KittyBedItem(net.minecraft.world.item.DyeColor.CYAN, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> KITTY_BED_GRAY = ITEMS.register("kitty_bed_gray",
                () -> new com.example.neomocreatures.item.KittyBedItem(net.minecraft.world.item.DyeColor.GRAY, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> KITTY_BED_GREEN = ITEMS.register("kitty_bed_green",
                () -> new com.example.neomocreatures.item.KittyBedItem(net.minecraft.world.item.DyeColor.GREEN, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> KITTY_BED_LIGHT_BLUE = ITEMS.register("kitty_bed_light_blue",
                () -> new com.example.neomocreatures.item.KittyBedItem(net.minecraft.world.item.DyeColor.LIGHT_BLUE, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> KITTY_BED_LIME = ITEMS.register("kitty_bed_lime",
                () -> new com.example.neomocreatures.item.KittyBedItem(net.minecraft.world.item.DyeColor.LIME, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> KITTY_BED_MAGENTA = ITEMS.register("kitty_bed_magenta",
                () -> new com.example.neomocreatures.item.KittyBedItem(net.minecraft.world.item.DyeColor.MAGENTA, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> KITTY_BED_ORANGE = ITEMS.register("kitty_bed_orange",
                () -> new com.example.neomocreatures.item.KittyBedItem(net.minecraft.world.item.DyeColor.ORANGE, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> KITTY_BED_PINK = ITEMS.register("kitty_bed_pink",
                () -> new com.example.neomocreatures.item.KittyBedItem(net.minecraft.world.item.DyeColor.PINK, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> KITTY_BED_PURPLE = ITEMS.register("kitty_bed_purple",
                () -> new com.example.neomocreatures.item.KittyBedItem(net.minecraft.world.item.DyeColor.PURPLE, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> KITTY_BED_RED = ITEMS.register("kitty_bed_red",
                () -> new com.example.neomocreatures.item.KittyBedItem(net.minecraft.world.item.DyeColor.RED, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> KITTY_BED_SILVER = ITEMS.register("kitty_bed_silver",
                () -> new com.example.neomocreatures.item.KittyBedItem(net.minecraft.world.item.DyeColor.LIGHT_GRAY, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> KITTY_BED_WHITE = ITEMS.register("kitty_bed_white",
                () -> new com.example.neomocreatures.item.KittyBedItem(net.minecraft.world.item.DyeColor.WHITE, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> KITTY_BED_YELLOW = ITEMS.register("kitty_bed_yellow",
                () -> new com.example.neomocreatures.item.KittyBedItem(net.minecraft.world.item.DyeColor.YELLOW, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> KITTY_LITTER = ITEMS.register("kitty_litter",
                () -> new com.example.neomocreatures.item.LitterBoxItem(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> MEDALLION = ITEMS.registerSimpleItem("medallion", new Item.Properties());
    // Places a MoCEggEntity when used on a block — see MoCEggItem.
    public static final DeferredItem<Item> MOC_EGG = ITEMS.register("moc_egg",
        () -> new com.example.neomocreatures.item.MoCEggItem(new Item.Properties()));

    // One egg per naturally-occurring wyvern variant (the 8 wild biome
    // textures + the plain mother) — each only ever hatches its own species.
    // The 8 wild ones have a 30% chance of hatching tier 2 instead of tier 1;
    // the mother egg always hatches the bigger, tamed-only mother form.
    public static final DeferredItem<Item> JUNGLE_WYVERN_EGG = ITEMS.register("jungle_wyvern_egg",
        () -> new com.example.neomocreatures.item.MoCEggItem(new Item.Properties(),
            new com.example.neomocreatures.item.MoCEggItem.HatchSpec(
                ModEntities.WYVERN, ModEntities.WYVERN_TIER2, 0.3D, "JUNGLE")));
    public static final DeferredItem<Item> SWAMP_WYVERN_EGG = ITEMS.register("swamp_wyvern_egg",
        () -> new com.example.neomocreatures.item.MoCEggItem(new Item.Properties(),
            new com.example.neomocreatures.item.MoCEggItem.HatchSpec(
                ModEntities.WYVERN, ModEntities.WYVERN_TIER2, 0.3D, "SWAMP")));
    public static final DeferredItem<Item> SAND_WYVERN_EGG = ITEMS.register("sand_wyvern_egg",
        () -> new com.example.neomocreatures.item.MoCEggItem(new Item.Properties(),
            new com.example.neomocreatures.item.MoCEggItem.HatchSpec(
                ModEntities.WYVERN, ModEntities.WYVERN_TIER2, 0.3D, "SAND")));
    public static final DeferredItem<Item> SUN_WYVERN_EGG = ITEMS.register("sun_wyvern_egg",
        () -> new com.example.neomocreatures.item.MoCEggItem(new Item.Properties(),
            new com.example.neomocreatures.item.MoCEggItem.HatchSpec(
                ModEntities.WYVERN, ModEntities.WYVERN_TIER2, 0.3D, "SUN")));
    public static final DeferredItem<Item> ARCTIC_WYVERN_EGG = ITEMS.register("arctic_wyvern_egg",
        () -> new com.example.neomocreatures.item.MoCEggItem(new Item.Properties(),
            new com.example.neomocreatures.item.MoCEggItem.HatchSpec(
                ModEntities.WYVERN, ModEntities.WYVERN_TIER2, 0.3D, "ARCTIC")));
    public static final DeferredItem<Item> CAVE_WYVERN_EGG = ITEMS.register("cave_wyvern_egg",
        () -> new com.example.neomocreatures.item.MoCEggItem(new Item.Properties(),
            new com.example.neomocreatures.item.MoCEggItem.HatchSpec(
                ModEntities.WYVERN, ModEntities.WYVERN_TIER2, 0.3D, "CAVE")));
    public static final DeferredItem<Item> MOUNTAIN_WYVERN_EGG = ITEMS.register("mountain_wyvern_egg",
        () -> new com.example.neomocreatures.item.MoCEggItem(new Item.Properties(),
            new com.example.neomocreatures.item.MoCEggItem.HatchSpec(
                ModEntities.WYVERN, ModEntities.WYVERN_TIER2, 0.3D, "MOUNTAIN")));
    public static final DeferredItem<Item> SEA_WYVERN_EGG = ITEMS.register("sea_wyvern_egg",
        () -> new com.example.neomocreatures.item.MoCEggItem(new Item.Properties(),
            new com.example.neomocreatures.item.MoCEggItem.HatchSpec(
                ModEntities.WYVERN, ModEntities.WYVERN_TIER2, 0.3D, "SEA")));
    public static final DeferredItem<Item> MOTHER_WYVERN_EGG = ITEMS.register("mother_wyvern_egg",
        () -> new com.example.neomocreatures.item.MoCEggItem(new Item.Properties(),
            new com.example.neomocreatures.item.MoCEggItem.HatchSpec(
                ModEntities.WYVERN_MOTHER_TAMED, null, 0.0D, "MOTHER")));
    public static final DeferredItem<Item> DIRT_SCORPION_EGG = ITEMS.register("dirt_scorpion_egg",
            () -> new com.example.neomocreatures.item.MoCEggItem(new Item.Properties(),
                    new com.example.neomocreatures.item.MoCEggItem.HatchSpec(
                            com.example.neomocreatures.init.ModEntities.MOC_SCORPION, null, 0.0D, "DIRT")));
    public static final DeferredItem<Item> CAVE_SCORPION_EGG = ITEMS.register("cave_scorpion_egg",
            () -> new com.example.neomocreatures.item.MoCEggItem(new Item.Properties(),
                    new com.example.neomocreatures.item.MoCEggItem.HatchSpec(
                            com.example.neomocreatures.init.ModEntities.MOC_SCORPION, null, 0.0D, "CAVE")));
    public static final DeferredItem<Item> FROST_SCORPION_EGG = ITEMS.register("frost_scorpion_egg",
            () -> new com.example.neomocreatures.item.MoCEggItem(new Item.Properties(),
                    new com.example.neomocreatures.item.MoCEggItem.HatchSpec(
                            com.example.neomocreatures.init.ModEntities.MOC_SCORPION, null, 0.0D, "FROST")));
    public static final DeferredItem<Item> FIRE_SCORPION_EGG = ITEMS.register("fire_scorpion_egg",
            () -> new com.example.neomocreatures.item.MoCEggItem(new Item.Properties(),
                    new com.example.neomocreatures.item.MoCEggItem.HatchSpec(
                            com.example.neomocreatures.init.ModEntities.MOC_SCORPION, null, 0.0D, "NETHER")));
    public static final DeferredItem<Item> UNDEAD_SCORPION_EGG = ITEMS.register("undead_scorpion_egg",
            () -> new com.example.neomocreatures.item.MoCEggItem(new Item.Properties(),
                    new com.example.neomocreatures.item.MoCEggItem.HatchSpec(
                            com.example.neomocreatures.init.ModEntities.MOC_SCORPION, null, 0.0D, "UNDEAD")));
    public static final DeferredItem<Item> PLAIN_MANTICORE_EGG = ITEMS.register("plain_manticore_egg",
        () -> new com.example.neomocreatures.item.MoCEggItem(new Item.Properties(),
                new com.example.neomocreatures.item.MoCEggItem.HatchSpec(
                        com.example.neomocreatures.init.ModEntities.MOC_MANTICORE, null, 0.0D, "PLAIN")));
    public static final DeferredItem<Item> DARK_MANTICORE_EGG = ITEMS.register("dark_manticore_egg",
            () -> new com.example.neomocreatures.item.MoCEggItem(new Item.Properties(),
                    new com.example.neomocreatures.item.MoCEggItem.HatchSpec(
                            com.example.neomocreatures.init.ModEntities.MOC_MANTICORE, null, 0.0D, "DARK")));
    public static final DeferredItem<Item> FROST_MANTICORE_EGG = ITEMS.register("frost_manticore_egg",
            () -> new com.example.neomocreatures.item.MoCEggItem(new Item.Properties(),
                    new com.example.neomocreatures.item.MoCEggItem.HatchSpec(
                            com.example.neomocreatures.init.ModEntities.MOC_MANTICORE, null, 0.0D, "FROST")));
    public static final DeferredItem<Item> FIRE_MANTICORE_EGG = ITEMS.register("fire_manticore_egg",
            () -> new com.example.neomocreatures.item.MoCEggItem(new Item.Properties(),
                    new com.example.neomocreatures.item.MoCEggItem.HatchSpec(
                            com.example.neomocreatures.init.ModEntities.MOC_MANTICORE, null, 0.0D, "FIRE")));
    public static final DeferredItem<Item> TOXIC_MANTICORE_EGG = ITEMS.register("toxic_manticore_egg",
            () -> new com.example.neomocreatures.item.MoCEggItem(new Item.Properties(),
                    new com.example.neomocreatures.item.MoCEggItem.HatchSpec(
                            com.example.neomocreatures.init.ModEntities.MOC_MANTICORE, null, 0.0D, "TOXIC")));
    public static final DeferredItem<Item> OSTRICH_EGG = ITEMS.register("ostrich_egg",
            () -> new com.example.neomocreatures.item.MoCEggItem(new Item.Properties(),
                        new com.example.neomocreatures.item.MoCEggItem.HatchSpec(
                                com.example.neomocreatures.init.ModEntities.MOC_OSTRICH, null, 0.0D, null)));
    public static final DeferredItem<Item> KOMODO_DRAGON_EGG = ITEMS.register("komodo_dragon_egg",
            () -> new com.example.neomocreatures.item.MoCEggItem(new Item.Properties(),
                        new com.example.neomocreatures.item.MoCEggItem.HatchSpec(
                                com.example.neomocreatures.init.ModEntities.MOC_KOMODO_DRAGON, null, 0.0D, null)));
    public static final DeferredItem<Item> SNAKE_EGG_GREEN_DARK = ITEMS.register("snake_egg_green_dark",
        () -> new com.example.neomocreatures.item.MoCEggItem(new Item.Properties(),
            new com.example.neomocreatures.item.MoCEggItem.HatchSpec(
                ModEntities.MOC_SNAKE, null, 0.0D, "GREEN_DARK")));
    public static final DeferredItem<Item> SNAKE_EGG_WOLF = ITEMS.register("snake_egg_wolf",
        () -> new com.example.neomocreatures.item.MoCEggItem(new Item.Properties(),
            new com.example.neomocreatures.item.MoCEggItem.HatchSpec(
                ModEntities.MOC_SNAKE, null, 0.0D, "WOLF")));
    public static final DeferredItem<Item> SNAKE_EGG_ORANGE = ITEMS.register("snake_egg_orange",
        () -> new com.example.neomocreatures.item.MoCEggItem(new Item.Properties(),
            new com.example.neomocreatures.item.MoCEggItem.HatchSpec(
                ModEntities.MOC_SNAKE, null, 0.0D, "ORANGE")));
    public static final DeferredItem<Item> SNAKE_EGG_GREEN_BRIGHT = ITEMS.register("snake_egg_green_bright",
        () -> new com.example.neomocreatures.item.MoCEggItem(new Item.Properties(),
            new com.example.neomocreatures.item.MoCEggItem.HatchSpec(
                ModEntities.MOC_SNAKE, null, 0.0D, "GREEN_BRIGHT")));
    public static final DeferredItem<Item> SNAKE_EGG_CORAL = ITEMS.register("snake_egg_coral",
        () -> new com.example.neomocreatures.item.MoCEggItem(new Item.Properties(),
            new com.example.neomocreatures.item.MoCEggItem.HatchSpec(
                ModEntities.MOC_SNAKE, null, 0.0D, "CORAL")));
    public static final DeferredItem<Item> SNAKE_EGG_COBRA = ITEMS.register("snake_egg_cobra",
        () -> new com.example.neomocreatures.item.MoCEggItem(new Item.Properties(),
            new com.example.neomocreatures.item.MoCEggItem.HatchSpec(
                ModEntities.MOC_SNAKE, null, 0.0D, "COBRA")));
    public static final DeferredItem<Item> SNAKE_EGG_RATTLE = ITEMS.register("snake_egg_rattle",
        () -> new com.example.neomocreatures.item.MoCEggItem(new Item.Properties(),
            new com.example.neomocreatures.item.MoCEggItem.HatchSpec(
                ModEntities.MOC_SNAKE, null, 0.0D, "RATTLE")));
    public static final DeferredItem<Item> SNAKE_EGG_PYTHON = ITEMS.register("snake_egg_python",
        () -> new com.example.neomocreatures.item.MoCEggItem(new Item.Properties(),
            new com.example.neomocreatures.item.MoCEggItem.HatchSpec(
                ModEntities.MOC_SNAKE, null, 0.0D, "PYTHON")));
    public static final DeferredItem<Item> SHARK_EGG = ITEMS.register("shark_egg",
        () -> new com.example.neomocreatures.item.SharkEggItem(new Item.Properties(),
            new com.example.neomocreatures.item.MoCEggItem.HatchSpec(
                ModEntities.MOC_SHARK, null, 0.0D, null)));
    public static final DeferredItem<Item> COD_EGG = ITEMS.register("cod_egg",
        () -> new com.example.neomocreatures.item.MediumFishEggItem(new Item.Properties(),
            new com.example.neomocreatures.item.MoCEggItem.HatchSpec(
                com.example.neomocreatures.init.ModEntities.MOC_COD, null, 0.0D, null)));
    public static final DeferredItem<Item> SALMON_EGG = ITEMS.register("salmon_egg",
        () -> new com.example.neomocreatures.item.MediumFishEggItem(new Item.Properties(),
            new com.example.neomocreatures.item.MoCEggItem.HatchSpec(
                com.example.neomocreatures.init.ModEntities.MOC_SALMON, null, 0.0D, null)));
    public static final DeferredItem<Item> BASS_EGG = ITEMS.register("bass_egg",
        () -> new com.example.neomocreatures.item.MediumFishEggItem(new Item.Properties(),
            new com.example.neomocreatures.item.MoCEggItem.HatchSpec(
                com.example.neomocreatures.init.ModEntities.MOC_BASS, null, 0.0D, null)));
    public static final DeferredItem<Item> ANCHOVY_EGG = ITEMS.register("anchovy_egg",
        () -> new com.example.neomocreatures.item.SmallFishEggItem(new Item.Properties(),
            new com.example.neomocreatures.item.MoCEggItem.HatchSpec(
                com.example.neomocreatures.init.ModEntities.MOC_SMALL_FISH, null, 0.0D, "ANCHOVY")));
    public static final DeferredItem<Item> ANGELFISH_EGG = ITEMS.register("angelfish_egg",
        () -> new com.example.neomocreatures.item.SmallFishEggItem(new Item.Properties(),
            new com.example.neomocreatures.item.MoCEggItem.HatchSpec(
                com.example.neomocreatures.init.ModEntities.MOC_SMALL_FISH, null, 0.0D, "ANGELFISH")));
    public static final DeferredItem<Item> ANGLERFISH_EGG = ITEMS.register("anglerfish_egg",
        () -> new com.example.neomocreatures.item.SmallFishEggItem(new Item.Properties(),
            new com.example.neomocreatures.item.MoCEggItem.HatchSpec(
                com.example.neomocreatures.init.ModEntities.MOC_SMALL_FISH, null, 0.0D, "ANGLER")));
    public static final DeferredItem<Item> CLOWNFISH_EGG = ITEMS.register("clownfish_egg",
        () -> new com.example.neomocreatures.item.SmallFishEggItem(new Item.Properties(),
            new com.example.neomocreatures.item.MoCEggItem.HatchSpec(
                com.example.neomocreatures.init.ModEntities.MOC_SMALL_FISH, null, 0.0D, "CLOWNFISH")));
    public static final DeferredItem<Item> GOLDFISH_EGG = ITEMS.register("goldfish_egg",
        () -> new com.example.neomocreatures.item.SmallFishEggItem(new Item.Properties(),
            new com.example.neomocreatures.item.MoCEggItem.HatchSpec(
                com.example.neomocreatures.init.ModEntities.MOC_SMALL_FISH, null, 0.0D, "GOLDFISH")));
    public static final DeferredItem<Item> HIPPOTANG_EGG = ITEMS.register("hippotang_egg",
        () -> new com.example.neomocreatures.item.SmallFishEggItem(new Item.Properties(),
            new com.example.neomocreatures.item.MoCEggItem.HatchSpec(
                com.example.neomocreatures.init.ModEntities.MOC_SMALL_FISH, null, 0.0D, "HIPPOTANG")));
    public static final DeferredItem<Item> MANDARINFISH_EGG = ITEMS.register("mandarinfish_egg",
        () -> new com.example.neomocreatures.item.SmallFishEggItem(new Item.Properties(),
            new com.example.neomocreatures.item.MoCEggItem.HatchSpec(
                com.example.neomocreatures.init.ModEntities.MOC_SMALL_FISH, null, 0.0D, "MANDARIN")));
   public static final DeferredItem<Item> PIRANHA_EGG = ITEMS.register("piranha_egg",
        () -> new com.example.neomocreatures.item.SmallFishEggItem(new Item.Properties(),
            new com.example.neomocreatures.item.MoCEggItem.HatchSpec(
                com.example.neomocreatures.init.ModEntities.MOC_SMALL_FISH, null, 0.0D, "PIRANHA")));

    public static final DeferredItem<Item> MYSTIC_PEAR = ITEMS.registerSimpleItem("mystic_pear", new Item.Properties());
    public static final DeferredItem<Item> NETHER_CANNON = ITEMS.registerSimpleItem("nether_cannon", new Item.Properties());
    public static final DeferredItem<Item> PET_FOOD = ITEMS.registerSimpleItem("pet_food", new Item.Properties());
    public static final ResourceKey<JukeboxSong> SHUFFLING_SONG = ResourceKey.create(Registries.JUKEBOX_SONG, ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "shuffling"));
    public static final DeferredItem<Item> RECORD_SHUFFLE = ITEMS.registerSimpleItem("record_shuffle", new Item.Properties().stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(SHUFFLING_SONG));
    public static final DeferredItem<Item> ROPE = ITEMS.registerSimpleItem("rope", new Item.Properties());
    public static final DeferredItem<Item> SCROLL_OF_FREEDOM = ITEMS.register("scroll_of_freedom",
            () -> new com.example.neomocreatures.item.ScrollOfFreedomItem(new Item.Properties()));
    public static final DeferredItem<Item> SCROLL_OF_OWNER = ITEMS.register("scroll_of_owner",
            () -> new com.example.neomocreatures.item.ScrollOfResetOwnerItem(new Item.Properties()));
    public static final DeferredItem<Item> SCROLL_OF_SALE = ITEMS.register("scroll_of_sale",
            () -> new com.example.neomocreatures.item.ScrollOfSaleItem(new Item.Properties()));
    public static final DeferredItem<Item> STAFF = ITEMS.registerSimpleItem("staff", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> STAFF2 = ITEMS.registerSimpleItem("staff2", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> STAFF3 = ITEMS.registerSimpleItem("staff3", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> STAFF_PORTAL = ITEMS.register("staff_portal",
            () -> new com.example.neomocreatures.item.StaffPortalItem(new Item.Properties().stacksTo(1).durability(4)));
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
    }