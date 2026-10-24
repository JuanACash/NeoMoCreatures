package com.example.neomocreatures;

import org.slf4j.Logger;

import com.example.neomocreatures.init.GleamingGlassBlocks;
import com.example.neomocreatures.init.ModArmorMaterials;
import com.example.neomocreatures.init.ModBiomeModifiers;
import com.example.neomocreatures.init.ModBlocks;
import com.example.neomocreatures.init.ModConditions;
import com.example.neomocreatures.init.ModCreativeTabs;
import com.example.neomocreatures.init.ModEntities;
import com.example.neomocreatures.init.ModEntityAttributes;
import com.example.neomocreatures.init.ModFeatures;
import com.example.neomocreatures.init.ModItems;
import com.example.neomocreatures.init.ModParticles;
import com.example.neomocreatures.init.ModSounds;
import com.example.neomocreatures.init.ModSpawnPlacements;
import com.example.neomocreatures.init.ModTrunkPlacerTypes;
import com.example.neomocreatures.init.SilverSandstoneBlocks;
import com.example.neomocreatures.init.WyvstoneBlocks;
import com.example.neomocreatures.init.WyvwoodBlocks;
import com.example.neomocreatures.network.ModNetworking;
import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;


@Mod(NeoMoCreatures.MODID)
public class NeoMoCreatures {
    public static final String MODID = "neomocreatures";
    public static final Logger LOGGER = LogUtils.getLogger();

    public NeoMoCreatures(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        registerDeferredRegistries(modEventBus);
        registerEntitiesAndSounds(modEventBus);
        registerNetworking(modEventBus);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

    }

    private void registerDeferredRegistries(IEventBus modEventBus) {
        WyvwoodBlocks.init();
        WyvstoneBlocks.init();
        SilverSandstoneBlocks.init();
        GleamingGlassBlocks.init();
        // Items are registered through ModItems.ITEMS below.
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        ModArmorMaterials.ARMOR_MATERIALS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModTrunkPlacerTypes.TRUNK_PLACER_TYPES.register(modEventBus);
        ModParticles.PARTICLE_TYPES.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModFeatures.FEATURES.register(modEventBus);
        ModConditions.CONDITIONS.register(modEventBus);
        ModBiomeModifiers.BIOME_MODIFIERS.register(modEventBus);
    }

    private void registerEntitiesAndSounds(IEventBus modEventBus) {
        ModEntities.ENTITY_TYPES.register(modEventBus);
        modEventBus.addListener(ModEntityAttributes::register);
        ModSounds.SOUND_EVENTS.register(modEventBus);
        modEventBus.addListener(ModSpawnPlacements::register);
    }

    private void registerNetworking(IEventBus modEventBus) {
        modEventBus.addListener(ModNetworking::register);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(ModBlocks::registerFlammability);
    }

}