package com.nietol.tcs.transaction.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.nietol.tcs.transaction.adapter.TransactionAdapter;
import com.nietol.tcs.transaction.model.Transaction;
import com.nietol.tcs.transaction.model.Transaction_;
import com.nietol.tcs.transaction.repository.TransactionRepository;

import io.swagger.model.SortDirection;
import io.swagger.model.TransactionDto;
import io.swagger.model.TransactionFilterDto;

@Component
public class TransactionServiceImpl implements TransactionService {

  private static final String formatter = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
  private static final SimpleDateFormat SIMPLE_DATE_HMSM_FORMATTTER =
      new SimpleDateFormat(formatter);
  private static final DateTimeFormatter DATE_HMSM_FORMATTTER =
      DateTimeFormatter.ofPattern(formatter);

  private final TransactionAdapter transactionAdapter;
  private final TransactionRepository transactionRepository;

  @Autowired
  public TransactionServiceImpl(final TransactionAdapter transactionAdapter,
      final TransactionRepository transactionRepository) {
    this.transactionAdapter = transactionAdapter;
    this.transactionRepository = transactionRepository;
  }

  @Override
  public TransactionDto createTransaction(TransactionDto transaction) {
    var result = transactionAdapter.adapt(transaction);

    if (StringUtils.isBlank(result.getReference())) {
      // generate unique reference
      var letter = RandomStringUtils.randomAlphabetic(1).toUpperCase();
      var now = Instant.now().toEpochMilli();
      result.setReference(String.format("%s%s", now, letter));
    }

    transactionRepository.save(result);

    transaction.setReference(result.getReference());
    return transaction;
  }

  @Override
  public List<TransactionDto> getTransactions(String accountIban, String sortByAmount) {
    List<Transaction> result;
    if (StringUtils.isNotBlank(accountIban)) {
      if (StringUtils.isNotBlank(sortByAmount)) {
        if (SortDirection.ASC.name().equals(sortByAmount)) {
          result = transactionRepository.findByAccountIban(accountIban,
              Sort.by(Sort.Direction.ASC, Transaction_.amount.getName()));
        } else {
          result = transactionRepository.findByAccountIban(accountIban,
              Sort.by(Sort.Direction.DESC, Transaction_.amount.getName()));
        }
      } else {
        result = transactionRepository.findByAccountIban(accountIban);
      }
    } else if (StringUtils.isNotBlank(sortByAmount)) {
      if (SortDirection.ASC.name().equals(sortByAmount)) {
        result = transactionRepository
            .findAll(Sort.by(Sort.Direction.ASC, Transaction_.amount.getName()));
      } else {
        result = transactionRepository
            .findAll(Sort.by(Sort.Direction.DESC, Transaction_.amount.getName()));
      }
    } else {
      result = transactionRepository.findAll();
    }

    return transactionAdapter.adapt(result);
  }

  @Override
  public TransactionDto getTransactionsByStatus(TransactionFilterDto filter) {
    var transaction = transactionRepository.findById(filter.getReference());
    TransactionDto result;
    if (transaction.isPresent()) {
      var now = Instant.now().toEpochMilli();
      var transFound = transaction.get();
      var transactionTms = formatTms(transFound.getDate());

      if (TransactionFilterDto.ChannelEnum.ATM.name().equals(filter.getChannel().name())
          || TransactionFilterDto.ChannelEnum.CLIENT.name().equals(filter.getChannel().name())) {
        result = calculateFromATMOrClient(transactionTms, now, filter, transFound);
      } else {
        // INTERNAL
        result = calculateFromInternal(transactionTms, now, transFound);
      }
    } else {
      // @formatter:off
      result = new TransactionDto()
              .reference("XXXXXX")
              .status(TransactionDto.StatusEnum.INVALID);
      // @formatter:on
    }
    return result;
  }

  private TransactionDto calculateFromATMOrClient(Long transactionTms, Long now,
      TransactionFilterDto filter, Transaction transaction) {
    //@formatter:off
    var result = new TransactionDto()
                .reference(transaction.getReference())
                .amount(subtract(transaction.getAmount(), transaction.getFee()));
    //@formatter:on
    if (transactionTms < now) {
      result.setStatus(TransactionDto.StatusEnum.SETTLED);
    } else if (transactionTms == now) {
      result.setStatus(TransactionDto.StatusEnum.PENDING);
    } else {
      // if transactionTms > now
      if (TransactionFilterDto.ChannelEnum.ATM.name().equals(filter.getChannel().name())) {
        result.setStatus(TransactionDto.StatusEnum.PENDING);
      } else {
        result.setStatus(TransactionDto.StatusEnum.FUTURE);
      }
    }
    return result;
  }

  private TransactionDto calculateFromInternal(Long transactionTms, Long now,
      Transaction transaction) {
    //@formatter:off
    var result = new TransactionDto()
            .reference(transaction.getReference())
            .amount(transaction.getAmount())
            .fee(transaction.getFee());
    //@formatter:on
    if (transactionTms < now) {
      result.setStatus(TransactionDto.StatusEnum.SETTLED);
    } else if (transactionTms == now) {
      result.setStatus(TransactionDto.StatusEnum.PENDING);
    } else {
      // if transactionTms > now
      result.setStatus(TransactionDto.StatusEnum.FUTURE);
    }
    return result;
  }

  private static Long formatTms(String tms) {
    var offset = ZoneOffset.UTC;
    SIMPLE_DATE_HMSM_FORMATTTER.setTimeZone(TimeZone.getTimeZone(offset));
    return LocalDateTime.parse(tms, DATE_HMSM_FORMATTTER).atZone(offset).toInstant().toEpochMilli();
  }

  private static BigDecimal subtract(BigDecimal n1, BigDecimal n2) {
    if (n1 == null && n2 == null) {
      return null;
    } else if (n1 == null) {
      return n2;
    } else if (n2 == null) {
      return n1;
    } else {
      return n1.subtract(n2);
    }
  }
}
