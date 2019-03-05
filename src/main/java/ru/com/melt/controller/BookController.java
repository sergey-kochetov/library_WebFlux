package ru.com.melt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.com.melt.mapper.Mapper;
import ru.com.melt.service.LibraryService;

@RestController
public class BookController {

    @Autowired
    private LibraryService libraryService;

    @GetMapping("/all")
    public Flux<BookDto> showAllBooksOnIndexPage() {
        return libraryService.getAllBooks()
                .map(Mapper::mapBookToDto);
    }

    @GetMapping("/comment")
    public Flux<CommentDto> showCommentsForBookId(@RequestParam(name = "id") String id) {
        return libraryService.getAllComments(id)
                .map(Mapper::mapCommentToDto);
    }

    @PutMapping("/edit")
    public Mono<BookDto> showBookForEdit(@RequestParam(name = "id") String id) {
        return libraryService.getBookById(id)
                .map(Mapper::mapBookToDto);
    }

    @PostMapping("/update")
    public Mono<BookDto> updateBook(@RequestParam Mono<BookDto> bookDto) {
        return libraryService
                .updateBook(bookDto.map(Mapper::mapDtoToBook))
                .map(Mapper::mapBookToDto);
    }

    @DeleteMapping("/delete")
    public ResponseEntity deleteBook(@RequestParam(name = "id") String id) {
        Long result = libraryService.deleteBookById(id).block();
        return result > 0 ?
                new ResponseEntity(HttpStatus.OK) :
                new ResponseEntity(HttpStatus.NOT_FOUND);
    }
}
