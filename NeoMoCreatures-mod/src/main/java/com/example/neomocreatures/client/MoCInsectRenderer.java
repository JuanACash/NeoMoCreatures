package com.example.neomocreatures.client;

import javax.annotation.Nullable;

import com.example.neomocreatures.entity.MoCInsectEntity;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;

/** Port of {@code MoCRenderInsect}: no shadow, uniform size factor, and a translucent render type so
 *  the wings can be drawn semi-transparent. */
public abstract class MoCInsectRenderer<T extends MoCInsectEntity, M extends EntityModel<T>> extends MobRenderer<T, M> {

    protected MoCInsectRenderer(EntityRendererProvider.Context context, M model) {
        super(context, model, 0.0F);
    }

    @Override
    protected void scale(T entity, PoseStack poseStack, float partialTick) {
        float size = entity.getSizeFactor();
        poseStack.scale(size, size, size);
    }

    @Nullable
    @Override
    protected RenderType getRenderType(T entity, boolean bodyVisible, boolean translucent, boolean glowing) {
        if (bodyVisible) {
            return RenderType.entityTranslucent(this.getTextureLocation(entity));
        }
        return super.getRenderType(entity, bodyVisible, translucent, glowing);
    }
}
