package iuh.fit.se.book;

import java.util.*;
public class SearchContext {
    private SearchStrategy strategy;

    public void setStrategy(SearchStrategy strategy) {
        this.strategy = strategy;
    }

    public List<Book> execute(List<Book> books, String keyword) {
        return strategy.search(books, keyword);
    }
}
