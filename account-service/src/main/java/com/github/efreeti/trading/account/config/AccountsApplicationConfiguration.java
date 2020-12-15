package com.github.efreeti.trading.account.config;

import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.transaction.ChainedKafkaTransactionManager;
import org.springframework.kafka.transaction.KafkaTransactionManager;

@Configuration
public class AccountsApplicationConfiguration {
	@Bean
	MongoTransactionManager mongoTransactionManager(MongoDatabaseFactory databaseFactory) {
		return new MongoTransactionManager(databaseFactory);
	}

	@Bean
	public ChainedKafkaTransactionManager<?, ?> chainedTransactionManager(
		KafkaTransactionManager<?, ?> kafkaTransactionManager,
		MongoTransactionManager mongoTransactionManager
	) {
		return new ChainedKafkaTransactionManager<>(kafkaTransactionManager, mongoTransactionManager);
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
		ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
		ConsumerFactory<Object, Object> kafkaConsumerFactory,
		ChainedKafkaTransactionManager<?, ?> chainedTransactionManager
	) {
		var factory = new ConcurrentKafkaListenerContainerFactory<>();
		configurer.configure(factory, kafkaConsumerFactory);
		factory.getContainerProperties().setTransactionManager(chainedTransactionManager);
		return factory;
	}
}
