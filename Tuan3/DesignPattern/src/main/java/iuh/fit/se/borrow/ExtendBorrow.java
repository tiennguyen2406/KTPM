package iuh.fit.se.borrow;

public class ExtendBorrow extends BorrowDecorator{
    public ExtendBorrow(Borrow borrow) {
        super(borrow);
    }

    public String getDescription() {
        return super.getDescription() + " + Extend time";
    }

    public double getCost() {
        return super.getCost() + 10;
    }
}
