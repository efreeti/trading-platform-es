package com.github.efreeti.trading.account.controller;

import com.github.efreeti.trading.account.commands.CreateAccountCommand;
import com.github.efreeti.trading.account.dto.AccountBalanceDto;
import com.github.efreeti.trading.account.dto.AccountPortfolioDto;
import com.github.efreeti.trading.account.service.AccountsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AccountsController {
	private final AccountsService accountsService;

	@PostMapping("/accounts")
	public UUID createAccount(@RequestBody @Valid CreateAccountCommand command) {
		return accountsService.createAccount(command);
	}

	@GetMapping("/accounts/{accountId}/balance")
	public AccountBalanceDto getAccountBalance(@PathVariable UUID accountId) throws AccountNotFoundException {
		return accountsService.getAccountBalance(accountId).orElseThrow(AccountNotFoundException::new);
	}

	@GetMapping("/accounts/{accountId}/portfolio")
	public AccountPortfolioDto getAccountPortfolio(@PathVariable UUID accountId) throws AccountNotFoundException {
		return accountsService.getAccountPortfolio(accountId).orElseThrow(AccountNotFoundException::new);
	}

	@ExceptionHandler(AccountNotFoundException.class)
	@ResponseStatus(code = HttpStatus.NOT_FOUND)
	public void handleAccountNotFoundException() {}
}
