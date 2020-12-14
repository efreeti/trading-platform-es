package com.github.efreeti.trading.events.service;

import com.github.efreeti.trading.events.domain.AccountCreatedEvent;
import com.github.efreeti.trading.events.domain.OrderCreatedEvent;
import com.github.efreeti.trading.events.domain.OrderStatusChangeEvent;
import com.github.efreeti.trading.events.domain.TradingEvent;

public interface EventsService {
	void processAccountCreatedEvent(AccountCreatedEvent event);
	void processOrderCreatedEvent(OrderCreatedEvent event);
	void processOrderStatusChangeEvent(OrderStatusChangeEvent event);
	void saveEvent(TradingEvent event);
}
