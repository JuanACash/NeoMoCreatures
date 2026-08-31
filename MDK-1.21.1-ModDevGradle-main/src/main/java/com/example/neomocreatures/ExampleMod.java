package com.example.neomocreatures;

import org.slf4j.Logger;

import com.example.neomocreatures.client.ModKeyMappings;
import com.example.neomocreatures.init.ModEntities;
import com.example.neomocreatures.init.ModItems;
import com.example.neomocreatures.init.ModSounds;
import com.example.neomocreatures.init.ModTrunkPlacerTypes;
import com.example.neomocreatures.network.ModNetworking;
import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;


// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(ExampleMod.MODID)
public class ExampleMod {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "neomocreatures";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "examplemod" namespace
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "examplemod" namespace
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // Creates a new Block with the id "examplemod:example_block", combining the namespace and path
    public static final DeferredBlock<Block> EXAMPLE_BLOCK = BLOCKS.registerSimpleBlock("example_block", BlockBehaviour.Properties.of().mapColor(MapColor.STONE));
    // Creates a new BlockItem with the id "examplemod:example_block", combining the namespace and path
    public static final DeferredItem<BlockItem> EXAMPLE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("example_block", EXAMPLE_BLOCK);

    // Creates a new food item with the id "examplemod:example_id", nutrition 1 and saturation 2

    // Creates a creative tab with the id "examplemod:example_tab" for the example item, that is placed after the combat tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
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
                    // ==== Piezas sueltas ====
                    output.accept(ModItems.ORE_FIRESTONE_ITEM.get());
                    output.accept(ModItems.FIRESTONE_CHUNK.get());
                    output.accept(ModItems.GLASS_GLEAMING_ITEM.get());
                    output.accept(ModItems.BLOCK_WYVERN_NEST_ITEM.get());

                    output.accept(ModItems.MOC_EGG.get());
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

                    // Spawn eggs al final, solo de los mobs ya implementados
                    output.accept(ModItems.WILD_HORSE_SPAWN_EGG.get());
                    output.accept(ModItems.MOC_HORSE_MOB_SPAWN_EGG.get());


                    //Useless Things.
                    output.accept(ModItems.STAFF.get());
                    output.accept(ModItems.STAFF2.get());
                    output.accept(ModItems.STAFF3.get());
                    output.accept(ModItems.NETHER_CANNON.get());

                }).build());

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public ExampleMod(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);

        com.example.neomocreatures.init.ModArmorMaterials.ARMOR_MATERIALS.register(modEventBus);
        com.example.neomocreatures.init.ModBlocks.BLOCKS.register(modEventBus);

        // --- Mo'Creatures: register our entity types and their attributes ---
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModEntities.registerAttributes(modEventBus);
        ModSounds.SOUND_EVENTS.register(modEventBus);
        modEventBus.addListener(ModEntities::registerSpawnPlacements);
        modEventBus.addListener(ModNetworking::register);
        modEventBus.addListener(ModEntities::registerHorseMobSpawnPlacements);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (ExampleMod) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(com.example.neomocreatures.event.ScorpArmorSetBonusHandler.class);
        NeoForge.EVENT_BUS.register(com.example.neomocreatures.event.NightmareRiderFireImmunityHandler.class);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        modEventBus.addListener(ModKeyMappings::register);

        ModTrunkPlacerTypes.TRUNK_PLACER_TYPES.register(modEventBus);

        com.example.neomocreatures.init.ModParticles.PARTICLE_TYPES.register(modEventBus);
        modEventBus.register(com.example.neomocreatures.client.ModClientParticles.class);

        NeoForge.EVENT_BUS.register(com.example.neomocreatures.client.NightmareOverlayHandler.class);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        ModItems.ITEMS.register(modEventBus);

    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.LOG_DIRT_BLOCK.getAsBoolean()) {
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
        }

        event.enqueueWork(() -> {
            net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent.class.getName(); // solo referencia, ver nota abajo
        });

        LOGGER.info("{}{}", Config.MAGIC_NUMBER_INTRODUCTION.get(), Config.MAGIC_NUMBER.getAsInt());

        Config.ITEM_STRINGS.get().forEach((item) -> LOGGER.info("ITEM >> {}", item));
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(EXAMPLE_BLOCK_ITEM);
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }
}
