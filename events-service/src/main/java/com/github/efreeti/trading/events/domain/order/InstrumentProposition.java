package com.github.efreeti.trading.events.domain.order;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InstrumentProposition {
	private final String instrumentId;
	private final int quantity;
	private final BigDecimal price;

	public BigDecimal getTotalPrice() {
		return price.multiply(BigDecimal.valueOf(quantity));
	}
}
