package gui;
import model.Plat;

public class CartItem {
    private Plat plat;
    private int quantity;

    public CartItem(Plat plat, int quantity) {
        this.plat = plat;
        this.quantity = quantity;
    }

    public Plat getPlat() {
        return plat;
    }

    public String getName() {
        return plat.getNom();
    }

    public double getPrice() {
        return plat.getPrix();
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return quantity + " x " + plat.getNom() + " (" + String.format("%.2f", plat.getPrix()) + " €)";
    }
}