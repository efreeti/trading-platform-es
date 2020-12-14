package com.github.efreeti.trading.events.domain;

public interface TradingEventVisitor {
	void visitAccountCreatedEvent(AccountCreatedEvent event);
	void visitOrderCreatedEvent(OrderCreatedEvent event);
	void visitOrderStatusChangeEvent(OrderStatusChangeEvent event);
}
