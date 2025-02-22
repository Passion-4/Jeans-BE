package Jeans.Jeans;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class JeansApplication {

	public static void main(String[] args) {
		SpringApplication.run(JeansApplication.class, args);
	}

}
