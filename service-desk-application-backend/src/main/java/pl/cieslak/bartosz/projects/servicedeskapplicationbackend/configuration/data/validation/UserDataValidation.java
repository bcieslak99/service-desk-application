package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.configuration.data.validation;

import org.springframework.context.annotation.Configuration;

@Configuration
public class UserDataValidation
{
    public static final int MINIMUM_LENGTH_OF_USER_NAME = 2;
    public static final int MAXIMUM_LENGTH_OF_USER_NAME = 150;
    public static final int MINIMUM_LENGTH_OF_USER_SURNAME = 2;
    public static final int MAXIMUM_LENGTH_OF_USER_SURNAME = 150;
    public static final int MINIMUM_LENGTH_OF_USER_MAIL = 5;
    public static final int MAXIMUM_LENGTH_OF_USER_MAIL = MAXIMUM_LENGTH_OF_USER_NAME + MAXIMUM_LENGTH_OF_USER_SURNAME + 25;
    public static final int MINIMUM_LENGTH_OF_USER_PASSWORD = 8;
    public static final int MAXIMUM_LENGTH_OF_USER_PASSWORD = 120;
    public static final int MAXIMUM_LENGTH_OF_PHONE_NUMBER = 14;
}
