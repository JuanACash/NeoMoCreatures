package com.example.neomocreatures.entity.butterfly;

/** The 7 butterflies plus the 3 moths, which the original stores as variants 8-10 of the same
 *  entity: moths are full size (butterflies render at 0.7) and are drawn to light. */
public enum ButterflyVariant {

    AGALAIS_URTICAE(1, "butterfly_agalais_urticae", false),
    ARGYREUS_HYPERBIUS(2, "butterfly_argyreus_hyperbius", false),
    ATHYMA_NEFTE(3, "butterfly_athyma_nefte", false),
    CATOPSILIA_POMONA(4, "butterfly_catopsilia_pomona", false),
    MORPHO_PELEIDES(5, "butterfly_morpho_peleides", false),
    VANESSA_ATALANTA(6, "butterfly_vanessa_atalanta", false),
    /** Original: id 7 has no case of its own in the texture switch, so it falls to this default. */
    PIERIS_RAPAE(7, "butterfly_pieris_rapae", false),
    CAMPTOGRAMMA_BILINEATA(8, "moth_camptogramma_bilineata", true),
    IDIA_AEMULA(9, "moth_idia_aemula", true),
    THYATIRA_BATIS(10, "moth_thyatira_batis", true);

    private final int id;
    private final String textureName;
    private final boolean moth;

    ButterflyVariant(int id, String textureName, boolean moth) {
        this.id = id;
        this.textureName = textureName;
        this.moth = moth;
    }

    public int getId() {
        return id;
    }

    public String getTextureName() {
        return textureName;
    }

    public boolean isMoth() {
        return moth;
    }

    public static ButterflyVariant byId(int id) {
        for (ButterflyVariant variant : values()) {
            if (variant.id == id) {
                return variant;
            }
        }
        return PIERIS_RAPAE;
    }
}
