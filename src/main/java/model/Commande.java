package model;

import gui.CartItem; // Import CartItem from gui package as per your clarification
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Commande {
    private int idCommande;
    private int idUtilisateur;
    private Date horodatage; // Keeping this as Date as per your original model
    private Double montantTotal; // Keeping this as Double as per your original model
    private String statut; // Renamed from 'Statut' for standard Java naming conventions
    private String tableNumber; // NEW: Field to store the table number
    private List<CartItem> items; // NEW: Field to store the list of CartItems for the order

    // Full constructor - Adjusted to include new fields and use CartItem
    public Commande(Date horodatage, int idCommande, int idUtilisateur, Double montantTotal, String statut, String tableNumber, List<CartItem> items) {
        this.horodatage = horodatage;
        this.idCommande = idCommande;
        this.idUtilisateur = idUtilisateur;
        this.montantTotal = montantTotal;
        this.statut = statut; // Assign renamed field
        this.tableNumber = tableNumber; // Assign new field
        this.items = new ArrayList<>(); // Initialize the list
        if (items != null) { // Add items if provided
            this.items.addAll(items);
        }
    }

    // No-arg constructor - Modified to initialize the items list
    public Commande(){
        super();
        this.items = new ArrayList<>(); // Ensure items list is initialized to avoid NullPointerException
    }

    // --- Getters and Setters ---

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

    public String getStatut() {
        return statut; // Use the new field name
    }

    public void setStatut(String statut) {
        this.statut = statut; // Use the new field name
    }

    // NEW: Getter and Setter for tableNumber
    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    // NEW: Getter and Setter for items (List<CartItem>)
    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    // Note: The original 'plats' field and its getters/setters have been replaced
    // by 'items' (List<CartItem>) to correctly manage item quantities within an order.
}