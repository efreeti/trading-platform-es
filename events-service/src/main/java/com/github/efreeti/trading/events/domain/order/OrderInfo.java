package com.github.efreeti.trading.events.domain.order;

import lombok.Data;

import java.util.UUID;

@Data
public class OrderInfo {
	private final UUID orderId;
	private final OrderType orderType;
	private final InstrumentProposition instrumentProposition;
}
