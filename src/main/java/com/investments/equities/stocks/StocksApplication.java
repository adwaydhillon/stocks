package com.investments.equities.stocks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class StocksApplication extends SpringBootServletInitializer {

	private static Logger log = LogManager.getLogger(StocksApplication.class);

	public static void main(String[] args) {
		log.info("firebolt - application is starting up!");
		SpringApplication.run(StocksApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(StocksApplication.class);
	}

}
