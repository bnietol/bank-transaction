package com.nietol.tcs.transaction.validation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.nietol.tcs.transaction.exception.CommonErrorCodes;
import com.nietol.tcs.transaction.exception.InvalidRequestParameterException;

import io.swagger.model.TransactionDto;

public final class ValidateTransaction {

  public static void isTransactionValid(TransactionDto transaction) {
    List<String> nullparams = new ArrayList<>();

    if (StringUtils.isBlank(transaction.getAccountIban())) {
      nullparams.add("transaction.accountIban");
    }
    if (transaction.getAmount() == null) {
      nullparams.add("transaction.amount");
    }
    if (!nullparams.isEmpty()) {
      throw new InvalidRequestParameterException(CommonErrorCodes.GENERIC_PARAM_MUST_NOT_BE_NULL,
          String.format("Validation error: '%s' params must not be null/empty", nullparams));
    }
  }
}
