package com.github.efreeti.trading.order.commands;

import com.github.efreeti.trading.events.domain.order.OrderType;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreateOrderCommand {
	@NotNull
	private final UUID accountId;
	@NotNull
	private final OrderType orderType;
	@NotNull
	@Size(min = 1, max = 5)
	private final String instrumentId;
	@Min(0)
	private final int quantity;
	@NotNull
	@DecimalMin(value = "0.0", inclusive = false)
	private final BigDecimal price;
}
