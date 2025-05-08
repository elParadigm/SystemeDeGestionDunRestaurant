package model;

import java.util.Date;

public class Facture {
    private int idFacture;
    private int idCommande;
    private double montantTotal;
    private Date horodatage;

    public Facture(Date horodatage, int idCommande, int idFacture, double montantTotal) {
        this.horodatage = horodatage;
        this.idCommande = idCommande;
        this.idFacture = idFacture;
        this.montantTotal = montantTotal;
    }
    public Facture() {
            super();
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

    public int getIdFacture() {
        return idFacture;
    }

    public void setIdFacture(int idFacture) {
        this.idFacture = idFacture;
    }

    public double getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(double montantTotal) {
        this.montantTotal = montantTotal;
    }
}
