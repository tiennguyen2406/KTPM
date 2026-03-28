package iuh.fit.se;

import iuh.fit.se.book.Book;

import java.util.*;

public class Library {
    private static Library instance;
    private List<Book> books;

    private Library() {
        books = new ArrayList<>();
    }

    public static Library getInstance() {
        if (instance == null) {
            instance = new Library();
        }
        return instance;
    }

    public void addBook(Book book) {
        books.add(book);
    }

    public List<Book> getBooks() {
        return books;
    }

    public void showBooks() {
        for (Book b : books) {
            System.out.println(b.getInfo());
        }
    }
}
