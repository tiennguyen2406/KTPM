package iuh.fit.se;

import java.util.*;

public class Task implements Subject{
    private String title;
    private String status;
    private List<Observer> members = new ArrayList<>();

    public Task(String title) {
        this.title = title;
        this.status = "MỚI TẠO (New)";
    }

    public void setStatus(String newStatus) {
        this.status = newStatus;
        // Tự động thông báo cho tất cả thành viên khi trạng thái thay đổi
        notifyObservers("Công việc '" + title + "' chuyển sang trạng thái: " + newStatus);
    }

    @Override
    public void attach(Observer o) {
        members.add(o);
    }

    @Override
    public void detach(Observer o) {
        members.remove(o);
    }

    @Override
    public void notifyObservers(String message) {
        for (Observer member : members) {
            member.update(message);
        }
    }
}
