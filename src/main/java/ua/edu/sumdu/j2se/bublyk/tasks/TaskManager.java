package ua.edu.sumdu.j2se.bublyk.tasks;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.log4j.PropertyConfigurator;
import ua.edu.sumdu.j2se.bublyk.tasks.controller.Controller;

import java.util.Objects;

public class TaskManager extends Application {
	@Override
	public void start(Stage stage) throws Exception {
		PropertyConfigurator.configure(TaskManager.class.getResource("log4j.properties"));
		FXMLLoader loader = new FXMLLoader(TaskManager.class.getResource("view.fxml"));
		Scene scene = new Scene(loader.load());
		Controller controller = loader.getController();
		controller.logger.debug("App is running.");
		stage.getIcons().add(new Image(Objects.requireNonNull(TaskManager.class.getResource("TaskMangerIcon.png")).toExternalForm()));
		stage.setResizable(false);
		stage.setTitle("Task Manager");
		stage.setScene(scene);
		stage.show();
		stage.setOnCloseRequest(windowEvent -> {
			controller.writingData();
			controller.logger.debug("App is closed.");
		});
	}

	public static void main(String[] args) {
		launch();
	}
}
