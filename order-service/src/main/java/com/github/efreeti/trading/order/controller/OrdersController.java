package com.github.efreeti.trading.order.controller;

import com.github.efreeti.trading.order.commands.CreateOrderCommand;
import com.github.efreeti.trading.order.dto.OrderDto;
import com.github.efreeti.trading.order.dto.OrdersDto;
import com.github.efreeti.trading.order.service.OrdersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class OrdersController {
	private final OrdersService ordersService;

	@PostMapping("/orders")
	public UUID createOrder(@RequestBody @Valid CreateOrderCommand command) {
		return ordersService.createOrder(command);
	}

	@GetMapping("/orders")
	public OrdersDto getAccountOrders(@RequestParam UUID accountId) {
		return ordersService.getAccountOrders(accountId);
	}

	@GetMapping("/orders/{orderId}")
	public OrderDto getOrder(@PathVariable UUID orderId) throws OrderNotFoundException {
		return ordersService.getOrder(orderId).orElseThrow(OrderNotFoundException::new);
	}

	@ExceptionHandler(OrderNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public void handleOrderNotFoundException() {}
}
