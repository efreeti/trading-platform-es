package com.github.efreeti.trading.events.domain;

import com.github.efreeti.trading.events.domain.order.OrderInfo;
import com.github.efreeti.trading.events.domain.order.OrderStatus;
import lombok.Data;

import java.util.UUID;

@Data
public class OrderStatusChangeEvent extends TradingEvent {
	private final UUID accountId;
	private final OrderInfo orderInfo;
	private final OrderStatus orderStatus;
	private final String details;

	@Override
	public void visit(TradingEventVisitor visitor) {
		visitor.visitOrderStatusChangeEvent(this);
	}
}
