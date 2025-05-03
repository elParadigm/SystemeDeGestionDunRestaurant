package model;

import java.util.ArrayList;
import java.util.List;

public class Menu {
    private int idMenu ;
    private String nomMenu ;
    private String descriptionMenu ;
    private List<Plat> palts ;

    public Menu(int idMenu, String nomMenu, String descriptionMenu) {
        this.idMenu = idMenu;
        this.nomMenu = nomMenu;
        this.descriptionMenu = descriptionMenu;
        palts = new ArrayList<>();
    }

    public String getDescriptionMenu() {
        return descriptionMenu;
    }

    public void setDescriptionMenu(String descriptionMenu) {
        this.descriptionMenu = descriptionMenu;
    }

    public int getIdMenu() {
        return idMenu;
    }

    public void setIdMenu(int idMenu) {
        this.idMenu = idMenu;
    }

    public String getNomMenu() {
        return nomMenu;
    }

    public void setNomMenu(String nomMenu) {
        this.nomMenu = nomMenu;
    }

    public List<Plat> getPalts() {
        return palts;
    }

    public void setPalts(List<Plat> palts) {
        this.palts = palts;
    }
}
