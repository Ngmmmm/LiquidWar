package util;
public record GameConfig(
    int largeur,
    int hauteur,
    int particulesParEquipe,
    float energieInitiale,
    float seuilConversion,
    float degatsAttaque,
    float transfertEnergie
){

    public GameConfig {
        if (largeur <= 0 || hauteur <= 0)
            throw new IllegalArgumentException("Dimensions invalides");
        if (particulesParEquipe < 0)
            throw new IllegalArgumentException("Nombre de particules invalide");
        if (energieInitiale <= 0)
            throw new IllegalArgumentException("Énergie initiale invalide");
        if (seuilConversion < 0)
            throw new IllegalArgumentException("Seuil conversion invalide");
        if (degatsAttaque < 0 || transfertEnergie < 0)
            throw new IllegalArgumentException("Delta négatif interdit");
    }

}