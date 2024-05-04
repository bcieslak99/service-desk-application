package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.exceptions.categories;

public class CategoryExistsException extends Exception
{
    public CategoryExistsException(String message)
    {
        super(message);
    }
}
