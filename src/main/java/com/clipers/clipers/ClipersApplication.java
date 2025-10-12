package com.clipers.clipers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
    scanBasePackages = {"com.clipers"},
    nameGenerator = org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator.class
)
public class ClipersApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClipersApplication.class, args);
	}

}
