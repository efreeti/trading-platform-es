package com.github.efreeti.trading.order.repository;

import com.github.efreeti.trading.order.aggregates.OrderAggregate;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface OrdersRepository extends CrudRepository<OrderAggregate, UUID> {
	List<OrderAggregate> findAllByAccountId(UUID accountId);
}
