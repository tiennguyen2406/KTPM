package iuh.fit.se.book;

public class Ebook extends Book{
    public Ebook(String title, String author) {
        super(title, author);
    }

    public String getInfo() {
        return "Ebook: " + title + " - " + author;
    }
}
