package com.github.efreeti.trading.account.dto;

import lombok.Data;

import java.util.Map;

@Data
public class AccountPortfolioDto {
	private final Map<String, Integer> portfolio;
}
