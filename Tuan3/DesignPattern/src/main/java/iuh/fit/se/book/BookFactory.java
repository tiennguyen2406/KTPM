package iuh.fit.se.book;

public class BookFactory {
    public static Book createBook(String type, String title, String author) {
        switch (type.toLowerCase()) {
            case "paper":
                return new PaperBook(title, author);
            case "ebook":
                return new Ebook(title, author);
            case "audio":
                return new AudioBook(title, author);
            default:
                throw new IllegalArgumentException("Invalid book type");
        }
    }
}
