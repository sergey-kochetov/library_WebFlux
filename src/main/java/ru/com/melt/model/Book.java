package ru.com.melt.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Document(collection = "books")
@ToString(exclude = "id")
public class Book {

    @Id
    private String id;

    @NonNull
    private String title;

    @DBRef
    private Genre genre;

    @DBRef
    private Set<Author> authors;

    @DBRef
    private Set<Comment> comments;

    public Book(String title, Genre genre, Set<Author> authors) {
        this.title = title;
        this.genre = genre;
        this.authors = authors;
    }

    public void addAuthor(Author author) {
        authors.add(author);
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }
}
