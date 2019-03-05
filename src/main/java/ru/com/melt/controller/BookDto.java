package ru.com.melt.controller;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.com.melt.model.Author;
import ru.com.melt.model.Book;
import ru.com.melt.model.Comment;

import java.util.Set;

@Data
@NoArgsConstructor
public class BookDto {

    private String id;
    private String authors;
    private String title;
    private String genre;
    private Set<Comment> comments;

    public BookDto(Book book) {
        this.id = book.getId();
        this.authors = book.getAuthors().stream()
                .map(Author::getName)
                .reduce((x, y) -> x + ", " + y)
                .orElse("unknown");
        this.title = book.getTitle();
        this.genre = (book.getGenre() != null) ? book.getGenre().getGenreName() : "";
        this.comments = book.getComments();
    }
}
