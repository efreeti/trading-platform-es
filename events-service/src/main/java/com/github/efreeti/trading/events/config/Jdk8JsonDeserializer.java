package com.github.efreeti.trading.events.config;

import org.springframework.kafka.support.serializer.JsonDeserializer;

public class Jdk8JsonDeserializer<T> extends JsonDeserializer<T> {
	public Jdk8JsonDeserializer() {
		super();
		this.objectMapper.findAndRegisterModules();
	}

}
