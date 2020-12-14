package com.github.efreeti.trading.account.aggregates;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountPortfolio {
	private Map<String, Integer> items;

	public int getInstrumentQuantity(String instrumentId) {
		return items.getOrDefault(instrumentId, 0);
	}

	public void decreaseInstrumentQuantity(String instrumentId, int delta) {
		items.compute(instrumentId, (key, value) -> {
			var resultingQuantity = (value == null ? 0 : value) - delta;

			return resultingQuantity == 0 ? null : resultingQuantity;
		});
	}

	public void increaseInstrumentQuantity(String instrumentId, int delta) {
		items.compute(instrumentId, (key, value) -> {
			var resultingQuantity = (value == null ? 0 : value) + delta;

			return resultingQuantity == 0 ? null : resultingQuantity;
		});
	}
}
