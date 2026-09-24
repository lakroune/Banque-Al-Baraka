package models;

public class CompteCourant extends Compte {
    private Double decouvertAutorise;

    public CompteCourant() {
        super();
    }

    public CompteCourant(String id, String numero, Double solde, Double decouvertAutorise) {
        super(id, numero, solde);
        this.decouvertAutorise = decouvertAutorise;
    }

    public CompteCourant(String numero, Double solde, Double decouvertAutorise) {
        super(numero, solde);
        this.decouvertAutorise = decouvertAutorise;
    }

    public CompteCourant(String id, String numero, Double solde, Double decouvertAutorise, Client client) {
        super(id, numero, solde, client);
        this.decouvertAutorise = decouvertAutorise;
    }

    public Double getDecouvertAutorise() {
        return decouvertAutorise;
    }

    public void setDecouvertAutorise(Double decouvertAutorise) {
        this.decouvertAutorise = decouvertAutorise;
    }

    @Override
    public String toString() {
        return "CompteCourant{" +
                "id='" + getId() + '\'' +
                ", numero='" + getNumero() + '\'' +
                ", solde=" + getSolde() +
                ", decouvertAutorise=" + decouvertAutorise +
                '}';
    }
}