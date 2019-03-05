package ru.com.melt.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "bookInfo")
public class BookInfo {

    @Id
    private String id;

    private String title;

    public BookInfo(Book book) {
        this.id = book.getId();
        this.title = book.getTitle();
    }
}
