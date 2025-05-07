package model;

public class Client extends Utilisateur{
    public Client(int idUtilisateur, String nomUtilisateur, String mdpUtilisateur) {
        super(idUtilisateur,nomUtilisateur,mdpUtilisateur,"Client");
    }
    public Client(){
        super();
    }
}
