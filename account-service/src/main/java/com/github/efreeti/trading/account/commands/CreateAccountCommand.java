package com.github.efreeti.trading.account.commands;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class CreateAccountCommand {
	@NotNull
	@DecimalMin("0.0")
	private final BigDecimal initialBalance;
}
