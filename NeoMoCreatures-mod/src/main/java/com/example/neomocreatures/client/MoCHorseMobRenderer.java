package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.monster.MoCHorseMobEntity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class MoCHorseMobRenderer extends MobRenderer<MoCHorseMobEntity, MoCHorseMobModel> {

    public static final ModelLayerLocation MOC_HORSE_MOB_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_horse_mob"), "main");

    public MoCHorseMobRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCHorseMobModel(context.bakeLayer(MOC_HORSE_MOB_LAYER)), 0.75F);
    }

    @Override
    public ResourceLocation getTextureLocation(MoCHorseMobEntity entity) {
        String fileName = switch (entity.getVariant()) {
            case UNDEAD -> {
                int stage = entity.getDecayStage(); // 0-3
                int frame = 1 + (entity.tickCount / 20) % 7; // 1-7, cambia cada 10 ticks
                yield "horseundead" + stage + frame;
            }
            case SKELETON -> "horseskeleton";
            case BATHORSE -> "horsebat";
            case NIGHTMARE -> {
                int frame = 1 + (entity.tickCount / 5) % 5; // 1-5
                yield "horsenightmare" + frame;
            }
        };
        return ResourceLocation.fromNamespaceAndPath(
                NeoMoCreatures.MODID, "textures/entity/moc_horse_mob/" + fileName + ".png");
    }
}