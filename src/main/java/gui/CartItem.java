package gui;
import model.Plat;

public class CartItem {
    // Changed to store the Plat object directly in the CartItem
    private Plat plat;
    private int quantity;

    // Updated constructor to accept a Plat object
    public CartItem(Plat plat, int quantity) {
        this.plat = plat;
        this.quantity = quantity;
    }

    public Plat getPlat() {
        return plat;
    }

    public String getName() {
        return plat.getNom(); // Get name from Plat object
    }

    public double getPrice() {
        return plat.getPrix(); // Get price from Plat object
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Override toString for easy display in a list
    @Override
    public String toString() {
        // Format price with 2 decimal places
        return quantity + " x " + plat.getNom() + " (" + String.format("%.2f", plat.getPrix()) + " €)";
    }
}