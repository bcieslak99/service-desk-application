package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.configuration.data.validation;

import org.springframework.context.annotation.Configuration;

@Configuration
public class GroupDataValidation
{
    public static final int MINIMUM_LENGTH_OF_GROUP_NAME = 5;
    public static final int MAXIMUM_LENGTH_OF_GROUP_NAME = 50;
    public static final int MINIMUM_LENGTH_OF_GROUP_DESCRIPTION = 5;
    public static final int MAXIMUM_LENGTH_OF_GROUP_DESCRIPTION = 1000;
}
