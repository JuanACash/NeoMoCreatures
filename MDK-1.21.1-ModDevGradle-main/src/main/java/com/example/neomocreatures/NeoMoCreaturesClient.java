package com.example.neomocreatures;

import com.example.neomocreatures.client.MoCHorseMobModel;
import com.example.neomocreatures.client.MoCHorseMobRenderer;
import com.example.neomocreatures.client.MoCHorseModel;
import com.example.neomocreatures.client.MoCHorseRenderer;
import com.example.neomocreatures.client.MoCEggModel;
import com.example.neomocreatures.client.MoCEggRenderer;
import com.example.neomocreatures.client.MoCWyvernModel;
import com.example.neomocreatures.client.MoCWyvernRenderer;
import com.example.neomocreatures.client.MoCElephantModel;
import com.example.neomocreatures.client.MoCElephantRenderer;
import com.example.neomocreatures.init.ModBlocks;
import com.example.neomocreatures.init.ModEntities;
import com.example.neomocreatures.client.MoCBigCatModel;
import com.example.neomocreatures.client.MoCBigCatRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = NeoMoCreatures.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = NeoMoCreatures.MODID, value = Dist.CLIENT)
public class NeoMoCreaturesClient {
    public NeoMoCreaturesClient(ModContainer container, IEventBus modEventBus) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        // Mo'Creatures: register the placeholder model layer + renderer for our horse
        modEventBus.addListener((EntityRenderersEvent.RegisterLayerDefinitions event) ->{
                event.registerLayerDefinition(MoCHorseRenderer.MOC_HORSE_LAYER, MoCHorseModel::createBodyLayer);
                event.registerLayerDefinition(MoCHorseMobRenderer.MOC_HORSE_MOB_LAYER, MoCHorseMobModel::createBodyLayer);
                event.registerLayerDefinition(MoCWyvernRenderer.MOC_WYVERN_LAYER, MoCWyvernModel::createBodyLayer);
                event.registerLayerDefinition(MoCEggRenderer.MOC_EGG_LAYER, MoCEggModel::createBodyLayer);
                event.registerLayerDefinition(MoCElephantRenderer.MOC_ELEPHANT_LAYER, MoCElephantModel::createBodyLayer);
                event.registerLayerDefinition(MoCBigCatRenderer.MOC_BIG_CAT_LAYER, MoCBigCatModel::createBodyLayer);
    });

        modEventBus.addListener((EntityRenderersEvent.RegisterRenderers event) -> {
                event.registerEntityRenderer(ModEntities.MOC_HORSE.get(), MoCHorseRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_HORSE_MOB.get(), MoCHorseMobRenderer::new);
                event.registerEntityRenderer(ModEntities.WYVERN.get(), MoCWyvernRenderer::new);
                event.registerEntityRenderer(ModEntities.WYVERN_TIER2.get(), MoCWyvernRenderer::new);
                event.registerEntityRenderer(ModEntities.WYVERN_MOTHER.get(), MoCWyvernRenderer::new);
                event.registerEntityRenderer(ModEntities.WYVERN_MOTHER_TAMED.get(), MoCWyvernRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_EGG.get(), MoCEggRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_ELEPHANT.get(), MoCElephantRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_BIG_CAT.get(), MoCBigCatRenderer::new);
        });
                
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        NeoMoCreatures.LOGGER.info("HELLO FROM CLIENT SETUP");
        NeoMoCreatures.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());

        // Wyvern block set: render as "cutout" 
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.WYVWOOD_SAPLING.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.TALL_WYVGRASS.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.WYVWOOD_LEAVES.get(), RenderType.cutout());
        });
    }
}