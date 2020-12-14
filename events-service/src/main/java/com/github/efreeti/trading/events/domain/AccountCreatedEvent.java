package com.github.efreeti.trading.events.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class AccountCreatedEvent extends TradingEvent {
	private final UUID accountId;
	private final BigDecimal initialBalance;

	@Override
	public void visit(TradingEventVisitor visitor) {
		visitor.visitAccountCreatedEvent(this);
	}
}
