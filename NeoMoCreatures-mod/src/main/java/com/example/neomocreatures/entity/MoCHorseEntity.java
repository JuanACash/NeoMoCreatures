package com.example.neomocreatures.entity;

import java.util.List;

import javax.annotation.Nullable;

import com.example.neomocreatures.breeding.MoCHorseGenetics;
import com.example.neomocreatures.breeding.MoCHorseGenetics.Coat;
import com.example.neomocreatures.breeding.MoCHorseGenetics.Species;
import com.example.neomocreatures.init.ModEntities;
import com.example.neomocreatures.init.ModItems;
import com.example.neomocreatures.init.ModParticles;
import com.example.neomocreatures.init.ModSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;

public class MoCHorseEntity extends AbstractHorse {

    private static final EntityDataAccessor<Integer> DATA_SPECIES =
            SynchedEntityData.defineId(MoCHorseEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_COAT =
            SynchedEntityData.defineId(MoCHorseEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_FAIRY_COLOR =
            SynchedEntityData.defineId(MoCHorseEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_FAIRY_COLOR_LOCKED =
            SynchedEntityData.defineId(MoCHorseEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_MOUTH_TICKS =
            SynchedEntityData.defineId(MoCHorseEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_BUCKING_TICKS =
            SynchedEntityData.defineId(MoCHorseEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TAME_HOLD_TICKS =
            SynchedEntityData.defineId(MoCHorseEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_SYNCED_AGE =
            SynchedEntityData.defineId(MoCHorseEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_GRAZE_TICKS =
        SynchedEntityData.defineId(MoCHorseEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_UNICORN_CHARGE_TICKS =
        SynchedEntityData.defineId(MoCHorseEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_HAS_CHEST =
        SynchedEntityData.defineId(MoCHorseEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_TRANSFORM_TARGET =
        SynchedEntityData.defineId(MoCHorseEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TRANSFORM_TICKS =
        SynchedEntityData.defineId(MoCHorseEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_COLOR_TRANSFORM_TARGET =
            SynchedEntityData.defineId(MoCHorseEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_COLOR_TRANSFORM_TICKS =
            SynchedEntityData.defineId(MoCHorseEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_DANCING =
        SynchedEntityData.defineId(MoCHorseEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_UNDEAD_STAGE =
        SynchedEntityData.defineId(MoCHorseEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_UNDEAD_TRANSFORM_TICKS =
        SynchedEntityData.defineId(MoCHorseEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_UNDEAD_LOCKED =
        SynchedEntityData.defineId(MoCHorseEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_WING_FLAP_TICKS =
        SynchedEntityData.defineId(MoCHorseEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_VANISH_TICKS =
        SynchedEntityData.defineId(MoCHorseEntity.class, EntityDataSerializers.INT);
        private static final EntityDataAccessor<Integer> DATA_VANISH_DURATION_TICKS =
            SynchedEntityData.defineId(MoCHorseEntity.class, EntityDataSerializers.INT);


    public static final int AMULET_VANISH_DURATION_TICKS = 140;
    private net.minecraft.world.item.Item pendingAmuletTemplate = null;
    private java.util.UUID pendingAmuletOwner = null;

    public static final int VANISH_DURATION_TICKS = 100;

    private static final int UNDEAD_TRANSFORM_DURATION_TICKS = 100;
    public static final int UNDEAD_NONE = 0;
    public static final int UNDEAD_STAGE_0 = 1;
    public static final int UNDEAD_STAGE_1 = 2;
    public static final int UNDEAD_STAGE_2 = 3;
    public static final int UNDEAD_STAGE_3 = 4;
    public static final int UNDEAD_SKELETON = 5;

    //Time it takes to change to next undead stage. 48000 ticks = 2 in-game days = 40 real-life minutes
    private static final int UNDEAD_STAGE_DURATION_TICKS = 48000;

    private int undeadDecayTicks = 0;

    private int fallImmuneTicks = 0;

    private static final int UNSET = -1;
    private static final int GESTATION_TICKS = 300;

    private static final float SUGAR_LUMP_GROWTH_FRACTION = 0.10F;
    private static final int FULL_GROWTH_TICKS = 24000;

    private static final int MOUTH_OPEN_TICKS = 30;

    public static final int WING_FLAP_DURATION_TICKS = 20;

    private static final int TRANSFORM_DURATION_TICKS = 100;

    private static final float RIDER_FORWARD = -0.15F;
    private static final float RIDER_HEIGHT = 0.7F;

    private static final double FLYER_THRUST = 0.3D;     // vertical thrust per tick while ascending/descending
    private static final float  FLYER_FRICTION = 0.91F;  // horizontal friction per tick
    private static final double FLYER_FALL_SPEED = 0.6D; // vertical damping (acts as "terminal velocity")
    private static final double FLYER_GRAVITY_PULL = 0.055D; // constant downward pull each tick
    private static final double PEGASUS_THRUST_BONUS = 0.05D;   // extra thrust when ascending/descending
    private static final float  PEGASUS_FRICTION = 0.93F;       // less friction = more horizontal speed
    private static final double DARK_PEGASUS_THRUST_BONUS = 0.025D;
    private static final float  DARK_PEGASUS_FRICTION = 0.92F;


    private int gestationProgress = 0;

    private static final int WING_FLAP_PERIOD_TICKS = 21; // must match the model's 0.3F (2π/0.3 ≈ 20.94)

    private static final int BUTTERFLY_WING_FLAP_PERIOD_TICKS = 21; // must match the model's 0.45F (2π/0.45 ≈ 13.96)

    private int groundedStreak = 0;
    private int butterflyGroundedStreak = 0;
    private static final int WING_FLAP_GROUND_GRACE = 5;

    // 27 slots (9x3) so it matches ChestMenu.threeRows() exactly — the
    // previous 15-slot container would throw out-of-bounds the moment the
    // menu actually tried to open. Real vanilla donkeys use a custom 15-slot
    // 5x3 layout via their own menu class; 27 is a simplification that
    // works with vanilla's stock ChestMenu with zero extra GUI code.
    private final SimpleContainer chestInventory = new SimpleContainer(27);

    public static final int GRAZE_DURATION_TICKS = 100;

    private float nightmareFleeYaw = 0F;

    private static final java.util.Set<net.minecraft.resources.ResourceKey<net.minecraft.world.level.biome.Biome>> TIER1_BIOMES = java.util.Set.of(
        net.minecraft.world.level.biome.Biomes.PLAINS, net.minecraft.world.level.biome.Biomes.SUNFLOWER_PLAINS,
        net.minecraft.world.level.biome.Biomes.SNOWY_PLAINS, net.minecraft.world.level.biome.Biomes.OLD_GROWTH_PINE_TAIGA,
        net.minecraft.world.level.biome.Biomes.OLD_GROWTH_SPRUCE_TAIGA, net.minecraft.world.level.biome.Biomes.MEADOW);

    private static final java.util.Set<net.minecraft.resources.ResourceKey<net.minecraft.world.level.biome.Biome>> ZEBRA_BIOMES = java.util.Set.of(
            net.minecraft.world.level.biome.Biomes.SAVANNA, net.minecraft.world.level.biome.Biomes.SAVANNA_PLATEAU,
            net.minecraft.world.level.biome.Biomes.WINDSWEPT_SAVANNA);

    private static final java.util.Set<net.minecraft.resources.ResourceKey<net.minecraft.world.level.biome.Biome>> DONKEY_BIOMES = java.util.Set.of(
            net.minecraft.world.level.biome.Biomes.PLAINS, net.minecraft.world.level.biome.Biomes.SUNFLOWER_PLAINS,
            net.minecraft.world.level.biome.Biomes.SAVANNA_PLATEAU, net.minecraft.world.level.biome.Biomes.OLD_GROWTH_PINE_TAIGA,
            net.minecraft.world.level.biome.Biomes.OLD_GROWTH_SPRUCE_TAIGA);

    private static final java.util.Map<net.minecraft.world.item.DyeColor, MoCHorseGenetics.FairyColor> DYE_TO_FAIRY_COLOR = java.util.Map.ofEntries(
            java.util.Map.entry(net.minecraft.world.item.DyeColor.WHITE, MoCHorseGenetics.FairyColor.WHITE),
            java.util.Map.entry(net.minecraft.world.item.DyeColor.ORANGE, MoCHorseGenetics.FairyColor.ORANGE),
            java.util.Map.entry(net.minecraft.world.item.DyeColor.YELLOW, MoCHorseGenetics.FairyColor.YELLOW),
            java.util.Map.entry(net.minecraft.world.item.DyeColor.LIME, MoCHorseGenetics.FairyColor.LIGHTGREEN),
            java.util.Map.entry(net.minecraft.world.item.DyeColor.GREEN, MoCHorseGenetics.FairyColor.GREEN),
            java.util.Map.entry(net.minecraft.world.item.DyeColor.CYAN, MoCHorseGenetics.FairyColor.CYAN),
            java.util.Map.entry(net.minecraft.world.item.DyeColor.LIGHT_BLUE, MoCHorseGenetics.FairyColor.BLUE),
            java.util.Map.entry(net.minecraft.world.item.DyeColor.BLUE, MoCHorseGenetics.FairyColor.DARKBLUE),
            java.util.Map.entry(net.minecraft.world.item.DyeColor.PURPLE, MoCHorseGenetics.FairyColor.PURPLE),
            java.util.Map.entry(net.minecraft.world.item.DyeColor.PINK, MoCHorseGenetics.FairyColor.PINK),
            java.util.Map.entry(net.minecraft.world.item.DyeColor.RED, MoCHorseGenetics.FairyColor.RED),
            java.util.Map.entry(net.minecraft.world.item.DyeColor.BLACK, MoCHorseGenetics.FairyColor.BLACK)
    );

    @Override
    public net.minecraft.world.entity.SpawnGroupData finalizeSpawn(net.minecraft.world.level.ServerLevelAccessor level,
            net.minecraft.world.DifficultyInstance difficulty, net.minecraft.world.entity.MobSpawnType spawnReason,
            @Nullable net.minecraft.world.entity.SpawnGroupData spawnGroupData) {
        if (spawnReason == net.minecraft.world.entity.MobSpawnType.NATURAL
        || spawnReason == net.minecraft.world.entity.MobSpawnType.CHUNK_GENERATION) {
            var biomeKey = level.getBiome(this.blockPosition()).unwrapKey().orElse(null);
            java.util.List<Species> options = new java.util.ArrayList<>();
            if (biomeKey != null && TIER1_BIOMES.contains(biomeKey)) options.add(Species.HORSE);
            if (biomeKey != null && ZEBRA_BIOMES.contains(biomeKey)) options.add(Species.ZEBRA);
            if (biomeKey != null && DONKEY_BIOMES.contains(biomeKey)) options.add(Species.DONKEY);
            if (!options.isEmpty()) {
                Species chosen = options.get(this.random.nextInt(options.size()));
                setSpecies(chosen);
                if (chosen == Species.HORSE) {
                    setCoat(MoCHorseGenetics.randomWildCoat());
                }
                this.setHealth((float) this.getMaxHealth());
            }
            // "Occasionally, babies will spawn in herds."
            if (this.random.nextInt(5) == 0) {
                this.setAge(-24000);
            }
        }
        return super.finalizeSpawn(level, difficulty, spawnReason, spawnGroupData);
    }

    public MoCHorseEntity(EntityType<? extends MoCHorseEntity> type, Level level) {
        super(type, level);
        Species species = MoCHorseGenetics.WILD_SPECIES[this.random.nextInt(MoCHorseGenetics.WILD_SPECIES.length)];
        Coat coat = MoCHorseGenetics.randomWildCoat();
        setSpecies(species);
        setCoat(coat);
        this.setHealth((float) this.getMaxHealth());
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 15.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.225D)
                .add(Attributes.JUMP_STRENGTH, 0.7D);
    }

    public static boolean isExemptZebraRider(net.minecraft.world.entity.player.Player player) {
        if (!(player.getVehicle() instanceof MoCHorseEntity mount)) {
            return false;
        }
        Species species = mount.getSpecies();
        if (species == Species.ZEBRA || species == Species.ZORSE) {
            return true;
        }
        return species == Species.HORSE && coatTier(mount.getCoat()) == 4;
    }

    // ---------------------------------------------------------------
    // Per-species/tier stats (health, speed, jump). The jump values come
    // from simulating a horse's real jump physics (gravity 0.08
    // blocks/tick², drag 0.98) to hit the exact requested height in
    // blocks, since jump_strength isn't linear with height. Undead/
    // Skeleton aren't handled separately: since they don't change
    // getSpecies(), they automatically inherit their counterpart's stats.
    // ---------------------------------------------------------------
    private static final double JUMP_1_5_BLOCKS = 0.4965D;
    private static final double JUMP_2_BLOCKS = 0.5750D;
    private static final double JUMP_3_BLOCKS = 0.7099D;
    private static final double JUMP_4_BLOCKS = 0.8254D;
    private static final double JUMP_4_5_BLOCKS = 0.8791D;
    private static final double JUMP_5_5_BLOCKS = 0.9790D;

    /** "Walking alone" speed (no rider) for the special horses: the same
     *  as a normal tier-4 horse. Zorse, donkey/mule/zonkey, zebra, and
     *  horse (all tiers) are left untouched. */
    private static final double SPECIAL_UNMOUNTED_SPEED = 0.2594D;

    private static int coatTier(Coat coat) {
        return switch (coat) {
            case WHITE, CREAMY, BROWN, DARKBROWN, BLACK -> 1;
            case BRIGHTCREAMY, SPECKLED, PALEBROWN, GREY -> 2;
            case PINTO, BRIGHTPINTO, PALESPECKLES -> 3;
            case SPOTTED, COW -> 4;
        };
    }

    private void applyMoCAttributes() {
        double health;
        double speed;
        double jump;

        switch (getSpecies()) {
            case DONKEY -> { health = 16D; speed = 0.175D; jump = JUMP_1_5_BLOCKS; }
            case MULE, ZONKY -> { health = 18D; speed = 0.1901D; jump = JUMP_1_5_BLOCKS; }
            case ZEBRA -> { health = 18D; speed = 0.2101D; jump = JUMP_2_BLOCKS; }
            case ZORSE -> { health = 24D; speed = 0.2594D; jump = JUMP_4_BLOCKS; }
            case BATHORSE, NIGHTMARE -> { health = 26D; speed = 0.3104D; jump = JUMP_4_5_BLOCKS; }
            case UNICORN -> { health = 28D; speed = 0.4D; jump = JUMP_5_5_BLOCKS; }
            case PEGASUS -> { health = 28D; speed = 0.37D; jump = JUMP_4_5_BLOCKS; }
            case DARK_PEGASUS -> { health = 28D; speed = 0.34D; jump = JUMP_4_5_BLOCKS; }
            case FAIRY_HORSE -> { health = 30D; speed = 0.4D; jump = JUMP_4_5_BLOCKS; }
            case GHOST, GHOST_WINGED, HORSE_BUG -> { health = 26D; speed = 0.3104D; jump = JUMP_4_5_BLOCKS; }
            case HORSE -> {
                switch (coatTier(getCoat())) {
                    case 3 -> { health = 20D; speed = 0.2432D; jump = JUMP_3_BLOCKS; }
                    case 4 -> { health = 24D; speed = 0.2594D; jump = JUMP_4_BLOCKS; }
                    default -> { health = 18D; speed = 0.2101D; jump = JUMP_2_BLOCKS; } // tier 1 y 2
                }
            }
            default -> { health = 18D; speed = 0.2101D; jump = JUMP_2_BLOCKS; }
        }

        net.minecraft.world.entity.ai.attributes.AttributeInstance healthAttr = this.getAttribute(Attributes.MAX_HEALTH);
        if (healthAttr != null) healthAttr.setBaseValue(health);
        net.minecraft.world.entity.ai.attributes.AttributeInstance speedAttr = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr != null) speedAttr.setBaseValue(speed);
        net.minecraft.world.entity.ai.attributes.AttributeInstance jumpAttr = this.getAttribute(Attributes.JUMP_STRENGTH);
        if (jumpAttr != null) jumpAttr.setBaseValue(jump);

        // Never heals for free (loading/transforming shouldn't raise current
        // health) — it only prevents it from exceeding the new max.
        if (this.getHealth() > this.getMaxHealth()) {
            this.setHealth(this.getMaxHealth());
        }
    }

    /** Bathorse, Nightmare, Unicorn, Pegasus, Dark Pegasus, Fairy y Ghost/
    *  Ghost Winged (includes their undead versions, since undead doesn't
    *  change species). Zorse, donkey/mule/zonkey, zebra, and horse (any
    *  tier, including undead horse) are deliberately left out. */
    private boolean isSlowedWhenUnridden() {
        return switch (getSpecies()) {
            case BATHORSE, NIGHTMARE, UNICORN, PEGASUS, DARK_PEGASUS, FAIRY_HORSE, GHOST, GHOST_WINGED -> true;
            default -> false;
        };
    }

    @Override
    protected void randomizeAttributes(RandomSource random) {
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new ZebraFleeGoal(this));
        this.goalSelector.addGoal(2, new PanicGoal(this, 1.5D));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SPECIES, UNSET);
        builder.define(DATA_COAT, UNSET);
        builder.define(DATA_MOUTH_TICKS, 0);
        builder.define(DATA_BUCKING_TICKS, 0);
        builder.define(DATA_TAME_HOLD_TICKS, 0);
        builder.define(DATA_SYNCED_AGE, 0);
        builder.define(DATA_GRAZE_TICKS, GRAZE_DURATION_TICKS);
        builder.define(DATA_UNICORN_CHARGE_TICKS, 0);
        builder.define(DATA_HAS_CHEST, false);
        builder.define(DATA_TRANSFORM_TARGET, UNSET);
        builder.define(DATA_TRANSFORM_TICKS, 0);
        builder.define(DATA_COLOR_TRANSFORM_TARGET, UNSET);
        builder.define(DATA_COLOR_TRANSFORM_TICKS, 0);
        builder.define(DATA_DANCING, false);
        builder.define(DATA_UNDEAD_STAGE, UNDEAD_NONE);
        builder.define(DATA_UNDEAD_TRANSFORM_TICKS, 0);
        builder.define(DATA_UNDEAD_LOCKED, false);
        builder.define(DATA_FAIRY_COLOR, MoCHorseGenetics.FairyColor.WHITE.ordinal());
        builder.define(DATA_FAIRY_COLOR_LOCKED, false);
        builder.define(DATA_WING_FLAP_TICKS, 0);
        builder.define(DATA_VANISH_TICKS, 0);
        builder.define(DATA_VANISH_DURATION_TICKS, VANISH_DURATION_TICKS);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("MoCSpecies", getSpecies().name());
        tag.putString("MoCCoat", getCoat().name());
        tag.putString("MoCFairyColor", getFairyColor().name());
        tag.putBoolean("MoCFairyColorLocked", isFairyColorLocked());
        tag.putBoolean("MoCHasChest", hasChest());
        tag.putInt("MoCUndeadStage", getUndeadStage());
        tag.putBoolean("MoCUndeadLocked", isUndeadLocked());
        tag.putInt("MoCUndeadDecayTicks", this.undeadDecayTicks);
        if (hasChest()) {
            ListTag items = new ListTag();
            for (int slot = 0; slot < chestInventory.getContainerSize(); slot++) {
                ItemStack stack = chestInventory.getItem(slot);
                if (!stack.isEmpty()) {
                    CompoundTag itemTag = new CompoundTag();
                    itemTag.putInt("Slot", slot);
                    items.add(stack.save(this.registryAccess(), itemTag));
                }
            }
            tag.put("MoCChestItems", items);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        // Vanilla only restores the saddle slot if the item is literally
        // minecraft:saddle (AbstractHorse#readAdditionalSaveData lo revisa
        // (checked via itemstack.is(Items.SADDLE)), so our own saddle item
        // gets discarded — we put it back manually here.
        if (this.inventory.getItem(0).isEmpty() && tag.contains("SaddleItem", 10)) {
            ItemStack saddle = ItemStack.parse(this.registryAccess(), tag.getCompound("SaddleItem")).orElse(ItemStack.EMPTY);
            if (!saddle.isEmpty() && this.isSaddleable()) {
                this.inventory.setItem(0, saddle);
                this.syncSaddleToClients();
            }
        }

        if (tag.contains("MoCUndeadStage")) {
            setUndeadStage(tag.getInt("MoCUndeadStage"));
            this.undeadDecayTicks = tag.getInt("MoCUndeadDecayTicks");
        }

        if (tag.contains("MoCUndeadLocked")) {
            setUndeadLocked(tag.getBoolean("MoCUndeadLocked"));
        }

        if (tag.contains("MoCSpecies")) {
            setSpecies(Species.valueOf(tag.getString("MoCSpecies")));
        }
        if (tag.contains("MoCCoat")) {
            setCoat(Coat.valueOf(tag.getString("MoCCoat")));
        }
        if (!tag.contains("Health")) {
            this.setHealth((float) this.getMaxHealth());
        }

        if (tag.contains("MoCFairyColor")) {
            setFairyColor(MoCHorseGenetics.FairyColor.valueOf(tag.getString("MoCFairyColor")));
        }

        if (tag.contains("MoCFairyColorLocked")) {
            setFairyColorLocked(tag.getBoolean("MoCFairyColorLocked"));
        }

        if (tag.getBoolean("MoCHasChest")) {
            setHasChest(true);
        }
        if (tag.contains("MoCChestItems")) {
            ListTag items = tag.getList("MoCChestItems", 10);
            for (int i = 0; i < items.size(); i++) {
                CompoundTag itemTag = items.getCompound(i);
                int slot = itemTag.getInt("Slot");
                ItemStack.parse(this.registryAccess(), itemTag).ifPresent(s -> {
                    if (slot >= 0 && slot < chestInventory.getContainerSize()) {
                        chestInventory.setItem(slot, s);
                    }
                });
            }
        }
    }

    public Species getSpecies() {
        int ordinal = this.entityData.get(DATA_SPECIES);
        return ordinal == UNSET ? Species.HORSE : Species.values()[ordinal];
    }

    public boolean isTransforming() {
        return this.entityData.get(DATA_TRANSFORM_TICKS) > 0;
    }

    public int getTransformTicks() {
        return this.entityData.get(DATA_TRANSFORM_TICKS);
    }

    public Species getTransformTarget() {
        int ordinal = this.entityData.get(DATA_TRANSFORM_TARGET);
        return ordinal == UNSET ? getSpecies() : Species.values()[ordinal];
    }

    private void startTransform(Species target) {
        this.entityData.set(DATA_TRANSFORM_TARGET, target.ordinal());
        this.entityData.set(DATA_TRANSFORM_TICKS, TRANSFORM_DURATION_TICKS);
    }

    public boolean isColorTransforming() {
        return this.entityData.get(DATA_COLOR_TRANSFORM_TICKS) > 0;
    }

    public int getColorTransformTicks() {
        return this.entityData.get(DATA_COLOR_TRANSFORM_TICKS);
    }

    public MoCHorseGenetics.FairyColor getColorTransformTarget() {
        int ordinal = this.entityData.get(DATA_COLOR_TRANSFORM_TARGET);
        return ordinal == UNSET ? getFairyColor() : MoCHorseGenetics.FairyColor.values()[ordinal];
    }

    private void startFairyColorTransform(MoCHorseGenetics.FairyColor target) {
        this.entityData.set(DATA_COLOR_TRANSFORM_TARGET, target.ordinal());
        this.entityData.set(DATA_COLOR_TRANSFORM_TICKS, TRANSFORM_DURATION_TICKS);
    }

    public boolean isVanishing() {
        return this.entityData.get(DATA_VANISH_TICKS) > 0;
    }

    public int getVanishTicks() {
        return this.entityData.get(DATA_VANISH_TICKS);
    }

/** 0 = fairy horse just born, 1 = fully opaque, VANISH_DURATION_TICKS = fully invisible. */    public float getVanishAlpha() {
        int duration = this.entityData.get(DATA_VANISH_DURATION_TICKS);
        return 1.0F - Math.min(1.0F, this.getVanishTicks() / (float) duration);
    }

    public void startVanish() {
        this.entityData.set(DATA_VANISH_TICKS, 1);
        this.entityData.set(DATA_VANISH_DURATION_TICKS, VANISH_DURATION_TICKS);
        this.playSound(ModSounds.HORSE_TRANSFORM.get(), 1.0F, 0.7F);
    }

    /** Especie que puede ser capturada por cada tipo de amuleto (vacío). */
    private boolean matchesAmulet(ItemStack amulet) {
        if (amulet.is(ModItems.PET_AMULET.get())) {
            boolean excluded = isUndead() // cubre tambien skeleton, ya que isSkeletonStage() implica isUndead()
                    || getSpecies() == Species.PEGASUS || getSpecies() == Species.DARK_PEGASUS
                    || getSpecies() == Species.GHOST || getSpecies() == Species.GHOST_WINGED
                    || getSpecies() == Species.FAIRY_HORSE;
            return !excluded;
        }
        // Los amuletos de plantilla (bone/fairy/pegasus/ghost) no aplican a
        // potros; solo el amuleto de mascota puede capturar uno.
        if (this.isBaby()) {
            return false;
        }
        if (amulet.is(ModItems.AMULET_BONE.get())) return isUndead();
        if (amulet.is(ModItems.AMULET_FAIRY.get())) return getSpecies() == Species.FAIRY_HORSE;
        if (amulet.is(ModItems.AMULET_PEGASUS.get())) {
            return (getSpecies() == Species.PEGASUS || getSpecies() == Species.DARK_PEGASUS) && !isUndead();
        }
        if (amulet.is(ModItems.AMULET_GHOST.get())) return getSpecies() == Species.GHOST || getSpecies() == Species.GHOST_WINGED;
        return false;
    }

    private net.minecraft.world.item.Item filledAmuletFor(net.minecraft.world.item.Item emptyAmulet) {
        if (emptyAmulet == ModItems.AMULET_BONE.get()) return ModItems.AMULET_BONE_FULL.get();
        if (emptyAmulet == ModItems.AMULET_FAIRY.get()) return ModItems.AMULET_FAIRY_FULL.get();
        if (emptyAmulet == ModItems.AMULET_PEGASUS.get()) return ModItems.AMULET_PEGASUS_FULL.get();
        if (emptyAmulet == ModItems.AMULET_GHOST.get()) return ModItems.AMULET_GHOST_FULL.get();
        if (emptyAmulet == ModItems.PET_AMULET.get()) return ModItems.PET_AMULET_FULL.get();
        return null;
    }

    private CompoundTag buildAmuletTag(java.util.UUID owner) {
        CompoundTag tag = new CompoundTag();
        tag.putString("Species", getSpecies().name());
        tag.putString("Coat", getCoat().name());
        tag.putFloat("Health", this.getHealth());
        tag.putDouble("MaxHealth", this.getAttributeValue(Attributes.MAX_HEALTH));
        tag.putDouble("MovementSpeed", this.getAttributeValue(Attributes.MOVEMENT_SPEED));
        tag.putDouble("JumpStrength", this.getAttributeValue(Attributes.JUMP_STRENGTH));
        tag.putBoolean("Adult", !this.isBaby());
        tag.putInt("Age", this.getAge());
        tag.putString("Name", this.getCustomName() != null ? this.getCustomName().getString() : "");
        if (owner != null) {
            tag.putUUID("OwnerUUID", owner);
        }
        if (getSpecies() == Species.FAIRY_HORSE) {
            tag.putString("FairyColor", getFairyColor().name());
        }
        if (isUndead()) {
            tag.putInt("UndeadStage", getUndeadStage());
            tag.putBoolean("UndeadLocked", isUndeadLocked());
            tag.putInt("UndeadDecayTicks", this.undeadDecayTicks);
        }
        return tag;
    }

    private void finishCapture(net.minecraft.world.item.Item filledItem, CompoundTag tag, boolean preserveEquipment) {
        if (preserveEquipment) {
            ItemStack saddle = this.inventory.getItem(0);
            if (!saddle.isEmpty()) {
                tag.putString("SaddleItem", net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(saddle.getItem()).toString());
            }
            ItemStack armor = this.getItemBySlot(EquipmentSlot.BODY);
            if (!armor.isEmpty()) {
                tag.putString("ArmorItem", net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(armor.getItem()).toString());
            }
            if (hasChest()) {
                tag.putBoolean("HasChest", true);
                ListTag chestItems = new ListTag();
                for (int slot = 0; slot < chestInventory.getContainerSize(); slot++) {
                    ItemStack chestStack = chestInventory.getItem(slot);
                    if (!chestStack.isEmpty()) {
                        CompoundTag itemTag = new CompoundTag();
                        itemTag.putInt("Slot", slot);
                        chestItems.add(chestStack.save(this.registryAccess(), itemTag));
                    }
                }
                tag.put("ChestItems", chestItems);
            }
        }

        ItemStack result = new ItemStack(filledItem);
        result.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                net.minecraft.world.item.component.CustomData.of(tag));
        this.spawnAtLocation(result);
        this.discard();
    }

    private void startAmuletCapture(ItemStack amulet, Player player) {
        net.minecraft.world.item.Item filledItem = filledAmuletFor(amulet.getItem());
        if (filledItem == null) {
            return;
        }
        this.pendingAmuletTemplate = amulet.getItem();
        this.pendingAmuletOwner = player.getUUID();
        this.entityData.set(DATA_VANISH_TICKS, 1);
        this.entityData.set(DATA_VANISH_DURATION_TICKS, AMULET_VANISH_DURATION_TICKS);
        this.getNavigation().stop();
        this.setDeltaMovement(net.minecraft.world.phys.Vec3.ZERO);
        this.playSound(ModSounds.AMULET_VANISH.get(), 1.0F, 1.0F);
    }

    private void capturePetInstant(Player player, InteractionHand hand) {
        dropSaddleAndArmor();
        dropChestAndContents();
        CompoundTag tag = buildAmuletTag(player.getUUID());
        ItemStack filled = new ItemStack(ModItems.PET_AMULET_FULL.get());
        filled.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                net.minecraft.world.item.component.CustomData.of(tag));
        player.setItemInHand(hand, filled);
        this.discard();
    }

    private void completeAmuletCapture() {
        net.minecraft.world.item.Item filledItem = filledAmuletFor(this.pendingAmuletTemplate);
        this.pendingAmuletTemplate = null;
        if (filledItem == null) {
            return;
        }
        CompoundTag tag = buildAmuletTag(this.pendingAmuletOwner);
        this.pendingAmuletOwner = null;
        finishCapture(filledItem, tag, true);
    }

    public void setSaddle(ItemStack saddle) {
        this.inventory.setItem(0, saddle);
    }

    public void setHasChestPublic(boolean value) {
        setHasChest(value);
    }

    public void setChestSlotPublic(int slot, ItemStack stack) {
        chestInventory.setItem(slot, stack);
    }

    public void setUndeadStagePublic(int stage) {
        setUndeadStage(stage);
    }

    public void setUndeadLockedPublic(boolean locked) {
        setUndeadLocked(locked);
    }

    public void setUndeadDecayTicksPublic(int ticks) {
        this.undeadDecayTicks = ticks;
    }

    public void setDescendHeld(boolean held) {
        this.descendHeld = held;
    }

    public void setSpecies(Species species) {
        this.entityData.set(DATA_SPECIES, species.ordinal());
        applyMoCAttributes();
    }

    public Coat getCoat() {
        int ordinal = this.entityData.get(DATA_COAT);
        return ordinal == UNSET ? Coat.WHITE : Coat.values()[ordinal];
    }

    public MoCHorseGenetics.FairyColor getFairyColor() {
        return MoCHorseGenetics.FairyColor.values()[this.entityData.get(DATA_FAIRY_COLOR)];
    }

    public void setFairyColor(MoCHorseGenetics.FairyColor color) {
        this.entityData.set(DATA_FAIRY_COLOR, color.ordinal());
    }

    public boolean isFairyColorLocked() {
        return this.entityData.get(DATA_FAIRY_COLOR_LOCKED);
    }

    public void setFairyColorLocked(boolean locked) {
        this.entityData.set(DATA_FAIRY_COLOR_LOCKED, locked);
    }

    public void setCoat(Coat coat) {
        this.entityData.set(DATA_COAT, coat.ordinal());
        if (getSpecies() == Species.HORSE) {
            applyMoCAttributes();
        }
    }

    public boolean isSterileHybrid() {
        return getSpecies().isSterile();
    }

    public int getSyncedAge() {
        return this.entityData.get(DATA_SYNCED_AGE);
    }

    public int getMouthTicks() {
        return this.entityData.get(DATA_MOUTH_TICKS);
    }

    /** True while the rearing pose from temper bucking lasts (unlike
     *  vanilla's entity.isStanding(), which also triggers while charging
     *  a mounted jump; this one is exclusive to a failed taming attempt). */
    public boolean isBucking() {
        return this.entityData.get(DATA_BUCKING_TICKS) > 0;
    }

    /** Duration (in ticks) the player is allowed to stay mounted before
     *  being thrown off on a failed taming attempt, to give the feeling
     *  that the horse "holds on" a bit before it starts bucking. */
    private static final int TAME_HOLD_DURATION_TICKS = 40;

    /** True during the brief window where the player is already mounted
     *  after a taming attempt that's going to fail, but hasn't been
     *  thrown off yet and the rearing animation hasn't started. While
     *  this lasts, no one else should be allowed to try mounting the horse. */
    public boolean isTameHolding() {
        return this.entityData.get(DATA_TAME_HOLD_TICKS) > 0;
    }

    /** Server-side-only flag for whether the current hold will end in a
     *  failed taming attempt (throwing the player off + rearing) instead
     *  of just expiring without doing anything. */
    private boolean pendingFailedTameThrow = false;

    public int getGrazeTicks() {
        return this.entityData.get(DATA_GRAZE_TICKS);
    }

    public int getWingFlapTicks() {
        return this.entityData.get(DATA_WING_FLAP_TICKS);
    }

    public boolean wantsHorseArmor() {
        return (getSpecies() == Species.HORSE || getSpecies() == Species.ZEBRA || getSpecies() == Species.ZORSE) && !isUndead();
    }

    private void dropArmorIfIncompatible() {
        if (!wantsHorseArmor() && !wantsCrystalArmor()) {
            ItemStack armor = this.getItemBySlot(EquipmentSlot.BODY);
            if (!armor.isEmpty()) {
                this.spawnAtLocation(armor);
                this.setItemSlot(EquipmentSlot.BODY, ItemStack.EMPTY);
            }
        }
    }
    
    @Override
    public void die(net.minecraft.world.damagesource.DamageSource damageSource) {
        super.die(damageSource);
        if (this.level().isClientSide || !this.isTamed()) {
            return;
        }
        boolean eligible = getSpecies() == Species.ZEBRA || getSpecies() == Species.ZORSE
                || getSpecies() == Species.UNICORN || getSpecies() == Species.NIGHTMARE
                || getSpecies() == Species.PEGASUS || getSpecies() == Species.DARK_PEGASUS
                || getSpecies() == Species.BATHORSE || getSpecies() == Species.FAIRY_HORSE
                || isUndead() || isSkeletonStage();
        if (!eligible || this.random.nextInt(4) != 0) { // 25%
            return;
        }
        boolean wasFlyer = getSpecies() == Species.BATHORSE || getSpecies() == Species.PEGASUS
                || getSpecies() == Species.DARK_PEGASUS || getSpecies() == Species.FAIRY_HORSE;

        MoCHorseEntity ghost = com.example.neomocreatures.init.ModEntities.MOC_HORSE.get().create(this.level());
        if (ghost == null) return;
        ghost.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0F);
        ghost.setSpecies(wasFlyer ? Species.GHOST_WINGED : Species.GHOST);
        ghost.setTamed(true);
        ghost.setOwnerUUID(this.getOwnerUUID());
        ghost.setAge(0);
        this.level().addFreshEntity(ghost);
        ghost.playSound(ModSounds.HORSE_GHOST_GRUNT1.get(), 1.0F, 1.0F);
        com.example.neomocreatures.util.NamingHelper.promptRename(ghost, this.getOwnerUUID());
    }

    private boolean fleeing = false;

    private static final net.minecraft.resources.ResourceLocation ZEBRA_FLEE_SPEED_MODIFIER_ID =
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(com.example.neomocreatures.NeoMoCreatures.MODID, "zebra_flee_speed");

    public boolean isFleeing() {
        return this.fleeing;
    }

    public void setFleeing(boolean fleeing) {
        if (this.fleeing == fleeing) {
            return;
        }
        this.fleeing = fleeing;
        net.minecraft.world.entity.ai.attributes.AttributeInstance speedAttr = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr == null) {
            return;
        }
        if (fleeing) {
            if (speedAttr.getModifier(ZEBRA_FLEE_SPEED_MODIFIER_ID) == null) {
                speedAttr.addTransientModifier(new net.minecraft.world.entity.ai.attributes.AttributeModifier(
                        ZEBRA_FLEE_SPEED_MODIFIER_ID, 0.5D,
                        net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            }
        } else {
            speedAttr.removeModifier(ZEBRA_FLEE_SPEED_MODIFIER_ID);
        }
    }

    public boolean wantsChest() {
        return getSpecies() == Species.DONKEY || getSpecies() == Species.MULE || getSpecies() == Species.ZONKY
                || getSpecies() == Species.FAIRY_HORSE;
    }

    public boolean hasChest() {
        return this.entityData.get(DATA_HAS_CHEST);
    }

    private void setHasChest(boolean hasChest) {
        this.entityData.set(DATA_HAS_CHEST, hasChest);
    }

    private static boolean isHorseArmorItem(ItemStack stack) {
        return stack.is(Items.IRON_HORSE_ARMOR) || stack.is(Items.GOLDEN_HORSE_ARMOR) || stack.is(Items.DIAMOND_HORSE_ARMOR)
                || stack.is(ModItems.HORSE_ARMOR_CRYSTAL.get());
    }

    private boolean acceptsArmorItem(ItemStack stack) {
        if (stack.is(ModItems.HORSE_ARMOR_CRYSTAL.get())) return wantsCrystalArmor();
        return wantsHorseArmor() && isHorseArmorItem(stack);
    }

    private static boolean isEssenceItem(ItemStack stack) {
        return stack.is(ModItems.ESSENCE_OF_DARKNESS.get())
                || stack.is(ModItems.ESSENCE_OF_FIRE.get())
                || stack.is(ModItems.ESSENCE_OF_UNDEAD.get())
                || stack.is(ModItems.ESSENCE_OF_LIGHT.get());
    }

    private void openMouth() {
        this.entityData.set(DATA_MOUTH_TICKS, MOUTH_OPEN_TICKS);
    }

    @Override
    public void playAmbientSound() {
        super.playAmbientSound();
        if (!this.level().isClientSide) {
            openMouth();
        }
    }

    @Override
    protected void playHurtSound(DamageSource source) {
        super.playHurtSound(source);
        if (!this.level().isClientSide) {
            openMouth();
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return gruntSound();
    }

    private SoundEvent gruntSound() {
        if (isUndead()) {
            return this.random.nextBoolean() ? ModSounds.HORSE_UNDEAD_GRUNT1.get() : ModSounds.HORSE_UNDEAD_GRUNT2.get();
        }
        if (getSpecies() == Species.GHOST || getSpecies() == Species.GHOST_WINGED) {
            return switch (this.random.nextInt(3)) {
                case 0 -> ModSounds.HORSE_GHOST_GRUNT1.get();
                case 1 -> ModSounds.HORSE_GHOST_GRUNT2.get();
                default -> ModSounds.HORSE_GHOST_GRUNT3.get();
            };
        }
        return switch (getSpecies()) {
            case DONKEY, MULE, ZONKY -> ModSounds.DONKEY_GRUNT.get();
            case ZEBRA, ZORSE -> ModSounds.ZEBRA_GRUNT.get();
            default -> ModSounds.HORSE_GRUNT.get();
        };
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        if (getSpecies() == Species.GHOST || getSpecies() == Species.GHOST_WINGED) return ModSounds.HORSE_GHOST_HURT.get();
        if (isUndead()) return ModSounds.HORSE_UNDEAD_HURT.get();
        return switch (getSpecies()) {
            case DONKEY, MULE, ZONKY -> ModSounds.DONKEY_HURT.get();
            case ZEBRA, ZORSE -> ModSounds.ZEBRA_HURT.get();
            default -> ModSounds.HORSE_HURT.get();
        };
    }

    

    @Override
    protected SoundEvent getDeathSound() {
        if (isUndead()) return ModSounds.HORSE_UNDEAD_DEATH.get();
        if (getSpecies() == Species.GHOST || getSpecies() == Species.GHOST_WINGED) return ModSounds.HORSE_GHOST_DEATH.get();
        return switch (getSpecies()) {
            case DONKEY, MULE, ZONKY -> ModSounds.DONKEY_DEATH.get();
            default -> ModSounds.HORSE_DEATH.get();
        };
    }

    /**
     * Sound vanilla plays (via makeMad()) when a player mounts an untamed
     * horse and it throws them off — the alternative taming path, next to
     * food/apples. makeMad() is already inherited from AbstractHorse and
     * already does everything else (throwing off the rider, raising
     * "temper", and rearing with setStanding(true)); here we just pick
     * which "mad" sound fits each variant.
     */
    @Override
    protected SoundEvent getAngrySound() {
        if (isUndead()) {
            return ModSounds.HORSE_MAD_UNDEAD.get();
        }
        if (getSpecies() == Species.GHOST || getSpecies() == Species.GHOST_WINGED) {
            return ModSounds.HORSE_GHOST_MAD.get();
        }
        return ModSounds.HORSE_MOB_AGGRESSIVE.get();
    }

    /**
     * Shared by both entry points into the chest UI (ground shift-right-click,
     * and E-while-riding via openCustomInventoryScreen below) so there's one
     * place that defines what the menu actually is.
     */
    private void openChestMenu(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            Component title = this.hasCustomName()
                    ? this.getDisplayName().copy().append(" Storage")
                    : Component.literal(this.getSpecies().name() + " Storage");
            serverPlayer.openMenu(new SimpleMenuProvider(
                    (id, inv, p) -> ChestMenu.threeRows(id, inv, this.chestInventory),
                    title));
        }
    }
    /**
     * AbstractHorse already wires up the E-while-riding key to this exact
     * method (that's inherited plumbing, not something added here) — it's
     * what was showing the plain saddle/armor screen instead of the chest.
     * Overriding it is the correct, minimal fix: for a donkey/mule/zonky
     * that actually has a chest, show our storage instead; everything else
     * (horse/zebra/zorse, or a chestless donkey) falls back to the normal
     * vanilla screen exactly as before.
     */
    @Override
    public void openCustomInventoryScreen(Player player) {
        if (this.level().isClientSide || !this.isTamed()) {
            return;
        }
        if (wantsChest() && hasChest()) {
            openChestMenu(player);
            return;
        }
        // No chest: none of vanilla's saddle/armor menu — instead, we ask
        // the client to open the player's regular inventory.
        if (player instanceof ServerPlayer serverPlayer) {
            net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(serverPlayer,
                    new com.example.neomocreatures.network.OpenPlayerInventoryPayload());
        }
    }

    /**
    * Consumes the item in the player's hand (except in creative mode),
    * plays the eating sound, and opens the horse's mouth. Every food
    * interaction in mobInteract() below used to start with these same
    * 5 copied lines; now they just call this method and apply their
    * specific effect (grow, heal, love, tame...).
    */
    private void consumeFoodItem(Player player, ItemStack stack) {
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        this.playSound(ModSounds.HORSE_EATING.get(), 1.0F, 1.0F);
        openMouth();
    }
    
    /**
    * The pumpkin only works to put the "normal" species (horse/zebra/
    * donkey) into heat. Special species (bathorse, dark pegasus,
    * nightmare, unicorn, pegasus, fairy horse) have their own essence
    * item for that; sterile hybrids don't breed; and an undead horse
    * doesn't go into heat either until it's cured.
    */
    private boolean canUsePumpkinForLove() {
        if (isSterileHybrid() || isUndead()) {
            return false;
        }
        return switch (getSpecies()) {
            case BATHORSE, DARK_PEGASUS, NIGHTMARE, UNICORN, PEGASUS, FAIRY_HORSE -> false;
            default -> true;
        };
    }

    /**
    * First group of mobInteract() interactions: everything related to
    * feeding it (growing a foal, making it fall in love, healing,
    * taming), plus the side effect of stopping the dance if it's
    * touched. Returns the result if some item matched, or null if
    * mobInteract() should keep checking the rest of the interactions.
    */
    private InteractionResult tryFeedingInteractions(Player player, ItemStack stack) {
        if (this.isBaby() && stack.is(ModItems.SUGAR_LUMP.get())) {
            if (!this.level().isClientSide) {
                consumeFoodItem(player, stack);
                growFromSugar(SUGAR_LUMP_GROWTH_FRACTION);
                ((ServerLevel) this.level()).sendParticles(
                        ParticleTypes.HAPPY_VILLAGER,
                        this.getX(), this.getY() + this.getBbHeight() * 0.5, this.getZ(),
                        8, 0.3, 0.3, 0.3, 0.0);
            }
            return InteractionResult.SUCCESS;
        }

        if (isDancing()) {
            this.getNavigation().stop();
        }

        if (this.isTamed() && !this.isBaby() && stack.is(Items.PUMPKIN) && canUsePumpkinForLove()) {
            if (!this.level().isClientSide && this.canFallInLove()) {
                consumeFoodItem(player, stack);
                this.setInLove(player);
            }
            return InteractionResult.SUCCESS;
        }

        if (this.isTamed() && stack.is(ModItems.HAYSTACK.get())) {
            if (!this.level().isClientSide) {
                consumeFoodItem(player, stack);
                this.setHealth(this.getMaxHealth());
            }
            return InteractionResult.SUCCESS;
        }

        if (!this.isTamed() && !this.isBaby() && isTamingFood(stack)) {
            if (!this.level().isClientSide) {
                consumeFoodItem(player, stack);
                this.applyOwnership(player);
                this.level().broadcastEntityEvent(this, (byte) 7);
            }
            return InteractionResult.SUCCESS;
        }

        return null;
    }

    /**
    * Second group of interactions: equipping armor (or ignoring the
    * click if it's already wearing some) and equipping the saddle. Same
    * as tryFeedingInteractions(), returns the result if something
    * matched, or null if mobInteract() should keep checking the rest.
    */
    private InteractionResult tryEquipmentInteractions(ItemStack stack) {
        if (this.isTamed() && !this.isBaby() && acceptsArmorItem(stack) && !this.getItemBySlot(EquipmentSlot.BODY).isEmpty()) {
            return InteractionResult.PASS;
        }

        if (this.isTamed() && !this.isBaby() && (wantsHorseArmor() || wantsCrystalArmor())
                && this.getItemBySlot(EquipmentSlot.BODY).isEmpty() && acceptsArmorItem(stack)) {
            if (!this.level().isClientSide) {
                this.setItemSlot(EquipmentSlot.BODY, stack.split(1));
            }
            return InteractionResult.SUCCESS;
        }
        if (this.isTamed() && !this.isBaby() && !this.isSaddled() && stack.is(ModItems.HORSE_SADDLE.get())) {
            if (!this.level().isClientSide) {
                this.inventory.setItem(0, stack.split(1));
                this.playSound(SoundEvents.HORSE_SADDLE, 1.0F, 1.0F);
            }
            return InteractionResult.SUCCESS;
        }

        return null;
    }

    /**
    * Third group: capturing it with the amulet, or otherwise mounting it
    * (giving priority first to whatever the held item wants to do, so
    * we don't have to keep adding exclusions by hand every time a new
    * item with its own interactLivingEntity gets created).
    */
    private InteractionResult tryAmuletOrRideInteraction(Player player, InteractionHand hand, ItemStack stack) {
        if (this.isTamed() && !isVanishing() && matchesAmulet(stack)) {
            if (!this.level().isClientSide) {
                if (stack.is(ModItems.PET_AMULET.get())) {
                    capturePetInstant(player, hand);
                } else {
                    ItemStack amulet = stack.split(1);
                    startAmuletCapture(amulet, player);
                }
            }
            return InteractionResult.SUCCESS;
        }
        if (this.isTamed() && this.isSaddled() && !this.isBaby() && !this.isVehicle() && !player.isSecondaryUseActive()
                && !isHorseArmorItem(stack) && !isEssenceItem(stack) && !isVanishing()) {
            // Before mounting, give priority to whatever the held item wants to
            // do (scrolls, future items with their own interactLivingEntity). That
            // way we don't have to keep adding exclusions by hand every time a
            // new item is created.
            if (!stack.isEmpty()) {
                InteractionResult itemResult = stack.interactLivingEntity(player, this, hand);
                if (itemResult.consumesAction()) {
                    return itemResult;
                }
            }
            if (!this.level().isClientSide) {
                player.startRiding(this);
            }
            return InteractionResult.SUCCESS;
        }

        return null;
    }

    /**
    * Fourth group: shears to remove armor/saddle, and dyeing a fairy
    * horse with its permanent color.
    */
    private InteractionResult tryUnequipOrDyeInteraction(Player player, ItemStack stack) {
        if (this.isTamed() && stack.is(Items.SHEARS) && !this.getItemBySlot(EquipmentSlot.BODY).isEmpty()) {
            if (!this.level().isClientSide) {
                ItemStack armor = this.getItemBySlot(EquipmentSlot.BODY);
                this.setItemSlot(EquipmentSlot.BODY, ItemStack.EMPTY);
                this.spawnAtLocation(armor);
            }
            return InteractionResult.SUCCESS;
        }

        /// Shears also remove the saddle.
        if (this.isTamed() && stack.is(Items.SHEARS) && this.isSaddled()) {
            if (!this.level().isClientSide) {
                ItemStack saddle = this.inventory.getItem(0);
                this.inventory.setItem(0, ItemStack.EMPTY);
                this.spawnAtLocation(saddle);
            }
            return InteractionResult.SUCCESS;
        }

        if (this.isTamed() && getSpecies() == Species.FAIRY_HORSE && !isFairyColorLocked() && !isColorTransforming()
                && stack.getItem() instanceof net.minecraft.world.item.DyeItem dyeItem
                && DYE_TO_FAIRY_COLOR.containsKey(dyeItem.getDyeColor())) {
            if (!this.level().isClientSide) {
                this.playSound(ModSounds.HORSE_EATING.get(), 1.0F, 1.0F);
                startFairyColorTransform(DYE_TO_FAIRY_COLOR.get(dyeItem.getDyeColor()));
                setFairyColorLocked(true);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            return InteractionResult.SUCCESS;
        }

        return null;
    }

    /**
    * Fifth group: renaming with a book (and adopting if it has no
    * owner), fitting a chest, and opening the chest while sneaking
    * (standing on the ground — the E key is used for that while
    * mounted, since right-click never reaches the entity you're
    * riding).
    */
    private InteractionResult tryNamingOrChestInteraction(Player player, ItemStack stack) {
        // A book lets the owner rename an already-tamed animal at any time.
        // An owner-less tamed horse (Scroll of Sale / Reset Owner) can be
        // renamed by anyone, which makes the renamer its new owner.
        if (this.isTamed() && stack.is(Items.BOOK)
                && (player.getUUID().equals(this.getOwnerUUID()) || this.getOwnerUUID() == null)) {
            if (!this.level().isClientSide && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                if (this.getOwnerUUID() == null) {
                    com.example.neomocreatures.util.NamingHelper.promptRenameAndAdopt(this, serverPlayer);
                } else {
                    com.example.neomocreatures.util.NamingHelper.promptRename(this, this.getOwnerUUID());
                }
            }
            return InteractionResult.SUCCESS;
        }

        if (this.isTamed() && !this.isBaby() && wantsChest() && !hasChest() && stack.is(Items.CHEST)) {
            if (!this.level().isClientSide) {
                setHasChest(true);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                this.playSound(SoundEvents.DONKEY_CHEST, 1.0F, 1.0F);
            }
            return InteractionResult.SUCCESS;
        }

        // Sneak + right-click, on the ground (not riding), opens the chest —
        // this only works when NOT currently riding the animal: right-click
        // targeting never hits the entity you're sitting on in vanilla, by
        // design, which is why the E-key path above exists at all. Use E
        // while mounted instead.
        if (this.isTamed() && !this.isBaby() && hasChest() && player.isSecondaryUseActive()) {
            openChestMenu(player);
            return InteractionResult.SUCCESS;
        }

        return null;
    }

    /**
    * Gradual temper-based taming (vanilla's classic mechanic): with an
    * empty hand the player DOES mount the horse. The attempt is
    * resolved right then: temper goes up (a native AbstractHorse field,
    * already saved to NBT on its own); if it hits max it tames exactly
    * like with an apple (applyOwnership -> naming screen) and the player
    * stays mounted. If it fails, they get automatically dismounted, take
    * a bit of damage, and the horse rears and plays "mad". While that
    * animation lasts (isBucking()) it can't be mounted again. With an
    * item in hand, or on a winged species: the horse reacts the same
    * (rearing + sound) but never gets mounted and temper doesn't go up.
    * Taming food (apple) is already handled above, in
    * tryFeedingInteractions(), and keeps working the same way. Only
    * called when the horse is NOT tamed; always returns a result (unlike
    * the previous groups, it never "falls through").
    */
    private InteractionResult tryRidingTameAttempt(Player player, ItemStack stack, InteractionHand hand) {
        if (this.isBaby()) {
            return super.mobInteract(player, hand);
        }
        if (this.isBucking() || this.isTameHolding()) {
            // Still rearing from a previous attempt, or already mounted and
            // about to be thrown off (temper hold in progress): ignore the
            // click without repeating the sound/animation until it's done.
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        if (!this.level().isClientSide) {
            if (stack.isEmpty() && !isWingedSpecies()) {
                attemptRidingTame(player);
            } else {
                buckWithoutTemperGain(player);
            }
        }
        return InteractionResult.sidedSuccess(this.level().isClientSide);
    }

    /**
     * Foals are not tamed through temperament (a wild foal cannot be ridden).
     * Instead, an apple tames them instantly, just like a wild adult horse
     * with isTamingFood() — but WITHOUT making them grow, since growth is
     * exclusively handled by growFromSugar(), and isFood() already blocks
     * the vanilla food shortcut. Any other item, or an empty hand, delegates
     * to the normal vanilla foal behavior (with no risk of growth for the
     * same reason).
     */
    private InteractionResult tryBabyTamingInteraction(Player player, ItemStack stack, InteractionHand hand) {
        if (isTamingFood(stack)) {
            if (!this.level().isClientSide) {
                consumeFoodItem(player, stack);
                this.applyOwnership(player);
                this.level().broadcastEntityEvent(this, (byte) 7);
            }
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    /**
    * Essence of Darkness: transforms zorse -> bathorse and pegasus ->
    * dark pegasus (with height/mounted restrictions in the latter case
    * while transforming), and also heals or triggers love mode on an
    * already-transformed bathorse/dark pegasus.
    */
    private InteractionResult tryEssenceOfDarknessInteraction(Player player, ItemStack stack) {
        //ESSENCE OF DARKNESS: ZORSE -> BATHORSE
        if (this.isTamed() && !this.isBaby() && stack.is(ModItems.ESSENCE_OF_DARKNESS.get())) {
            if (getSpecies() == Species.ZORSE && !isTransforming()) {
                if (!this.level().isClientSide) {
                    startTransform(Species.BATHORSE);
                    useEssence(player, stack);
                }
                return InteractionResult.SUCCESS;
            }
            if (getSpecies() == Species.BATHORSE) {
                if (!this.level().isClientSide) {
                    if (this.getHealth() < this.getMaxHealth()) {
                        this.heal(this.getMaxHealth());
                    } else if (!this.isInLove() && this.canFallInLove()) {
                        this.setInLove(player);
                    } else {
                        return InteractionResult.PASS;
                    }
                    useEssence(player, stack);
                }
                return InteractionResult.SUCCESS;
            }
        }

        //ESSENCE OF DARKNESS: PEGASUS -> DARK PEGASUS
        if (this.isTamed() && !this.isBaby() && stack.is(ModItems.ESSENCE_OF_DARKNESS.get())
                && getSpecies() == Species.PEGASUS) {
            if (!isTransforming()) {
                if (this.getY() < 150.0D) {
                    return InteractionResult.PASS;
                }
                if (this.isVehicle()) {
                    return InteractionResult.PASS;
                }
                if (!this.level().isClientSide) {
                    startTransform(Species.DARK_PEGASUS);
                    useEssence(player, stack);
                }
                return InteractionResult.SUCCESS;
            }
        }

        if (this.isTamed() && !this.isBaby() && stack.is(ModItems.ESSENCE_OF_DARKNESS.get())
                && getSpecies() == Species.DARK_PEGASUS) {
            if (!this.level().isClientSide) {
                if (this.getHealth() < this.getMaxHealth()) {
                    this.heal(this.getMaxHealth());
                } else if (!this.isInLove() && this.canFallInLove()) {
                    this.setInLove(player);
                } else {
                    return InteractionResult.PASS;
                }
                useEssence(player, stack);
            }
            return InteractionResult.SUCCESS;
        }

        return null;
    }

    /**
    * Essence of Fire: transforms zorse -> nightmare, and heals or
    * triggers love mode on an already-transformed nightmare.
    */
    private InteractionResult tryEssenceOfFireInteraction(Player player, ItemStack stack) {
        //ESSENCE OF FIRE: ZORSE -> NIGHTMARE
        if (this.isTamed() && !this.isBaby() && stack.is(ModItems.ESSENCE_OF_FIRE.get())) {
            if (getSpecies() == Species.ZORSE && !isTransforming()) {
                if (!this.level().isClientSide) {
                    startTransform(Species.NIGHTMARE);
                    useEssence(player, stack);
                }
                return InteractionResult.SUCCESS;
            }
            if (getSpecies() == Species.NIGHTMARE) {
                if (!this.level().isClientSide) {
                    if (this.getHealth() < this.getMaxHealth()) {
                        this.heal(this.getMaxHealth());
                    } else if (!this.isInLove() && this.canFallInLove()) {
                        this.setInLove(player);
                    } else {
                        return InteractionResult.PASS;
                    }
                    useEssence(player, stack);
                }
                return InteractionResult.SUCCESS;
            }
        }

        return null;
    }

    /**
    * Essence of Undead: converts horse/zorse/unicorn, or
    * bathorse/pegasus/dark_pegasus, into their undead version (as long
    * as it's not blocked by Essence of Light); if it's already undead,
    * it instead resets the stage to 0 and heals.
    */
    private InteractionResult tryEssenceOfUndeadInteraction(Player player, ItemStack stack) {
        //ESSENCE OF UNDEAD: HORSE/ZORSE -> UNDEAD HORSE
        if (this.isTamed() && !this.isBaby() && stack.is(ModItems.ESSENCE_OF_UNDEAD.get())
                && (getSpecies() == Species.HORSE || getSpecies() == Species.ZORSE || getSpecies() == Species.UNICORN)
                && !isUndeadLocked()) {
            if (!this.level().isClientSide) {
                if (!isUndead() && !isUndeadTransforming()) {
                    this.entityData.set(DATA_UNDEAD_TRANSFORM_TICKS, UNDEAD_TRANSFORM_DURATION_TICKS);
                    useEssence(player, stack);
                } else if (!isUndeadTransforming()) {
                    if (this.getHealth() < this.getMaxHealth()) {
                        this.heal(this.getMaxHealth());
                    }
                    setUndeadStage(UNDEAD_STAGE_0);
                    undeadDecayTicks = 0;
                    useEssence(player, stack);
                }
            }
            return InteractionResult.SUCCESS;
        }

        //ESSENCE OF UNDEAD: BATHORSE/PEGASUS/DARK_PEGASUS -> UNDEAD PEGASUS
        if (this.isTamed() && !this.isBaby() && stack.is(ModItems.ESSENCE_OF_UNDEAD.get())
                && (getSpecies() == Species.BATHORSE || getSpecies() == Species.PEGASUS || getSpecies() == Species.DARK_PEGASUS)
                && !isUndeadLocked()) {
            if (!this.level().isClientSide) {
                if (!isUndead() && !isUndeadTransforming()) {
                    this.entityData.set(DATA_UNDEAD_TRANSFORM_TICKS, UNDEAD_TRANSFORM_DURATION_TICKS);
                    useEssence(player, stack);
                } else if (!isUndeadTransforming()) {
                    if (this.getHealth() < this.getMaxHealth()) {
                        this.heal(this.getMaxHealth());
                    }
                    setUndeadStage(UNDEAD_STAGE_0);
                    undeadDecayTicks = 0;
                    useEssence(player, stack);
                }
            }
            return InteractionResult.SUCCESS;
        }

        return null;
    }

    /**
    * Essence of Light: permanently locks the current undead stage
    * pegasus (with the same height/mounted restrictions as Essence of
    * Darkness), and heals or triggers love mode on already-transformed
    * unicorn/pegasus/fairy horse.
    */
    private InteractionResult tryEssenceOfLightInteraction(Player player, ItemStack stack) {
        //ESSENCE OF LIGHT: Permanent Undead Stage
        if (this.isTamed() && !this.isBaby() && stack.is(ModItems.ESSENCE_OF_LIGHT.get())
                && isUndead() && !isUndeadTransforming()) {
            if (!this.level().isClientSide) {
                this.heal(this.getMaxHealth());
                setUndeadLocked(true);
                useEssence(player, stack);
            }
            return InteractionResult.SUCCESS;
        }

        //ESSENCE OF LIGHT: NIGHTMARE -> UNICORN
        if (this.isTamed() && !this.isBaby() && stack.is(ModItems.ESSENCE_OF_LIGHT.get())) {
            if (getSpecies() == Species.NIGHTMARE && !isTransforming()) {
                if (!this.level().isClientSide) {
                    startTransform(Species.UNICORN);
                    useEssence(player, stack);
                }
                return InteractionResult.SUCCESS;
            }
            if (getSpecies() == Species.UNICORN) {
                if (!this.level().isClientSide) {
                    if (this.getHealth() < this.getMaxHealth()) {
                        this.heal(this.getMaxHealth());
                    } else if (!this.isInLove() && this.canFallInLove()) {
                        this.setInLove(player);
                    } else {
                        return InteractionResult.PASS;
                    }
                    useEssence(player, stack);
                }
                return InteractionResult.SUCCESS;
            }
        }

        //ESSENCE OF LIGHT: BATHORSE -> PEGASUS
        if (this.isTamed() && !this.isBaby() && stack.is(ModItems.ESSENCE_OF_LIGHT.get())
                && getSpecies() == Species.BATHORSE) {
            if (!isTransforming()) {
                if (this.getY() < 150.0D) {
                    return InteractionResult.PASS;
                }
                if (this.isVehicle()) {
                    return InteractionResult.PASS;
                }
                if (!this.level().isClientSide) {
                    startTransform(Species.PEGASUS);
                    useEssence(player, stack);
                }
                return InteractionResult.SUCCESS;
            }
        }

        if (this.isTamed() && !this.isBaby() && stack.is(ModItems.ESSENCE_OF_LIGHT.get())
                && getSpecies() == Species.PEGASUS) {
            if (!this.level().isClientSide) {
                if (this.getHealth() < this.getMaxHealth()) {
                    this.heal(this.getMaxHealth());
                } else if (!this.isInLove() && this.canFallInLove()) {
                    this.setInLove(player);
                } else {
                    return InteractionResult.PASS;
                }
                useEssence(player, stack);
            }
            return InteractionResult.SUCCESS;
        }

        if (this.isTamed() && !this.isBaby() && stack.is(ModItems.ESSENCE_OF_LIGHT.get())
                && getSpecies() == Species.FAIRY_HORSE) {
            if (!this.level().isClientSide) {
                if (this.getHealth() < this.getMaxHealth()) {
                    this.heal(this.getMaxHealth());
                } else if (!this.isInLove() && this.canFallInLove()) {
                    this.setInLove(player);
                } else {
                    return InteractionResult.PASS;
                }
                useEssence(player, stack);
            }
            return InteractionResult.SUCCESS;
        }

        return null;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        InteractionResult feedResult = tryFeedingInteractions(player, stack);
        if (feedResult != null) {
            return feedResult;
        }
        
        InteractionResult equipResult = tryEquipmentInteractions(stack);
        if (equipResult != null) {
            return equipResult;
        }


        InteractionResult amuletOrRideResult = tryAmuletOrRideInteraction(player, hand, stack);
        if (amuletOrRideResult != null) {
            return amuletOrRideResult;
        }

        InteractionResult unequipOrDyeResult = tryUnequipOrDyeInteraction(player, stack);
        if (unequipOrDyeResult != null) {
            return unequipOrDyeResult;
        }

        InteractionResult namingOrChestResult = tryNamingOrChestInteraction(player, stack);
        if (namingOrChestResult != null) {
            return namingOrChestResult;
        }

        // Baby taming
        if (!this.isTamed() && this.isBaby()) {
            return tryBabyTamingInteraction(player, stack, hand);
        }

        if (!this.isTamed()) {
            return tryRidingTameAttempt(player, stack, hand);
        }

        InteractionResult darknessResult = tryEssenceOfDarknessInteraction(player, stack);
        if (darknessResult != null) {
            return darknessResult;
        }

        InteractionResult fireResult = tryEssenceOfFireInteraction(player, stack);
        if (fireResult != null) {
            return fireResult;
        }
        

        InteractionResult undeadResult = tryEssenceOfUndeadInteraction(player, stack);
        if (undeadResult != null) {
            return undeadResult;
        }

        InteractionResult lightResult = tryEssenceOfLightInteraction(player, stack);
        if (lightResult != null) {
            return lightResult;
        }

        if (this.isTamed() && !this.isBaby() && getSpecies() == Species.NIGHTMARE && stack.is(Items.REDSTONE)) {
            if (!this.level().isClientSide) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                this.playSound(ModSounds.HORSE_EATING.get(), 1.0F, 1.0F);
                openMouth();
                this.nightmareFleeYaw = this.getYRot();
                this.getNavigation().stop();
                setNightmareTicks(200); // 10 segundos
            }
            return InteractionResult.SUCCESS;
        }

        if (getSpecies() == Species.ZEBRA && !stack.isEmpty() &&stack.has(DataComponents.JUKEBOX_PLAYABLE)) {
            if (!this.level().isClientSide) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                this.playSound(ModSounds.HORSE_EATING.get(), 1.0F, 1.0F);
                openMouth();
                ItemEntity itemEntity = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(),
                        new ItemStack(ModItems.RECORD_SHUFFLE.get()));
                itemEntity.setPickUpDelay(20);
                this.level().addFreshEntity(itemEntity);
            }
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    private void growFromSugar(float fraction) {
        int reduction = Math.round(FULL_GROWTH_TICKS * fraction);
        int newAge = Math.min(0, this.getAge() + reduction);
        this.setAge(newAge);
    }

    /** Winged species: the rearing animation doesn't account for them (the
     *  wings don't follow the body's tilt), so they're left out of mount-
     *  based taming and can only be tamed with food, as before this feature. */
    private boolean isWingedSpecies() {
        return getSpecies() == Species.BATHORSE || getSpecies() == Species.PEGASUS
                || getSpecies() == Species.DARK_PEGASUS || getSpecies() == Species.FAIRY_HORSE
                || getSpecies() == Species.GHOST_WINGED;
    }

    private void applyOwnership(Player player) {
        this.setTamed(true);
        this.setOwnerUUID(player.getUUID());
        com.example.neomocreatures.util.NamingHelper.promptRename(this, player.getUUID());
    }

    /**
    * An attempt to mount a wild horse with an empty hand: the player
    * mounts and the attempt is resolved right there. Vanilla temper goes
    * up (AbstractHorse already saves it in NBT); if that reaches max it
    * tames like with an apple and the player stays mounted. Otherwise,
    * they get dismounted, take a bit of damage, and the horse rears.
    * Only called on the server.
    */
    private void attemptRidingTame(Player player) {
        player.startRiding(this);
        int gained = 5 + this.random.nextInt(20);
        int newTemper = this.modifyTemper(gained);
        if (newTemper >= this.getMaxTemper()) {
            this.applyOwnership(player);
            this.level().broadcastEntityEvent(this, (byte) 7);
        } else {
            // The attempt failed, but instead of throwing the player off right
            // away we keep them mounted for a while (TAME_HOLD_DURATION_TICKS)
            // so it feels like the horse holds on for a bit. The actual throw-off
            // + rearing animation are resolved in tick() when the hold reaches 0
            // (see resolveFailedTameAttempt).
            this.pendingFailedTameThrow = true;
            this.entityData.set(DATA_TAME_HOLD_TICKS, TAME_HOLD_DURATION_TICKS);
        }
    }

    /**
    * Resolves a failed taming attempt whose "hold" has already ended:
    * throws the player off (if still mounted), deals a bit of damage,
    * applies a push, and starts the rearing animation. If the player
    * had already dismounted on their own during the hold, it just skips
    * the damage and push but still triggers the animation. Only called
    * on the server, from tick().
    */
    private void resolveFailedTameAttempt() {
        Entity passenger = this.getFirstPassenger();
        if (passenger instanceof Player player) {
            player.stopRiding();
            player.hurt(this.damageSources().mobAttack(this), 1.0F);
            player.knockback(0.6D, this.getX() - player.getX(), this.getZ() - player.getZ());
        }
        this.makeMad();
        this.openMouth();
        this.entityData.set(DATA_BUCKING_TICKS, 20);
    }

    /**
    * A wild horse's reaction when interacted with while holding an item,
    * or on a winged species: plays the "mad" sound and pushes the player
    * the same as a failed taming attempt, but it does NOT count as a
    * real attempt, so it doesn't touch temper. The rearing pose only
    * triggers if it's NOT a winged species, since that animation doesn't
    * account for them. Only called on the server.
    */
    private void buckWithoutTemperGain(Player player) {
        this.makeMad();
        this.openMouth();
        if (!isWingedSpecies()) {
            this.entityData.set(DATA_BUCKING_TICKS, 20);
        }
        player.knockback(0.6D, this.getX() - player.getX(), this.getZ() - player.getZ());
    }

    private boolean isTamingFood(ItemStack stack) {
        return stack.is(Items.APPLE);
    }

    /**
     * Foal growth in this mod is exclusively handled via sugar cubes
     * (growFromSugar), not through generic food. Without this override,
     * Minecraft uses its own default "food" criteria (which includes
     * apples) and allows a foal to grow when fed an apple without going
     * through our logic — which is why we block it entirely here.
     */
    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    public boolean isFlyingNow() {
        return (getSpecies() == Species.BATHORSE || getSpecies() == Species.PEGASUS || getSpecies() == Species.DARK_PEGASUS || getSpecies() == Species.FAIRY_HORSE || getSpecies() == Species.GHOST_WINGED)
                && this.isTamed() && !this.onGround() && !isTransforming();
    }

    @Override
    public boolean fireImmune() {
        return getSpecies() == Species.NIGHTMARE || getSpecies() == Species.DARK_PEGASUS || super.fireImmune();
    }

    @Override
    public boolean canDrownInFluidType(net.neoforged.neoforge.fluids.FluidType type) {
        return !isSkeletonStage() && super.canDrownInFluidType(type);
    }

    @Override
    public boolean isInvertedHealAndHarm() {
        return isUndead();
    }

    public boolean wantsCrystalArmor() {
        return (getSpecies() == Species.UNICORN || getSpecies() == Species.PEGASUS
                || getSpecies() == Species.DARK_PEGASUS || getSpecies() == Species.BATHORSE
                || getSpecies() == Species.FAIRY_HORSE
                || getSpecies() == Species.GHOST || getSpecies() == Species.GHOST_WINGED) && !isUndead();
    }

    private boolean descendHeld = false;

    private boolean ascendHeld = false;

    private int shuffleCounter = 0;

    public boolean isDancing() {
        return this.entityData.get(DATA_DANCING);
    }

    private void setDancing(boolean dancing) {
        this.entityData.set(DATA_DANCING, dancing);
    }

    private boolean isNearPlayingShuffleRecord() {
        BlockPos center = this.blockPosition();
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-6, -6, -6), center.offset(6, 6, 6))) {
            if (this.level().getBlockEntity(pos) instanceof JukeboxBlockEntity jukebox
                    && jukebox.getTheItem().is(ModItems.RECORD_SHUFFLE.get())) {
                return true;
            }
        }
        return false;
    }

    public void setAscendHeld(boolean held) {
        this.ascendHeld = held;
    }

    private int nightmareTicks = 0;

    public void setNightmareTicks(int ticks) {
        this.nightmareTicks = ticks;
    }

    public int getNightmareTicks() {
        return this.nightmareTicks;
    }

    private int unicornChargeTicks = 0;
    public static final int UNICORN_CHARGE_DURATION_TICKS = 60; // 3 segundos

    public void startUnicornCharge() {
        this.unicornChargeTicks = UNICORN_CHARGE_DURATION_TICKS;
        this.entityData.set(DATA_UNICORN_CHARGE_TICKS, UNICORN_CHARGE_DURATION_TICKS);
        this.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED, 60, 2, false, true));
    }

    public int getUnicornChargeTicks() {
        return this.entityData.get(DATA_UNICORN_CHARGE_TICKS);
    }

    private void unicornChargeTick() {
        net.minecraft.world.phys.AABB aabb = this.getBoundingBox().inflate(0.6D);
        for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class, aabb,
                e -> e != this && e != this.getControllingPassenger() && e.isAlive())) {
            if (target.hurt(this.damageSources().mobAttack(this), 6.0F)) {
                net.minecraft.world.phys.Vec3 knockDir = target.position().subtract(this.position()).normalize();
                target.setDeltaMovement(target.getDeltaMovement().add(knockDir.x * 1.2D, 0.4D, knockDir.z * 1.2D));
                target.hurtMarked = true;
            }
        }
        unicornChargeTicks--;
        if (this.getUnicornChargeTicks() > 0) {
            this.entityData.set(DATA_UNICORN_CHARGE_TICKS, this.getUnicornChargeTicks() - 1);
        }
    }

    public int getUndeadStage() {
        return this.entityData.get(DATA_UNDEAD_STAGE);
    }

    private void setUndeadStage(int stage) {
        this.entityData.set(DATA_UNDEAD_STAGE, stage);
    }

    public boolean isUndead() {
        return getUndeadStage() != UNDEAD_NONE;
    }

    public boolean isUndeadTransforming() {
        return this.entityData.get(DATA_UNDEAD_TRANSFORM_TICKS) > 0;
    }

    public int getUndeadTransformTicks() {
        return this.entityData.get(DATA_UNDEAD_TRANSFORM_TICKS);
    }

    public boolean isSkeletonStage() {
        return getUndeadStage() == UNDEAD_SKELETON;
    }

    public boolean isUndeadLocked() {
        return this.entityData.get(DATA_UNDEAD_LOCKED);
    }

    private void setUndeadLocked(boolean locked) {
        this.entityData.set(DATA_UNDEAD_LOCKED, locked);
    }

    private void nightmareFireEffect() {
        BlockPos pos = new BlockPos(
                net.minecraft.util.Mth.floor(this.getX()),
                net.minecraft.util.Mth.floor(this.getBoundingBox().minY),
                net.minecraft.util.Mth.floor(this.getZ())
        ).offset(-1, 0, -1);

        if (this.level().getBlockState(pos).isAir()) {
            this.level().setBlockAndUpdate(pos, net.minecraft.world.level.block.Blocks.FIRE.defaultBlockState());
        }
    }

    private void unicornJumpTrail() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        net.minecraft.core.particles.SimpleParticleType particle =
                getSpecies() == Species.FAIRY_HORSE
                        ? com.example.neomocreatures.init.ModParticles.starFxForFairyColor(getFairyColor())
                        : com.example.neomocreatures.init.ModParticles.STAR_FX.get();
        serverLevel.sendParticles(particle,
                this.getX(), this.getY() + 0.2D, this.getZ(),
                1, 0.3D, 0.1D, 0.3D, 0.01D);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return ModEntities.MOC_HORSE.get().create(level);
    }

    /**
    * All the horse's timed transformations (species change, fairy color
    * change, amulet fade-out, and undead transformation) followed the
    * same pattern: countdown, sound at 60 ticks, final effect on
    * reaching 0. tick() used to call them one after another inline; now
    * they live together here so tick() reads at a glance.
    */
    private void tickTransformationTimers() {
        if (isTransforming()) {
            int ticks = getTransformTicks() - 1;
            this.entityData.set(DATA_TRANSFORM_TICKS, ticks);
            if (ticks == 60) {
                this.playSound(ModSounds.HORSE_TRANSFORM.get(), 1.0F, 1.0F);
            }
            if (ticks <= 0) {
                setSpecies(getTransformTarget());
                this.entityData.set(DATA_TRANSFORM_TARGET, UNSET);
                dropArmorIfIncompatible();
                this.setHealth((float) this.getMaxHealth());
            }
        }

        if (isColorTransforming()) {
            int ticks = getColorTransformTicks() - 1;
            this.entityData.set(DATA_COLOR_TRANSFORM_TICKS, ticks);
            if (ticks == 60) {
                this.playSound(ModSounds.HORSE_TRANSFORM.get(), 1.0F, 1.0F);
            }
            if (ticks <= 0) {
                setFairyColor(getColorTransformTarget());
                this.entityData.set(DATA_COLOR_TRANSFORM_TARGET, UNSET);
            }
        }
        if (isVanishing()) {
            int ticks = getVanishTicks() + 1;
            int duration = this.entityData.get(DATA_VANISH_DURATION_TICKS);
            if (ticks > duration) {
                if (this.pendingAmuletTemplate != null) {
                    completeAmuletCapture();
                } else {
                    this.dropSaddleAndArmor();
                    this.discard();
                }
            } else {
                this.entityData.set(DATA_VANISH_TICKS, ticks);
            }
        }

        if (isUndeadTransforming()) {
            int ticks = getUndeadTransformTicks() - 1;
            this.entityData.set(DATA_UNDEAD_TRANSFORM_TICKS, ticks);
            if (ticks == 60) {
                this.playSound(ModSounds.HORSE_TRANSFORM.get(), 1.0F, 1.0F);
            }
            if (ticks <= 0) {
                if (getSpecies() == Species.BATHORSE || getSpecies() == Species.DARK_PEGASUS) {
                    setSpecies(Species.PEGASUS);
                }
                setUndeadStage(UNDEAD_STAGE_0);
                undeadDecayTicks = 0;
                dropArmorIfIncompatible();
                this.setHealth((float) this.getMaxHealth());
            }
        }
    }

    /**
    * Per-tick counters with no relation to each other beyond all being
    * "subtract 1 and apply the effect when it hits 0/a special state":
    * open mouth, rearing, failed-taming hold (with its resolution), fall
    * immunity, and the speed adjustment while dismounted. Also syncs the
    * visible age to the client.
    */
    private void tickCountdownTimers() {
        if (this.getMouthTicks() > 0) {
            this.entityData.set(DATA_MOUTH_TICKS, this.getMouthTicks() - 1);
        }
        if (this.entityData.get(DATA_BUCKING_TICKS) > 0) {
            this.entityData.set(DATA_BUCKING_TICKS, this.entityData.get(DATA_BUCKING_TICKS) - 1);
        }
        if (this.entityData.get(DATA_TAME_HOLD_TICKS) > 0) {
            int holdTicks = this.entityData.get(DATA_TAME_HOLD_TICKS) - 1;
            this.entityData.set(DATA_TAME_HOLD_TICKS, holdTicks);
            if (holdTicks <= 0 && this.pendingFailedTameThrow) {
                this.pendingFailedTameThrow = false;
                this.resolveFailedTameAttempt();
            }
        }
        if (this.fallImmuneTicks > 0) {
            this.fallImmuneTicks--;
        }
        if (this.isSlowedWhenUnridden()) {
            net.minecraft.world.entity.ai.attributes.AttributeInstance speedAttr = this.getAttribute(Attributes.MOVEMENT_SPEED);
            if (speedAttr != null) {
                if (!this.isVehicle() && speedAttr.getBaseValue() != SPECIAL_UNMOUNTED_SPEED) {
                    speedAttr.setBaseValue(SPECIAL_UNMOUNTED_SPEED);
                } else if (this.isVehicle() && speedAttr.getBaseValue() == SPECIAL_UNMOUNTED_SPEED) {
                    applyMoCAttributes();
                }
            }
        }
        this.entityData.set(DATA_SYNCED_AGE, this.getAge());
    }
    
    /**
     * "Grazing" state: resets if it gets mounted or is fleeing, decreases
     * normally, and starts randomly (1 in 1000 per tick) while it's
     * tamed, an adult, on the ground, and not fleeing.
     */
    private void tickGrazing() {
        if (this.isVehicle() && this.getGrazeTicks() > 0) {
            this.entityData.set(DATA_GRAZE_TICKS, 0);
        }

        if (this.getGrazeTicks() > 0) {
            if (isFleeing()) {
                this.entityData.set(DATA_GRAZE_TICKS, 0);
            } else {
                this.entityData.set(DATA_GRAZE_TICKS, this.getGrazeTicks() - 1);
            }
        } else if (!this.isBaby() && !this.isVehicle() && !isFleeing() && this.random.nextInt(1000) == 0) { //How common is grazing
            this.entityData.set(DATA_GRAZE_TICKS, 100);
        }
    }

     /**
     * Synced wing-flap flag for the fairy horse (different from the
     * pegasus/bathorse flap sound, which lives in tickWingFlapSounds()):
     * counts down while active, and triggers randomly (1 in 150) while
     * on the ground.
     */
    private void tickFairyWingFlapFlag() {
        if (getSpecies() == Species.FAIRY_HORSE) {
            if (this.getWingFlapTicks() > 0) {
                this.entityData.set(DATA_WING_FLAP_TICKS, this.getWingFlapTicks() - 1);
            } else if (this.onGround() && this.random.nextInt(150) == 0) {
                this.entityData.set(DATA_WING_FLAP_TICKS, WING_FLAP_DURATION_TICKS);
            }
        }
    }

    /**
     * Slow, random passive regeneration (1 in 300 per tick) while the
     * horse is alive and not at max health.
     */
    private void tickPassiveRegen() {
        if (this.random.nextInt(300) == 0 && this.getHealth() > 0F && this.getHealth() < this.getMaxHealth()) {
            this.heal(1.0F);
        }
    }

     /**
     * Zebra dance when a shuffle disc plays nearby: starts randomly while
     * it's playing, stops if the disc stops, and halts navigation while
     * dancing.
     */
    private void tickZebraDancing() {
        if (getSpecies() == Species.ZEBRA && this.isTamed() && !this.isBaby()) {
            if (shuffleCounter == 0 && this.random.nextInt(50) == 0 && isNearPlayingShuffleRecord()) {
                shuffleCounter = 1;
                setDancing(true);
            }
            if (shuffleCounter > 0) {
                shuffleCounter++;
                if (!isNearPlayingShuffleRecord()) {
                    shuffleCounter = 0;
                    setDancing(false);
                }
            }
            if (isDancing()) {
                this.getNavigation().stop();
            }
        }
    }

    /**
    * Periodic wing-flap sound for tamed flyers that aren't transforming:
    * bathorse/pegasus/dark pegasus on one side, and fairy horse/ghost
    * winged on the other (each group with its own period and its own
    * "grounded streak" to stop the sound if they've been on the ground
    */
    private void tickWingFlapSounds() {
        if ((getSpecies() == Species.BATHORSE || getSpecies() == Species.PEGASUS || getSpecies() == Species.DARK_PEGASUS)
                && this.isTamed() && !isTransforming()) {
            groundedStreak = this.onGround() ? groundedStreak + 1 : 0;
            boolean flappingEligible = groundedStreak < WING_FLAP_GROUND_GRACE;

            if (flappingEligible && this.tickCount % WING_FLAP_PERIOD_TICKS == 0) {
                this.playSound(ModSounds.HORSE_WING_FLAP.get(), 1.0F, 1.0F);
            }
        } else {
            groundedStreak = 0;
        }

        if ((getSpecies() == Species.FAIRY_HORSE || getSpecies() == Species.GHOST_WINGED)
                && this.isTamed() && !isTransforming()) {
            butterflyGroundedStreak = this.onGround() ? butterflyGroundedStreak + 1 : 0;
            boolean butterflyFlappingEligible = butterflyGroundedStreak < WING_FLAP_GROUND_GRACE;

            if (butterflyFlappingEligible && this.tickCount % BUTTERFLY_WING_FLAP_PERIOD_TICKS == 0) {
                this.playSound(ModSounds.HORSE_WING_FLAP.get(), 1.0F, 1.0F);
            }
        } else {
            butterflyGroundedStreak = 0;
        }
    }

    /**
     * Nightmare's behavior while fleeing (triggered by redstone dust):
     * random fire effect and forced straight-line movement along its
     * flee yaw, until the counter runs out.
     */
    private void tickNightmareBehavior() {
        if (getSpecies() == Species.NIGHTMARE && getNightmareTicks() > 0) {
            if (this.random.nextInt(2) == 0) {
                nightmareFireEffect();
            }
            if (!this.isVehicle()) {
                float yaw = this.nightmareFleeYaw * ((float) Math.PI / 180F);
                double speed = 0.4D;
                this.setDeltaMovement(-Math.sin(yaw) * speed, this.getDeltaMovement().y, Math.cos(yaw) * speed);
                this.setYRot(this.nightmareFleeYaw);
                this.setYHeadRot(this.nightmareFleeYaw);
            }
            setNightmareTicks(getNightmareTicks() - 1);
        }
    }

    /**
     * Progressive advance of the undead stage (not to be confused with
     * tickTransformationTimers(), which handles the initial/final undead
     * transformation): while it's undead, isn't a skeleton yet, and isn't
     * locked, it counts up to the stage duration and advances by one.
     */
    private void tickUndeadDecayProgress() {
        if (isUndead() && !isSkeletonStage() && !isUndeadLocked()) {
            undeadDecayTicks++;
            if (undeadDecayTicks >= UNDEAD_STAGE_DURATION_TICKS) {
                undeadDecayTicks = 0;
                setUndeadStage(getUndeadStage() + 1);
            }
        }
    }

     /**
     * All the purely cosmetic particles that are only computed on the
     * client: dance notes, the fade-out spiral, undead decay motes, and
     * nightmare embers.
     */
    private void tickClientSideParticles() {
        if (isDancing() && this.random.nextInt(4) == 0) {
            double dx = this.random.nextGaussian() * 0.5D;
            double dy = this.random.nextGaussian() * -0.1D;
            double dz = this.random.nextGaussian() * 0.02D;
            this.level().addParticle(ParticleTypes.NOTE,
                    this.getX() + this.random.nextFloat() * this.getBbWidth() * 2.0F - this.getBbWidth(),
                    this.getY() + 0.5D + this.random.nextFloat() * this.getBbHeight(),
                    this.getZ() + this.random.nextFloat() * this.getBbWidth() * 2.0F - this.getBbWidth(),
                    dx, dy, dz);
        }

        if (isVanishing()) {
            int duration = this.entityData.get(DATA_VANISH_DURATION_TICKS);
            float progress = this.getVanishTicks() / (float) duration;
            double maxRadius = this.getBbWidth() * 1.3D;
            double radius = maxRadius * Math.pow(1.0D - progress, 2.0D);
            double spinSpeed = 0.5D + progress * 2.5D;

            int points = 8;
            double baseAngle = this.getVanishTicks() * spinSpeed;
            for (int i = 0; i < points; i++) {
                double angle = baseAngle + (2 * Math.PI * i / points);
                double px = this.getX() + Math.cos(angle) * radius;
                double pz = this.getZ() + Math.sin(angle) * radius;
                double py = this.getY() + 0.1D;
                this.level().addParticle(ModParticles.VANISH_FX.get(), px, py, pz, 0.0D, 0.01D, 0.0D);
            }
        }
        if (isUndead() && !isSkeletonStage() && !isUndeadLocked() && this.random.nextInt(8) == 0) {
            this.level().addParticle(ModParticles.UNDEAD_DECAY.get(),
                    this.getX() + (this.random.nextDouble() - 0.5) * this.getBbWidth(),
                    this.getY() + this.random.nextDouble() * this.getBbHeight(),
                    this.getZ() + (this.random.nextDouble() - 0.5) * this.getBbWidth(),
                    0.0D, 0.0D, 0.0D);
        }

        if (getSpecies() == Species.NIGHTMARE && this.random.nextInt(50) == 0) {
            double vx = this.random.nextGaussian() * 0.02D;
            double vy = this.random.nextGaussian() * 0.02D;
            double vz = this.random.nextGaussian() * 0.02D;
            this.level().addParticle(net.minecraft.core.particles.ParticleTypes.LAVA,
                    this.getX() + this.random.nextFloat() * this.getBbWidth() - this.getBbWidth(),
                    this.getY() + 0.5D + this.random.nextFloat() * this.getBbHeight(),
                    this.getZ() + this.random.nextFloat() * this.getBbWidth() - this.getBbWidth(),
                    vx, vy, vz);
        }
    }

    @Override
    public void tick() {
        super.tick();
        this.calculateEntityAnimation(false);

        if (!this.level().isClientSide) {
            if (isVanishing()) {
                this.getNavigation().stop();
                this.setDeltaMovement(net.minecraft.world.phys.Vec3.ZERO);
            }

            tickCountdownTimers();

            tickGrazing();

            tickFairyWingFlapFlag();

            tickPassiveRegen();

            tickTransformationTimers();

            tickZebraDancing();

            tickWingFlapSounds();

            if ((getSpecies() == Species.UNICORN || getSpecies() == Species.FAIRY_HORSE) && unicornChargeTicks > 0) {
                unicornChargeTick();
            }

            tickNightmareBehavior();

            tickUndeadDecayProgress();

            if ((getSpecies() == Species.UNICORN || getSpecies() == Species.FAIRY_HORSE)
                    && this.isVehicle() && !this.onGround()) {
                unicornJumpTrail();
            }
        } else {
            tickClientSideParticles();
        }

        if (this.level().isClientSide || !this.isTamed() || this.isBaby() || isSterileHybrid()) {
            return;
        }
        tryBreed();
    }
    private void tryBreed() {
        if (!this.isInLove()) {
            gestationProgress = 0;
            return;
        }

        List<MoCHorseEntity> mates = this.level().getEntitiesOfClass(
                MoCHorseEntity.class, this.getBoundingBox().inflate(4.0D, 2.0D, 4.0D),
                other -> other != this && other.isTamed() && !other.isBaby()
                        && !other.isSterileHybrid() && other.isInLove()
                        && MoCHorseGenetics.canBreed(this.getSpecies(), this.getCoat(), other.getSpecies(), other.getCoat()));

        if (mates.isEmpty()) {
            gestationProgress = 0;
            return;
        }

        gestationProgress++;
        if (gestationProgress < GESTATION_TICKS) {
            return;
        }

        MoCHorseEntity mate = mates.get(0);
        if (this.getUUID().compareTo(mate.getUUID()) > 0) {
            return;
        }

        // No third horse within 8 blocks horizontally — checked last, right
        // before actually spawning, so gestation progress isn't lost while
        // waiting for the area to clear; it just keeps retrying each tick.
        boolean crowded = !this.level().getEntitiesOfClass(
                MoCHorseEntity.class, this.getBoundingBox().inflate(8.0D, 4.0D, 8.0D),
                other -> other != this && other != mate).isEmpty();
        if (crowded) {
            return;
        }

        gestationProgress = 0;

        boolean isFairyBreeding = (this.getSpecies() == Species.UNICORN && mate.getSpecies() == Species.PEGASUS)
                || (this.getSpecies() == Species.PEGASUS && mate.getSpecies() == Species.UNICORN)
                || (this.getSpecies() == Species.FAIRY_HORSE && mate.getSpecies() == Species.FAIRY_HORSE);

        Species foalSpecies = MoCHorseGenetics.resolveOffspringSpecies(this.getSpecies(), mate.getSpecies());
        Coat foalCoat = (foalSpecies == Species.HORSE && this.getSpecies() == Species.HORSE && mate.getSpecies() == Species.HORSE)
                ? MoCHorseGenetics.resolveOffspringCoat(this.getCoat(), mate.getCoat())
                : Coat.WHITE;

        MoCHorseGenetics.FairyColor foalFairyColor = MoCHorseGenetics.FairyColor.WHITE;
        if (this.getSpecies() == Species.FAIRY_HORSE && mate.getSpecies() == Species.FAIRY_HORSE) {
            if (this.getFairyColor() == mate.getFairyColor()) {
                foalFairyColor = this.getFairyColor();
            } else {
                foalSpecies = Species.HORSE_BUG; // colores distintos -> easter egg
            }
        }

        MoCHorseEntity foal = ModEntities.MOC_HORSE.get().create(this.level());
        if (foal == null) return;

        foal.moveTo(this.getX(), this.getY(), this.getZ(), 0.0F, 0.0F);
        foal.setSpecies(foalSpecies);
        foal.setCoat(foalCoat);
        if (foalSpecies == Species.FAIRY_HORSE) {
            foal.setFairyColor(foalFairyColor);
        }
        foal.setHealth((float) foal.getMaxHealth());
        foal.setAge(-24000);
        if (this.getOwnerUUID() != null) {
            foal.setOwnerUUID(this.getOwnerUUID());
            foal.setTamed(true);
            com.example.neomocreatures.util.NamingHelper.promptRename(foal, this.getOwnerUUID());
        }
        this.level().addFreshEntity(foal);

        if (isFairyBreeding) {
            this.startVanish();
            mate.startVanish();
        } else {
            this.resetLove();
            mate.resetLove();
        }
    }

    public void dropSaddleAndArmor() {
        ItemStack saddle = this.inventory.getItem(0);
        if (!saddle.isEmpty()) {
            this.spawnAtLocation(saddle);
            this.inventory.setItem(0, ItemStack.EMPTY);
        }
        ItemStack armor = this.getItemBySlot(EquipmentSlot.BODY);
        if (!armor.isEmpty()) {
            this.spawnAtLocation(armor);
            this.setItemSlot(EquipmentSlot.BODY, ItemStack.EMPTY);
        }
    }

    public void dropChestAndContents() {
        if (hasChest()) {
            this.spawnAtLocation(Items.CHEST);
            for (int slot = 0; slot < chestInventory.getContainerSize(); slot++) {
                ItemStack stack = chestInventory.getItem(slot);
                if (!stack.isEmpty()) {
                    this.spawnAtLocation(stack);
                }
            }
            chestInventory.clearContent();
            setHasChest(false);
        }
    }

    private void useEssence(Player player, ItemStack stack) {
        openMouth();
        this.playSound(ModSounds.HORSE_DRINKING.get(), 1.0F, 1.0F);
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        if (!player.getInventory().add(new ItemStack(Items.GLASS_BOTTLE))) {
            player.drop(new ItemStack(Items.GLASS_BOTTLE), false);
        }
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHitByPlayer) {
        ItemStack armorBeforeSuper = this.getItemBySlot(EquipmentSlot.BODY).copy();
        super.dropCustomDeathLoot(level, damageSource, recentlyHitByPlayer);
        if (!armorBeforeSuper.isEmpty() && !this.getItemBySlot(EquipmentSlot.BODY).isEmpty()) {
            this.spawnAtLocation(this.getItemBySlot(EquipmentSlot.BODY));
            this.setItemSlot(EquipmentSlot.BODY, ItemStack.EMPTY);
        }
        dropChestAndSaddleContents();

        dropCombatLoot(recentlyHitByPlayer);

        // Requested: a tamed undead horse spawns maggots on death, same as the undead wyvern.
        if (this.isTamed() && this.isUndead()) {
            spawnMaggotsOnDeath(level);
        }
    }

    /** Spawns 1-3 maggots at the death location. */
    private void spawnMaggotsOnDeath(ServerLevel level) {
        int count = 1 + this.random.nextInt(3);
        for (int i = 0; i < count; i++) {
            com.example.neomocreatures.entity.MoCMaggotEntity maggot =
                    com.example.neomocreatures.init.ModEntities.MOC_MAGGOT.get().create(level);
            if (maggot != null) {
                maggot.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
                level.addFreshEntity(maggot);
            }
        }
    }

    /** Drops the chest (if it has one) with all its contents, and the saddle. */
    private void dropChestAndSaddleContents() {
        if (hasChest()) {
            this.spawnAtLocation(Items.CHEST);
            for (int slot = 0; slot < chestInventory.getContainerSize(); slot++) {
                ItemStack stack = chestInventory.getItem(slot);
                if (!stack.isEmpty()) {
                    this.spawnAtLocation(stack);
                }
            }
        }

        ItemStack saddle = this.inventory.getItem(0);
        if (!saddle.isEmpty()) {
            this.spawnAtLocation(saddle);
            this.inventory.setItem(0, ItemStack.EMPTY);
        }
    }

    /**
    * Loot that only drops if it was killed by a player (or a wolf): base
    * materials (leather/bone/rotten flesh depending on species and
    * stage) plus special drops by chance (unicorn horn, ghast tear,
    * undead/fire/darkness hearts).
    */
    private void dropCombatLoot(boolean recentlyHitByPlayer) {
        boolean killedByPlayerOrWolf = recentlyHitByPlayer || this.getLastHurtByMob() instanceof Wolf;
        if (!killedByPlayerOrWolf) {
            return;
        }

        LivingEntity killer = this.getLastHurtByMob();
        int lootingLevel = 0;
        if (killer != null) {
            Holder<Enchantment> looting = killer.level().registryAccess()
                    .lookupOrThrow(Registries.ENCHANTMENT)
                    .getOrThrow(Enchantments.LOOTING);
            lootingLevel = EnchantmentHelper.getEnchantmentLevel(looting, killer);
        }

        boolean isGhostSpecies = getSpecies() == Species.GHOST || getSpecies() == Species.GHOST_WINGED;
        if (!isUndead() && !isGhostSpecies) {
            int leatherCount = this.random.nextInt(3 + lootingLevel);
            for (int i = 0; i < leatherCount; i++) {
                this.spawnAtLocation(Items.LEATHER);
            }
        }

        if (isSkeletonStage()) {
            int boneCount = this.random.nextInt(3 + lootingLevel);
            for (int i = 0; i < boneCount; i++) {
                this.spawnAtLocation(Items.BONE);
            }
        } else if (isUndead()) {
            int fleshCount = this.random.nextInt(3 + lootingLevel);
            for (int i = 0; i < fleshCount; i++) {
                this.spawnAtLocation(Items.ROTTEN_FLESH);
            }
        }

        if (getSpecies() == Species.UNICORN || getSpecies() == Species.FAIRY_HORSE) {
            dropChanceItems(ModItems.UNICORN_HORN.get(), 0.25F);
        }
        if (isGhostSpecies) {
            dropChanceItems(Items.GHAST_TEAR, 0.25F);
        }
        if (isUndead()) {
            dropChanceItems(ModItems.HEART_OF_UNDEAD.get(), 0.25F);
        }
        if (getSpecies() == Species.NIGHTMARE) {
            dropChanceItems(ModItems.HEART_OF_FIRE.get(), 0.25F);
        }
        if (getSpecies() == Species.BATHORSE) {
            dropChanceItems(ModItems.HEART_OF_DARKNESS.get(), 0.25F);
        }
    }

    private void dropChanceItems(net.minecraft.world.item.Item item, float chancePerRoll) {
        for (int i = 0; i < 2; i++) {
            if (this.random.nextFloat() < chancePerRoll) {
                this.spawnAtLocation(item);
            }
        }
    }

    @Override
    protected int getBaseExperienceReward() {
        return 1 + this.random.nextInt(3);
    }

    @Override
    public boolean shouldDropExperience() {
        return super.shouldDropExperience() || this.getLastHurtByMob() instanceof Wolf;
    }

    @Override
    public void setAge(int age) {
        super.setAge(age);
        this.entityData.set(DATA_SYNCED_AGE, age);
    }

    @Override
    public boolean shouldShowName() {
        if (!this.hasCustomName()) {
            return false;
        }
        if (this.level().isClientSide) {
            net.minecraft.client.player.LocalPlayer player = net.minecraft.client.Minecraft.getInstance().player;
            return player != null && this.distanceToSqr(player) < 64.0D; // 8 bloques
        }
        return super.shouldShowName();
    }

    @Override
    public void lavaHurt() {
        if (getSpecies() == Species.NIGHTMARE) {
            return;
        }
        super.lavaHurt();
    }

     /**
     * Movement while actually flying/gliding (in the air, mounted or
     * not): friction depending on species, and two falling modes —
     * floating in water/lava with no gravity pull, or falling with
     * normal flyer gravity.
     */
    private void applyFlightMovement(net.minecraft.world.phys.Vec3 travelVector) {
        float friction = switch (getSpecies()) {
            case PEGASUS -> isUndead() ? FLYER_FRICTION : PEGASUS_FRICTION;
            case DARK_PEGASUS -> DARK_PEGASUS_FRICTION;
            case FAIRY_HORSE -> PEGASUS_FRICTION;
            default -> FLYER_FRICTION;
        };

        boolean floatingInWater = this.isInWater() && !isSkeletonStage();
        boolean floatingInLava = this.isInLava() && !isSkeletonStage();

        this.move(net.minecraft.world.entity.MoverType.SELF, this.getDeltaMovement());
        this.moveRelative(friction / 10F, travelVector);

        if (floatingInWater || floatingInLava) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(friction, FLYER_FALL_SPEED, friction));
            double fluidHeight = floatingInLava
                    ? this.getFluidHeight(net.minecraft.tags.FluidTags.LAVA)
                    : this.getFluidHeight(net.minecraft.tags.FluidTags.WATER);
            if (this.getDeltaMovement().y < 0 && !this.onGround() && fluidHeight >= 0.5) {
                this.setDeltaMovement(this.getDeltaMovement().multiply(1, 0.0, 1));
            }
            if (this.getControllingPassenger() instanceof net.minecraft.world.entity.LivingEntity controllingRider
                    && controllingRider.isShiftKeyDown()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0, -0.08, 0));
            }
        } else {
            this.setDeltaMovement(this.getDeltaMovement()
                    .multiply(friction, FLYER_FALL_SPEED, friction)
                    .subtract(0.0D, FLYER_GRAVITY_PULL, 0.0D));
        }
    }

    /**
     * Floating while standing in water (any species) or lava (nightmare
     * only) while mounted and not a skeleton: prevents it from sinking
     * suddenly and lets a sneaking rider push it down on purpose.
     */
    private void applyGroundedFluidBuoyancy() {
        if (this.isInWater() && this.isVehicle() && !isSkeletonStage()) {
            double submergedFraction = this.getFluidHeight(net.minecraft.tags.FluidTags.WATER);
            if (this.getDeltaMovement().y < 0 && !this.onGround() && submergedFraction >= 0.5) {
                this.setDeltaMovement(this.getDeltaMovement().multiply(1, 0.0, 1));
            }
            if (this.getControllingPassenger() instanceof net.minecraft.world.entity.LivingEntity controllingRider
                    && controllingRider.isShiftKeyDown()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0, -0.08, 0));
            }
        }

        if (this.isInLava() && this.isVehicle() && !isSkeletonStage()) {
            double submergedFraction = this.getFluidHeight(net.minecraft.tags.FluidTags.LAVA);
            if (this.getDeltaMovement().y < 0 && !this.onGround() && submergedFraction >= 0.5) {
                this.setDeltaMovement(this.getDeltaMovement().multiply(1, 0.0, 1));
            }
            if (this.getControllingPassenger() instanceof net.minecraft.world.entity.LivingEntity controllingRider
                    && controllingRider.isShiftKeyDown()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0, -0.08, 0));
            }
        }
    }

    @Override
    public void travel(net.minecraft.world.phys.Vec3 travelVector) {
        boolean isBatFlyer = (getSpecies() == Species.BATHORSE || getSpecies() == Species.PEGASUS
                || getSpecies() == Species.DARK_PEGASUS || getSpecies() == Species.FAIRY_HORSE || getSpecies() == Species.GHOST_WINGED)
                && this.isTamed() && !isTransforming();
        boolean canControlFlight = isBatFlyer && this.isVehicle();

        boolean flyingMount = isFlyingNow();

        boolean ascend = this.ascendHeld;
        boolean descend = this.descendHeld;
        if (this.level().isClientSide
                && this.getControllingPassenger() == net.minecraft.client.Minecraft.getInstance().player) {
            ascend = net.minecraft.client.Minecraft.getInstance().options.keyJump.isDown();
            descend = com.example.neomocreatures.client.ModKeyMappings.DESCEND.isDown();
        }

        if (!this.isVehicle() && this.getGrazeTicks() > 0) {
            this.getNavigation().stop();
            super.travel(net.minecraft.world.phys.Vec3.ZERO);
            return;
        }

        if (canControlFlight) {
            double thrust = FLYER_THRUST + switch (getSpecies()) {
                case PEGASUS -> isUndead() ? 0.0D : PEGASUS_THRUST_BONUS;
                case DARK_PEGASUS -> DARK_PEGASUS_THRUST_BONUS;
                case FAIRY_HORSE -> PEGASUS_THRUST_BONUS;
                default -> 0.0D;
            };
            if (ascend) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, thrust, 0.0D));
            } else if (descend) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -thrust, 0.0D));
            }
        }
        this.setNoGravity(flyingMount);

        if (flyingMount) {
            applyFlightMovement(travelVector);
            return;
        }

        applyGroundedFluidBuoyancy();

        if ((getSpecies() == Species.UNICORN || getSpecies() == Species.GHOST) && this.getDeltaMovement().y < -0.1D && !this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.6D, 1.0D));
        }

        super.travel(travelVector);
    }

    @Override
    public void onPlayerJump(int jumpPower) {
        if (this.isInWater() || this.isInLava()) {
            if (jumpPower < 0) {
                jumpPower = 0;
            }
            double jumpStrength = this.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.JUMP_STRENGTH)
                    * (jumpPower / 100.0);
            this.setDeltaMovement(this.getDeltaMovement().add(0, jumpStrength, 0));
            this.hasImpulse = true;
        } else {
            super.onPlayerJump(jumpPower);
        }
    }
    
    @Override
    protected void positionRider(Entity passenger, MoveFunction moveFunction) {
        if (!this.hasPassenger(passenger)) {
            return;
        }

        float yaw = this.getYRot() * ((float) Math.PI / 180F);

        double x = this.getX() - Math.sin(yaw) * RIDER_FORWARD;
        double z = this.getZ() + Math.cos(yaw) * RIDER_FORWARD;
        double y = this.getY() + RIDER_HEIGHT;

        if (getSpecies() == Species.GHOST || getSpecies() == Species.GHOST_WINGED) {
            y += 0.3D;
        }

        moveFunction.accept(passenger, x, y, z);
    }

    private double getSafeFallBlocks() {
        return switch (getSpecies()) {
            case ZORSE -> 4.2D;
            case NIGHTMARE, HORSE_BUG -> 4.2D;
            case HORSE -> coatTier(getCoat()) == 4 ? 4.2D : 3.2D;
            default -> 3.2D;
        };
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, net.minecraft.world.damagesource.DamageSource source) {
        if (getSpecies() == Species.BATHORSE || getSpecies() == Species.UNICORN
                || getSpecies() == Species.PEGASUS || getSpecies() == Species.DARK_PEGASUS || getSpecies() == Species.FAIRY_HORSE
                || getSpecies() == Species.GHOST || getSpecies() == Species.GHOST_WINGED) {
            return false;
        }
        double safe = getSafeFallBlocks();
        if (fallDistance <= safe) {
            return false;
        }
        return super.causeFallDamage(fallDistance - (float) safe, multiplier, source);
    }

    @Override
    public boolean canJump() {
        if (getSpecies() == Species.BATHORSE || getSpecies() == Species.PEGASUS || getSpecies() == Species.DARK_PEGASUS || getSpecies() == Species.FAIRY_HORSE
                || getSpecies() == Species.GHOST_WINGED) {
            return false;
        }
        return super.canJump();
    }

    @Override
    public void setItemSlot(EquipmentSlot slot, ItemStack stack) {
        if (slot == EquipmentSlot.BODY && !this.level().isClientSide) {
            boolean hadArmor = !this.getItemBySlot(EquipmentSlot.BODY).isEmpty();
            boolean willHaveArmor = !stack.isEmpty();
            if (!hadArmor && willHaveArmor) {
                this.playSound(ModSounds.HORSE_ARMOR_PUT.get(), 1.0F, 1.0F);
            } else if (hadArmor && !willHaveArmor) {
                this.playSound(ModSounds.HORSE_ARMOR_OFF.get(), 1.0F, 1.0F);
            }
        }
        super.setItemSlot(slot, stack);
    }

    @Override
    public boolean canUseSlot(EquipmentSlot slot) {
        if (slot == EquipmentSlot.BODY) {
            return wantsHorseArmor();
        }
        return super.canUseSlot(slot);
    }
}