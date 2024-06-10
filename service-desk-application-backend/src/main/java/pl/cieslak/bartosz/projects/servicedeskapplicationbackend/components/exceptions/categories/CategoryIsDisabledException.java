package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.categories;

public class CategoryIsDisabledException extends Exception
{
    public CategoryIsDisabledException(String message)
    {
        super(message);
    }
}
