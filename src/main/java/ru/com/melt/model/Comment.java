package ru.com.melt.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Document(collection = "book_comments")
@ToString(exclude = "id")
public class Comment {

    @Id
    private String id;

    @DBRef
    private Customer customer;

    private BookInfo book;

    private String commentText;

    private Date commentDate;

    public Comment() {
        this.commentDate = new Date();
    }

    public Comment(Customer customer, BookInfo book, String commentText) {
        this.customer = customer;
        this.book = book;
        this.commentText = commentText;
        this.commentDate = new Date();
    }
}
