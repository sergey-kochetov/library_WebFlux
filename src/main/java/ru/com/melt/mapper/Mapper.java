package ru.com.melt.mapper;

import ru.com.melt.controller.BookDto;
import ru.com.melt.controller.CommentDto;
import ru.com.melt.model.Author;
import ru.com.melt.model.Book;
import ru.com.melt.model.Comment;
import ru.com.melt.model.Genre;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class Mapper {

    public static Book mapDtoToBook(BookDto bookDto) {
        Genre genre = new Genre(bookDto.getGenre());
        Set<Author> authors = mapAuthors(bookDto.getAuthors());
        return new Book(bookDto.getTitle(), genre, authors);
    }

    public static BookDto mapBookToDto(Book book) {
        BookDto result = new BookDto();
        result.setId(book.getId());
        result.setTitle(book.getTitle());

        if (book.getAuthors() != null) {
            result.setAuthors(book.getAuthors().stream()
                    .map(Author::getName)
                    .reduce((s1, s2) -> s1 + ", " + s2)
                    .orElse("no author"));
        }

        Genre genre = book.getGenre();
        if (genre != null) {
            result.setGenre(genre.getGenreName());
        } else {
            result.setGenre("no genre");
        }
        Set<Comment> comments = book.getComments();
        if (comments != null) {
            comments.stream()
                    .map(Mapper::mapCommentToDto)
                    .collect(Collectors.toSet());
        }
        result.setComments(comments);

        return result;
    }

    public static CommentDto mapCommentToDto(Comment comment) {
        CommentDto result = new CommentDto();
        result.setTitle(comment.getBook().getTitle());
        result.setCustomer(comment.getCustomer().getUsername());
        result.setText(comment.getCommentText());

        SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy", new Locale("ru"));
        result.setDate(format.format(comment.getCommentDate()));

        return result;
    }

    private static Set<Author> mapAuthors(String authors) {
        return Arrays.stream(authors.split(","))
                .map(s -> new Author(s.trim()))
                .collect(Collectors.toSet());
    }

}
