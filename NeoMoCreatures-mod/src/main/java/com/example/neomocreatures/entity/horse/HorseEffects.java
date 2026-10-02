package com.example.neomocreatures.entity.horse;

import com.example.neomocreatures.breeding.MoCHorseGenetics.Species;
import com.example.neomocreatures.entity.MoCHorseEntity;
import com.example.neomocreatures.init.ModParticles;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

/**
 * World effects of a horse: client particles (dancing notes, vanish spiral, undead decay,
 * nightmare lava), the unicorn/fairy star trail and the nightmare's fire trail.
 */
public final class HorseEffects {

    private static final int VANISH_SPIRAL_POINTS = 8;

    private final MoCHorseEntity horse;

    public HorseEffects(MoCHorseEntity horse) {
        this.horse = horse;
    }

    /** Client side, every tick. */
    public void tickClientParticles() {
        Level level = this.horse.level();
        RandomSource random = this.horse.getRandom();
        float width = this.horse.getBbWidth();
        float height = this.horse.getBbHeight();

        if (this.horse.isDancing() && random.nextInt(4) == 0) {
            double dx = random.nextGaussian() * 0.5D;
            double dy = random.nextGaussian() * -0.1D;
            double dz = random.nextGaussian() * 0.02D;
            level.addParticle(ParticleTypes.NOTE,
                    this.horse.getX() + random.nextFloat() * width * 2.0F - width,
                    this.horse.getY() + 0.5D + random.nextFloat() * height,
                    this.horse.getZ() + random.nextFloat() * width * 2.0F - width,
                    dx, dy, dz);
        }

        if (this.horse.isVanishing()) {
            this.spawnVanishSpiral(level, width);
        }

        if (this.horse.isUndead() && !this.horse.isSkeletonStage() && !this.horse.isUndeadLocked()
                && random.nextInt(8) == 0) {
            level.addParticle(ModParticles.UNDEAD_DECAY.get(),
                    this.horse.getX() + (random.nextDouble() - 0.5) * width,
                    this.horse.getY() + random.nextDouble() * height,
                    this.horse.getZ() + (random.nextDouble() - 0.5) * width,
                    0.0D, 0.0D, 0.0D);
        }

        if (this.horse.getSpecies() == Species.NIGHTMARE && random.nextInt(50) == 0) {
            double vx = random.nextGaussian() * 0.02D;
            double vy = random.nextGaussian() * 0.02D;
            double vz = random.nextGaussian() * 0.02D;
            level.addParticle(ParticleTypes.LAVA,
                    this.horse.getX() + random.nextFloat() * width - width,
                    this.horse.getY() + 0.5D + random.nextFloat() * height,
                    this.horse.getZ() + random.nextFloat() * width - width,
                    vx, vy, vz);
        }
    }

    /** Ring of particles that shrinks and spins faster as the vanish progresses. */
    private void spawnVanishSpiral(Level level, float width) {
        float progress = this.horse.getVanishTicks() / (float) this.horse.getVanishDurationTicks();
        double maxRadius = width * 1.3D;
        double radius = maxRadius * Math.pow(1.0D - progress, 2.0D);
        double spinSpeed = 0.5D + progress * 2.5D;

        double baseAngle = this.horse.getVanishTicks() * spinSpeed;
        for (int i = 0; i < VANISH_SPIRAL_POINTS; i++) {
            double angle = baseAngle + (2 * Math.PI * i / VANISH_SPIRAL_POINTS);
            double px = this.horse.getX() + Math.cos(angle) * radius;
            double pz = this.horse.getZ() + Math.sin(angle) * radius;
            double py = this.horse.getY() + 0.1D;
            level.addParticle(ModParticles.VANISH_FX.get(), px, py, pz, 0.0D, 0.01D, 0.0D);
        }
    }

    /** Server side: star particles behind a jumping unicorn or fairy horse. */
    public void spawnJumpTrail() {
        if (!(this.horse.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        SimpleParticleType particle =
                this.horse.getSpecies() == Species.FAIRY_HORSE
                        ? ModParticles.starFxForFairyColor(this.horse.getFairyColor())
                        : ModParticles.STAR_FX.get();
        serverLevel.sendParticles(particle,
                this.horse.getX(), this.horse.getY() + 0.2D, this.horse.getZ(),
                1, 0.3D, 0.1D, 0.3D, 0.01D);
    }

    /** Server side: a fleeing nightmare leaves fire behind it. */
    public void placeFireTrail() {
        BlockPos pos = new BlockPos(
                Mth.floor(this.horse.getX()),
                Mth.floor(this.horse.getBoundingBox().minY),
                Mth.floor(this.horse.getZ())
        ).offset(-1, 0, -1);

        if (this.horse.level().getBlockState(pos).isAir()) {
            this.horse.level().setBlockAndUpdate(pos, Blocks.FIRE.defaultBlockState());
        }
    }
}