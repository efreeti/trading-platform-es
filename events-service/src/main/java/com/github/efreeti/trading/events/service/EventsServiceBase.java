package com.github.efreeti.trading.events.service;

import com.github.efreeti.trading.events.domain.AccountCreatedEvent;
import com.github.efreeti.trading.events.domain.OrderCreatedEvent;
import com.github.efreeti.trading.events.domain.OrderStatusChangeEvent;
import com.github.efreeti.trading.events.domain.TradingEvent;
import com.github.efreeti.trading.events.domain.TradingEventVisitor;
import lombok.Setter;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

public abstract class EventsServiceBase implements EventsService {
	@Setter
	@Autowired
	private KafkaTemplate<String, TradingEvent> kafkaTemplate;

	@KafkaListener(topics = "events")
	public void processEvent(ConsumerRecord<String, TradingEvent> record) {
		record.value().visit(new TradingEventVisitor() {
			@Override
			public void visitAccountCreatedEvent(AccountCreatedEvent event) {
				processAccountCreatedEvent(event);
			}

			@Override
			public void visitOrderCreatedEvent(OrderCreatedEvent event) {
				processOrderCreatedEvent(event);
			}

			@Override
			public void visitOrderStatusChangeEvent(OrderStatusChangeEvent event) {
				processOrderStatusChangeEvent(event);
			}
		});
	}

	@Override
	public void processAccountCreatedEvent(AccountCreatedEvent event) {
	}

	@Override
	public void processOrderCreatedEvent(OrderCreatedEvent event) {
	}

	@Override
	public void processOrderStatusChangeEvent(OrderStatusChangeEvent event) {
	}

	@Override
	public void saveEvent(TradingEvent event) {
		kafkaTemplate.send("events", event);
	}
}
