package com.example.neomocreatures.client;

import com.example.neomocreatures.client.particle.StarParticle;
import com.example.neomocreatures.client.particle.UndeadDecayParticle;
import com.example.neomocreatures.init.ModParticles;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

public class ModClientParticles {

    @SubscribeEvent
    public static void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.UNDEAD_DECAY.get(), UndeadDecayParticle.Provider::new);
        event.registerSpriteSet(ModParticles.STAR_FX.get(), StarParticle.Provider::new);

        event.registerSpriteSet(ModParticles.STAR_FX_WHITE.get(),
                sprites -> new StarParticle.Provider(sprites, 1.0F, 1.0F, 1.0F));
        event.registerSpriteSet(ModParticles.STAR_FX_ORANGE.get(),
                sprites -> new StarParticle.Provider(sprites, 1.0F, 0.6F, 0.1F));
        event.registerSpriteSet(ModParticles.STAR_FX_YELLOW.get(),
                sprites -> new StarParticle.Provider(sprites, 1.0F, 0.95F, 0.3F));
        event.registerSpriteSet(ModParticles.STAR_FX_LIGHTGREEN.get(),
                sprites -> new StarParticle.Provider(sprites, 0.6F, 1.0F, 0.4F));
        event.registerSpriteSet(ModParticles.STAR_FX_GREEN.get(),
                sprites -> new StarParticle.Provider(sprites, 0.25F, 0.65F, 0.2F));
        event.registerSpriteSet(ModParticles.STAR_FX_CYAN.get(),
                sprites -> new StarParticle.Provider(sprites, 0.3F, 0.9F, 0.9F));
        event.registerSpriteSet(ModParticles.STAR_FX_BLUE.get(),
                sprites -> new StarParticle.Provider(sprites, 0.3F, 0.55F, 1.0F));
        event.registerSpriteSet(ModParticles.STAR_FX_DARKBLUE.get(),
                sprites -> new StarParticle.Provider(sprites, 0.15F, 0.2F, 0.75F));
        event.registerSpriteSet(ModParticles.STAR_FX_PURPLE.get(),
                sprites -> new StarParticle.Provider(sprites, 0.65F, 0.3F, 0.9F));
        event.registerSpriteSet(ModParticles.STAR_FX_PINK.get(),
                sprites -> new StarParticle.Provider(sprites, 1.0F, 0.6F, 0.8F));
        event.registerSpriteSet(ModParticles.STAR_FX_RED.get(),
                sprites -> new StarParticle.Provider(sprites, 1.0F, 0.25F, 0.25F));
        event.registerSpriteSet(ModParticles.STAR_FX_BLACK.get(),
                sprites -> new StarParticle.Provider(sprites, 0.35F, 0.35F, 0.35F));
        event.registerSpriteSet(ModParticles.VANISH_FX.get(), StarParticle.Provider::new);
    }
}