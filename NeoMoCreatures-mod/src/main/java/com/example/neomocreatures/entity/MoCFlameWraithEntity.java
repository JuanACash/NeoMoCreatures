package com.example.neomocreatures.entity;

import com.example.neomocreatures.util.MoCLootUtil;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

/**
 * Port of {@code drzhark.mocreatures.entity.hostile.MoCEntityFlameWraith}. Fire immune, and ignites
 * whatever it hits for 30 ticks (respecting mobGriefing's fire counterpart, doFireTick). Unlike the
 * base Wraith, it doesn't ignite in sunlight — being fire-immune would make that pointless — instead
 * it takes 2 direct damage per qualifying tick.
 */
public class MoCFlameWraithEntity extends MoCWraithEntity {

    private static final double MAX_HEALTH = 25.0D;
    private static final double ATTACK_DAMAGE = 4.0D;
    private static final int IGNITE_SECONDS = 15;

    public MoCFlameWraithEntity(EntityType<? extends MoCFlameWraithEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return MoCWraithEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE);
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public void aiStep() {
        // Same direct sun-damage rule as the base Wraith — inherited as-is, nothing to override here.
        super.aiStep();
        // Being fire immune only blocks the damage — actual contact with fire/lava still visually
        // sets it "on fire" with vanilla's own flame texture. Clearing it every tick keeps only our
        // own particle effect below as the "always looks aflame" look.
        this.clearFire();
        if (this.level().isClientSide) {
            for (int i = 0; i < 2; i++) {
                this.level().addParticle(ParticleTypes.FLAME,
                        this.getX() + (this.random.nextDouble() - 0.5D) * this.getBbWidth(),
                        this.getY() + this.random.nextDouble() * this.getBbHeight(),
                        this.getZ() + (this.random.nextDouble() - 0.5D) * this.getBbWidth(),
                        0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean success = super.doHurtTarget(target);
        if (success && !this.level().isClientSide
                && this.level().getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
            target.igniteForSeconds(IGNITE_SECONDS);
        }
        return success;
    }

    /** Wiki: 0-2 redstone dust only (the real loot table also has blaze powder, but the wiki doesn't). */
    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        int lootingLevel = MoCLootUtil.getLootingLevel(level, damageSource);
        MoCLootUtil.dropItems(this, Items.REDSTONE, MoCLootUtil.rollWithLootingBonus(this.random, 3, lootingLevel));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return com.example.neomocreatures.init.ModSounds.WRAITH_AMBIENT.get();
    }
}