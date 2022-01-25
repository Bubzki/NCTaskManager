package ua.edu.sumdu.j2se.bublyk.tasks.controller;

import ua.edu.sumdu.j2se.bublyk.tasks.model.Task;
import ua.edu.sumdu.j2se.bublyk.tasks.notification.Notificator;

public class NotificatorController {
    private final Notificator notificator;

    protected NotificatorController(Controller controller) {
        notificator = new Notificator(controller.getTaskList(), controller.icon);
    }

    protected void runNotificator() {
        notificator.setDaemon(true);
        notificator.start();
    }

    protected void stopNotificator() {
        if (notificator.isAlive()) {
            notificator.stop();
        }
    }

    public void updateNotificator(Iterable<Task> list) {
        notificator.setTasksList(list);
    }

    protected void updateNotificationTime(int seconds) {
        notificator.setNotificationTime(seconds);
    }

}
