package ru.com.melt.controller;

import lombok.Data;

@Data
public class CommentDto {

    private String title;
    private String customer;
    private String text;
    private String date;
}
