package ua.edu.sumdu.j2se.bublyk.tasks.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import org.apache.log4j.Logger;
import tornadofx.control.DateTimePicker;
import ua.edu.sumdu.j2se.bublyk.tasks.model.*;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MainController {
    private final static String PATH_TO_LIST = "data/tasks.bin";
    private final static int MIN_SPINNER_VALUE = 0;
    private final static int MAX_SPINNER_VALUE = Integer.MAX_VALUE;
    private final static int INIT_SPINNER_VALUE = 0;
    private final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    private NotificatorController notificator;
    private final AbstractTaskList list = TaskListFactory.createTaskList(ListTypes.types.ARRAY);
    private LocalDateTime cachedFromField;
    private LocalDateTime cachedToField;
    private static final Logger logger = Logger.getLogger(MainController.class);

    @FXML
    private TableView<Task> mainTable;
    @FXML
    private TableColumn<Task, String> titleMainColumn;
    @FXML
    private TableColumn<Task, String> timeMainColumn;
    @FXML
    private TableColumn<Task, String> startMainColumn;
    @FXML
    private TableColumn<Task, String> endMainColumn;
    @FXML
    private TableColumn<Task, String> activeMainColumn;
    @FXML
    private TableColumn<Task, String> intervalMainColumn;

    @FXML
    private TextField titleField;
    @FXML
    private Label startTimeLabel;
    @FXML
    private DateTimePicker startTimeField;
    @FXML
    private Label endTimeLabel;
    @FXML
    private DateTimePicker endTimeField;
    @FXML
    private Label intervalLabel;
    @FXML
    private Spinner<Integer> intervalField;

    @FXML
    private ToggleGroup activateGroup;
    @FXML
    private RadioButton activeRadioTrue;
    @FXML
    private RadioButton activeRadioFalse;
    @FXML
    private ToggleGroup repeatGroup;
    @FXML
    private RadioButton repeatRadioTrue;
    @FXML
    private RadioButton repeatRadioFalse;

    @FXML
    private TableView<CalendarTableHelper> calendarTable;
    @FXML
    private TableColumn<CalendarTableHelper, String> titleCalendarColumn;
    @FXML
    private TableColumn<CalendarTableHelper, String> timeCalendarColumn;

    @FXML
    private DateTimePicker fromField;
    @FXML
    private DateTimePicker toField;

    @FXML
    private void initialize() {
        titleMainColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        timeMainColumn.setCellValueFactory(param -> {
            Task task = param.getValue();
            LocalDateTime time = task.getTime();
            return new SimpleObjectProperty<>(time.format(DATE_TIME_FORMATTER));
        });
        startMainColumn.setCellValueFactory(param -> {
            Task task = param.getValue();
            LocalDateTime time = task.getStartTime();
            return new SimpleObjectProperty<>(time.format(DATE_TIME_FORMATTER));
        });
        endMainColumn.setCellValueFactory(param -> {
            Task task = param.getValue();
            LocalDateTime time = task.getEndTime();
            if (task.isRepeated()) {
                return new SimpleObjectProperty<>(time.format(DATE_TIME_FORMATTER));
            } else {
                return new SimpleObjectProperty<>("-");
            }
        });
        activeMainColumn.setCellValueFactory(param -> {
            Task task = param.getValue();
            boolean active = task.isActive();
            String str = active ? "+" : "-";
            return new SimpleObjectProperty<>(str);
        });
        intervalMainColumn.setCellValueFactory(param -> {
            Task task = param.getValue();
            int interval = task.getRepeatInterval();
            String str = interval == 0 ? "-" : Integer.toString(interval);
            return new SimpleObjectProperty<>(str);
        });
        repeatGroup.selectedToggleProperty().addListener((observableValue, toggle, t1) -> {
            if (repeatGroup.getSelectedToggle() == repeatRadioFalse) {
                startTimeLabel.setText("Time:");
                intervalLabel.setVisible(false);
                intervalField.setVisible(false);
                endTimeLabel.setVisible(false);
                endTimeField.setVisible(false);
            } else if (repeatGroup.getSelectedToggle() == repeatRadioTrue) {
                startTimeLabel.setText("Start time:");
                intervalLabel.setVisible(true);
                intervalField.setVisible(true);
                endTimeLabel.setVisible(true);
                endTimeField.setVisible(true);
            }
        });
        intervalField.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(MIN_SPINNER_VALUE, MAX_SPINNER_VALUE, INIT_SPINNER_VALUE));
        titleCalendarColumn.setCellValueFactory(new PropertyValueFactory<>("titles"));
        timeCalendarColumn.setCellValueFactory(param -> {
            CalendarTableHelper helper = param.getValue();
            LocalDateTime time = helper.getTime();
            return new SimpleObjectProperty<>(time.format(DATE_TIME_FORMATTER));
        });
        repeatRadioFalse.setSelected(true);
        dateStyle();
        readingData();
        notificator = new NotificatorController(list);
        notificator.updateNotificatorFrequency(60);
        notificator.runNotificator();
        loadMainTable();
    }

    @FXML
    private void selectColumn() {
        if (mainTable.getSelectionModel().getSelectedItem() != null) {
            Task temp = mainTable.getSelectionModel().getSelectedItem();
            titleField.setText(temp.getTitle());
            if (temp.isActive()) {
                activeRadioTrue.setSelected(true);
            } else {
                activeRadioFalse.setSelected(true);
            }
            if (temp.isRepeated()) {
                repeatRadioTrue.setSelected(true);
                startTimeField.setDateTimeValue(temp.getStartTime());
                endTimeField.setDateTimeValue(temp.getEndTime());
                intervalField.getValueFactory().setValue(temp.getRepeatInterval());
            } else {
                repeatRadioFalse.setSelected(true);
                startTimeField.setDateTimeValue(temp.getTime());
            }
        }
    }

    @FXML
    private void addButtonAction() {
        Task temp;
        if (activateGroup.getSelectedToggle() != null) {
            try {
                if (!textFieldIsEmpty(titleField)) {
                    if (repeatRadioTrue.isSelected()) {
                        temp = new Task(titleField.getText(), startTimeField.getDateTimeValue(), endTimeField.getDateTimeValue(), intervalField.getValue());
                    } else if (repeatRadioFalse.isSelected()) {
                        temp = new Task(titleField.getText(), startTimeField.getDateTimeValue());
                    } else {
                        throw new IllegalArgumentException("Select whether the task is repeated.");
                    }
                    temp.setActive(activeRadioTrue.isSelected());
                    list.add(temp);
                    unselectColumn();
                    notificator.updateNotificator(list);
                    loadMainTable();
                } else {
                    throw new IllegalArgumentException("Title filed must be filled in.");
                }
            } catch (IllegalArgumentException e) {
                showError(e);
                logger.error(e);
            }
        } else {
            showError("Unable to add task.", "Please, select an activity status for the task.");
        }
    }

    @FXML
    private void editButtonAction() {
        if (mainTable.getSelectionModel().getSelectedItem() != null) {
            Task temp = mainTable.getSelectionModel().getSelectedItem();
            if (activateGroup.getSelectedToggle() != null) {
                try {
                    if (!textFieldIsEmpty(titleField)) {
                        temp.setTitle(titleField.getText());
                        if (repeatRadioTrue.isSelected()) {
                            temp.setTime(startTimeField.getDateTimeValue(), endTimeField.getDateTimeValue(), intervalField.getValue());
                        } else {
                            temp.setTime(startTimeField.getDateTimeValue());
                        }
                        temp.setActive(activeRadioTrue.isSelected());
                        unselectColumn();
                        notificator.updateNotificator(list);
                        mainTable.refresh();
                    } else {
                        throw new IllegalArgumentException("Title filed must be filled in.");
                    }
                } catch (IllegalArgumentException e) {
                    showError(e);
                    logger.error(e);
                }
            } else {
                showError("Unable to edit task.", "Please, select an activity status for the task.");
            }
        } else {
            showError("Unable to edit task.", "Please, select a row to edit.");
        }
    }

    @FXML
    private void removeButtonAction() {
        if (mainTable.getSelectionModel().getSelectedItem() != null) {
            Task temp = mainTable.getSelectionModel().getSelectedItem();
            list.remove(temp);
            unselectColumn();
            notificator.updateNotificator(list);
            loadMainTable();
        } else {
            showError("Unable to remove task.", "Please, select a row to remove.");
        }
    }

    @FXML
    private void resetButtonAction() {
        unselectColumn();
        repeatRadioTrue.setSelected(false);
        repeatRadioFalse.setSelected(true);
        loadMainTable();
    }

    @FXML
    private void calendarButtonAction() {
        try {
            cachedFromField = fromField.getDateTimeValue();
            cachedToField = toField.getDateTimeValue();
            loadCalendarTable();
        } catch (IllegalArgumentException e) {
            showError(e);
            logger.error(e);
        }
    }

    @FXML
    private void refreshButtonAction() {
        calendarTable.getSelectionModel().clearSelection();
        if (cachedFromField != null && cachedToField != null) {
            fromField.setDateTimeValue(cachedFromField);
            toField.setDateTimeValue(cachedToField);
            loadCalendarTable();
        }
    }

    private void loadMainTable() {
        ObservableList<Task> taskList = FXCollections.observableArrayList();
        for (Task temp : list) {
            taskList.add(temp);
        }
        mainTable.setItems(taskList);
    }

    private void loadCalendarTable() {
        SortedMap<LocalDateTime, Set<Task>> map = Tasks.calendar(list, cachedFromField, cachedToField);
        List<CalendarTableHelper> calendarTableHelperList = new ArrayList<>(map.size());
        for (Map.Entry<LocalDateTime, Set<Task>> entry : map.entrySet()) {
            calendarTableHelperList.add(new CalendarTableHelper(entry.getKey(), entry.getValue()));
        }
        ObservableList<CalendarTableHelper> calendar = FXCollections.observableList(calendarTableHelperList);
        calendarTable.setItems(calendar);
    }

    protected static class CalendarTableHelper {
        private LocalDateTime time;
        private String titles;
        private Set<Task> tasks;

        public CalendarTableHelper(LocalDateTime time, Set<Task> tasks) {
            this.time = time;
            this.tasks = tasks;
            transformToString();
        }

        private void transformToString() {
            StringBuilder str = new StringBuilder();
            for (Task temp : tasks) {
                str.append(temp.getTitle()).append("\n");
            }
            titles = str.toString();
        }

        public void setTime(LocalDateTime time) {
            this.time = time;
        }

        public void setTitles(String titles) {
            this.titles = titles;
        }

        public void setTasks(Set<Task> tasks) {
            this.tasks = tasks;
        }

        public LocalDateTime getTime() {
            return time;
        }

        public String getTitles() {
            return titles;
        }

        public Set<Task> getTasks() {
            return tasks;
        }
    }

    private void unselectColumn() {
        mainTable.getSelectionModel().clearSelection();
        titleField.clear();
        startTimeField.setDateTimeValue(null);
        endTimeField.setDateTimeValue(null);
        intervalField.getValueFactory().setValue(INIT_SPINNER_VALUE);
        activeRadioTrue.setSelected(false);
        activeRadioFalse.setSelected(false);
    }

    private void showError(String headerMessage, String contentMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(titleField.getScene().getWindow());
        alert.setTitle("Error");
        alert.setHeaderText(headerMessage);
        alert.setContentText(contentMessage);
        alert.showAndWait();
    }

    private void showError(Exception exception) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(titleField.getScene().getWindow());
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(exception.getMessage());
        alert.showAndWait();
    }

    private void showError(String headerMessage, Exception exception) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(titleField.getScene().getWindow());
        alert.setTitle("Error");
        alert.setHeaderText(headerMessage);
        Label label = new Label("Stack Trace:");
        VBox alertContent = new VBox();
        TextArea textArea = new TextArea();
        String contentMessage;
        try (StringWriter sw = new StringWriter();
              PrintWriter pw = new PrintWriter(sw)) {
            exception.printStackTrace(pw);
            contentMessage = sw.toString();
            textArea.setText(contentMessage);
        } catch (IOException e) {
            logger.error(e);
        }
        textArea.setEditable(false);
        alertContent.getChildren().addAll(label, textArea);
        alert.getDialogPane().setContent(alertContent);
        alert.showAndWait();
    }

    private void dateStyle() {
        startTimeField.setDateTimeValue(null);
        endTimeField.setDateTimeValue(null);
        toField.setDateTimeValue(null);
        fromField.setDateTimeValue(null);
        startTimeField.setFormat("dd.MM.yyyy HH:mm:ss");
        endTimeField.setFormat("dd.MM.yyyy HH:mm:ss");
        toField.setFormat("dd.MM.yyyy HH:mm:ss");
        fromField.setFormat("dd.MM.yyyy HH:mm:ss");
    }

    private void readingData() {
        Path path = Paths.get(PATH_TO_LIST);
        if (Files.notExists(path)) {
            try {
                if (Files.notExists(path.getParent())) {
                    Files.createDirectory(path.getParent());
                }
                Files.createFile(path);
            } catch (IOException e) {
                logger.error(e);
                showError("Unsuccessful reading from file \"" + path + "\".", e);
            }
        } else {
            TaskIO.readBinary(list, path.toFile());
        }
    }

    public void writingData() {
        Path path = Paths.get(PATH_TO_LIST);
        if (Files.notExists(path)) {
            try {
                if (Files.notExists(path.getParent())) {
                    Files.createDirectory(path.getParent());
                }
                Files.createFile(path);
            } catch (IOException e) {
                logger.error(e);
                showError("Unsuccessful writing to file \"" + path + "\".", e);
                System.out.println("Unsuccessful writing to file \"" + path + "\".");
            }
        }
        TaskIO.writeBinary(list, path.toFile());
    }

    private boolean textFieldIsEmpty(TextField textField) {
        if (textField.getText() == null) {
            return true;
        }
        else {
            return textField.getText().isEmpty();
        }
    }
}
