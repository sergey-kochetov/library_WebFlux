package ru.com.melt.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = "name")
@Document(collection = "authors")
@ToString(exclude = "id")
public class Author {

    @Id
    private String id;

    @NonNull
    private String name;

    public Author(String name) {
        this.name = name;
    }
}
