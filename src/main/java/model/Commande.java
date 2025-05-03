package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Commande {
    private int idCommande;
    private int idUtilisateur;
    private List<Plat> plats;
    private String Statut;
    private Date horodatage;
    private Double montantTotal;

    public Commande(Date horodatage, int idCommande, int idUtilisateur, Double montantTotal, List<Plat> plats, String statut) {
        this.horodatage = horodatage;
        this.idCommande = idCommande;
        this.idUtilisateur = idUtilisateur;
        this.montantTotal = montantTotal;
        this.plats = new ArrayList<>();
        this.Statut = statut;
    }

    public Date getHorodatage() {
        return horodatage;
    }

    public void setHorodatage(Date horodatage) {
        this.horodatage = horodatage;
    }

    public int getIdCommande() {
        return idCommande;
    }

    public void setIdCommande(int idCommande) {
        this.idCommande = idCommande;
    }

    public int getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public Double getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(Double montantTotal) {
        this.montantTotal = montantTotal;
    }

    public List<Plat> getPlats() {
        return plats;
    }

    public void setPlats(List<Plat> plats) {
        this.plats = plats;
    }

    public String getStatut() {
        return Statut;
    }

    public void setStatut(String statut) {
        Statut = statut;
    }
}
