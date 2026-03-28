package iuh.fit.se.book;

import java.util.*;

public class SearchByAuthor implements SearchStrategy {
    public List<Book> search(List<Book> books, String keyword) {
        List<Book> result = new ArrayList<>();
        for (Book b : books) {
            if (b.author.contains(keyword)) {
                result.add(b);
            }
        }
        return result;
    }
}
