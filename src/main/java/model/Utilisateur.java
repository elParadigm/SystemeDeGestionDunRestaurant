package model;

public class Utilisateur {
    private int idUtilisateur;
    private String nomUtilisateur;
    private String motDePasse;
    private String role;
    public Utilisateur(int id, String nomUtilisateur, String motDePasse, String role) {
        this.idUtilisateur = id;
        this.nomUtilisateur = nomUtilisateur;
        this.motDePasse = motDePasse;
        this.role = role;
    }

    public int getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getNomUtilisateur() {
        return nomUtilisateur;
    }

    public void setNomUtilisateur(String nomUtilisateur) {
        this.nomUtilisateur = nomUtilisateur;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean authentifier(String nomUtilisateur, String motDePasse) {
        return this.nomUtilisateur.equals(nomUtilisateur) && this.motDePasse.equals(motDePasse);
    }


}
