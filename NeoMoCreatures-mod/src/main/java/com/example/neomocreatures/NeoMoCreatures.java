package com.example.neomocreatures;

import org.slf4j.Logger;

import com.example.neomocreatures.client.ModKeyMappings;
import com.example.neomocreatures.init.ModEntities;
import com.example.neomocreatures.init.ModItems;
import com.example.neomocreatures.init.ModSounds;
import com.example.neomocreatures.init.ModTrunkPlacerTypes;
import net.neoforged.fml.config.ModConfig;
import com.example.neomocreatures.network.ModNetworking;
import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;


@Mod(NeoMoCreatures.MODID)
public class NeoMoCreatures {
    public static final String MODID = "neomocreatures";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // Creative tab with all of Mo'Creatures' items already implemented
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MOC_CREATURES_TAB = CREATIVE_MODE_TABS.register("moc_creatures_tab", () -> CreativeModeTab.builder()
        .title(Component.translatable("itemGroup.neomocreatures"))
        .withTabsBefore(CreativeModeTabs.COMBAT)
        .icon(() -> ModItems.PET_AMULET_FULL.get().getDefaultInstance())
                .displayItems((parameters, output) -> {
                    output.accept(ModItems.WYVSTONE_ITEM.get());
                    output.accept(ModItems.COBBLED_WYVSTONE_ITEM.get());
                    output.accept(ModItems.DEEP_WYVSTONE_ITEM.get());
                    output.accept(ModItems.COBBLED_DEEP_WYVSTONE_ITEM.get());
                    output.accept(ModItems.MOSSY_COBBLED_WYVSTONE_ITEM.get());
                    output.accept(ModItems.MOSSY_COBBLED_DEEP_WYVSTONE_ITEM.get());
                    output.accept(ModItems.WYVDIRT_ITEM.get());
                    output.accept(ModItems.WYVGRASS_ITEM.get());
                    output.accept(ModItems.TALL_WYVGRASS_ITEM.get());
                    output.accept(ModItems.WYVWOOD_LOG_ITEM.get());
                    output.accept(ModItems.WYVWOOD_LEAVES_ITEM.get());
                    output.accept(ModItems.WYVWOOD_PLANKS_ITEM.get());
                    output.accept(ModItems.WYVWOOD_SAPLING_ITEM.get());
                    output.accept(ModItems.WYVERN_DIAMOND_ORE_ITEM.get());
                    output.accept(ModItems.WYVERN_EMERALD_ORE_ITEM.get());
                    output.accept(ModItems.WYVERN_LAPIS_ORE_ITEM.get());
                    output.accept(ModItems.WYVERN_GOLD_ORE_ITEM.get());
                    output.accept(ModItems.WYVERN_IRON_ORE_ITEM.get());
                    output.accept(ModItems.WYVERN_ANCIENT_ORE_ITEM.get());
                    output.accept(ModItems.DIRT_OGRE_LAIR_ITEM.get());
                    output.accept(ModItems.GRASS_OGRE_LAIR_ITEM.get());
                    output.accept(ModItems.TALL_GRASS_OGRE_LAIR_ITEM.get());
                    output.accept(ModItems.LOG_OGRE_LAIR_ITEM.get());
                    output.accept(ModItems.LEAVES_OGRE_LAIR_ITEM.get());
                    output.accept(ModItems.WOOD_PLANKS_OGRE_LAIR_ITEM.get());
                    output.accept(ModItems.SILVER_SAND_ITEM.get());
                    output.accept(ModItems.SILVER_SANDSTONE_ITEM.get());
                    output.accept(ModItems.SILVER_SANDSTONE_CARVED_ITEM.get());
                    output.accept(ModItems.SILVER_SANDSTONE_SMOOTH_ITEM.get());
                    // ==== Loose pieces ====
                    output.accept(ModItems.ORE_FIRESTONE_ITEM.get());
                    output.accept(ModItems.FIRESTONE_CHUNK.get());
                    output.accept(ModItems.GLASS_GLEAMING_ITEM.get());
                    output.accept(ModItems.BLOCK_WYVERN_NEST_ITEM.get());

                    output.accept(ModItems.MOC_EGG.get());
                    output.accept(ModItems.JUNGLE_WYVERN_EGG.get());
                    output.accept(ModItems.SWAMP_WYVERN_EGG.get());
                    output.accept(ModItems.SAND_WYVERN_EGG.get());
                    output.accept(ModItems.SUN_WYVERN_EGG.get());
                    output.accept(ModItems.ARCTIC_WYVERN_EGG.get());
                    output.accept(ModItems.CAVE_WYVERN_EGG.get());
                    output.accept(ModItems.MOUNTAIN_WYVERN_EGG.get());
                    output.accept(ModItems.SEA_WYVERN_EGG.get());
                    output.accept(ModItems.MOTHER_WYVERN_EGG.get());
                    output.accept(ModItems.DIRT_SCORPION_EGG.get());
                    output.accept(ModItems.CAVE_SCORPION_EGG.get());
                    output.accept(ModItems.FROST_SCORPION_EGG.get());
                    output.accept(ModItems.FIRE_SCORPION_EGG.get());
                    output.accept(ModItems.UNDEAD_SCORPION_EGG.get());
                    output.accept(ModItems.PLAIN_MANTICORE_EGG.get());
                    output.accept(ModItems.DARK_MANTICORE_EGG.get());
                    output.accept(ModItems.FROST_MANTICORE_EGG.get());
                    output.accept(ModItems.FIRE_MANTICORE_EGG.get());
                    output.accept(ModItems.TOXIC_MANTICORE_EGG.get());
                    output.accept(ModItems.DIRT_SCORPION_EGG.get());
                    output.accept(ModItems.CAVE_SCORPION_EGG.get());
                    output.accept(ModItems.FROST_SCORPION_EGG.get());
                    output.accept(ModItems.FIRE_SCORPION_EGG.get());
                    output.accept(ModItems.UNDEAD_SCORPION_EGG.get());
                    output.accept(ModItems.OSTRICH_EGG.get());
                    output.accept(ModItems.KOMODO_DRAGON_EGG.get());

                    output.accept(ModItems.HORSE_SADDLE.get());
                    output.accept(ModItems.HORSE_ARMOR_CRYSTAL.get());
                    output.accept(ModItems.SHARK_TEETH.get());
                    output.accept(ModItems.FISH_NET.get());
                    output.accept(ModItems.FISH_NET_FULL.get());
                    output.accept(ModItems.HAYSTACK.get());
                    output.accept(ModItems.SUGAR_LUMP.get());
                    output.accept(ModItems.BIG_CAT_CLAW.get());
                    output.accept(ModItems.WHIP.get());
                    output.accept(ModItems.MEDALLION.get());
                    output.accept(ModItems.KITTY_LITTER.get());
                    output.accept(ModItems.KITTY_BED.get());
                    output.accept(ModItems.KITTY_BED_WHITE.get());
                    output.accept(ModItems.KITTY_BED_ORANGE.get());
                    output.accept(ModItems.KITTY_BED_MAGENTA.get());
                    output.accept(ModItems.KITTY_BED_LIGHT_BLUE.get());
                    output.accept(ModItems.KITTY_BED_YELLOW.get());
                    output.accept(ModItems.KITTY_BED_LIME.get());
                    output.accept(ModItems.KITTY_BED_PINK.get());
                    output.accept(ModItems.KITTY_BED_GRAY.get());
                    output.accept(ModItems.KITTY_BED_SILVER.get());
                    output.accept(ModItems.KITTY_BED_CYAN.get());
                    output.accept(ModItems.KITTY_BED_PURPLE.get());
                    output.accept(ModItems.KITTY_BED_BLUE.get());
                    output.accept(ModItems.KITTY_BED_BROWN.get());
                    output.accept(ModItems.KITTY_BED_GREEN.get());
                    output.accept(ModItems.KITTY_BED_RED.get());
                    output.accept(ModItems.KITTY_BED_BLACK.get());
                    output.accept(ModItems.WOOL_BALL.get());
                    output.accept(ModItems.PET_FOOD.get());
                    output.accept(ModItems.STAFF_PORTAL.get());
                    output.accept(ModItems.STAFF_TELEPORT.get());
                    output.accept(ModItems.BUILDER_HAMMER.get());
                    output.accept(ModItems.ESSENCE_OF_DARKNESS.get());
                    output.accept(ModItems.ESSENCE_OF_FIRE.get());
                    output.accept(ModItems.ESSENCE_OF_UNDEAD.get());
                    output.accept(ModItems.ESSENCE_OF_LIGHT.get());
                    output.accept(ModItems.AMULET_BONE.get());
                    output.accept(ModItems.AMULET_BONE_FULL.get());
                    output.accept(ModItems.AMULET_GHOST.get());
                    output.accept(ModItems.AMULET_GHOST_FULL.get());
                    output.accept(ModItems.AMULET_FAIRY.get());
                    output.accept(ModItems.AMULET_FAIRY_FULL.get());
                    output.accept(ModItems.AMULET_PEGASUS.get());
                    output.accept(ModItems.AMULET_PEGASUS_FULL.get());
                    output.accept(ModItems.PET_AMULET.get());
                    output.accept(ModItems.PET_AMULET_FULL.get());
                    output.accept(ModItems.HEART_OF_DARKNESS.get());
                    output.accept(ModItems.HEART_OF_FIRE.get());
                    output.accept(ModItems.HEART_OF_UNDEAD.get());
                    output.accept(ModItems.UNICORN_HORN.get());
                    output.accept(ModItems.RECORD_SHUFFLE.get());
                    output.accept(ModItems.OMELET.get());
                    output.accept(ModItems.OSTRICH_RAW.get());
                    output.accept(ModItems.OSTRICH_COOKED.get());
                    output.accept(ModItems.TURTLE_RAW.get());
                    output.accept(ModItems.TURTLE_COOKED.get());
                    output.accept(ModItems.TURTLE_SOUP.get());
                    output.accept(ModItems.TURKEY_RAW.get());
                    output.accept(ModItems.TURKEY_COOKED.get());
                    output.accept(ModItems.DUCK_RAW.get());
                    output.accept(ModItems.DUCK_COOKED.get());
                    output.accept(ModItems.RAT_RAW.get());
                    output.accept(ModItems.RAT_COOKED.get());
                    output.accept(ModItems.RAT_BURGER.get());
                    output.accept(ModItems.VENISON_RAW.get());
                    output.accept(ModItems.VENISON_COOKED.get());
                    output.accept(ModItems.CRAB_RAW.get());
                    output.accept(ModItems.CRAB_COOKED.get());
                    output.accept(ModItems.MYSTIC_PEAR.get());
                    output.accept(ModItems.TUSKS_WOOD.get());
                    output.accept(ModItems.TUSKS_IRON.get());
                    output.accept(ModItems.TUSKS_DIAMOND.get());
                    output.accept(ModItems.ELEPHANT_CHEST.get());
                    output.accept(ModItems.ELEPHANT_GARMENT.get());
                    output.accept(ModItems.ELEPHANT_HARNESS.get());
                    output.accept(ModItems.ELEPHANT_HOWDAH.get());
                    output.accept(ModItems.MAMMOTH_PLATFORM.get());
                    output.accept(ModItems.SCROLL_OF_FREEDOM.get());
                    output.accept(ModItems.SCROLL_OF_SALE.get());
                    output.accept(ModItems.SCROLL_OF_OWNER.get());
                    output.accept(ModItems.REPTILE_HIDE.get());
                    output.accept(ModItems.REPTILE_HELMET.get());
                    output.accept(ModItems.REPTILE_PLATE.get());
                    output.accept(ModItems.REPTILE_LEGS.get());
                    output.accept(ModItems.REPTILE_BOOTS.get());
                    output.accept(ModItems.FUR.get());
                    output.accept(ModItems.FUR_HELMET.get());
                    output.accept(ModItems.FUR_CHEST.get());
                    output.accept(ModItems.FUR_LEGS.get());
                    output.accept(ModItems.FUR_BOOTS.get());
                    output.accept(ModItems.HIDE.get());
                    output.accept(ModItems.HIDE_HELMET.get());
                    output.accept(ModItems.HIDE_CHEST.get());
                    output.accept(ModItems.HIDE_LEGS.get());
                    output.accept(ModItems.HIDE_BOOTS.get());
                    output.accept(ModItems.CHITIN.get());
                    output.accept(ModItems.SCORP_HELMET_DIRT.get());
                    output.accept(ModItems.SCORP_PLATE_DIRT.get());
                    output.accept(ModItems.SCORP_LEGS_DIRT.get());
                    output.accept(ModItems.SCORP_BOOTS_DIRT.get());
                    output.accept(ModItems.SCORP_SWORD_DIRT.get());
                    output.accept(ModItems.SCORP_AXE_DIRT.get());
                    output.accept(ModItems.SCORP_STING_DIRT.get());
                    output.accept(ModItems.CHITIN_BLACK.get());
                    output.accept(ModItems.SCORP_HELMET_CAVE.get());
                    output.accept(ModItems.SCORP_PLATE_CAVE.get());
                    output.accept(ModItems.SCORP_LEGS_CAVE.get());
                    output.accept(ModItems.SCORP_BOOTS_CAVE.get());
                    output.accept(ModItems.SCORP_SWORD_CAVE.get());
                    output.accept(ModItems.SCORP_AXE_CAVE.get());
                    output.accept(ModItems.SCORP_STING_CAVE.get());
                    output.accept(ModItems.CHITIN_FROST.get());
                    output.accept(ModItems.SCORP_HELMET_FROST.get());
                    output.accept(ModItems.SCORP_PLATE_FROST.get());
                    output.accept(ModItems.SCORP_LEGS_FROST.get());
                    output.accept(ModItems.SCORP_BOOTS_FROST.get());
                    output.accept(ModItems.SCORP_SWORD_FROST.get());
                    output.accept(ModItems.SCORP_AXE_FROST.get());
                    output.accept(ModItems.SCORP_STING_FROST.get());
                    output.accept(ModItems.CHITIN_NETHER.get());
                    output.accept(ModItems.SCORP_HELMET_NETHER.get());
                    output.accept(ModItems.SCORP_PLATE_NETHER.get());
                    output.accept(ModItems.SCORP_LEGS_NETHER.get());
                    output.accept(ModItems.SCORP_BOOTS_NETHER.get());
                    output.accept(ModItems.SCORP_SWORD_NETHER.get());
                    output.accept(ModItems.SCORP_AXE_NETHER.get());
                    output.accept(ModItems.SCORP_STING_NETHER.get());
                    output.accept(ModItems.CHITIN_UNDEAD.get());
                    output.accept(ModItems.SCORP_HELMET_UNDEAD.get());
                    output.accept(ModItems.SCORP_PLATE_UNDEAD.get());
                    output.accept(ModItems.SCORP_LEGS_UNDEAD.get());
                    output.accept(ModItems.SCORP_BOOTS_UNDEAD.get());
                    output.accept(ModItems.SCORP_SWORD_UNDEAD.get());
                    output.accept(ModItems.SCORP_AXE_UNDEAD.get());
                    output.accept(ModItems.SCORP_STING_UNDEAD.get());
                    output.accept(ModItems.ANCIENT_SILVER_SCRAP.get());
                    output.accept(ModItems.ANCIENT_SILVER_INGOT.get());
                    output.accept(ModItems.ANCIENT_SILVER_NUGGET.get());
                    output.accept(ModItems.ANCIENT_SILVER_BLOCK_ITEM.get());
                    output.accept(ModItems.ANCIENT_SILVER_HELMET.get());
                    output.accept(ModItems.ANCIENT_SILVER_CHESTPLATE.get());
                    output.accept(ModItems.ANCIENT_SILVER_LEGGINGS.get());
                    output.accept(ModItems.ANCIENT_SILVER_BOOTS.get());
                    output.accept(ModItems.SILVER_SWORD.get());
                    output.accept(ModItems.SILVER_AXE.get());
                    output.accept(ModItems.KATANA.get());
                    output.accept(ModItems.BO.get());
                    output.accept(ModItems.SAI.get());
                    output.accept(ModItems.NUNCHAKU.get());
                    output.accept(ModItems.SHARK_SWORD.get());
                    output.accept(ModItems.SHARK_AXE.get());

                    // Spawn eggs
                    output.accept(ModItems.WILD_HORSE_SPAWN_EGG.get());
                    output.accept(ModItems.MOC_HORSE_MOB_SPAWN_EGG.get());
                    output.accept(ModItems.WYVERN_SPAWN_EGG.get());
                    output.accept(ModItems.MOC_ELEPHANT_SPAWN_EGG.get());
                    output.accept(ModItems.OSTRICH_SPAWN_EGG.get());
                    output.accept(ModItems.KITTY_SPAWN_EGG.get());
                    output.accept(ModItems.LION_SPAWN_EGG.get());
                    output.accept(ModItems.TIGER_SPAWN_EGG.get());
                    output.accept(ModItems.PANTHER_SPAWN_EGG.get());
                    output.accept(ModItems.LEOPARD_SPAWN_EGG.get());
                    output.accept(ModItems.LEOGER_SPAWN_EGG.get());
                    output.accept(ModItems.LIARD_SPAWN_EGG.get());
                    output.accept(ModItems.LIGER_SPAWN_EGG.get());
                    output.accept(ModItems.LITHER_SPAWN_EGG.get());
                    output.accept(ModItems.PANTHARD_SPAWN_EGG.get());
                    output.accept(ModItems.PANTHGER_SPAWN_EGG.get());
                    output.accept(ModItems.BLACK_BEAR_SPAWN_EGG.get());
                    output.accept(ModItems.GRIZZLY_BEAR_SPAWN_EGG.get());
                    output.accept(ModItems.POLAR_BEAR_SPAWN_EGG.get());
                    output.accept(ModItems.PANDA_BEAR_SPAWN_EGG.get());
                    output.accept(ModItems.FOX_SPAWN_EGG.get());
                    output.accept(ModItems.RACCOON_SPAWN_EGG.get());
                    output.accept(ModItems.TURKEY_SPAWN_EGG.get());
                    output.accept(ModItems.GOAT_SPAWN_EGG.get());
                    output.accept(ModItems.KOMODO_DRAGON_SPAWN_EGG.get());
                    output.accept(ModItems.MANTICORE_SPAWN_EGG.get());
                    output.accept(ModItems.SCORPION_SPAWN_EGG.get());

                }).build());

    public NeoMoCreatures(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        registerDeferredRegistries(modEventBus);
        registerEntitiesAndSounds(modEventBus);
        registerNetworkingAndClient(modEventBus);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(com.example.neomocreatures.event.ScorpArmorSetBonusHandler.class);
        NeoForge.EVENT_BUS.register(com.example.neomocreatures.event.NightmareRiderFireImmunityHandler.class);
        NeoForge.EVENT_BUS.register(com.example.neomocreatures.client.NightmareOverlayHandler.class);
        NeoForge.EVENT_BUS.register(com.example.neomocreatures.event.ScorpionHoldReleaseHandler.class);
        NeoForge.EVENT_BUS.register(new com.example.neomocreatures.entity.CaveScorpionSpawner());
    }

    private void registerDeferredRegistries(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        com.example.neomocreatures.init.ModArmorMaterials.ARMOR_MATERIALS.register(modEventBus);
        com.example.neomocreatures.init.ModBlocks.BLOCKS.register(modEventBus);
        ModTrunkPlacerTypes.TRUNK_PLACER_TYPES.register(modEventBus);
        com.example.neomocreatures.init.ModParticles.PARTICLE_TYPES.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        com.example.neomocreatures.init.ModFeatures.FEATURES.register(modEventBus);
    }

    private void registerEntitiesAndSounds(IEventBus modEventBus) {
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModEntities.registerAttributes(modEventBus);
        ModSounds.SOUND_EVENTS.register(modEventBus);
        modEventBus.addListener(ModEntities::registerSpawnPlacements);
        modEventBus.addListener(ModEntities::registerHorseMobSpawnPlacements);
        modEventBus.addListener(ModEntities::registerWyvernSpawnPlacements);
        modEventBus.addListener(ModEntities::registerManticoreSpawnPlacements);
        modEventBus.addListener(ModEntities::registerElephantSpawnPlacements);
        modEventBus.addListener(ModEntities::registerBigCatSpawnPlacements);
        modEventBus.addListener(ModEntities::registerScorpionSpawnPlacements);
        modEventBus.addListener(ModEntities::registerOstrichSpawnPlacements);
        modEventBus.addListener(ModEntities::registerBearSpawnPlacements);
        modEventBus.addListener(ModEntities::registerKomodoSpawnPlacements);
        modEventBus.addListener(ModEntities::registerFoxSpawnPlacements);
        modEventBus.addListener(ModEntities::registerRaccoonSpawnPlacements);
        modEventBus.addListener(ModEntities::registerTurkeySpawnPlacements);
        modEventBus.addListener(ModEntities::registerGoatSpawnPlacements);
    }

    private void registerNetworkingAndClient(IEventBus modEventBus) {
        modEventBus.addListener(ModNetworking::register);
        modEventBus.addListener(ModKeyMappings::register);
        modEventBus.register(com.example.neomocreatures.client.ModClientParticles.class);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("neomocreatures common setup complete");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }
}