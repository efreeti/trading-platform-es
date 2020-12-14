package com.github.efreeti.trading.account.service;

import com.github.efreeti.trading.account.aggregates.AccountAggregate;
import com.github.efreeti.trading.account.aggregates.AccountPortfolio;
import com.github.efreeti.trading.account.commands.CreateAccountCommand;
import com.github.efreeti.trading.account.dto.AccountPortfolioDto;
import com.github.efreeti.trading.account.repository.AccountsRepository;
import com.github.efreeti.trading.account.dto.AccountBalanceDto;
import com.github.efreeti.trading.events.domain.AccountCreatedEvent;
import com.github.efreeti.trading.events.domain.OrderCreatedEvent;
import com.github.efreeti.trading.events.domain.OrderStatusChangeEvent;
import com.github.efreeti.trading.events.domain.order.OrderInfo;
import com.github.efreeti.trading.events.domain.order.OrderStatus;
import com.github.efreeti.trading.events.service.EventsServiceBase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountsServiceImpl extends EventsServiceBase implements AccountsService {
	@RequiredArgsConstructor
	private enum RejectionReason {
		NON_EXISTING_ACCOUNT("Order created for non existing account"),
		NO_SUFFICIENT_BALANCE("No sufficient balance to perform buying order"),
		NO_SUFFICIENT_QUANTITY("No sufficient quantity to perform selling order");

		private final String message;
	}

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

	public void processOrderCreatedEvent(OrderCreatedEvent event) {
		accountsRepository.findById(event.getAccountId()).ifPresentOrElse(
			accountAggregate -> {
				switch (event.getOrderInfo().getOrderType()) {
					case BUY:
						processBuyOrderCreatedEvent(event, accountAggregate);
						break;

					case SELL:
						processSellOrderCreatedEvent(event, accountAggregate);
						break;
				}
			},
			() -> rejectOrder(
				event.getAccountId(),
				event.getOrderInfo(),
				RejectionReason.NON_EXISTING_ACCOUNT.message
			)
		);
	}

	public void processOrderStatusChangeEvent(OrderStatusChangeEvent event) {
		if (event.getOrderStatus().equals(OrderStatus.FULFILLED)) {
			accountsRepository.findById(event.getAccountId()).ifPresentOrElse(
				accountAggregate -> {
					switch (event.getOrderInfo().getOrderType()) {
						case BUY:
							processBuyOrderFulfilledEvent(event, accountAggregate);
							break;

						case SELL:
							processSellOrderFulfilledEvent(event, accountAggregate);
							break;
					}
				},
				// Normally there should be more comprehensive handling that this
				// but that would require proper analysis of business domain to define
				// how to handle cases when account is missing after order execution
				() -> rejectOrder(
					event.getAccountId(),
					event.getOrderInfo(),
					RejectionReason.NON_EXISTING_ACCOUNT.message
				)
			);
		}
	}

	private void processBuyOrderCreatedEvent(OrderCreatedEvent event, AccountAggregate accountAggregate) {
		var neededFunds = event.getOrderInfo().getInstrumentProposition().getTotalPrice();

		if (accountAggregate.getBalance().compareTo(neededFunds) >= 0) {
			accountAggregate.subtractFromBalance(neededFunds);

			accountsRepository.save(accountAggregate);

			acceptOrder(event.getAccountId(), event.getOrderInfo());
		} else {
			rejectOrder(
				event.getAccountId(),
				event.getOrderInfo(),
				RejectionReason.NO_SUFFICIENT_BALANCE.message
			);
		}
	}

	private void processSellOrderCreatedEvent(OrderCreatedEvent event, AccountAggregate accountAggregate) {
		var instrumentProposition = event.getOrderInfo().getInstrumentProposition();
		var availableQuantity = accountAggregate.getPortfolio().getInstrumentQuantity(
			instrumentProposition.getInstrumentId()
		);

		if (instrumentProposition.getQuantity() <= availableQuantity) {
			accountAggregate.getPortfolio().decreaseInstrumentQuantity(
				instrumentProposition.getInstrumentId(),
				instrumentProposition.getQuantity()
			);

			accountsRepository.save(accountAggregate);

			acceptOrder(event.getAccountId(), event.getOrderInfo());
		} else {
			rejectOrder(
				event.getAccountId(),
				event.getOrderInfo(),
				RejectionReason.NO_SUFFICIENT_QUANTITY.message
			);
		}
	}

	private void processBuyOrderFulfilledEvent(OrderStatusChangeEvent event, AccountAggregate accountAggregate) {
		var instrumentProposition = event.getOrderInfo().getInstrumentProposition();
		accountAggregate.getPortfolio().increaseInstrumentQuantity(
			instrumentProposition.getInstrumentId(),
			instrumentProposition.getQuantity()
		);

		accountsRepository.save(accountAggregate);
	}

	private void processSellOrderFulfilledEvent(OrderStatusChangeEvent event, AccountAggregate accountAggregate) {
		accountAggregate.addToBalance(event.getOrderInfo().getInstrumentProposition().getTotalPrice());

		accountsRepository.save(accountAggregate);
	}

	private void acceptOrder(UUID accountId, OrderInfo orderInfo) {
		saveEvent(new OrderStatusChangeEvent(accountId, orderInfo, OrderStatus.ACCEPTED, ""));
	}

	private void rejectOrder(UUID accountId, OrderInfo orderInfo, String details) {
		saveEvent(new OrderStatusChangeEvent(accountId, orderInfo, OrderStatus.REJECTED, details));
	}
}
