package com.nietol.tcs.transaction.adapter;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.nietol.tcs.transaction.model.Transaction;

import io.swagger.model.TransactionDto;

@Component
public class TransactionAdapter {

  public Transaction adapt(TransactionDto transaction) {
    //@formatter:off
    return Transaction.builder()
            .withAccountIban(transaction.getAccountIban())
            .withDate(transaction.getDate())
            .withAmount(transaction.getAmount())
            .withDescription(transaction.getDescription())
            .withFee(transaction.getFee())
            .withReference(transaction.getReference())
            .build();
    //@formatter:on
  }

  public List<TransactionDto> adapt(List<Transaction> acts) {
    return Optional.ofNullable(acts).orElseGet(Collections::emptyList).stream().map(this::adapt)
            .collect(Collectors.toList());
  }

  private TransactionDto adapt(Transaction transaction) {
    //@formatter:off
    return new TransactionDto()
            .accountIban(transaction.getAccountIban())
            .amount(transaction.getAmount())
            .date(transaction.getDate())
            .description(transaction.getDescription())
            .fee(transaction.getFee())
            .reference(transaction.getReference());
    //@formatter:on
  }
}
