package com.github.efreeti.trading.account.repository;

import com.github.efreeti.trading.account.aggregates.AccountAggregate;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface AccountsRepository extends CrudRepository<AccountAggregate, UUID> {
}
