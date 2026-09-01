package com.example.neomocreatures;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Config común del mod. Vacío por ahora — agrega aquí valores reales
 * cuando necesites que algo sea ajustable sin recompilar
 * (ej. spawn rates, daño de armaduras, si cría con caballos vanilla, etc).
 *
 * Ejemplo de cómo se vería un valor real, para no tener que buscar la sintaxis:
 *
 * public static final ModConfigSpec.BooleanValue ALLOW_VANILLA_BREEDING = BUILDER
 *         .comment("Si los caballos de Mo'Creatures pueden criar con caballos/burros vanilla")
 *         .define("allowVanillaBreeding", false);
 */
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    static final ModConfigSpec SPEC = BUILDER.build();
}