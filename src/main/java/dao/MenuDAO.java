package dao;

import model.Menu;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuDAO {

    // The connection is obtained from the Singleton in each method,
    // but not closed here. The Singleton manages its lifecycle.

    public List<Menu> getMenuList() { // Removed throws clauses
        List<Menu> menuList = new ArrayList<>();
        String sql = "select idMenu, nom, description from menu"; // Select columns needed
        Connection conn = null; // Declare connection outside try-with-resources
        try { // Start try block
            conn = SingletonConnection.getInstance(); // Get connection from Singleton
            try (PreparedStatement ps = conn.prepareStatement(sql); // Manage statement here
                 ResultSet rs = ps.executeQuery()) { // Manage result set here

                while (rs.next()) {
                    int id = rs.getInt("idMenu");
                    String nom = rs.getString("nom");
                    String description = rs.getString("description");

                    // Use your provided Menu constructor
                    Menu menu = new Menu(id, nom, description);

                    menuList.add(menu); // Add the created Menu object to the list
                }
            } // PreparedStatement and ResultSet are closed here automatically
        } catch (SQLException e) {
            e.printStackTrace();
            // Log the error
        }
        // Connection is NOT closed here. The Singleton manages it.
        return menuList;
    }


    public void ajouterMenu(Menu menu) { // Removed throws clauses
        String sql = "insert into menu (nom,description) values (?,?)";
        Connection conn = null; // Declare connection outside try-with-resources
        try { // Start try block
            conn = SingletonConnection.getInstance(); // Get connection from Singleton
            try (PreparedStatement ps = conn.prepareStatement(sql)) { // Manage statement here
                ps.setString(1, menu.getNomMenu());
                ps.setString(2, menu.getDescriptionMenu());
                // idMenu is auto-increment, so no need to set it here
                ps.executeUpdate();
            } // PreparedStatement is closed here automatically
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Connection is NOT closed here. The Singleton manages it.
    }

    // Removed JetBrains annotation for wider compatibility
    public void miseAJourMenu(Menu menu) { // Removed throws clauses
        String sql = "Update menu set nom=?, description=? where idMenu=?";
        Connection conn = null; // Declare connection outside try-with-resources
        try { // Start try block
            conn = SingletonConnection.getInstance(); // Get connection from Singleton
            try (PreparedStatement ps = conn.prepareStatement(sql)) { // Manage statement here
                ps.setString(1, menu.getNomMenu());
                ps.setString(2, menu.getDescriptionMenu());
                ps.setInt(3, menu.getIdMenu());
                ps.executeUpdate();
            } // PreparedStatement is closed here automatically
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Connection is NOT closed here. The Singleton manages it.
    }

    public void suprimerMenu(int menuID) { // Removed throws clauses
        String sql = "delete from menu where idMenu=?";
        Connection conn = null; // Declare connection outside try-with-resources
        try { // Start try block
            conn = SingletonConnection.getInstance(); // Get connection from Singleton
            try (PreparedStatement ps = conn.prepareStatement(sql)) { // Manage statement here
                ps.setInt(1, menuID);
                ps.executeUpdate();
            } // PreparedStatement is closed here automatically
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Connection is NOT closed here. The Singleton manages it.
    }
}