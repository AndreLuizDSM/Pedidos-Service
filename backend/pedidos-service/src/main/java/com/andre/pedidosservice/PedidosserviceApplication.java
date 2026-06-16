package com.andre.pedidosservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PedidosserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PedidosserviceApplication.class, args);
	}

}
