package model;

// Import necessary classes for byte array image data
import java.io.Serializable; // Often useful for model classes

public class Plat implements Serializable { // Implement Serializable if you plan to pass Plat objects
    private int  idPlat;
    private int  idMenu;
    private String  nom;
    private double prix;
    private String  description;
    private byte[] image; // Changed from File to byte[]

    // Constructor with all fields (including byte[])
    public Plat(int idPlat, int idMenu, String nom, double prix, String description, byte[] image) {
        this.idPlat = idPlat;
        this.idMenu = idMenu;
        this.nom = nom;
        this.prix = prix;
        this.description = description;
        this.image = image;
    }

    // No-arg constructor
    public Plat(){
    }

    // Getter for image (returns byte[])
    public byte[] getImage() {
        return image;
    }

    // Setter for image (accepts byte[])
    public void setImage(byte[] image) {
        this.image = image;
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
