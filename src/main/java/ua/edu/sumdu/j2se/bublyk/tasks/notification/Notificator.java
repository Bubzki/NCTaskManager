package ua.edu.sumdu.j2se.bublyk.tasks.notification;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import ua.edu.sumdu.j2se.bublyk.tasks.model.Task;
import ua.edu.sumdu.j2se.bublyk.tasks.model.Tasks;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;

public class Notificator extends Thread {
    private Iterable<Task> tasksList;
    private final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final static int DEFAULT_NOTIFICATION_FREQUENCY = 30;
    private int notificationFrequency ;

    public Notificator(Iterable<Task> tasksList) {
        this.tasksList = tasksList;
        notificationFrequency = DEFAULT_NOTIFICATION_FREQUENCY;
    }

    private Map.Entry<LocalDateTime, Set<Task>> notifyTask(int seconds) {
        for (Map.Entry<LocalDateTime, Set<Task>> entry : Tasks.calendar(tasksList, LocalDateTime.now(), LocalDateTime.MAX).entrySet()) {
            if (entry.getKey().isEqual(LocalDateTime.now().plusSeconds(seconds).withNano(0))) {
                return entry;
            }
        }
        return null;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Map.Entry<LocalDateTime, Set<Task>> entry = notifyTask(notificationFrequency);
                if (entry != null) {
                    Platform.runLater(() -> {
                        showAlert(entry, notificationFrequency);
                    });
                }
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void showAlert(Map.Entry<LocalDateTime, Set<Task>> result, int seconds) {
        StringBuilder message = new StringBuilder();
        String verb;
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notification");
        alert.setHeaderText(null);
        alert.initModality(Modality.NONE);
        if (result.getValue().size() > 1) {
            message.append("Tasks ");
            verb = " are";
        } else {
            message.append("Task ");
            verb = " is";
        }
        for (Task temp : result.getValue()) {
            message.append("\"").append(temp.getTitle()).append("\", ");
        }
        message.delete(message.length() - 2,  message.length())
                .append(verb)
                .append(" scheduled in ")
                .append(seconds).append(" seconds at ")
                .append(result.getKey().format(DATE_TIME_FORMATTER));
        alert.setContentText(message.toString());
        alert.showAndWait();
    }

    public void setNotificationFrequency(int seconds) {
        notificationFrequency = seconds;
    }

    public void setTasksList(Iterable<Task> tasksList) {
        this.tasksList = tasksList;
    }

}
