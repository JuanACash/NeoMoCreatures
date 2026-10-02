package com.example.neomocreatures.entity;

import com.example.neomocreatures.init.ModItems;
import com.example.neomocreatures.init.ModSounds;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/**
 * Step 1+2 port of drzhark.mocreatures.entity.item.MoCEntityKittyBed:
 * placeable furniture, fill with pet food/milk, pick up sneaking with an
 * empty hand. Kitty-riding/sleeping/feeding integration is a later step.
 */
public class MoCKittyBedEntity extends Mob {

    private static final EntityDataAccessor<Boolean> DATA_HAS_MILK =
            SynchedEntityData.defineId(MoCKittyBedEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_HAS_FOOD =
            SynchedEntityData.defineId(MoCKittyBedEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_PICKED_UP =
            SynchedEntityData.defineId(MoCKittyBedEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_SHEET_COLOR =
            SynchedEntityData.defineId(MoCKittyBedEntity.class, EntityDataSerializers.INT);

    /** How far the food/milk has been eaten (0 to 2); synced so the bowl visibly empties on clients. */
    private static final EntityDataAccessor<Float> DATA_MILK_LEVEL =
            SynchedEntityData.defineId(MoCKittyBedEntity.class, EntityDataSerializers.FLOAT);

    public MoCKittyBedEntity(EntityType<? extends MoCKittyBedEntity> type, Level level) {
        super(type, level);
        this.setNoAi(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_HAS_MILK, false);
        builder.define(DATA_HAS_FOOD, false);
        builder.define(DATA_PICKED_UP, false);
        builder.define(DATA_SHEET_COLOR, 0);
        builder.define(DATA_MILK_LEVEL, 0.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 20.0D);
    }

    /** Placed furniture, not a wild mob: it must never despawn when players wander off. */
    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    protected void registerGoals() {
        // No AI — it never moves on its own.
    }

    public boolean hasMilk() {
        return this.entityData.get(DATA_HAS_MILK);
    }

    public void setHasMilk(boolean value) {
        this.entityData.set(DATA_HAS_MILK, value);
    }

    public boolean hasFood() {
        return this.entityData.get(DATA_HAS_FOOD);
    }

    public void setHasFood(boolean value) {
        this.entityData.set(DATA_HAS_FOOD, value);
    }

    public boolean isPickedUp() {
        return this.entityData.get(DATA_PICKED_UP);
    }

    public void setPickedUp(boolean value) {
        this.entityData.set(DATA_PICKED_UP, value);
    }

    public int getSheetColor() {
        return this.entityData.get(DATA_SHEET_COLOR);
    }

    public void setSheetColor(int value) {
        this.entityData.set(DATA_SHEET_COLOR, value);
    }

    public float getMilkLevel() {
        return this.entityData.get(DATA_MILK_LEVEL);
    }

    private void setMilkLevel(float level) {
        this.entityData.set(DATA_MILK_LEVEL, level);
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Override
    public boolean isPushable() {
        return !this.isRemoved();
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!stack.isEmpty() && !hasFood() && !hasMilk()) {
            if (stack.is(ModItems.PET_FOOD.get())) {
                if (!this.level().isClientSide) {
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                    this.playSound(ModSounds.KITTY_BED_POURING_FOOD.get(), 1.0F, 1.0F);
                    setHasMilk(false);
                    setHasFood(true);
                }
                return InteractionResult.SUCCESS;
            }
            if (stack.is(Items.MILK_BUCKET)) {
                if (!this.level().isClientSide) {
                    player.setItemInHand(hand, new ItemStack(Items.BUCKET));
                    this.playSound(ModSounds.KITTY_BED_POURING_MILK.get(), 1.0F, 1.0F);
                    setHasMilk(true);
                    setHasFood(false);
                }
                return InteractionResult.SUCCESS;
            }
        }

        if (this.getVehicle() == null) {
            if (stack.isEmpty() && player.isShiftKeyDown()) {
                if (!this.level().isClientSide) {
                    player.getInventory().add(new ItemStack(itemForSheetColor(getSheetColor())));
                    if (hasFood()) {
                        player.getInventory().add(new ItemStack(ModItems.PET_FOOD.get()));
                    }
                    this.playSound(SoundEvents.ITEM_PICKUP, 0.2F, ((this.random.nextFloat() - this.random.nextFloat()) * 1.4F + 2.0F) * 1.0F);
                    this.discard();
                }
                return InteractionResult.SUCCESS;
            }
            if (!this.level().isClientSide) {
                this.setYRot(Math.round(this.getYRot() / 90.0F) * 90.0F + 90.0F);
                this.setYHeadRot(this.getYRot());
                this.playSound(SoundEvents.ITEM_FRAME_ROTATE_ITEM, 1.0F, 1.0F);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void positionRider(Entity passenger, Entity.MoveFunction moveFunction) {
        if (!this.hasPassenger(passenger)) {
            return;
        }
        moveFunction.accept(passenger, this.getX(), this.getY() + 0.2D, this.getZ());
    }

    private static Item itemForSheetColor(int colorId) {
        return switch (colorId) {
            case 1 -> ModItems.KITTY_BED_ORANGE.get();
            case 2 -> ModItems.KITTY_BED_MAGENTA.get();
            case 3 -> ModItems.KITTY_BED_LIGHT_BLUE.get();
            case 4 -> ModItems.KITTY_BED_YELLOW.get();
            case 5 -> ModItems.KITTY_BED_LIME.get();
            case 6 -> ModItems.KITTY_BED_PINK.get();
            case 7 -> ModItems.KITTY_BED_GRAY.get();
            case 8 -> ModItems.KITTY_BED_SILVER.get();
            case 9 -> ModItems.KITTY_BED_CYAN.get();
            case 10 -> ModItems.KITTY_BED_PURPLE.get();
            case 11 -> ModItems.KITTY_BED_BLUE.get();
            case 12 -> ModItems.KITTY_BED_BROWN.get();
            case 13 -> ModItems.KITTY_BED_GREEN.get();
            case 14 -> ModItems.KITTY_BED_RED.get();
            case 15 -> ModItems.KITTY_BED_BLACK.get();
            default -> ModItems.KITTY_BED_WHITE.get();
        };
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && (hasFood() || hasMilk()) && this.isVehicle()
                && this.getFirstPassenger() instanceof MoCKittyEntity kitty
                && kitty.getKittyState() != 12) {
            this.setMilkLevel(this.getMilkLevel() + 0.003F);
            if (this.getMilkLevel() > 2.0F) {
                this.setMilkLevel(0.0F);
                setHasMilk(false);
                setHasFood(false);
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("HasMilk", hasMilk());
        tag.putBoolean("HasFood", hasFood());
        tag.putInt("SheetColor", getSheetColor());
        tag.putFloat("MilkLevel", this.getMilkLevel());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setHasMilk(tag.getBoolean("HasMilk"));
        setHasFood(tag.getBoolean("HasFood"));
        if (tag.contains("SheetColor")) {
            setSheetColor(tag.getInt("SheetColor"));
        }
        this.setMilkLevel(tag.getFloat("MilkLevel"));
    }
}