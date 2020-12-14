package com.github.efreeti.trading.account.service;

import com.github.efreeti.trading.account.commands.CreateAccountCommand;
import com.github.efreeti.trading.account.dto.AccountBalanceDto;
import com.github.efreeti.trading.account.dto.AccountPortfolioDto;

import java.util.Optional;
import java.util.UUID;

public interface AccountsService {
	UUID createAccount(CreateAccountCommand command);
	Optional<AccountBalanceDto> getAccountBalance(UUID accountId);
	Optional<AccountPortfolioDto> getAccountPortfolio(UUID accountId);
}
