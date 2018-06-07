package org.kysubrse.tigernguyen.japaneseanalysis.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			//System.out.println(getClass().getResource("Form.fxml").getPath());
			Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("org/kysubrse/tigernguyen/japaneseanalysis/gui/Form.fxml"));
			root.getStylesheets().add(Main.class.getResource("application.css").toExternalForm());

			primaryStage.setTitle("Japanese Analysis @Kysubrse.com");
			primaryStage.setScene(new Scene(root));
			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
