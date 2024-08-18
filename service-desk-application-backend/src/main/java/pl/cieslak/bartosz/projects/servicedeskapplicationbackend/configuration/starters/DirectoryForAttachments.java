package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.configuration.starters;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class DirectoryForAttachments implements CommandLineRunner
{
    public static final String DIRECTORY_NAME = "/service-desk-attachments";

    @Override
    public void run(String... args) throws Exception
    {
        File directory = new File(DIRECTORY_NAME);

        if(!directory.exists())
            if(directory.mkdir())
                System.out.println("Utworzono katalog na załączniki");
    }
}
