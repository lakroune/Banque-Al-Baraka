package models;

public abstract class Compte {

    private String id;
    private String numero;
    private double solde;
    private Client client;

    protected Compte() {
    }

    public Compte(String id, String numero, double solde) {
        this.id = id;
        this.numero = numero;
        this.solde = solde;
    }

    public Compte(String id, String numero, double solde, Client client) {
        this.id = id;
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