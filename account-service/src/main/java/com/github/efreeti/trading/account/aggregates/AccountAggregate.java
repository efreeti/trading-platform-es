package com.github.efreeti.trading.account.aggregates;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Document
@NoArgsConstructor
@AllArgsConstructor
public class AccountAggregate {
	@Id
	private UUID accountId;
	private BigDecimal balance;
	private AccountPortfolio portfolio;
}
