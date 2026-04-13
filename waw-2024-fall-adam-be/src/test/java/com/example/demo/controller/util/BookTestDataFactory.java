package com.example.demo.controller.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static java.util.Map.entry;

public class BookTestDataFactory {
    public static Map<String, String> getSampleBookFormData() throws JSONException {
        Map<String, String> result = new HashMap<>(Map.ofEntries(
                entry("title", "The Annotated Turing: A Guided Tour Through Alan Turing's Historic Paper on Computability and the Turing Machine"),
                entry("author", "Charles Petzold"),
                entry("description", "Mathematician Alan Turing invented an imaginary computer."),
                entry("format", "Paperback"),
                entry("numberOfPages", "528"),
                entry("publicationDate", "2017-06-22"),
                entry("publisher", "Vintage Publishing"),
                entry("isbn", "1784703931"),
                entry("editionLanguage", "English"),
                entry("series", "Art of Computer Programming"),
                entry("category", "Other Editions")
        ));
        JSONArray bookCopies = createBookCopies(Map.of(
                3, 4 // {officeId: 3, copyCount: 4}
        ));
        result.put("newBookCopies", bookCopies.toString());
        return result;
    }

    /**
     * Alternative data to `getSampleBookFormData()`
     */
    public static Map<String, String> getAlternativeBookFormData() throws JSONException {
        Map<String, String> result = new HashMap<>(Map.ofEntries(
                entry("title", "Ulysses"),
                entry("author", "James Joyce"),
                entry("description", "Set entirely on one day, 16 June 1904, Ulysses follows Leopold Bloom and Stephen Daedalus as they go about their daily business in Dublin."),
                entry("format", "Paperback"),
                entry("numberOfPages", "783"),
                entry("publicationDate", "1990-06-16"),
                entry("publisher", "Vintage Publishing"),
                entry("isbn", "9780679722762"),
                entry("editionLanguage", "English"),
                entry("series", "Classics"),
                entry("category", "Modernist novel")
        ));
        JSONArray bookCopies = createBookCopies(Map.of(
                3, 4 // {officeId: 3, copyCount: 4}
        ));
        result.put("newBookCopies", bookCopies.toString());
        return result;
    }

    public static JSONArray createBookCopies(Map<Integer, Integer> bookCopiesData) throws JSONException {
        JSONArray bookCopies = new JSONArray();
        for (Map.Entry<Integer, Integer> entry : bookCopiesData.entrySet()) {
            JSONObject bookCopy = new JSONObject();
            bookCopy.put("officeId", entry.getKey());
            bookCopy.put("copyCount", entry.getValue());
            bookCopies.put(bookCopy);
        }
        return bookCopies;
    }
}
