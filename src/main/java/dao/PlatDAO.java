package dao;

import model.Plat;
import java.io.ByteArrayInputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlatDAO {

    public int addPlat(Plat plat) {
        int generatedId = -1;
        String sql = "INSERT INTO plat(nom, description, prix, idMenu, imagePlat) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = SingletonConnection.getInstance();
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, plat.getNom());
                ps.setString(2, plat.getDescription());
                ps.setDouble(3, plat.getPrix());
                ps.setInt(4, plat.getIdMenu());
                if (plat.getImage() != null) {
                    ps.setBytes(5, plat.getImage());
                } else {
                    ps.setNull(5, Types.BLOB);
                }

                int rowsAffected = ps.executeUpdate();

                if (rowsAffected > 0) {
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            generatedId = rs.getInt(1);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return generatedId;
    }

    public List<Plat> getAllPlats() {
        List<Plat> plats = new ArrayList<>();
        String sql = "SELECT idPlat, nom, description, prix, idMenu, imagePlat FROM plat";
        Connection conn = null;
        try {
            conn = SingletonConnection.getInstance();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    Plat plat = new Plat();
                    plat.setIdPlat(rs.getInt("idPlat"));
                    plat.setNom(rs.getString("nom"));
                    plat.setDescription(rs.getString("description"));
                    plat.setPrix(rs.getDouble("prix"));
                    plat.setIdMenu(rs.getInt("idMenu"));
                    plat.setImage(rs.getBytes("imagePlat"));
                    plats.add(plat);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return plats;
    }

    public Plat getPlatById(int id) {
        Plat plat = null;
        String sql = "SELECT idPlat, nom, description, prix, idMenu, imagePlat FROM plat WHERE idPlat = ?";
        Connection conn = null;
        try {
            conn = SingletonConnection.getInstance();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        plat = new Plat();
                        plat.setIdPlat(rs.getInt("idPlat"));
                        plat.setNom(rs.getString("nom"));
                        plat.setDescription(rs.getString("description"));
                        plat.setPrix(rs.getDouble("prix"));
                        plat.setIdMenu(rs.getInt("idMenu"));
                        plat.setImage(rs.getBytes("imagePlat"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return plat;
    }

    public boolean updatePlat(Plat plat) {
        String sql = "UPDATE plat SET nom = ?, description = ?, prix = ?, idMenu = ?, imagePlat = ? WHERE idPlat = ?";
        Connection conn = null;
        try {
            conn = SingletonConnection.getInstance();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, plat.getNom());
                ps.setString(2, plat.getDescription());
                ps.setDouble(3, plat.getPrix());
                ps.setInt(4, plat.getIdMenu());
                if (plat.getImage() != null) {
                    ps.setBytes(5, plat.getImage());
                } else {
                    ps.setNull(5, Types.BLOB);
                }
                ps.setInt(6, plat.getIdPlat());

                int rowsAffected = ps.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletePlat(int id) {
        String sql = "DELETE FROM plat WHERE idPlat = ?";
        Connection conn = null;
        try {
            conn = SingletonConnection.getInstance();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                int rowsAffected = ps.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Plat> getPlatsByMenuId(int idMenu) {
        List<Plat> plats = new ArrayList<>();
        String sql = "SELECT idPlat, nom, description, prix, idMenu, imagePlat FROM plat WHERE idMenu = ?";
        Connection conn = null;
        try {
            conn = SingletonConnection.getInstance();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idMenu);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Plat plat = new Plat();
                        plat.setIdPlat(rs.getInt("idPlat"));
                        plat.setNom(rs.getString("nom"));
                        plat.setDescription(rs.getString("description"));
                        plat.setPrix(rs.getDouble("prix"));
                        plat.setIdMenu(rs.getInt("idMenu"));
                        plat.setImage(rs.getBytes("imagePlat"));
                        plats.add(plat);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return plats;
    }
}