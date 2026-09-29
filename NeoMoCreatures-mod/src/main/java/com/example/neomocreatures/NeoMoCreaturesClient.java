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
import com.example.neomocreatures.client.MoCSmallFishModel;
import com.example.neomocreatures.client.MoCSmallFishRenderer;
import com.example.neomocreatures.client.MoCJellyfishModel;
import com.example.neomocreatures.client.MoCJellyfishRenderer;
import com.example.neomocreatures.client.MoCCrabModel;
import com.example.neomocreatures.client.MoCCrabRenderer;
import com.example.neomocreatures.client.MoCCrocodileModel;
import com.example.neomocreatures.client.MoCCrocodileRenderer;
import com.example.neomocreatures.client.MoCOgreModel;
import com.example.neomocreatures.client.MoCOgreRenderer;
import com.example.neomocreatures.client.MoCWerewolfModel;
import com.example.neomocreatures.client.MoCWerewolfRenderer;
import com.example.neomocreatures.client.MoCWildWolfModel;
import com.example.neomocreatures.client.MoCWildWolfRenderer;
import com.example.neomocreatures.client.MoCBoarModel;
import com.example.neomocreatures.client.MoCBoarRenderer;
import com.example.neomocreatures.client.MoCDeerModel;
import com.example.neomocreatures.client.MoCDeerRenderer;
import com.example.neomocreatures.client.MoCRatModel;
import com.example.neomocreatures.client.MoCRatRenderer;
import com.example.neomocreatures.client.MoCHellRatRenderer;
import com.example.neomocreatures.client.MoCMouseModel;
import com.example.neomocreatures.client.MoCMouseRenderer;
import com.example.neomocreatures.client.MoCMoleModel;
import com.example.neomocreatures.client.MoCMoleRenderer;
import com.example.neomocreatures.client.MoCDuckModel;
import com.example.neomocreatures.client.MoCDuckRenderer;
import com.example.neomocreatures.client.MoCBeeModel;
import com.example.neomocreatures.client.MoCBeeRenderer;
import com.example.neomocreatures.client.MoCButterflyModel;
import com.example.neomocreatures.client.MoCButterflyRenderer;
import com.example.neomocreatures.client.MoCDragonflyModel;
import com.example.neomocreatures.client.MoCDragonflyRenderer;
import com.example.neomocreatures.client.MoCFireflyModel;
import com.example.neomocreatures.client.MoCFireflyRenderer;
import com.example.neomocreatures.client.MoCFlyModel;
import com.example.neomocreatures.client.MoCFlyRenderer;
import com.example.neomocreatures.client.MoCAntModel;
import com.example.neomocreatures.client.MoCAntRenderer;
import com.example.neomocreatures.client.MoCCricketModel;
import com.example.neomocreatures.client.MoCCricketRenderer;
import com.example.neomocreatures.client.MoCGrasshopperModel;
import com.example.neomocreatures.client.MoCGrasshopperRenderer;
import com.example.neomocreatures.client.MoCMaggotModel;
import com.example.neomocreatures.client.MoCMaggotRenderer;
import com.example.neomocreatures.client.MoCRoachModel;
import com.example.neomocreatures.client.MoCRoachRenderer;
import com.example.neomocreatures.client.MoCSnailModel;
import com.example.neomocreatures.client.MoCSnailRenderer;
import com.example.neomocreatures.client.MoCSilverSkeletonModel;
import com.example.neomocreatures.client.MoCSilverSkeletonRenderer;
import com.example.neomocreatures.client.MoCWraithModel;
import com.example.neomocreatures.client.MoCWraithRenderer;
import com.example.neomocreatures.client.MoCEntModel;
import com.example.neomocreatures.client.MoCEntRenderer;
import com.example.neomocreatures.client.MoCMiniGolemModel;
import com.example.neomocreatures.client.MoCMiniGolemRenderer;
import com.example.neomocreatures.client.MoCThrowableRockRenderer;

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
                event.registerLayerDefinition(MoCSmallFishRenderer.MOC_SMALL_FISH_LAYER, MoCSmallFishModel::createBodyLayer);
                event.registerLayerDefinition(MoCJellyfishRenderer.MOC_JELLYFISH_LAYER, MoCJellyfishModel::createBodyLayer);
                event.registerLayerDefinition(MoCCrabRenderer.MOC_CRAB_LAYER, MoCCrabModel::createBodyLayer);
                event.registerLayerDefinition(MoCCrocodileRenderer.MOC_CROCODILE_LAYER, MoCCrocodileModel::createBodyLayer);
                event.registerLayerDefinition(MoCOgreRenderer.MOC_OGRE_LAYER, MoCOgreModel::createBodyLayer);
                event.registerLayerDefinition(MoCWerewolfRenderer.MOC_WEREWOLF_LAYER, MoCWerewolfModel::createBodyLayer);
                event.registerLayerDefinition(MoCWerewolfRenderer.MOC_WEREHUMAN_LAYER,
                        () -> net.minecraft.client.model.geom.builders.LayerDefinition.create(
                                net.minecraft.client.model.HumanoidModel.createMesh(
                                        net.minecraft.client.model.geom.builders.CubeDeformation.NONE, 0.0F),
                                64, 32));
                event.registerLayerDefinition(MoCWildWolfRenderer.MOC_WILD_WOLF_LAYER, MoCWildWolfModel::createBodyLayer);
                event.registerLayerDefinition(MoCBoarRenderer.MOC_BOAR_LAYER, MoCBoarModel::createBodyLayer);
                event.registerLayerDefinition(MoCDeerRenderer.MOC_DEER_LAYER, MoCDeerModel::createBodyLayer);
                event.registerLayerDefinition(MoCRatRenderer.MOC_RAT_LAYER, MoCRatModel::createBodyLayer);
                event.registerLayerDefinition(MoCMouseRenderer.MOC_MOUSE_LAYER, MoCMouseModel::createBodyLayer);
                event.registerLayerDefinition(MoCMoleRenderer.MOC_MOLE_LAYER, MoCMoleModel::createBodyLayer);
                event.registerLayerDefinition(MoCDuckRenderer.MOC_DUCK_LAYER, MoCDuckModel::createBodyLayer);
                event.registerLayerDefinition(MoCFlyRenderer.LAYER, MoCFlyModel::createBodyLayer);
                event.registerLayerDefinition(MoCButterflyRenderer.LAYER, MoCButterflyModel::createBodyLayer);
                event.registerLayerDefinition(MoCDragonflyRenderer.LAYER, MoCDragonflyModel::createBodyLayer);
                event.registerLayerDefinition(MoCFireflyRenderer.LAYER, MoCFireflyModel::createBodyLayer);
                event.registerLayerDefinition(MoCBeeRenderer.LAYER, MoCBeeModel::createBodyLayer);
                event.registerLayerDefinition(MoCSnailRenderer.LAYER, MoCSnailModel::createBodyLayer);
                event.registerLayerDefinition(MoCRoachRenderer.LAYER, MoCRoachModel::createBodyLayer);
                event.registerLayerDefinition(MoCAntRenderer.LAYER, MoCAntModel::createBodyLayer);
                event.registerLayerDefinition(MoCMaggotRenderer.LAYER, MoCMaggotModel::createBodyLayer);
                event.registerLayerDefinition(MoCCricketRenderer.LAYER, MoCCricketModel::createBodyLayer);
                event.registerLayerDefinition(MoCGrasshopperRenderer.LAYER, MoCGrasshopperModel::createBodyLayer);
                event.registerLayerDefinition(MoCSilverSkeletonRenderer.MOC_SILVER_SKELETON_LAYER, MoCSilverSkeletonModel::createBodyLayer);
                event.registerLayerDefinition(MoCWraithRenderer.MOC_WRAITH_LAYER,
                        () -> net.minecraft.client.model.geom.builders.LayerDefinition.create(
                                net.minecraft.client.model.HumanoidModel.createMesh(
                                        net.minecraft.client.model.geom.builders.CubeDeformation.NONE, 0.0F),
                                64, 40));
                event.registerLayerDefinition(MoCEntRenderer.MOC_ENT_LAYER, MoCEntModel::createBodyLayer);
                event.registerLayerDefinition(MoCMiniGolemRenderer.MOC_MINI_GOLEM_LAYER, MoCMiniGolemModel::createBodyLayer);
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
                event.registerEntityRenderer(ModEntities.MOC_SMALL_FISH.get(), MoCSmallFishRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_JELLYFISH.get(), MoCJellyfishRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_CRAB.get(), MoCCrabRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_CROCODILE.get(), MoCCrocodileRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_GREEN_OGRE.get(), MoCOgreRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_FIRE_OGRE.get(), MoCOgreRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_CAVE_OGRE.get(), MoCOgreRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_WEREWOLF.get(), MoCWerewolfRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_WILD_WOLF.get(), MoCWildWolfRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_BOAR.get(), MoCBoarRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_DEER.get(), MoCDeerRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_RAT.get(), MoCRatRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_HELL_RAT.get(), MoCHellRatRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_MOUSE.get(), MoCMouseRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_MOLE.get(), MoCMoleRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_DUCK.get(), MoCDuckRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_FLY.get(), MoCFlyRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_BUTTERFLY.get(), MoCButterflyRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_DRAGONFLY.get(), MoCDragonflyRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_FIREFLY.get(), MoCFireflyRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_BEE.get(), MoCBeeRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_SNAIL.get(), MoCSnailRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_ROACH.get(), MoCRoachRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_ANT.get(), MoCAntRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_MAGGOT.get(), MoCMaggotRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_CRICKET.get(), MoCCricketRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_GRASSHOPPER.get(), MoCGrasshopperRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_SILVER_SKELETON.get(), MoCSilverSkeletonRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_WRAITH.get(), MoCWraithRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_FLAME_WRAITH.get(), (MoCWraithRenderer::new));
                event.registerEntityRenderer(ModEntities.MOC_ENT.get(), MoCEntRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_MINI_GOLEM.get(), MoCMiniGolemRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_THROWABLE_ROCK.get(), MoCThrowableRockRenderer::new);
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