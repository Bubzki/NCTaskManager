package ua.edu.sumdu.j2se.bublyk.tasks.notification;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ua.edu.sumdu.j2se.bublyk.tasks.TaskManager;
import ua.edu.sumdu.j2se.bublyk.tasks.model.Task;
import ua.edu.sumdu.j2se.bublyk.tasks.model.Tasks;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Notificator extends Thread {
    private Iterable<Task> tasksList;
    private final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final static int DEFAULT_NOTIFICATION_TIME = 60;
    private int notificationTime;
    private final Image icon;

    public Notificator(Iterable<Task> tasksList, Image icon) {
        this.tasksList = tasksList;
        this.icon = icon;
        notificationTime = DEFAULT_NOTIFICATION_TIME;
    }

    public Notificator(Iterable<Task> tasksList) {
        this.tasksList = tasksList;
        this.icon = new Image(Objects.requireNonNull(TaskManager.class.getResource("TaskMangerIcon.png")).toExternalForm());
        notificationTime = DEFAULT_NOTIFICATION_TIME;
    }

    private Map.Entry<LocalDateTime, Set<Task>> notifyTask(int seconds) {
        for (Map.Entry<LocalDateTime, Set<Task>> entry : Tasks.calendar(tasksList, LocalDateTime.now(), LocalDateTime.now().plusHours(1).plusSeconds(notificationTime)).entrySet()) {
            System.out.println("Time: " + entry.getKey());
            if (entry.getKey().withNano(0).isEqual(LocalDateTime.now().plusSeconds(seconds).withNano(0))) {
                System.out.println("Time in: " + entry.getKey());
                return entry;
            }
        }
        return null;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Map.Entry<LocalDateTime, Set<Task>> entry = notifyTask(notificationTime);
                if (entry != null) {
                    System.out.println("notification");
                    Platform.runLater(() -> {
                        showAlert(entry, notificationTime);
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
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(icon);
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

    public void setNotificationTime(int seconds) {
        notificationTime = seconds;
    }

    public void setTasksList(Iterable<Task> tasksList) {
        this.tasksList = tasksList;
    }

}
