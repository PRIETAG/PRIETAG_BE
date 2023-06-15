package com.tag.prietag;

import com.tag.prietag.core.util.TimeStamped;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.ZonedDateTime;
import java.util.Optional;

@EnableJpaAuditing(dateTimeProviderRef = "zonedDateTimeProvider")
@SpringBootApplication
public class PrietagApplication {

	public static void main(String[] args) {
		SpringApplication.run(PrietagApplication.class, args);
	}

	@Bean
	public DateTimeProvider zonedDateTimeProvider(){
		return () -> Optional.of(ZonedDateTime.now(TimeStamped.SEOUL_ZONE_ID));
	}
}
