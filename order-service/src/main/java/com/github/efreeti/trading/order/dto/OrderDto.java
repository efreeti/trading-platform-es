package com.github.efreeti.trading.order.dto;

import com.github.efreeti.trading.events.domain.order.InstrumentProposition;
import com.github.efreeti.trading.events.domain.order.OrderStatus;
import com.github.efreeti.trading.events.domain.order.OrderType;
import lombok.Data;

import java.util.UUID;

@Data
public class OrderDto {
	private final UUID orderId;
	private final OrderType orderType;
	private final InstrumentProposition instrumentProposition;
	private final OrderStatus orderStatus;
	private final String details;
}
