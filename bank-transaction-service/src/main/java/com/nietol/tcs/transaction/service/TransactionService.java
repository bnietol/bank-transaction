package com.nietol.tcs.transaction.service;

import java.util.List;

import io.swagger.model.TransactionDto;
import io.swagger.model.TransactionFilterDto;

public interface TransactionService {

  TransactionDto createTransaction(final TransactionDto transaction);
  List<TransactionDto> getTransactions(final String accountIban, final String sortByAmount);
  TransactionDto getTransactionsByStatus(final TransactionFilterDto filter);

}
