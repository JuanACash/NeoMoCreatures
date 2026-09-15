package com.example.neomocreatures.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import com.example.neomocreatures.entity.MoCKittyEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;

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

    /** Rises as a kitty feeds from it; wiring the actual tick-up is a later step. */
    private float milkLevel;

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
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 20.0D);
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
        return this.milkLevel;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, net.minecraft.world.damagesource.DamageSource source) {
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
            if (stack.is(com.example.neomocreatures.init.ModItems.PET_FOOD.get())) {
                if (!this.level().isClientSide) {
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                    this.playSound(com.example.neomocreatures.init.ModSounds.KITTY_BED_POURING_FOOD.get(), 1.0F, 1.0F);
                    setHasMilk(false);
                    setHasFood(true);
                }
                return InteractionResult.SUCCESS;
            }
            if (stack.is(Items.MILK_BUCKET)) {
                if (!this.level().isClientSide) {
                    player.setItemInHand(hand, new ItemStack(Items.BUCKET));
                    this.playSound(com.example.neomocreatures.init.ModSounds.KITTY_BED_POURING_MILK.get(), 1.0F, 1.0F);
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
                        player.getInventory().add(new ItemStack(com.example.neomocreatures.init.ModItems.PET_FOOD.get()));
                    }
                    this.playSound(net.minecraft.sounds.SoundEvents.ITEM_PICKUP, 0.2F, ((this.random.nextFloat() - this.random.nextFloat()) * 1.4F + 2.0F) * 1.0F);
                    this.discard();
                }
                return InteractionResult.SUCCESS;
            }
            if (!this.level().isClientSide) {
                this.setYRot(Math.round(this.getYRot() / 90.0F) * 90.0F + 90.0F);
                this.setYHeadRot(this.getYRot());
                this.playSound(net.minecraft.sounds.SoundEvents.ITEM_FRAME_ROTATE_ITEM, 1.0F, 1.0F);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void positionRider(net.minecraft.world.entity.Entity passenger, Entity.MoveFunction moveFunction) {
        if (!this.hasPassenger(passenger)) {
            return;
        }
        moveFunction.accept(passenger, this.getX(), this.getY() + 0.2D, this.getZ());
    }

    private static net.minecraft.world.item.Item itemForSheetColor(int colorId) {
        return switch (colorId) {
            case 1 -> com.example.neomocreatures.init.ModItems.KITTY_BED_ORANGE.get();
            case 2 -> com.example.neomocreatures.init.ModItems.KITTY_BED_MAGENTA.get();
            case 3 -> com.example.neomocreatures.init.ModItems.KITTY_BED_LIGHT_BLUE.get();
            case 4 -> com.example.neomocreatures.init.ModItems.KITTY_BED_YELLOW.get();
            case 5 -> com.example.neomocreatures.init.ModItems.KITTY_BED_LIME.get();
            case 6 -> com.example.neomocreatures.init.ModItems.KITTY_BED_PINK.get();
            case 7 -> com.example.neomocreatures.init.ModItems.KITTY_BED_GRAY.get();
            case 8 -> com.example.neomocreatures.init.ModItems.KITTY_BED_SILVER.get();
            case 9 -> com.example.neomocreatures.init.ModItems.KITTY_BED_CYAN.get();
            case 10 -> com.example.neomocreatures.init.ModItems.KITTY_BED_PURPLE.get();
            case 11 -> com.example.neomocreatures.init.ModItems.KITTY_BED_BLUE.get();
            case 12 -> com.example.neomocreatures.init.ModItems.KITTY_BED_BROWN.get();
            case 13 -> com.example.neomocreatures.init.ModItems.KITTY_BED_GREEN.get();
            case 14 -> com.example.neomocreatures.init.ModItems.KITTY_BED_RED.get();
            case 15 -> com.example.neomocreatures.init.ModItems.KITTY_BED_BLACK.get();
            default -> com.example.neomocreatures.init.ModItems.KITTY_BED_WHITE.get();
        };
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && (hasFood() || hasMilk()) && this.isVehicle()
                && this.getFirstPassenger() instanceof MoCKittyEntity kitty
                && kitty.getKittyState() != 12) {
            this.milkLevel += 0.003F;
            if (this.milkLevel > 2.0F) {
                this.milkLevel = 0.0F;
                setHasMilk(false);
                setHasFood(false);
            }
        }
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("HasMilk", hasMilk());
        tag.putBoolean("HasFood", hasFood());
        tag.putInt("SheetColor", getSheetColor());
        tag.putFloat("MilkLevel", this.milkLevel);
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setHasMilk(tag.getBoolean("HasMilk"));
        setHasFood(tag.getBoolean("HasFood"));
        if (tag.contains("SheetColor")) {
            setSheetColor(tag.getInt("SheetColor"));
        }
        this.milkLevel = tag.getFloat("MilkLevel");
    }
}