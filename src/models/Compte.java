package models;

import java.util.UUID;

public abstract class Compte {

    private String id;
    private String numero;
    private double solde;
    private Client client;

    protected Compte() {
        this.id = UUID.randomUUID().toString();
    }

    public Compte(String id, String numero, double solde) {
        this.id = (id != null && !id.isEmpty()) ? id : UUID.randomUUID().toString();
        this.numero = numero;
        this.solde = solde;
    }

    public Compte(String numero, double solde) {
        this.id = UUID.randomUUID().toString();
        this.numero = numero;
        this.solde = solde;
    }

    public Compte(String id, String numero, double solde, Client client) {
        this.id = (id != null && !id.isEmpty()) ? id : UUID.randomUUID().toString();
        this.numero = numero;
        this.solde = solde;
        this.client = client;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public double getSolde() {
        return solde;
    }

    public void setSolde(double solde) {
        this.solde = solde;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public String toString() {
        return "Compte{" + "id='" + id + '\'' + ", numero='" + numero + '\'' + ", solde=" + solde + '}';
    }
}