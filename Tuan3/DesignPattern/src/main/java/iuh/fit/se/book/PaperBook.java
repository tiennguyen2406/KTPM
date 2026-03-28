package iuh.fit.se.book;

public class PaperBook extends Book {
    public PaperBook(String title, String author) {
        super(title, author);
    }

    public String getInfo() {
        return "PaperBook: " + title + " - " + author;
    }
}
