package com.github.efreeti.trading.account.aggregates;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountPortfolio {
	private Map<String, Integer> items;
}
