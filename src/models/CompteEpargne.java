package models;

public class CompteEpargne extends Compte {
    private Double tauxInteret;

    public CompteEpargne() {
        super();
    }

    public CompteEpargne(String id, String numero, Double solde, Double tauxInteret) {
        super(id, numero, solde);
        this.tauxInteret = tauxInteret;
    }

    public CompteEpargne(String id, String numero, Double solde, Double tauxInteret, Client client) {
        super(id, numero, solde, client);
        this.tauxInteret = tauxInteret;
    }

    public Double getTauxInteret() {
        return tauxInteret;
    }

    public void setTauxInteret(Double tauxInteret) {
        this.tauxInteret = tauxInteret;
    }

    @Override
    public String toString() {
        return "CompteEpargne{" +
                "id='" + getId() + '\'' +
                ", numero='" + getNumero() + '\'' +
                ", solde=" + getSolde() +
                ", tauxInteret=" + tauxInteret +
                '}';
    }
}