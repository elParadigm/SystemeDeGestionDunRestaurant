package model;

public class Plat {
    public int  idPlat;
    public int  idMenu;
    public String  nom;
    public double prix;
    public String  description;

    public Plat(String description, int idMenu, int idPlat, String nom, double prix) {
        this.description = description;
        this.idMenu = idMenu;
        this.idPlat = idPlat;
        this.nom = nom;
        this.prix = prix;
    }
    public Plat(){

    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getIdMenu() {
        return idMenu;
    }

    public void setIdMenu(int idMenu) {
        this.idMenu = idMenu;
    }

    public int getIdPlat() {
        return idPlat;
    }

    public void setIdPlat(int idPlat) {
        this.idPlat = idPlat;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }
}
