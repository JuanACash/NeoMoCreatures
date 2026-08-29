package com.example.examplemod.init;

import java.util.List;
import java.util.Map;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModArmorMaterials {

    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS =
            DeferredRegister.create(
                    Registries.ARMOR_MATERIAL,
                    com.example.examplemod.ExampleMod.MODID
            );

    private static final Map<ArmorItem.Type, Integer> DIAMOND_DEFENSE = Map.of(
            ArmorItem.Type.BOOTS, 3,
            ArmorItem.Type.LEGGINGS, 6,
            ArmorItem.Type.CHESTPLATE, 8,
            ArmorItem.Type.HELMET, 3
    );

    private static final Map<ArmorItem.Type, Integer> GOLD_DEFENSE = Map.of(
            ArmorItem.Type.BOOTS, 2,
            ArmorItem.Type.LEGGINGS, 3,
            ArmorItem.Type.CHESTPLATE, 5,
            ArmorItem.Type.HELMET, 2
    );

    private static final Map<ArmorItem.Type, Integer> IRON_DEFENSE = Map.of(
            ArmorItem.Type.BOOTS, 2,
            ArmorItem.Type.LEGGINGS, 5,
            ArmorItem.Type.CHESTPLATE, 6,
            ArmorItem.Type.HELMET, 2
    );

    private static ArmorMaterial.Layer layer(String name) {
        return new ArmorMaterial.Layer(
                ResourceLocation.fromNamespaceAndPath(
                        com.example.examplemod.ExampleMod.MODID,
                        name
                )
        );
    }

    private static DeferredHolder<ArmorMaterial, ArmorMaterial> register(
            String name,
            int durabilityMultiplier,
            Map<ArmorItem.Type, Integer> defense,
            int enchantmentValue,
            float toughness,
            float knockbackResistance,
            TagKey<Item> repairIngredient,
            String textureLayerName) {

        return ARMOR_MATERIALS.register(name, () -> new ArmorMaterial(
                defense,
                enchantmentValue,
                SoundEvents.ARMOR_EQUIP_IRON,
                () -> Ingredient.of(repairIngredient),
                List.of(layer(textureLayerName)),
                toughness,
                knockbackResistance
        ));
    }

    // ==== Scorpion (5 biomas) ====
    // Stats de diamante, enchantability 10

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> SCORP_CAVE =
            register(
                    "scorpc",
                    15,
                    DIAMOND_DEFENSE,
                    10,
                    2.0F,
                    0.0F,
                    ModTags.REPAIRS_SCORP_CAVE,
                    "scorpc"
            );

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> SCORP_DIRT =
            register(
                    "scorpd",
                    15,
                    DIAMOND_DEFENSE,
                    10,
                    2.0F,
                    0.0F,
                    ModTags.REPAIRS_SCORP_DIRT,
                    "scorpd"
            );

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> SCORP_NETHER =
            register(
                    "scorpn",
                    15,
                    DIAMOND_DEFENSE,
                    10,
                    2.0F,
                    0.0F,
                    ModTags.REPAIRS_SCORP_NETHER,
                    "scorpn"
            );

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> SCORP_FROST =
            register(
                    "scorpf",
                    15,
                    DIAMOND_DEFENSE,
                    10,
                    2.0F,
                    0.0F,
                    ModTags.REPAIRS_SCORP_FROST,
                    "scorpf"
            );

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> SCORP_UNDEAD =
            register(
                    "scorpu",
                    15,
                    DIAMOND_DEFENSE,
                    10,
                    2.0F,
                    0.0F,
                    ModTags.REPAIRS_SCORP_UNDEAD,
                    "scorpu"
            );

    // ==== Silver (Ancient Silver) ====
    // Clon de oro:
    // defensa de oro, enchantability 25, toughness 0

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> SILVER =
            register(
                    "silver",
                    7,
                    GOLD_DEFENSE,
                    25,
                    0.0F,
                    0.0F,
                    ModTags.REPAIRS_SILVER,
                    "silver"
            );

    // ==== Fur ====
    // Clon de hierro

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> FUR =
            register(
                    "fur",
                    15,
                    IRON_DEFENSE,
                    9,
                    0.0F,
                    0.0F,
                    ModTags.REPAIRS_FUR,
                    "fur"
            );

    // ==== Reptile ====
    // Clon de hierro

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> REPTILE =
            register(
                    "croc",
                    15,
                    IRON_DEFENSE,
                    9,
                    0.0F,
                    0.0F,
                    ModTags.REPAIRS_REPTILE,
                    "croc"
            );

    // ==== Hide ====
    // Clon de hierro

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> HIDE =
            register(
                    "hide",
                    15,
                    IRON_DEFENSE,
                    9,
                    0.0F,
                    0.0F,
                    ModTags.REPAIRS_HIDE,
                    "hide"
            );
}