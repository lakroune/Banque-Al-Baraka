package models;

import java.util.ArrayList;
import java.util.List;

public class Client {

    private int id;
    private String nom;
    private String email;
    private List<Compte> compteList;

    public Client() {
        this.compteList = new ArrayList<>();
    }

    public Client(int id, String nom, String email, List<Compte> compteList) {
        this.id = id;
        this.nom = nom;
        this.email = email;
        this.compteList = compteList != null ? compteList : new ArrayList<>();
    }

    public Client(int id, String nom, String email) {
        this.id = id;
        this.nom = nom;
        this.email = email;
        this.compteList = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Compte> getCompteList() {
        return compteList;
    }

    public void setCompteList(List<Compte> compteList) {
        this.compteList = compteList;
    }

    @Override
    public String toString() {
        return "Client{" + "id=" + id + ", nom='" + nom + '\'' + ", email='" + email + '\'' + '}';
    }
}