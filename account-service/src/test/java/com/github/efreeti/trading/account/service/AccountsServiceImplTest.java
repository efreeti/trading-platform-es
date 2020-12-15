package com.github.efreeti.trading.account.service;

import com.github.efreeti.trading.account.aggregates.AccountAggregate;
import com.github.efreeti.trading.account.aggregates.AccountPortfolio;
import com.github.efreeti.trading.account.commands.CreateAccountCommand;
import com.github.efreeti.trading.account.repository.AccountsRepository;
import com.github.efreeti.trading.events.domain.AccountCreatedEvent;
import com.github.efreeti.trading.events.domain.OrderCreatedEvent;
import com.github.efreeti.trading.events.domain.OrderStatusChangeEvent;
import com.github.efreeti.trading.events.domain.TradingEvent;
import com.github.efreeti.trading.events.domain.order.InstrumentProposition;
import com.github.efreeti.trading.events.domain.order.OrderInfo;
import com.github.efreeti.trading.events.domain.order.OrderStatus;
import com.github.efreeti.trading.events.domain.order.OrderType;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class AccountsServiceImplTest {
	@Mock
	private AccountsRepository accountsRepository;
	@Mock
	private KafkaTemplate<String, TradingEvent> kafkaTemplate;
	@Captor
	private ArgumentCaptor<TradingEvent> eventArgumentCaptor;

	private AccountsServiceImpl accountsService;

	@BeforeEach
	public void setUp() {
		accountsService = new AccountsServiceImpl(accountsRepository);
		accountsService.setKafkaTemplate(kafkaTemplate);
	}

	@Test
	public void testCreateAccount() {
		accountsService.createAccount(new CreateAccountCommand(new BigDecimal("100.00")));

		verify(kafkaTemplate).send(eq("events"), eventArgumentCaptor.capture());

		assertThat(eventArgumentCaptor.getValue(), instanceOf(AccountCreatedEvent.class));

		var event = (AccountCreatedEvent) eventArgumentCaptor.getValue();

		assertThat(event.getAccountId(), any(UUID.class));
		assertThat(event.getInitialBalance(), is(new BigDecimal("100.00")));

		accountsService.processEvent(new ConsumerRecord<>("events", 0, 0, "key", event));

		verify(accountsRepository).save(argThat((arg) -> arg.equals(new AccountAggregate(
			event.getAccountId(), new BigDecimal("100.00"), new AccountPortfolio(Map.of())
		))));
	}

	@Test
	public void testSuccessfulBuyOrderFlow() {
		var accountId = UUID.randomUUID();
		var orderInfo = new OrderInfo(UUID.randomUUID(), OrderType.BUY, new InstrumentProposition(
			"GOOG", 2, BigDecimal.TEN
		));

		when(accountsRepository.findById(accountId)).thenAnswer((mock) -> Optional.of(
			new AccountAggregate(accountId, new BigDecimal("100.00"), new AccountPortfolio(new HashMap<>()))
		));

		triggerOrderCreation(1, accountId, orderInfo);

		verify(accountsRepository).save(argThat((arg) -> arg.equals(new AccountAggregate(
			accountId, new BigDecimal("80.00"), new AccountPortfolio(Map.of())
		))));
		verify(kafkaTemplate).send(eq("events"), argThat((arg) -> arg.equals(
			new OrderStatusChangeEvent(accountId, orderInfo, OrderStatus.ACCEPTED, "")
		)));

		triggerOrderStatusChange(2, accountId, orderInfo, OrderStatus.ACCEPTED, "");
		triggerOrderStatusChange(3, accountId, orderInfo, OrderStatus.PLACED, "");

		when(accountsRepository.findById(accountId)).thenAnswer((mock) -> Optional.of(
			new AccountAggregate(accountId, new BigDecimal("80.00"), new AccountPortfolio(new HashMap<>()))
		));

		triggerOrderStatusChange(4, accountId, orderInfo, OrderStatus.FULFILLED, "");

		verify(accountsRepository).save(argThat((arg) -> arg.equals(new AccountAggregate(
			accountId, new BigDecimal("80.00"), new AccountPortfolio(Map.of("GOOG", 2))
		))));
	}

	@Test
	public void testFailedBuyOrderFlow() {
		var accountId = UUID.randomUUID();
		var orderInfo = new OrderInfo(UUID.randomUUID(), OrderType.BUY, new InstrumentProposition(
			"GOOG", 2, BigDecimal.TEN
		));

		when(accountsRepository.findById(accountId)).thenAnswer((mock) -> Optional.of(
			new AccountAggregate(accountId, new BigDecimal("10.00"), new AccountPortfolio(new HashMap<>()))
		));

		triggerOrderCreation(1, accountId, orderInfo);

		verify(kafkaTemplate).send(eq("events"), argThat((arg) -> arg.equals(new OrderStatusChangeEvent(
			accountId, orderInfo, OrderStatus.REJECTED, AccountsServiceImpl.RejectionReason.NO_SUFFICIENT_BALANCE.message
		))));
		verify(accountsRepository).findById(accountId);
		verifyNoMoreInteractions(accountsRepository);
	}


	@Test
	public void testSuccessfulSellOrderFlow() {
		var accountId = UUID.randomUUID();
		var orderInfo = new OrderInfo(UUID.randomUUID(), OrderType.SELL, new InstrumentProposition(
			"GOOG", 2, BigDecimal.TEN
		));

		when(accountsRepository.findById(accountId)).thenAnswer((mock) -> Optional.of(
			new AccountAggregate(accountId, new BigDecimal("0.00"), new AccountPortfolio(new HashMap<>(
				Map.of("GOOG", 5)
			)))
		));

		triggerOrderCreation(1, accountId, orderInfo);

		verify(accountsRepository).save(argThat((arg) -> arg.equals(new AccountAggregate(
			accountId, new BigDecimal("0.00"), new AccountPortfolio(Map.of("GOOG", 3))
		))));
		verify(kafkaTemplate).send(eq("events"), argThat((arg) -> arg.equals(
			new OrderStatusChangeEvent(accountId, orderInfo, OrderStatus.ACCEPTED, "")
		)));

		triggerOrderStatusChange(2, accountId, orderInfo, OrderStatus.ACCEPTED, "");
		triggerOrderStatusChange(3, accountId, orderInfo, OrderStatus.PLACED, "");

		when(accountsRepository.findById(accountId)).thenAnswer((mock) -> Optional.of(
			new AccountAggregate(accountId, new BigDecimal("0.00"), new AccountPortfolio(new HashMap<>(
				Map.of("GOOG", 3)
			)))
		));

		triggerOrderStatusChange(4, accountId, orderInfo, OrderStatus.FULFILLED, "");

		verify(accountsRepository).save(argThat((arg) -> arg.equals(new AccountAggregate(
			accountId, new BigDecimal("20.00"), new AccountPortfolio(Map.of("GOOG", 3))
		))));
	}

	@Test
	public void testFailedSellOrderFlow() {
		var accountId = UUID.randomUUID();
		var orderInfo = new OrderInfo(UUID.randomUUID(), OrderType.SELL, new InstrumentProposition(
			"GOOG", 2, BigDecimal.TEN
		));

		when(accountsRepository.findById(accountId)).thenAnswer((mock) -> Optional.of(
			new AccountAggregate(accountId, new BigDecimal("100.00"), new AccountPortfolio(new HashMap<>()))
		));

		triggerOrderCreation(1, accountId, orderInfo);

		verify(kafkaTemplate).send(eq("events"), argThat((arg) -> arg.equals(new OrderStatusChangeEvent(
			accountId, orderInfo, OrderStatus.REJECTED, AccountsServiceImpl.RejectionReason.NO_SUFFICIENT_QUANTITY.message
		))));
		verify(accountsRepository).findById(accountId);
		verifyNoMoreInteractions(accountsRepository);
	}

	private void triggerOrderCreation(int eventNr, UUID accountId, OrderInfo orderInfo) {
		accountsService.processEvent(new ConsumerRecord<>(
			"events", 0, eventNr, "key " + (eventNr + 1), new OrderCreatedEvent(accountId, orderInfo)
		));
	}

	private void triggerOrderStatusChange(int eventNr, UUID accountId, OrderInfo orderInfo, OrderStatus orderStatus, String details) {
		accountsService.processEvent(new ConsumerRecord<>(
			"events", 0, eventNr - 1, "key " + eventNr, new OrderStatusChangeEvent(
				accountId, orderInfo, orderStatus, details
			)
		));
	}
}
