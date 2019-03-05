package ru.com.melt.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@NoArgsConstructor
@EqualsAndHashCode(of = "genreName")
@Document(collection = "genres")
@ToString(exclude = "id")
public class Genre {

    @Id
    private String id;

    @NonNull
    private String genreName;

    public Genre(String genreName) {
        this.genreName = genreName;
    }
}
