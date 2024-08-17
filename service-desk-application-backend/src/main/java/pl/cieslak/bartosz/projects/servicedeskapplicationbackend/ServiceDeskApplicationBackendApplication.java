package pl.cieslak.bartosz.projects.servicedeskapplicationbackend;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@RequiredArgsConstructor
public class ServiceDeskApplicationBackendApplication
{
	private static ConfigurableApplicationContext ctx;

	public static void shutDown()
	{
		if(ctx != null) ctx.close();
	}

	public static void main(String[] args)
	{
		ctx = SpringApplication.run(ServiceDeskApplicationBackendApplication.class, args);
	}
}
