package iuh.fit.se.borrow;

public class BasicBorrow implements Borrow {
    public String getDescription() {
        return "Borrow book";
    }

    public double getCost() {
        return 0;
    }
}
