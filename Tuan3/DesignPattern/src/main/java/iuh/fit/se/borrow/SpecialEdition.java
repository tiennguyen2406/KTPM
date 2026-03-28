package iuh.fit.se.borrow;

public class SpecialEdition extends BorrowDecorator {
    public SpecialEdition(Borrow borrow) {
        super(borrow);
    }

    public String getDescription() {
        return super.getDescription() + " + Special Edition";
    }

    public double getCost() {
        return super.getCost() + 20;
    }
}
