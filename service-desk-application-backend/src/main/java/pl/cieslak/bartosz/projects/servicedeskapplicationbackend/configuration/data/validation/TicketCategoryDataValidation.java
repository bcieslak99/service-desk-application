package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.configuration.data.validation;

import org.springframework.context.annotation.Configuration;

@Configuration
public class TicketCategoryDataValidation
{
    public static final int MINIMUM_LENGTH_OF_CATEGORY_NAME = 3;
    public static final int MAXIMUM_LENGTH_OF_CATEGORY_NAME = 300;
    public static final int MINIMUM_LENGTH_OF_CATEGORY_DESCRIPTION = 5;
    public static final int MAXIMUM_LENGTH_OF_CATEGORY_DESCRIPTION = 1000;
}
