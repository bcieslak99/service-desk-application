package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.configuration.data.validation;

import org.springframework.context.annotation.Configuration;

@Configuration
public class NoteDataValidation
{
    public static final int MINIMUM_LENGTH_OF_NOTE_TITLE = 3;
    public static final int MAXIMUM_LENGTH_OF_NOTE_TITLE = 200;
    public static final int MINIMUM_LENGTH_OF_NOTE_DESCRIPTION = 10;
    public static final int MAXIMUM_LENGTH_OF_NOTE_DESCRIPTION = 1500;
}
