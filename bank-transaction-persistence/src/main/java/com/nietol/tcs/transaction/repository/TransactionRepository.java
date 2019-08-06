package com.nietol.tcs.transaction.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.nietol.tcs.transaction.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, String> {

  List<Transaction> findByAccountIban(String accountIban);

  List<Transaction> findByAccountIban(String accountIban, Sort sort);

}
