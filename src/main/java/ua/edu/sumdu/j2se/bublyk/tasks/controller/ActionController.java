package ua.edu.sumdu.j2se.bublyk.tasks.controller;

import javafx.fxml.FXML;
import ua.edu.sumdu.j2se.bublyk.tasks.model.Task;

public class ActionController extends Controller {
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
                logger.error("Add task error.", e);
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
                    logger.error("Edit task error.", e);
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
            logger.error("Calendar error.", e);
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
}
