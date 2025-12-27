package com.vti.springdatajpa.dto;

import java.math.BigDecimal;

public record WithdrawRequest(Integer bankAccountId, BigDecimal amount, String note) {}
