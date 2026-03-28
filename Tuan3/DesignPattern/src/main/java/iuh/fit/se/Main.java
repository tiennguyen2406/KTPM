package iuh.fit.se;

import iuh.fit.se.book.BookFactory;
import iuh.fit.se.book.SearchByTitle;
import iuh.fit.se.book.SearchContext;
import iuh.fit.se.borrow.BasicBorrow;
import iuh.fit.se.borrow.Borrow;
import iuh.fit.se.borrow.ExtendBorrow;
import iuh.fit.se.borrow.SpecialEdition;
import iuh.fit.se.notify.LibrarySubject;
import iuh.fit.se.notify.User;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        // Singleton + Factory
        Library lib = Library.getInstance();
        lib.addBook(BookFactory.createBook("paper", "Java", "A"));
        lib.addBook(BookFactory.createBook("ebook", "Python", "B"));

        lib.showBooks();

        // Strategy
        SearchContext context = new SearchContext();
        context.setStrategy(new SearchByTitle());
        System.out.println("Search: " + context.execute(lib.getBooks(), "Java"));

        // Observer
        LibrarySubject subject = new LibrarySubject();
        subject.addObserver(new User("Tiến"));
        subject.notifyObservers("Book overdue!");

        // Decorator
        Borrow borrow = new BasicBorrow();
        borrow = new ExtendBorrow(borrow);
        borrow = new SpecialEdition(borrow);

        System.out.println(borrow.getDescription());
        System.out.println("Cost: " + borrow.getCost());
    }
}