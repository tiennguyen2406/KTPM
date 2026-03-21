package iuh.fit.se;

import java.util.*;

public class Stock implements Subject{

    private String symbol;
    private List<Observer> observers = new ArrayList<>();

    public Stock(String symbol) { this.symbol = symbol; }

    public void setPrice(double price) {
        notifyObservers("Cổ phiếu " + symbol + " vừa thay đổi giá sang: " + price);
    }

    @Override
    public void attach(Observer o) { observers.add(o); }
    @Override
    public void detach(Observer o) { observers.remove(o); }
    @Override
    public void notifyObservers(String message) {
        for (Observer o : observers) o.update(message);
    }
}
