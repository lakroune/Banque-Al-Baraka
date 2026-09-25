-- Active: 1789486337362@@127.0.0.1@5433@bankab
DROP TABLE IF EXISTS transactions CASCADE;

DROP TABLE IF EXISTS comptes CASCADE;

DROP TABLE IF EXISTS clients CASCADE;

CREATE TABLE clients (
    id VARCHAR(50) PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL
);

CREATE TABLE comptes (
    id VARCHAR(50) PRIMARY KEY,
    numero VARCHAR(50) UNIQUE NOT NULL,
    solde DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    type_compte VARCHAR(20) NOT NULL,
    decouvert_autorise DOUBLE PRECISION DEFAULT NULL,
    taux_interet DOUBLE PRECISION DEFAULT NULL,
    client_id VARCHAR NOT NULL,
    CONSTRAINT fk_compte_client FOREIGN KEY (client_id) REFERENCES clients (id) ON DELETE CASCADE
);

CREATE TABLE transactions (
    id VARCHAR(50) PRIMARY KEY,
    compte_id VARCHAR(50) NOT NULL,
    montant DOUBLE PRECISION NOT NULL,
    type VARCHAR(50) NOT NULL, -- 'VERSEMENT', 'RETRAIT', 'VIREMENT'
    date_transaction TIMESTAMP NOT NULL,
    lieu VARCHAR(100),
    CONSTRAINT fk_transaction_compte FOREIGN KEY (compte_id) REFERENCES comptes (id) ON DELETE CASCADE
);

SELECT * from transactions;

SELECT t.* FROM transactions t JOIN comptes c ON t.compte_id = c.id WHERE c.numero = '2020' ORDER BY t.date_transaction DESC;