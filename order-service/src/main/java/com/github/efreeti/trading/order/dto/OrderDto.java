package com.github.efreeti.trading.order.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class OrderDto {
	private final UUID orderId;
	private final OrderType orderType;
	private final InstrumentPropositionDto instrumentProposition;
	private final OrderStatus orderStatus;
	private final String details;
}
