package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.breeding.MoCHorseGenetics;
import com.example.neomocreatures.entity.MoCHorseEntity;
import com.example.neomocreatures.init.ModItems;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

import com.mojang.blaze3d.vertex.PoseStack;

public class MoCHorseRenderer extends MobRenderer<MoCHorseEntity, MoCHorseModel> {

    private static final EntityTextureCache TEXTURE_CACHE = new EntityTextureCache("moc_horse");

    public static final ModelLayerLocation MOC_HORSE_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_horse"), "main");

    public MoCHorseRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCHorseModel(context.bakeLayer(MOC_HORSE_LAYER)), 0.75F);
    }

    @Override
    protected void scale(MoCHorseEntity entity, PoseStack poseStack, float partialTick) {
        float scale;
        if (entity.isBaby()) {
            int age = entity.getSyncedAge(); // NOT getAge() — that's server-only
            float progress = (age + 24000F) / 24000F;
            progress = Math.max(0F, Math.min(1F, progress));
            scale = 0.5F + 0.5F * progress;
        } else {
            scale = 1.0F;
        }
        poseStack.scale(scale, scale, scale);
    }

    @Override
    protected RenderType getRenderType(MoCHorseEntity entity, boolean bodyVisible, boolean translucent, boolean showOutline) {
        boolean isGhost = entity.getSpecies() == MoCHorseGenetics.Species.GHOST
                || entity.getSpecies() == MoCHorseGenetics.Species.GHOST_WINGED;
        return super.getRenderType(entity, bodyVisible, translucent || isGhost || entity.isVanishing(), showOutline);
    }

    @Override
    public Vec3 getRenderOffset(MoCHorseEntity entity, float partialTicks) {
        Vec3 offset = super.getRenderOffset(entity, partialTicks);
        if (entity.getSpecies() == MoCHorseGenetics.Species.GHOST
                || entity.getSpecies() == MoCHorseGenetics.Species.GHOST_WINGED) {
            offset = offset.add(0D, 0.3D, 0D);
        }
        return offset;
    }

    @Override
    public void render(MoCHorseEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        boolean isGhost = entity.getSpecies() == MoCHorseGenetics.Species.GHOST
                || entity.getSpecies() == MoCHorseGenetics.Species.GHOST_WINGED;
        if (!isGhost && !entity.isVanishing()) {
            super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
            return;
        }

        float alphaF = isGhost
                ? (entity.getSpecies() == MoCHorseGenetics.Species.GHOST_WINGED ? 0.35F : 0.6F)
                : entity.getVanishAlpha();
        int alphaInt = (int) (alphaF * 255F);

        MultiBufferSource alphaBuffer = renderType ->
                new AlphaVertexConsumer(buffer.getBuffer(renderType), alphaInt);
        super.render(entity, entityYaw, partialTicks, poseStack, alphaBuffer, packedLight);
    }

    @Override
    protected void renderNameTag(MoCHorseEntity entity, Component displayName, PoseStack poseStack, MultiBufferSource buffer, int packedLight, float partialTick) {
        TameableOverlayRenderer.renderHealthBar(entity, poseStack, buffer, packedLight, this.entityRenderDispatcher);
        super.renderNameTag(entity, displayName, poseStack, buffer, packedLight, partialTick);
    }

    @Override
    public ResourceLocation getTextureLocation(MoCHorseEntity entity) {
        if (entity.isUndeadTransforming()) {
            int ticksLeft = entity.getUndeadTransformTicks();
            int interval = Math.max(1, ticksLeft / 8);
            boolean showUndead = (entity.tickCount / interval) % 2 == 0;
            if (showUndead) {
                String flickerFile = switch (entity.getSpecies()) {
                    case UNICORN -> "horseundeadunicorn01";
                    case PEGASUS, DARK_PEGASUS -> "horseundeadpegasus01";
                    default -> "horseundead01";
                };
                return TEXTURE_CACHE.get(flickerFile);
            }
            return textureFor(entity.getSpecies(), entity);
        }
        if (entity.isUndead()) {
            String fileName;
            if (entity.isSkeletonStage()) {
                fileName = switch (entity.getSpecies()) {
                    case UNICORN -> "horseunicornskeleton";
                    case PEGASUS, DARK_PEGASUS -> "horsepegasusskeleton";
                    default -> "horseskeleton";
                };
            } else {
                int stage = entity.getUndeadStage() - MoCHorseEntity.UNDEAD_STAGE_0; // 0-3
                String prefix;
                int frameCount;
                switch (entity.getSpecies()) {
                    case UNICORN -> { prefix = "horseundeadunicorn"; frameCount = 6; }
                    case PEGASUS, DARK_PEGASUS -> { prefix = "horseundeadpegasus"; frameCount = 7; }
                    default -> { prefix = "horseundead"; frameCount = 7; }
                }
                int frame = 1 + (entity.tickCount / 20) % frameCount;
                fileName = prefix + stage + frame;
            }
            return TEXTURE_CACHE.get(fileName);
        }
        if (entity.isTransforming()) {
            int ticksLeft = entity.getTransformTicks();
            int interval = Math.max(1, ticksLeft / 8);
            boolean showTarget = (entity.tickCount / interval) % 2 == 0;
            var species = showTarget ? entity.getTransformTarget() : entity.getSpecies();
            return textureFor(species, entity);
        }
        if (entity.isColorTransforming()) {
            int ticksLeft = entity.getColorTransformTicks();
            int interval = Math.max(1, ticksLeft / 8);
            boolean showTarget = (entity.tickCount / interval) % 2 == 0;
            var targetColor = entity.getColorTransformTarget();
            String fileName = "horsefairy" + (showTarget ? targetColor.name().toLowerCase() : entity.getFairyColor().name().toLowerCase());
            return TEXTURE_CACHE.get(fileName);
        }
        return textureFor(entity.getSpecies(), entity);
    }
        
    private ResourceLocation textureFor(MoCHorseGenetics.Species species, MoCHorseEntity entity) {
        String fileName = switch (species) {
            case ZEBRA -> "horsezebra" + armorSuffix(entity);
            case DONKEY -> "horsedonkey";
            case MULE -> "horsemule";
            case ZONKY -> "horsezonky";
            case ZORSE -> "horsezorse" + armorSuffix(entity);
            case BATHORSE -> "horsebat" + crystalSuffix(entity);
            case NIGHTMARE -> {
                int frame = 1 + (entity.tickCount / 5) % 5;
                yield "horsenightmare" + frame;
            }
            case UNICORN -> "horseunicorn" + crystalSuffix(entity);
            case PEGASUS -> "horsepegasus" + crystalSuffix(entity);
            case DARK_PEGASUS -> "horsedarkpegasus" + crystalSuffix(entity);
            case GHOST -> "horseghostb" + crystalSuffix(entity);
            case GHOST_WINGED -> "horseghost" + crystalSuffix(entity);
            case HORSE_BUG -> "horsebug";
            case FAIRY_HORSE -> "horsefairy" + entity.getFairyColor().name().toLowerCase() + crystalSuffix(entity);
            case HORSE -> "horse" + entity.getCoat().name().toLowerCase() + armorSuffix(entity);
        };
        return TEXTURE_CACHE.get(fileName);
    }
    private static String armorSuffix(MoCHorseEntity entity) {
        ItemStack armorItem = entity.getItemBySlot(EquipmentSlot.BODY);
        if (armorItem.is(Items.IRON_HORSE_ARMOR)) return "metal";
        if (armorItem.is(Items.GOLDEN_HORSE_ARMOR)) return "gold";
        if (armorItem.is(Items.DIAMOND_HORSE_ARMOR)) return "diamond";
        return "";
    }

    private static String crystalSuffix(MoCHorseEntity entity) {
        ItemStack armorItem = entity.getItemBySlot(EquipmentSlot.BODY);
        return armorItem.is(ModItems.HORSE_ARMOR_CRYSTAL.get()) ? "crystaline" : "";
    }
}