package com.example.neomocreatures.entity;

import javax.annotation.Nullable;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.Tags;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

/**
 * Port of {@code MoCEntityAnt}. Silent and drop-less. Its one real trick: it walks to the nearest
 * edible item within 8 blocks and carries it away on its back (the item literally rides it).
 * <p>
 * The original tracked this with a "has food" flag that got cleared again a tick after picking the
 * item up, and made the walk speed equal to the distance to the item (so it sped up the farther
 * away the item was). Both are dropped: the ant is simply carrying something or it isn't, and it
 * walks at a normal pace.
 */
public class MoCAntEntity extends MoCCrawlerEntity {

    private static final double SEARCH_RADIUS = 8.0D;
    private static final double PICKUP_DISTANCE = 1.0D;
    private static final double NORMAL_SPEED = 0.28D;
    /** Wiki: it "tends to walk slower than usual" while carrying food. */
    private static final double CARRYING_SPEED = 0.17D;

    public MoCAntEntity(EntityType<? extends MoCAntEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 3.0D)
                .add(Attributes.ARMOR, 1.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.28D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new WaterAvoidingRandomStrollGoal(this, 1.2D));
    }

    /** Original: MoCTools.isItemEdible() - anything with a food value, any seed, and wheat, sugar,
     *  cake and eggs. */
    private static boolean isEdible(ItemStack stack) {
        return stack.has(DataComponents.FOOD) || stack.is(Tags.Items.SEEDS)
                || stack.is(Items.WHEAT) || stack.is(Items.SUGAR) || stack.is(Items.CAKE) || stack.is(Items.EGG);
    }

    @Nullable
    private ItemEntity findClosestFood() {
        AABB area = this.getBoundingBox().inflate(SEARCH_RADIUS);
        ItemEntity closest = null;
        double closestDistance = Double.MAX_VALUE;
        for (ItemEntity item : this.level().getEntitiesOfClass(ItemEntity.class, area,
                i -> i.isAlive() && i.getVehicle() == null && isEdible(i.getItem()))) {
            double distance = item.distanceToSqr(this);
            if (distance < closestDistance) {
                closestDistance = distance;
                closest = item;
            }
        }
        return closest;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) {
            return;
        }
        this.updateCarrySpeed();
        if (this.isVehicle()) {
            return;
        }
        ItemEntity food = this.findClosestFood();
        if (food == null) {
            return;
        }
        if (food.distanceTo(this) > PICKUP_DISTANCE) {
            this.getNavigation().moveTo(food, 1.0D);
        } else {
            food.startRiding(this);
        }
    }

    private void updateCarrySpeed() {
        AttributeInstance speed = this.getAttribute(Attributes.MOVEMENT_SPEED);
        double target = this.isVehicle() ? CARRYING_SPEED : NORMAL_SPEED;
        if (speed != null && speed.getBaseValue() != target) {
            speed.setBaseValue(target);
        }
    }
}
