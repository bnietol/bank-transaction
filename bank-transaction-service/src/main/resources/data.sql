DROP TABLE IF EXISTS transaction;

CREATE TABLE transaction (
  reference VARCHAR(45) PRIMARY KEY,
  account_iban VARCHAR(100) NOT NULL,
  date VARCHAR(100) DEFAULT NULL,
  amount NUMERIC NOT NULL,
  fee NUMERIC DEFAULT NULL,
  description TEXT DEFAULT NULL,
  creation_date BIGINT NOT NULL,
  last_modified_date BIGINT DEFAULT NULL
);
