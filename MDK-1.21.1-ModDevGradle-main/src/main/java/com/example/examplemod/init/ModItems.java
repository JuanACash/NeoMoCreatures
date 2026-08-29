package com.example.examplemod.init;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.item.ScorpionSwordItem;
import com.example.examplemod.item.WildHorseSpawnEggItem;

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

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ExampleMod.MODID);

    public static final DeferredItem<Item> WILD_HORSE_SPAWN_EGG = ITEMS.register("wild_horse_spawn_egg",
            () -> new WildHorseSpawnEggItem(ModEntities.MOC_HORSE, new Item.Properties()));

    public static final DeferredItem<Item> MOC_HORSE_MOB_SPAWN_EGG = ITEMS.register("moc_horse_mob_spawn_egg",
        () -> new net.neoforged.neoforge.common.DeferredSpawnEggItem(ModEntities.MOC_HORSE_MOB, 0x1A1A2E, 0x5B2C6F, new Item.Properties()));
    public static final DeferredItem<Item> HEART_OF_UNDEAD = ITEMS.registerSimpleItem("heart_of_undead", new Item.Properties());
    public static final DeferredItem<Item> HEART_OF_FIRE = ITEMS.registerSimpleItem("heart_of_fire", new Item.Properties());
    public static final DeferredItem<Item> HEART_OF_DARKNESS = ITEMS.registerSimpleItem("heart_of_darkness", new Item.Properties());
    public static final DeferredItem<Item> ESSENCE_OF_UNDEAD = ITEMS.registerSimpleItem("essence_of_undead", new Item.Properties());
    public static final DeferredItem<Item> ESSENCE_OF_FIRE = ITEMS.registerSimpleItem("essence_of_fire", new Item.Properties());
    public static final DeferredItem<Item> ESSENCE_OF_DARKNESS = ITEMS.registerSimpleItem("essence_of_darkness", new Item.Properties());
    public static final DeferredItem<Item> ESSENCE_OF_LIGHT = ITEMS.registerSimpleItem("essence_of_light", new Item.Properties());

    // ==== Items importados de mocreatures_texture_items.zip (placeholders, sin funcion aun) ====
    // ---- Amulets ----
    public static final DeferredItem<Item> AMULET_BONE = ITEMS.registerSimpleItem("amulet_bone", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> AMULET_BONE_FULL = ITEMS.registerSimpleItem("amulet_bone_full", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> AMULET_FAIRY = ITEMS.registerSimpleItem("amulet_fairy", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> AMULET_FAIRY_FULL = ITEMS.registerSimpleItem("amulet_fairy_full", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> AMULET_GHOST = ITEMS.registerSimpleItem("amulet_ghost", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> AMULET_GHOST_FULL = ITEMS.registerSimpleItem("amulet_ghost_full", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> AMULET_PEGASUS = ITEMS.registerSimpleItem("amulet_pegasus", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> AMULET_PEGASUS_FULL = ITEMS.registerSimpleItem("amulet_pegasus_full", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> PET_AMULET = ITEMS.registerSimpleItem("pet_amulet", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> PET_AMULET_FULL = ITEMS.registerSimpleItem("pet_amulet_full", new Item.Properties().stacksTo(1));

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
        () -> new com.example.examplemod.item.ScorpionAxeItem(net.minecraft.world.item.Tiers.DIAMOND,
            new Item.Properties().stacksTo(1)
                .attributes(net.minecraft.world.item.AxeItem.createAttributes(net.minecraft.world.item.Tiers.DIAMOND, 5.0F, -3.0F))
                .durability(net.minecraft.world.item.Tiers.DIAMOND.getUses()),
            () -> new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.CONFUSION, 100, 0)));

    public static final DeferredItem<Item> SCORP_AXE_DIRT = ITEMS.register("scorp_axe_dirt",
        () -> new com.example.examplemod.item.ScorpionAxeItem(net.minecraft.world.item.Tiers.DIAMOND,
            new Item.Properties().stacksTo(1)
                .attributes(net.minecraft.world.item.AxeItem.createAttributes(net.minecraft.world.item.Tiers.DIAMOND, 5.0F, -3.0F))
                .durability(net.minecraft.world.item.Tiers.DIAMOND.getUses()),
            () -> new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.POISON, 100, 0)));

    public static final DeferredItem<Item> SCORP_AXE_NETHER = ITEMS.register("scorp_axe_nether",
        () -> new com.example.examplemod.item.ScorpionAxeItem(net.minecraft.world.item.Tiers.DIAMOND,
            new Item.Properties().stacksTo(1)
                .attributes(net.minecraft.world.item.AxeItem.createAttributes(net.minecraft.world.item.Tiers.DIAMOND, 5.0F, -3.0F))
                .durability(net.minecraft.world.item.Tiers.DIAMOND.getUses()),
            4)); // 4s de fuego, mismo valor usado en la Nether Scorpion Sword

    public static final DeferredItem<Item> SCORP_AXE_FROST = ITEMS.register("scorp_axe_frost",
        () -> new com.example.examplemod.item.ScorpionAxeItem(net.minecraft.world.item.Tiers.DIAMOND,
            new Item.Properties().stacksTo(1)
                .attributes(net.minecraft.world.item.AxeItem.createAttributes(net.minecraft.world.item.Tiers.DIAMOND, 5.0F, -3.0F))
                .durability(net.minecraft.world.item.Tiers.DIAMOND.getUses()),
            () -> new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 100, 0)));

    public static final DeferredItem<Item> SCORP_AXE_UNDEAD = ITEMS.register("scorp_axe_undead",
        () -> new com.example.examplemod.item.ScorpionAxeItem(net.minecraft.world.item.Tiers.DIAMOND,
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
    public static final DeferredItem<Item> TUSKS_DIAMOND = ITEMS.registerSimpleItem("tusks_diamond", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> TUSKS_IRON = ITEMS.registerSimpleItem("tusks_iron", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> TUSKS_WOOD = ITEMS.registerSimpleItem("tusks_wood", new Item.Properties().stacksTo(1));

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

    // Al comer, devuelve el bowl vacío — mismo mecanismo que mushroom stew de vanilla.
    public static final DeferredItem<Item> TURTLE_SOUP = ITEMS.register("turtle_soup",
    () -> new com.example.examplemod.item.BowlFoodItem(new Item.Properties()
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
                         net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "horse_armor_crystal"),
                         19.0D,
                         net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE),
                     net.minecraft.world.entity.EquipmentSlotGroup.BODY)
                .build())));    public static final DeferredItem<Item> HORSE_SADDLE = ITEMS.register("horse_saddle",
        () -> new net.minecraft.world.item.SaddleItem(new Item.Properties().stacksTo(16)));
    public static final DeferredItem<Item> UNICORN_HORN = ITEMS.registerSimpleItem("unicorn_horn", new Item.Properties());
    public static final DeferredItem<Item> WHIP = ITEMS.register("whip",
        () -> new com.example.examplemod.item.WhipItem(new Item.Properties().stacksTo(1)));
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
    public static final DeferredItem<Item> FISH_NET = ITEMS.registerSimpleItem("fish_net", new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> FISH_NET_FULL = ITEMS.registerSimpleItem("fish_net_full", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> HAYSTACK = ITEMS.registerSimpleItem("haystack", new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> KEY = ITEMS.registerSimpleItem("key", new Item.Properties());
    public static final DeferredItem<Item> KITTY_BED = ITEMS.registerSimpleItem("kitty_bed", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> KITTY_BED_BLACK = ITEMS.registerSimpleItem("kitty_bed_black", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> KITTY_BED_BLUE = ITEMS.registerSimpleItem("kitty_bed_blue", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> KITTY_BED_BROWN = ITEMS.registerSimpleItem("kitty_bed_brown", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> KITTY_BED_CYAN = ITEMS.registerSimpleItem("kitty_bed_cyan", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> KITTY_BED_GRAY = ITEMS.registerSimpleItem("kitty_bed_gray", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> KITTY_BED_GREEN = ITEMS.registerSimpleItem("kitty_bed_green", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> KITTY_BED_LIGHT_BLUE = ITEMS.registerSimpleItem("kitty_bed_light_blue", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> KITTY_BED_LIME = ITEMS.registerSimpleItem("kitty_bed_lime", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> KITTY_BED_MAGENTA = ITEMS.registerSimpleItem("kitty_bed_magenta", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> KITTY_BED_ORANGE = ITEMS.registerSimpleItem("kitty_bed_orange", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> KITTY_BED_PINK = ITEMS.registerSimpleItem("kitty_bed_pink", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> KITTY_BED_PURPLE = ITEMS.registerSimpleItem("kitty_bed_purple", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> KITTY_BED_RED = ITEMS.registerSimpleItem("kitty_bed_red", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> KITTY_BED_SILVER = ITEMS.registerSimpleItem("kitty_bed_silver", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> KITTY_BED_WHITE = ITEMS.registerSimpleItem("kitty_bed_white", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> KITTY_BED_YELLOW = ITEMS.registerSimpleItem("kitty_bed_yellow", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> KITTY_LITTER = ITEMS.registerSimpleItem("kitty_litter", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> MEDALLION = ITEMS.registerSimpleItem("medallion", new Item.Properties());
    public static final DeferredItem<Item> MOC_EGG = ITEMS.registerSimpleItem("moc_egg", new Item.Properties());
    public static final DeferredItem<Item> MYSTIC_PEAR = ITEMS.registerSimpleItem("mystic_pear", new Item.Properties());
    public static final DeferredItem<Item> NETHER_CANNON = ITEMS.registerSimpleItem("nether_cannon", new Item.Properties());
    public static final DeferredItem<Item> PET_FOOD = ITEMS.registerSimpleItem("pet_food", new Item.Properties());
    public static final ResourceKey<JukeboxSong> SHUFFLING_SONG = ResourceKey.create(Registries.JUKEBOX_SONG, ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "shuffling"));
    public static final DeferredItem<Item> RECORD_SHUFFLE = ITEMS.registerSimpleItem("record_shuffle", new Item.Properties().stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(SHUFFLING_SONG));
    public static final DeferredItem<Item> ROPE = ITEMS.registerSimpleItem("rope", new Item.Properties());
    public static final DeferredItem<Item> SCROLL_OF_FREEDOM = ITEMS.registerSimpleItem("scroll_of_freedom", new Item.Properties());
    public static final DeferredItem<Item> SCROLL_OF_OWNER = ITEMS.registerSimpleItem("scroll_of_owner", new Item.Properties());
    public static final DeferredItem<Item> SCROLL_OF_SALE = ITEMS.registerSimpleItem("scroll_of_sale", new Item.Properties());
    public static final DeferredItem<Item> STAFF = ITEMS.registerSimpleItem("staff", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> STAFF2 = ITEMS.registerSimpleItem("staff2", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> STAFF3 = ITEMS.registerSimpleItem("staff3", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> STAFF_PORTAL = ITEMS.registerSimpleItem("staff_portal", new Item.Properties().stacksTo(1));
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

    // ==== Piezas sueltas — BlockItems ====
    public static final DeferredItem<BlockItem> ORE_FIRESTONE_ITEM = ITEMS.registerSimpleBlockItem("ore_firestone", ModBlocks.ORE_FIRESTONE);
    public static final DeferredItem<BlockItem> GLASS_GLEAMING_ITEM = ITEMS.registerSimpleBlockItem("glass_gleaming", ModBlocks.GLASS_GLEAMING);
    public static final DeferredItem<BlockItem> BLOCK_WYVERN_NEST_ITEM = ITEMS.registerSimpleBlockItem("block_wyvern_nest", ModBlocks.BLOCK_WYVERN_NEST);
    }