package com.example.notepad.util;

import org.springframework.stereotype.Component;

@Component 
public class Slugifier {
    public String slug(String input) {
        return input == null ? "" :
               input.trim().toLowerCase().replaceAll("[^a-z0-9]+","-").replaceAll("^-|-$","");
    }
}