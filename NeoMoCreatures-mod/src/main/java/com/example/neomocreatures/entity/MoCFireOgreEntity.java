package com.example.neomocreatures.entity;

import com.example.neomocreatures.init.ModItems;
import com.example.neomocreatures.util.MoCLootUtil;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

/** Port of {@code drzhark.mocreatures.entity.hostile.MoCEntityFireOgre}. */
public class MoCFireOgreEntity extends MoCOgreEntity {

    private static final double MAX_HEALTH = 65.0D;
    private static final double ARMOR = 9.0D;
    private static final double ATTACK_DAMAGE = 7.5D;
    /** Original: attackEntityFrom(DamageSource.DROWN, 1.0F) every tick it's wet (rain or water). */
    private static final float WET_DAMAGE = 1.0F;

    public MoCFireOgreEntity(EntityType<? extends MoCFireOgreEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return MoCOgreEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.ARMOR, ARMOR)
                .add(Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE);
    }

    @Override
    public String getTextureName() {
        return "ogre_fire";
    }

    @Override
    public double getDestroyRadius() {
        return 2.0D;
    }

    @Override
    public boolean isFireStarter() {
        return true;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide && this.isInWaterRainOrBubble()) {
            this.hurt(this.damageSources().drown(), WET_DAMAGE);
        }
    }

    /** 0-2 Heart of Fire, scaling with Looting. */
    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        MoCLootUtil.dropItems(this, ModItems.HEART_OF_FIRE.get(), MoCLootUtil.rollWithLootingBonus(this.random, 3, MoCLootUtil.getLootingLevel(level, damageSource)));
    }

}