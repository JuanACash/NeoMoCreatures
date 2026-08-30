Comandos /summon — Mo' Creatures Horses

Base: /summon examplemod:moc_horse ~ ~ ~ {...} Agrega ,Tame:1b dentro del NBT si quieres que aparezca domado.

Horse — Tier 1 (coats)

/summon examplemod:moc_horse ~ ~ ~ {MoCSpecies:"HORSE",MoCCoat:"WHITE"}

/summon examplemod:moc_horse ~ ~ ~ {MoCSpecies:"HORSE",MoCCoat:"CREAMY"}

/summon examplemod:moc_horse ~ ~ ~ {MoCSpecies:"HORSE",MoCCoat:"BROWN"}

/summon examplemod:moc_horse ~ ~ ~ {MoCSpecies:"HORSE",MoCCoat:"DARKBROWN"}

/summon examplemod:moc_horse ~ ~ ~ {MoCSpecies:"HORSE",MoCCoat:"BLACK"}

Horse — Tier 2 (coats)

/summon examplemod:moc_horse ~ ~ ~ {MoCSpecies:"HORSE",MoCCoat:"BRIGHTCREAMY"}

/summon examplemod:moc_horse ~ ~ ~ {MoCSpecies:"HORSE",MoCCoat:"SPECKLED"}

/summon examplemod:moc_horse ~ ~ ~ {MoCSpecies:"HORSE",MoCCoat:"PALEBROWN"}

/summon examplemod:moc_horse ~ ~ ~ {MoCSpecies:"HORSE",MoCCoat:"GREY"}

Horse — Tier 3 (coats)

/summon examplemod:moc_horse ~ ~ ~ {MoCSpecies:"HORSE",MoCCoat:"PINTO"}

/summon examplemod:moc_horse ~ ~ ~ {MoCSpecies:"HORSE",MoCCoat:"BRIGHTPINTO"}

/summon examplemod:moc_horse ~ ~ ~ {MoCSpecies:"HORSE",MoCCoat:"PALESPECKLES"}

Horse — Tier 4 (coats)

/summon examplemod:moc_horse ~ ~ ~ {MoCSpecies:"HORSE",MoCCoat:"SPOTTED"}

/summon examplemod:moc_horse ~ ~ ~ {MoCSpecies:"HORSE",MoCCoat:"COW"}

Otras especies

/summon examplemod:moc_horse ~ ~ ~ {MoCSpecies:"DONKEY"}

/summon examplemod:moc_horse ~ ~ ~ {MoCSpecies:"MULE"}

/summon examplemod:moc_horse ~ ~ ~ {MoCSpecies:"ZONKY"}

/summon examplemod:moc_horse ~ ~ ~ {MoCSpecies:"ZEBRA"}

/summon examplemod:moc_horse ~ ~ ~ {MoCSpecies:"ZORSE"}

/summon examplemod:moc_horse ~ ~ ~ {MoCSpecies:"BATHORSE"}

/summon examplemod:moc_horse ~ ~ ~ {MoCSpecies:"NIGHTMARE"}

/summon examplemod:moc_horse ~ ~ ~ {MoCSpecies:"UNICORN"}

/summon examplemod:moc_horse ~ ~ ~ {MoCSpecies:"PEGASUS"}

/summon examplemod:moc_horse ~ ~ ~ {MoCSpecies:"DARK_PEGASUS"}


/summon examplemod:moc_horse ~ ~ ~ {MoCSpecies:"GHOST"}
/summon examplemod:moc_horse ~ ~ ~ {MoCSpecies:"GHOST_WINGED"}

/summon examplemod:moc_horse ~ ~ ~ {MoCSpecies:"HORSE_BUG"}

Fairy Horse

/summon examplemod:moc_horse ~ ~ ~ {MoCSpecies:"FAIRY_HORSE",MoCFairyColor:"WHITE"}

/summon examplemod:moc_horse ~ ~ ~ {MoCSpecies:"FAIRY_HORSE",MoCFairyColor:"ORANGE"}

/summon examplemod:moc_horse ~ ~ ~ {MoCSpecies:"FAIRY_HORSE",MoCFairyColor:"YELLOW"}

/summon examplemod:moc_horse ~ ~ ~ {MoCSpecies:"FAIRY_HORSE",MoCFairyColor:"LIGHTGREEN"}

/summon examplemod:moc_horse ~ ~ ~ {MoCSpecies:"FAIRY_HORSE",MoCFairyColor:"GREEN"}

/summon examplemod:moc_horse ~ ~ ~ {MoCSpecies:"FAIRY_HORSE",MoCFairyColor:"CYAN"}

/summon examplemod:moc_horse ~ ~ ~ {MoCSpecies:"FAIRY_HORSE",MoCFairyColor:"BLUE"}

/summon examplemod:moc_horse ~ ~ ~ {MoCSpecies:"FAIRY_HORSE",MoCFairyColor:"DARKBLUE"}

/summon examplemod:moc_horse ~ ~ ~ {MoCSpecies:"FAIRY_HORSE",MoCFairyColor:"PURPLE"}

/summon examplemod:moc_horse ~ ~ ~ {MoCSpecies:"FAIRY_HORSE",MoCFairyColor:"PINK"}

/summon examplemod:moc_horse ~ ~ ~ {MoCSpecies:"FAIRY_HORSE",MoCFairyColor:"RED"}

/summon examplemod:moc_horse ~ ~ ~ {MoCSpecies:"FAIRY_HORSE",MoCFairyColor:"BLACK"}

Undead / Skeleton (Horse, Zorse, Unicorn)

MoCUndeadStage valores: 1=stage 0, 2=stage 1, 3=stage 2, 4=stage 3, 5=skeleton.

/summon examplemod:moc_horse ~ ~ ~ {MoCSpecies:"HORSE",MoCUndeadStage:1}

/summon examplemod:moc_horse ~ ~ ~ {MoCSpecies:"HORSE",MoCUndeadStage:5}

/summon examplemod:moc_horse ~ ~ ~ {MoCSpecies:"ZORSE",MoCUndeadStage:1}

/summon examplemod:moc_horse ~ ~ ~ {MoCSpecies:"ZORSE",MoCUndeadStage:5}

/summon examplemod:moc_horse ~ ~ ~ {MoCSpecies:"UNICORN",MoCUndeadStage:1}

/summon examplemod:moc_horse ~ ~ ~ {MoCSpecies:"UNICORN",MoCUndeadStage:5}

Undead consolidado (Bathorse / Dark Pegasus → Pegasus undead)

Estos en juego siempre terminan como PEGASUS + undead activo, así que para invocarlos directamente:

/summon examplemod:moc_horse ~ ~ ~ {MoCSpecies:"PEGASUS",MoCUndeadStage:1}

/summon examplemod:moc_horse ~ ~ ~ {MoCSpecies:"PEGASUS",MoCUndeadStage:5}

Con dueño (tamed + owner)

Si quieres que ya tenga dueño, agrega tu UUID:

/summon examplemod:moc_horse ~ ~ ~ {MoCSpecies:"ZEBRA",Tame:1b,Owner:[I;0,0,0,0]}

(usa /data get entity @s Owner sobre un mob tuyo tameado, o /summon seguido de domar a mano si prefieres no lidiar con el UUID)
