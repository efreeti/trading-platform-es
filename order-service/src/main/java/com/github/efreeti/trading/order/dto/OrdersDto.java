package com.github.efreeti.trading.order.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrdersDto {
	private final List<OrderDto> orders;
}
