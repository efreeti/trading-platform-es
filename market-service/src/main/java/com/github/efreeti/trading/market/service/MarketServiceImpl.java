package com.github.efreeti.trading.market.service;

import com.github.efreeti.trading.events.domain.OrderStatusChangeEvent;
import com.github.efreeti.trading.events.domain.order.OrderStatus;
import com.github.efreeti.trading.events.service.EventsServiceBase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MarketServiceImpl extends EventsServiceBase implements MarketService {
	@Override
	public void processOrderStatusChangeEvent(OrderStatusChangeEvent event) {
		if (event.getOrderStatus().equals(OrderStatus.ACCEPTED)) {
			saveEvent(new OrderStatusChangeEvent(
				event.getAccountId(),
				event.getOrderInfo(),
				OrderStatus.PLACED,
				event.getDetails()
			));
		} else if (event.getOrderStatus().equals(OrderStatus.PLACED)) {
			saveEvent(new OrderStatusChangeEvent(
				event.getAccountId(),
				event.getOrderInfo(),
				OrderStatus.FULFILLED,
				event.getDetails()
			));
		}
	}
}
