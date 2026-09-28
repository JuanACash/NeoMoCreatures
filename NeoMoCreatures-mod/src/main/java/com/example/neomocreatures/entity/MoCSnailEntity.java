package com.example.neomocreatures.entity;

import javax.annotation.Nullable;

import com.example.neomocreatures.entity.snail.SnailVariant;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import java.util.Set;

/**
 * Port of {@code MoCEntitySnail}, which also covers the slugs (variants 5 and 6). A snail pulls into
 * its shell whenever a creature bigger than half a block is within 3 blocks and in sight, and stays
 * frozen until it leaves; slugs never hide. Climbs walls, never jumps, no drops.
 */
public class MoCSnailEntity extends MoCCrawlerEntity {

    private static final double HIDE_RADIUS = 3.0D;

    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(MoCSnailEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_HIDING =
            SynchedEntityData.defineId(MoCSnailEntity.class, EntityDataSerializers.BOOLEAN);

    public MoCSnailEntity(EntityType<? extends MoCSnailEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 4.0D)
                .add(Attributes.ARMOR, 2.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.1D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new WaterAvoidingRandomStrollGoal(this, 0.8D));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, SnailVariant.BROWN.getId());
        builder.define(DATA_HIDING, false);
    }

    public SnailVariant getVariant() {
        return SnailVariant.byId(this.entityData.get(DATA_VARIANT));
    }

    public boolean isSlug() {
        return this.getVariant().isSlug();
    }

    public boolean isHiding() {
        return this.entityData.get(DATA_HIDING);
    }

    private static final String TAG_VARIANT = "SnailVariant";

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt(TAG_VARIANT, this.getVariant().getId());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains(TAG_VARIANT, Tag.TAG_INT)) {
            this.entityData.set(DATA_VARIANT, tag.getInt(TAG_VARIANT));
        }
    }

    private static final Set<String> SLUG_BIOMES = Set.of("swamp", "mangrove_swamp", "forest");

    /** Original: selectType() - uniform 1-6, so a third of them are slugs. A natural spawn outside the
     *  slug biomes is narrowed to snails only (1-4). */
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        boolean natural = spawnType == MobSpawnType.NATURAL || spawnType == MobSpawnType.CHUNK_GENERATION;
        String biome = level.getBiome(this.blockPosition()).unwrapKey()
                .map(key -> key.location().getPath()).orElse("");
        int variant = natural && !SLUG_BIOMES.contains(biome)
                ? this.random.nextInt(4) + 1
                : this.random.nextInt(6) + 1;
        this.entityData.set(DATA_VARIANT, variant);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Override
    protected boolean isMovementCeased() {
        return this.isHiding();
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) {
            return;
        }
        // Wiki: reacts to any creature within 3 blocks regardless of its size, even an ant. Anything
        // stops it in its tracks; only a snail that has a shell also pulls into it.
        LivingEntity threat = this.findNearbyCreature(HIDE_RADIUS, 0.0F);
        boolean threatened = threat != null && this.hasLineOfSight(threat);
        if (threatened) {
            this.getNavigation().stop();
        }
        boolean shouldHide = threatened && !this.isSlug();
        if (shouldHide != this.isHiding()) {
            this.entityData.set(DATA_HIDING, shouldHide);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isHiding()) {
            // Original: locks its facing while pulled into the shell.
            this.yBodyRot = this.getYRot();
            this.yBodyRotO = this.getYRot();
            this.yRotO = this.getYRot();
        }
    }

    /** Original: climbs any wall it bumps into. */
    @Override
    public boolean onClimbable() {
        return this.horizontalCollision;
    }

    /** Original: overrides jumping with nothing at all - it never jumps. */
    @Override
    public void jumpFromGround() {
    }

    /** The original reuses one vanilla sound for both hurt and death; the exact constant can't be
     *  identified from the obfuscated source, so this uses the silverfish's, the closest bug sound. */
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return null;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return null;
    }


    @Override
    protected boolean dropsSlimeballs() {
        return true;
    }
}
