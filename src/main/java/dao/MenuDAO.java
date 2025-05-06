package dao;
import model.Menu;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class MenuDAO {
    public List<Menu> getMenuList() throws ClassNotFoundException, SQLException {
        List<Menu> menuList = new ArrayList<>();
        String sql = "select * from menu";

        try (Connection conn = SingletonConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("idMenu");
                String nom = rs.getString("nom");
                String description = rs.getString("description");
            }
        }
        return menuList;
    }


    public void ajouterMenu(Menu menu) throws ClassNotFoundException, SQLException {
        String sql = "insert into menu (nom,description) values (?,?)";
        try (Connection conn = SingletonConnection.getInstance();
        PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, menu.getNomMenu());
            ps.setString(2, menu.getDescriptionMenu());
            ps.executeUpdate();
        }

    }

    public void miseAJourMenu(@org.jetbrains.annotations.NotNull Menu menu) throws ClassNotFoundException, SQLException {
        String sql = "Update menu set nom=?, description=? where idMenu=?";
        try (Connection conn = SingletonConnection.getInstance();
        PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, menu.getNomMenu());
            ps.setString(2, menu.getDescriptionMenu());
            ps.setInt(3, menu.getIdMenu());
            ps.executeUpdate();

        }
    }
    public void suprimerMenu(int menuID) throws ClassNotFoundException, SQLException {
        String sql = "delete from menu where idMenu=?";
        try (Connection conn = SingletonConnection.getInstance();
        PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, menuID);
            ps.executeUpdate();
        }
    }
}

