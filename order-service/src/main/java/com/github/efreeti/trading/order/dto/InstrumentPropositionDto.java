package com.github.efreeti.trading.order.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InstrumentPropositionDto {
	private final String instrumentId;
	private final int quantity;
	private final BigDecimal price;

	public BigDecimal getTotalPrice() {
		return price.multiply(BigDecimal.valueOf(quantity));
	}
}
