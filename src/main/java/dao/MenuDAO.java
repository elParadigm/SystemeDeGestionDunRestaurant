package dao;

import model.Menu;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuDAO {



    public List<Menu> getMenuList() { // Removed throws clauses
        List<Menu> menuList = new ArrayList<>();
        String sql = "select idMenu, nom, description from menu";
        Connection conn = null;
        try {
            conn = SingletonConnection.getInstance();
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    int id = rs.getInt("idMenu");
                    String nom = rs.getString("nom");
                    String description = rs.getString("description");


                    Menu menu = new Menu(id, nom, description);

                    menuList.add(menu);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }

        return menuList;
    }


    public void ajouterMenu(Menu menu) {
        String sql = "insert into menu (nom,description) values (?,?)";
        Connection conn = null;
        try {
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

    }


    public void miseAJourMenu(Menu menu) {
        String sql = "Update menu set nom=?, description=? where idMenu=?";
        Connection conn = null;
        try {
            conn = SingletonConnection.getInstance(); // Get connection from Singleton
            try (PreparedStatement ps = conn.prepareStatement(sql)) { // Manage statement here
                ps.setString(1, menu.getNomMenu());
                ps.setString(2, menu.getDescriptionMenu());
                ps.setInt(3, menu.getIdMenu());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void suprimerMenu(int menuID) {
        String sql = "delete from menu where idMenu=?";
        Connection conn = null;
        try {
            conn = SingletonConnection.getInstance();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, menuID);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}