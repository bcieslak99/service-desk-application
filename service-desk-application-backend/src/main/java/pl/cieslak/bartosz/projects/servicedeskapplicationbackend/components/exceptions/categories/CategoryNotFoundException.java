package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.categories;

public class CategoryNotFoundException extends Exception
{
    public CategoryNotFoundException(String message)
    {
        super(message);
    }
}
