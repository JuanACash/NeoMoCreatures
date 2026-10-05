package com.example.neomocreatures.entity;

import com.example.neomocreatures.Config;

import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

/** Port of {@code drzhark.mocreatures.entity.hostile.MoCEntityGreenOgre}. */
public class MoCGreenOgreEntity extends MoCOgreEntity {

    private static final double MAX_HEALTH = 50.0D;
    private static final double ARMOR = 8.0D;
    private static final double ATTACK_DAMAGE = 7.0D;

    public MoCGreenOgreEntity(EntityType<? extends MoCGreenOgreEntity> type, Level level) {
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
        return "ogre_green";
    }

    @Override
    public double getDestroyRadius() {
        return Config.MONSTERS.ogreStrength.get();
    }

    /** 0-2 obsidian, scaling with Looting; 0-1 diamond at a 5% chance per roll, boosted by Looting. */
    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        int lootingLevel = this.getLootingLevel(level, damageSource);
        int obsidian = this.random.nextInt(3) + this.random.nextInt(lootingLevel + 1);
        if (obsidian > 0) {
            this.spawnAtLocation(new ItemStack(Items.OBSIDIAN, obsidian));
        }
        if (this.random.nextFloat() < 0.05F + 0.01F * lootingLevel) {
            this.spawnAtLocation(new ItemStack(Items.DIAMOND));
        }
    }

    private int getLootingLevel(ServerLevel level, DamageSource damageSource) {
        if (damageSource.getEntity() instanceof LivingEntity attacker) {
            return EnchantmentHelper.getEnchantmentLevel(
                    level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.LOOTING),
                    attacker);
        }
        return 0;
    }
}