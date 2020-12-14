package com.github.efreeti.trading.order.service;

import com.github.efreeti.trading.order.commands.CreateOrderCommand;
import com.github.efreeti.trading.order.dto.OrderDto;
import com.github.efreeti.trading.order.dto.OrdersDto;

import java.util.Optional;
import java.util.UUID;

public interface OrdersService {
	UUID createOrder(CreateOrderCommand command);
	Optional<OrderDto> getOrder(UUID orderId);
	OrdersDto getAccountOrders(UUID accountId);
}
