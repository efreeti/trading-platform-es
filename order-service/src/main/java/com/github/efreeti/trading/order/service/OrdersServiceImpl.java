package com.github.efreeti.trading.order.service;

import com.github.efreeti.trading.events.domain.OrderCreatedEvent;
import com.github.efreeti.trading.events.domain.OrderStatusChangeEvent;
import com.github.efreeti.trading.events.domain.order.InstrumentProposition;
import com.github.efreeti.trading.events.domain.order.OrderInfo;
import com.github.efreeti.trading.events.domain.order.OrderStatus;
import com.github.efreeti.trading.events.service.EventsServiceBase;
import com.github.efreeti.trading.order.aggregates.OrderAggregate;
import com.github.efreeti.trading.order.commands.CreateOrderCommand;
import com.github.efreeti.trading.order.dto.OrderDto;
import com.github.efreeti.trading.order.dto.OrdersDto;
import com.github.efreeti.trading.order.repository.OrdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class OrdersServiceImpl extends EventsServiceBase implements OrdersService {
	private final OrdersRepository ordersRepository;

	@Override
	public UUID createOrder(CreateOrderCommand command) {
		var orderId = UUID.randomUUID();

		saveEvent(new OrderCreatedEvent(command.getAccountId(), new OrderInfo(
			orderId, command.getOrderType(), new InstrumentProposition(
				command.getInstrumentId(),
				command.getQuantity(),
				command.getPrice()
			)
		)));

		return orderId;
	}

	@Override
	public Optional<OrderDto> getOrder(UUID orderId) {
		return ordersRepository.findById(orderId).map(this::mapOrderAggregateToOrderDto);
	}

	@Override
	public OrdersDto getAccountOrders(UUID accountId) {
		return new OrdersDto(
			ordersRepository.findAllByAccountId(accountId).stream()
				.map(this::mapOrderAggregateToOrderDto)
				.collect(toList())
		);
	}

	@Override
	public void processOrderCreatedEvent(OrderCreatedEvent event) {
		ordersRepository.save(new OrderAggregate(
			event.getOrderInfo().getOrderId(),
			event.getAccountId(),
			event.getOrderInfo().getOrderType(),
			event.getOrderInfo().getInstrumentProposition(),
			OrderStatus.CREATED,
			""
		));
	}

	@Override
	public void processOrderStatusChangeEvent(OrderStatusChangeEvent event) {
		ordersRepository.save(new OrderAggregate(
			event.getOrderInfo().getOrderId(),
			event.getAccountId(),
			event.getOrderInfo().getOrderType(),
			event.getOrderInfo().getInstrumentProposition(),
			event.getOrderStatus(),
			event.getDetails()
		));
	}

	private OrderDto mapOrderAggregateToOrderDto(OrderAggregate orderAggregate) {
		return new OrderDto(
			orderAggregate.getOrderId(),
			orderAggregate.getOrderType(),
			orderAggregate.getInstrumentProposition(),
			orderAggregate.getOrderStatus(),
			orderAggregate.getDetails()
		);
	}
}
