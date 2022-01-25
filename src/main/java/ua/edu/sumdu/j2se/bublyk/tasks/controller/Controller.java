package ua.edu.sumdu.j2se.bublyk.tasks.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import ua.edu.sumdu.j2se.bublyk.tasks.TaskManager;
import ua.edu.sumdu.j2se.bublyk.tasks.model.AbstractTaskList;
import ua.edu.sumdu.j2se.bublyk.tasks.model.Task;
import ua.edu.sumdu.j2se.bublyk.tasks.view.View;

import java.util.*;

public class Controller extends View {
    protected final Image icon = new Image(Objects.requireNonNull(TaskManager.class.getResource("TaskMangerIcon.png")).toExternalForm());

    private final LoadController loadController = new LoadController(this);
    protected final NotificatorController notificator = new NotificatorController(this);

   @FXML
   protected void initialize() {
       initializeView();
       loadController.readingData();
       notificator.runNotificator();
       loadMainTable();
   }

    protected boolean textFieldIsEmpty(TextField textField) {
        if (textField.getText() == null) {
            return true;
        }
        else {
            return textField.getText().isEmpty();
        }
    }

    public void writingData() {
        loadController.writingData();
    }

    public AbstractTaskList getTaskList() {
       return list;
    }

}
