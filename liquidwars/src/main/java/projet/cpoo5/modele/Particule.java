package projet.cpoo5.modele;

public class Particule {

    private Equipe equipe;
    private int x , y;
    private float energie;

    public Particule(Equipe equipe, int x, int y, float energie) {
        this.equipe = equipe;
        this.x = x;
        this.y = y;
        this.energie = energie;
    }

    public Equipe getEquipe() {
        return equipe;
    }

    public void setEquipe(Equipe equipe) {
        this.equipe = equipe;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public float getEnergie() {
        return energie;
    }
    public void addEnergie(float d) {
        energie += d;
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
