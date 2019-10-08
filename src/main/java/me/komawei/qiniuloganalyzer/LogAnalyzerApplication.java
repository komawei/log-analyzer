package me.komawei.qiniuloganalyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LogAnalyzerApplication {

	public static void main(String[] args) {
		SpringApplication.run(LogAnalyzerApplication.class, args);
	}

}
