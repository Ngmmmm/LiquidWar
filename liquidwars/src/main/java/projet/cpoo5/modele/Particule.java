package projet.cpoo5.modele;


/**
 * Représente une entité unitaire du jeu (un "blob").
 * Possède une position, une quantité d'énergie et appartient à une équipe.
 */
public final class Particule implements IntParticule {

    private Equipe equipe;
    private int x , y;
    private float energie;
    private static final float MAX_ENERGIE = 100.0f;

    public Particule(Equipe equipe, int x, int y, float energie) {
        this.equipe = equipe;
        this.x = x;
        this.y = y;
        this.energie = energie;
    }

    @Override
    public Equipe getEquipe() {
        return equipe;
    }

    public void setEquipe(Equipe equipe) {
        this.equipe = equipe;
    }

    @Override
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    @Override
    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public float getEnergie() {
        return energie;
    }
    public void addEnergie(float d) {
        energie += d;
        if (this.energie > MAX_ENERGIE) this.energie = MAX_ENERGIE;
    }
    public void removeEnergie (float d) {
        energie -= d;
        if (energie < 0) energie = 0;
    }

    public void deplacer(int nx , int ny){
        this.setX(nx);
        this.setY(ny);
    }
    
}
