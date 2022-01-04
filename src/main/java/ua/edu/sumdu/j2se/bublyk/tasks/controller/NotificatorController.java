package ua.edu.sumdu.j2se.bublyk.tasks.controller;

import ua.edu.sumdu.j2se.bublyk.tasks.model.Task;
import ua.edu.sumdu.j2se.bublyk.tasks.notification.Notificator;

public class NotificatorController {
    private final Notificator notificator;

    public NotificatorController(Iterable<Task> taskList) {
        notificator = new Notificator(taskList);
    }

    public void runNotificator() {
        notificator.setDaemon(true);
        notificator.start();
    }

    public void stopNotificator() {
        if (notificator.isAlive()) {
            notificator.stop();
        }
    }

    public void updateNotificator(Iterable<Task> list) {
        notificator.setTasksList(list);
    }

    public void updateNotificatorFrequency(int seconds) {
        notificator.setNotificationFrequency(seconds);
    }

}
