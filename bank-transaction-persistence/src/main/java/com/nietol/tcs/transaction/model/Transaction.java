package com.nietol.tcs.transaction.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.nietol.tcs.transaction.generic.Auditable;

@Entity
@Table(name = "transaction")
public class Transaction extends Auditable implements java.io.Serializable {

  @Id
  @Column(name = "reference", unique = true, nullable = false)
  private String reference;

  @Column(name = "account_iban")
  private String accountIban;

  @Column(name = "date")
  private String date;

  @Column(name = "amount")
  private BigDecimal amount;

  @Column(name = "fee")
  private BigDecimal fee;

  @Column(name = "description")
  private String description;

  public Transaction() {}

  public String getReference() {
    return reference;
  }

  public void setReference(String reference) {
    this.reference = reference;
  }

  public String getAccountIban() {
    return accountIban;
  }

  public void setAccountIban(String accountIban) {
    this.accountIban = accountIban;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public BigDecimal getFee() {
    return fee;
  }

  public void setFee(BigDecimal fee) {
    this.fee = fee;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private String reference;
    private String accountIban;
    private String date;
    private BigDecimal amount;
    private BigDecimal fee;
    private String description;

    private Builder() {}

    public Builder withReference(String reference) {
      this.reference = reference;
      return this;
    }

    public Builder withAccountIban(String accountIban) {
      this.accountIban = accountIban;
      return this;
    }

    public Builder withDate(String date) {
      this.date = date;
      return this;
    }

    public Builder withAmount(BigDecimal amount) {
      this.amount = amount;
      return this;
    }

    public Builder withFee(BigDecimal fee) {
      this.fee = fee;
      return this;
    }

    public Builder withDescription(String description) {
      this.description = description;
      return this;
    }

    public Transaction build() {
      Transaction transaction = new Transaction();
      transaction.setReference(reference);
      transaction.setAccountIban(accountIban);
      transaction.setDate(date);
      transaction.setAmount(amount);
      transaction.setFee(fee);
      transaction.setDescription(description);
      return transaction;
    }
  }
}


