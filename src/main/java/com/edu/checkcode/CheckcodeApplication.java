package com.edu.checkcode;

import com.mercadopago.MercadoPagoConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CheckcodeApplication {

	public static void main(String[] args) {
		MercadoPagoConfig.setAccessToken("APP_USR-8675648792667839-110211-e6781e17432124b2352be369845c6477-3811204418");
		SpringApplication.run(CheckcodeApplication.class, args);
	}

}
