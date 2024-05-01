package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.configuration.data.validation;

import org.springframework.context.annotation.Configuration;

@Configuration
public class TicketActivityDataValidation
{
    public static final int MINIMUM_LENGTH_OF_ACTIVITY_DESCRIPTION = 3;
    public static final int MAXIMUM_LENGTH_OF_ACTIVITY_DESCRIPTION = 1000;
}
