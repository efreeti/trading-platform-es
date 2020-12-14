package com.github.efreeti.trading.account.service;

import com.github.efreeti.trading.account.aggregates.AccountAggregate;
import com.github.efreeti.trading.account.aggregates.AccountPortfolio;
import com.github.efreeti.trading.account.commands.CreateAccountCommand;
import com.github.efreeti.trading.account.dto.AccountPortfolioDto;
import com.github.efreeti.trading.account.repository.AccountsRepository;
import com.github.efreeti.trading.account.dto.AccountBalanceDto;
import com.github.efreeti.trading.events.domain.AccountCreatedEvent;
import com.github.efreeti.trading.events.service.EventsServiceBase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountsServiceImpl extends EventsServiceBase implements AccountsService {
	private final AccountsRepository accountsRepository;

	@Override
	public UUID createAccount(CreateAccountCommand command) {
		var accountId = UUID.randomUUID();

		saveEvent(new AccountCreatedEvent(accountId, command.getInitialBalance()));

		return accountId;
	}

	@Override
	public Optional<AccountBalanceDto> getAccountBalance(UUID accountId) {
		return accountsRepository.findById(accountId).map(
			accountAggregate -> new AccountBalanceDto(accountAggregate.getBalance())
		);
	}

	@Override
	public Optional<AccountPortfolioDto> getAccountPortfolio(UUID accountId) {
		return accountsRepository.findById(accountId).map(
			accountAggregate -> new AccountPortfolioDto(accountAggregate.getPortfolio().getItems())
		);
	}

	public void processAccountCreatedEvent(AccountCreatedEvent event) {
		accountsRepository.save(new AccountAggregate(
			event.getAccountId(),
			event.getInitialBalance(),
			new AccountPortfolio(new HashMap<>())
		));
	}
}
