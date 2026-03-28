package iuh.fit.se.borrow;

public abstract class BorrowDecorator implements Borrow {
    protected Borrow borrow;

    public BorrowDecorator(Borrow borrow) {
        this.borrow = borrow;
    }

    public String getDescription() {
        return borrow.getDescription();
    }

    public double getCost() {
        return borrow.getCost();
    }
}
