package com.example.examplemod;

import com.example.examplemod.client.MoCHorseMobModel;
import com.example.examplemod.client.MoCHorseMobRenderer;
import com.example.examplemod.client.MoCHorsePlaceholderModel;
import com.example.examplemod.client.MoCHorseRenderer;
import com.example.examplemod.init.ModBlocks;
import com.example.examplemod.init.ModEntities;

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
@Mod(value = ExampleMod.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT)
public class ExampleModClient {
    public ExampleModClient(ModContainer container, IEventBus modEventBus) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        // Mo'Creatures: register the placeholder model layer + renderer for our horse
        modEventBus.addListener((EntityRenderersEvent.RegisterLayerDefinitions event) ->{
                event.registerLayerDefinition(MoCHorseRenderer.MOC_HORSE_LAYER, MoCHorsePlaceholderModel::createBodyLayer);
                event.registerLayerDefinition(MoCHorseMobRenderer.MOC_HORSE_MOB_LAYER, MoCHorseMobModel::createBodyLayer);
    });

        modEventBus.addListener((EntityRenderersEvent.RegisterRenderers event) -> {
                event.registerEntityRenderer(ModEntities.MOC_HORSE.get(), MoCHorseRenderer::new);
                event.registerEntityRenderer(ModEntities.MOC_HORSE_MOB.get(), MoCHorseMobRenderer::new);
        });
                
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        ExampleMod.LOGGER.info("HELLO FROM CLIENT SETUP");
        ExampleMod.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());

        // Wyvern block set: render como "cutout" (transparencia real) en vez de solido
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.WYVWOOD_SAPLING.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.TALL_WYVGRASS.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.WYVWOOD_LEAVES.get(), RenderType.cutout());
        });
    }
}