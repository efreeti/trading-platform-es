package com.github.efreeti.trading.order.aggregates;

import com.github.efreeti.trading.events.domain.order.InstrumentProposition;
import com.github.efreeti.trading.events.domain.order.OrderStatus;
import com.github.efreeti.trading.events.domain.order.OrderType;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Data
@Document
public class OrderAggregate {
	@Id
	private final UUID orderId;
	private final UUID accountId;
	private final OrderType orderType;
	private final InstrumentProposition instrumentProposition;
	private final OrderStatus orderStatus;
	private final String details;
}
