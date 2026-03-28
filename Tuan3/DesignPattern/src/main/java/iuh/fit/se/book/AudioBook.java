package iuh.fit.se.book;

public class AudioBook extends Book{
    public AudioBook(String title, String author) {
        super(title, author);
    }

    public String getInfo() {
        return "AudioBook: " + title + " - " + author;
    }
}
