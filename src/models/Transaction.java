package models;

import java.time.LocalDate;

public class Transaction {
    private String id;
    private LocalDate date;
    private Double montant;
    private TypeTransaction type;
    private String lieu;
    private Compte compte;

    public Transaction() {
    }

    public Transaction(String id, LocalDate date, Double montant, TypeTransaction type, String lieu, Compte compte) {
        this.id = id;
        this.date = date;
        this.montant = montant;
        this.type = type;
        this.lieu = lieu;
        this.compte = compte;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Double getMontant() {
        return montant;
    }

    public void setMontant(Double montant) {
        this.montant = montant;
    }

    public TypeTransaction getType() {
        return type;
    }

    public void setType(TypeTransaction type) {
        this.type = type;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public Compte getCompte() {
        return compte;
    }

    public void setCompte(Compte compte) {
        this.compte = compte;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", date=" + date +
                ", montant=" + montant +
                ", type=" + type +
                ", lieu='" + lieu + '\'' +
                '}';
    }
}