package com.isabelly.tessiebooks.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "books")
public class Book {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
private Integer pages;
    private String title;
    private String author;
    private String coverImageUrl; // opcional, capa
    private String description;
    private Integer publicationYear; 

 private String genre; // dark academia, vitoriano etc

    private String coverUrl;

    
    public Book() {
    }

    public Book(String author, String coverImageUrl, String description, Long id, String title) {
        this.author = author;
        this.coverImageUrl = coverImageUrl;
        this.description = description;
        this.id = id;
        this.title = title;
    }
private String keywords; 
    // getters/setters
 public Integer getPages() {
        return pages;
    }
    public void setPages(Integer pages) {
        this.pages = pages;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

  public void setGenre(String genre) {
    this.genre = genre;
}
public String getGenre() {
    return genre;

}
  public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }
    public Integer getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }

    public String getCoverUrl() {
        return coverImageUrl;
    }
}