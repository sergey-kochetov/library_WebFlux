package ru.com.melt.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Document(collection = "customers")
@ToString(exclude = "id")
public class Customer {

    @Id
    private String id;

    @NonNull
    private String username;

    @DBRef
    private Set<Comment> comments = new HashSet<>();

    public Customer(@NonNull String username) {
        this.username = username;
    }
}
