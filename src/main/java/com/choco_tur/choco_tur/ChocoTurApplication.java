package com.choco_tur.choco_tur;

import com.choco_tur.choco_tur.config.ConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ConfigProperties.class)
public class ChocoTurApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChocoTurApplication.class, args);
	}

}
