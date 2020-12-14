package com.github.efreeti.trading.account.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountBalanceDto {
	private final BigDecimal balance;
}
