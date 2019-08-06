package com.nietol.tcs.transaction.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nietol.tcs.transaction.service.TransactionService;
import com.nietol.tcs.transaction.validation.ValidateTransaction;

import io.swagger.api.TransactionApi;
import io.swagger.model.TransactionDto;
import io.swagger.model.TransactionFilterDto;

@RestController
public class TransactionController implements TransactionApi {
  @Autowired
  TransactionService service;

  @Override
  public ResponseEntity<TransactionDto> createTransaction(@RequestBody TransactionDto transaction) {
    ValidateTransaction.isTransactionValid(transaction);
    return new ResponseEntity<>(service.createTransaction(transaction), HttpStatus.CREATED);
  }

  @Override
  public ResponseEntity<List<TransactionDto>> getTransactions(
      @RequestParam(value = "accountIban", required = false) String accountIban,
      @RequestParam(value = "sortByAmount", required = false) String sortByAmount) {
    return new ResponseEntity<>(service.getTransactions(accountIban, sortByAmount), HttpStatus.OK);
  }

  @Override
  public ResponseEntity<TransactionDto> getTransactionsByStatus(
      @RequestBody TransactionFilterDto filter) {
    return new ResponseEntity<>(service.getTransactionsByStatus(filter), HttpStatus.OK);
  }

}

