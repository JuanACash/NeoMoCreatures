package com.example.neomocreatures.entity;

import net.minecraft.core.BlockPos;
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

/** Port of {@code drzhark.mocreatures.entity.hostile.MoCEntityCaveOgre}. */
public class MoCCaveOgreEntity extends MoCOgreEntity {

    private static final double MAX_HEALTH = 60.0D;
    private static final double ARMOR = 10.0D;
    private static final double ATTACK_DAMAGE = 8.0D;
    /** Flat 1% per roll, up to 2 rolls — not boosted by Looting. */
    private static final float NETHERITE_SCRAP_CHANCE = 0.01F;

    /** Original: getBrightness() ceiling above which it can start burning. */
    private static final float BURN_BRIGHTNESS_THRESHOLD = 0.5F;
    private static final int BURN_SECONDS = 1;

    public MoCCaveOgreEntity(EntityType<? extends MoCCaveOgreEntity> type, Level level) {
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
        return "ogre_cave";
    }

    @Override
    public double getDestroyRadius() {
        return 3.0D;
    }

    /**
     * Original: isHarmedByDaylight() = true for the cave ogre only — defensive, since its own spawn
     * rule already keeps it from ever naturally seeing the sky. No shared vanilla hook for this
     * (only Zombie/Skeleton implement their own sun-tick logic), so it's a plain aiStep check here.
     */
    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide && this.level().isDay()) {
            float brightness = this.getBrightness();
            BlockPos pos = this.blockPosition();
            if (brightness > BURN_BRIGHTNESS_THRESHOLD && this.level().canSeeSky(pos)
                    && this.random.nextFloat() * 30.0F < (brightness - 0.4F) * 2.0F) {
                this.igniteForSeconds(BURN_SECONDS);
            }
        }
    }

    /** 0-2 diamond, scaling with Looting; 0-2 netherite scrap at a flat 1% chance per roll,
     *  unaffected by Looting. */
    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        int lootingLevel = this.getLootingLevel(level, damageSource);
        int diamonds = this.random.nextInt(3) + this.random.nextInt(lootingLevel + 1);
        if (diamonds > 0) {
            this.spawnAtLocation(new ItemStack(Items.DIAMOND, diamonds));
        }
        int scraps = 0;
        for (int i = 0; i < 2; i++) {
            if (this.random.nextFloat() < NETHERITE_SCRAP_CHANCE) {
                scraps++;
            }
        }
        if (scraps > 0) {
            this.spawnAtLocation(new ItemStack(Items.NETHERITE_SCRAP, scraps));
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