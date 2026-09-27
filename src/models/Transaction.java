package models;

import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction {
    private String id;
    private LocalDateTime date;
    private Double montant;
    private TypeTransaction type;
    private String lieu;
    private Compte compteSource;
    private Compte compteDestination;

    public Transaction() {
        this.id = UUID.randomUUID().toString();
    }

    public Transaction(String id, LocalDateTime date, Double montant, TypeTransaction type, String lieu,
            Compte compteSource, Compte compteDestination) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.date = date;
        this.montant = montant;
        this.type = type;
        this.lieu = lieu;
        this.compteSource = compteSource;
        this.compteDestination = compteDestination;
    }

    public Transaction(LocalDateTime date, Double montant, TypeTransaction type, String lieu, Compte compteSource,
            Compte compteDestination) {
        this.id = UUID.randomUUID().toString();
        this.date = date;
        this.montant = montant;
        this.type = type;
        this.lieu = lieu;
        this.compteSource = compteSource;
        this.compteDestination = compteDestination;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
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

    public Compte getCompteSource() {
        return compteSource;
    }

    public void setCompteSource(Compte compteSource) {
        this.compteSource = compteSource;
    }

    public Compte getCompteDestination() {
        return compteDestination;
    }

    public void setCompteDestination(Compte compteDestination) {
        this.compteDestination = compteDestination;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", date=" + date +
                ", montant=" + montant +
                ", type=" + type +
                ", lieu='" + lieu + '\'' +
                ", compteSource=" + (compteSource != null ? compteSource.getNumero() : "null") +
                ", compteDestination=" + (compteDestination != null ? compteDestination.getNumero() : "null") +
                '}';
    }
}