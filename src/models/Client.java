package models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Client {

    private String id;
    private String nom;
    private String email;
    private List<Compte> compteList;

    public Client() {
        this.id = UUID.randomUUID().toString();
        this.compteList = new ArrayList<>();
    }

    public Client(String id, String nom, String email, List<Compte> compteList) {
        this.id = (id != null && !id.isEmpty()) ? id : UUID.randomUUID().toString();
        this.nom = nom;
        this.email = email;
        this.compteList = compteList != null ? compteList : new ArrayList<>();
    }

    public Client(String id, String nom, String email) {
        this.id = (id != null && !id.isEmpty()) ? id : UUID.randomUUID().toString();
        this.nom = nom;
        this.email = email;
        this.compteList = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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