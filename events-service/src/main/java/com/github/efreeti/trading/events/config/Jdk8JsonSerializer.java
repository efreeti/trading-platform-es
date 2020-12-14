package com.github.efreeti.trading.events.config;

import org.springframework.kafka.support.serializer.JsonSerializer;

public class Jdk8JsonSerializer<T> extends JsonSerializer<T> {
	public Jdk8JsonSerializer() {
		super();
		this.objectMapper.findAndRegisterModules();
	}

}
