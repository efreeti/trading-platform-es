package com.github.efreeti.trading.events.domain;

import com.github.efreeti.trading.events.domain.order.OrderInfo;
import lombok.Data;

import java.util.UUID;

@Data
public class OrderCreatedEvent extends TradingEvent {
	private final UUID accountId;
	private final OrderInfo orderInfo;

	@Override
	public void visit(TradingEventVisitor visitor) {
		visitor.visitOrderCreatedEvent(this);
	}
}
