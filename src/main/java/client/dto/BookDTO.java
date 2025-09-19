package client.dto;

import java.io.Serializable;


public class BookDTO implements Serializable {
    private int id;
    private String title;
    private AuthorDTO author;
    private CategoryDTO category;
    private StatusDTO status;
    private String coverImagePath;

    public BookDTO() {
    }

    // Constructor using DTO objects
    public BookDTO(int id, String title, AuthorDTO author, CategoryDTO category, StatusDTO status, String coverImagePath) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.category = category;
        this.status = status;
        this.coverImagePath = coverImagePath;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public AuthorDTO getAuthor() {
        return author;
    }

    public void setAuthor(AuthorDTO author) {
        this.author = author;
    }

    public CategoryDTO getCategory() {
        return category;
    }

    public void setCategory(CategoryDTO category) {
        this.category = category;
    }

    public StatusDTO getStatus() {
        return status;
    }

    public void setStatus(StatusDTO status) {
        this.status = status;
    }

    public String getCoverImagePath() {
        return coverImagePath;
    }

    public void setCoverImagePath(String coverImagePath) {
        this.coverImagePath = coverImagePath;
    }
}
