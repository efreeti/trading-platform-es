package com.github.efreeti.trading.events.domain;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
public abstract class TradingEvent {
	public abstract void visit(TradingEventVisitor visitor);
}
