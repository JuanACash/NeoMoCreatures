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
import com.example.neomocreatures.client.MoCManticoreModel;
import com.example.neomocreatures.client.MoCManticoreRenderer;
import com.example.neomocreatures.client.MoCOstrichModel;
import com.example.neomocreatures.client.MoCOstrichRenderer;
import com.example.neomocreatures.client.MoCScorpionModel;
import com.example.neomocreatures.client.MoCScorpionRenderer;
import com.example.neomocreatures.client.MoCBearModel;
import com.example.neomocreatures.client.MoCBearRenderer;
import com.example.neomocreatures.client.MoCKomodoDragonModel;
import com.example.neomocreatures.client.MoCKomodoDragonRenderer;
import com.example.neomocreatures.client.MoCFoxModel;
import com.example.neomocreatures.client.MoCFoxRenderer;
import com.example.neomocreatures.client.MoCRaccoonModel;
import com.example.neomocreatures.client.MoCRaccoonRenderer;
import com.example.neomocreatures.client.MoCTurkeyModel;
import com.example.neomocreatures.client.MoCTurkeyRenderer;
import com.example.neomocreatures.client.MoCGoatModel;
import com.example.neomocreatures.client.MoCGoatRenderer;
import com.example.neomocreatures.client.MoCKittyModel;
import com.example.neomocreatures.client.MoCKittyRenderer;
import com.example.neomocreatures.client.MoCKittyBedModel;
import com.example.neomocreatures.client.MoCKittyBedRenderer;
import com.example.neomocreatures.client.MoCLitterBoxModel;
import com.example.neomocreatures.client.MoCLitterBoxRenderer;
import com.example.neomocreatures.client.MoCSnakeModel;
import com.example.neomocreatures.client.MoCSnakeRenderer;
import com.example.neomocreatures.client.MoCBunnyModel;
import com.example.neomocreatures.client.MoCBunnyRenderer;
import com.example.neomocreatures.client.MoCBirdModel;
import com.example.neomocreatures.client.MoCBirdRenderer;
import com.example.neomocreatures.client.MoCSharkModel;
import com.example.neomocreatures.client.MoCSharkRenderer;
import com.example.neomocreatures.client.MoCTurtleModel;
import com.example.neomocreatures.client.MoCTurtleRenderer;
import com.example.neomocreatures.client.MoCStingrayModel;
import com.example.neomocreatures.client.MoCStingrayRenderer;
import com.example.neomocreatures.client.MoCDolphinModel;
import com.example.neomocreatures.client.MoCDolphinRenderer;
import com.example.neomocreatures.client.MoCMantaRayModel;
import com.example.neomocreatures.client.MoCMantaRayRenderer;
import com.example.neomocreatures.client.MoCFishyModel;
import com.example.neomocreatures.client.MoCFishyRenderer;
import com.example.neomocreatures.client.MoCMediumFishModel;
import com.example.neomocreatures.client.MoCMediumFishRenderer;

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
                event.registerLayerDefinition(MoCManticoreRenderer.MOC_MANTICORE_LAYER, MoCManticoreModel::createBodyLayer);
                event.registerLayerDefinition(MoCScorpionRenderer.MOC_SCORPION_LAYER, MoCScorpionModel::createBodyLayer);
                event.registerLayerDefinition(MoCOstrichRenderer.MOC_OSTRICH_LAYER, MoCOstrichModel::createBodyLayer);
                event.registerLayerDefinition(MoCBearRenderer.MOC_BEAR_LAYER, MoCBearModel::createBodyLayer);
                event.registerLayerDefinition(MoCFoxRenderer.MOC_FOX_LAYER, MoCFoxModel::createBodyLayer);
                event.registerLayerDefinition(MoCKomodoDragonRenderer.MOC_KOMODO_LAYER, MoCKomodoDragonModel::createBodyLayer);
                event.registerLayerDefinition(MoCRaccoonRenderer.MOC_RACCOON_LAYER, MoCRaccoonModel::createBodyLayer);
                event.registerLayerDefinition(MoCTurkeyRenderer.MOC_TURKEY_LAYER, MoCTurkeyModel::createBodyLayer);
                event.registerLayerDefinition(MoCGoatRenderer.MOC_GOAT_LAYER, MoCGoatModel::createBodyLayer);
                event.registerLayerDefinition(MoCKittyRenderer.MOC_KITTY_LAYER, MoCKittyModel::createBodyLayer);
                event.registerLayerDefinition(MoCKittyBedRenderer.MOC_KITTY_BED_LAYER, MoCKittyBedModel::createBodyLayer);
                event.registerLayerDefinition(MoCLitterBoxRenderer.MOC_LITTER_BOX_LAYER, MoCLitterBoxModel::createBodyLayer);
                event.registerLayerDefinition(MoCSnakeRenderer.MOC_SNAKE_LAYER, MoCSnakeModel::createBodyLayer);
                event.registerLayerDefinition(MoCBunnyRenderer.MOC_BUNNY_LAYER, MoCBunnyModel::createBodyLayer);
                event.registerLayerDefinition(MoCBirdRenderer.MOC_BIRD_LAYER, MoCBirdModel::createBodyLayer);
                event.registerLayerDefinition(MoCSharkRenderer.MOC_SHARK_LAYER, MoCSharkModel::createBodyLayer);
                event.registerLayerDefinition(MoCTurtleRenderer.MOC_TURTLE_LAYER, MoCTurtleModel::createBodyLayer);
                event.registerLayerDefinition(MoCStingrayRenderer.MOC_STINGRAY_LAYER, MoCStingrayModel::createBodyLayer);
                event.registerLayerDefinition(MoCDolphinRenderer.MOC_DOLPHIN_LAYER, MoCDolphinModel::createBodyLayer);
                event.registerLayerDefinition(MoCMantaRayRenderer.MOC_MANTA_RAY_LAYER, MoCMantaRayModel::createBodyLayer);
                event.registerLayerDefinition(MoCFishyRenderer.MOC_FISHY_LAYER, MoCFishyModel::createBodyLayer);
                event.registerLayerDefinition(MoCMediumFishRenderer.MOC_MEDIUM_FISH_LAYER, MoCMediumFishModel::createBodyLayer);
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
                event.registerEntityRenderer(ModEntities.MOC_MANTICORE.get(), MoCManticoreRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_SCORPION.get(), MoCScorpionRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_OSTRICH.get(), MoCOstrichRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_BEAR.get(), MoCBearRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_FOX.get(), MoCFoxRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_KOMODO_DRAGON.get(), MoCKomodoDragonRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_RACCOON.get(), MoCRaccoonRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_TURKEY.get(), MoCTurkeyRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_GOAT.get(), MoCGoatRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_KITTY.get(), MoCKittyRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_KITTY_BED.get(), MoCKittyBedRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_LITTER_BOX.get(), MoCLitterBoxRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_SNAKE.get(), MoCSnakeRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_BUNNY.get(), MoCBunnyRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_BIRD.get(), MoCBirdRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_SHARK.get(), MoCSharkRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_TURTLE.get(), MoCTurtleRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_STINGRAY.get(), MoCStingrayRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_DOLPHIN.get(), MoCDolphinRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_MANTA_RAY.get(), MoCMantaRayRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_FISHY.get(), MoCFishyRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_COD.get(), MoCMediumFishRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_SALMON.get(), MoCMediumFishRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_BASS.get(), MoCMediumFishRenderer::new);
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