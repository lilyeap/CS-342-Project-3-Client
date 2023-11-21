
import java.io.IOException;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.control.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GuiClient extends Application{

	private Socket socket;
	private ObjectInputStream inputStream;
	private ObjectOutputStream outputStream;

//	private TextField ipField = new TextField();
	private TextField portField = new TextField();
	private Button loginButton = new Button("Login");

	private Scene loginScene;
	private Scene gameScene;

	private TextField categoryField = new TextField();
	private TextField guessField = new TextField();
	private Label resultLabel = new Label();
	private Button submitButton = new Button("Submit Guess");
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Word Guessing Game - Client");

		createLoginScene();
		createGameScene();

		primaryStage.setScene(loginScene);

		primaryStage.show();

		loginButton.setOnAction(event -> {
//			String serverAddress = ipField.getText(); // Replace with the actual server IP
			String portPass = portField.getText();; // Replace with the actual server port

			try {
				int port = Integer.parseInt(portPass);

				System.out.println(port);
				connectToServer(port);

				primaryStage.setScene(gameScene);
			} catch (NumberFormatException e) {
//				e.printStackTrace();
			}
		});

//		submitButton.setOnAction(event -> submitGuess());
	}

	private void createLoginScene() {
		Label portLabel = new Label("Port:");

		VBox loginLayout = new VBox(10); // 10 is the spacing between children
		loginLayout.getChildren().addAll(
				portLabel, portField,
				loginButton
		);

		loginScene = new Scene(loginLayout, 500, 400);
	}

	private void createGameScene() {
		Label categoryLabel = new Label("Enter Category:");
		Label guessLabel = new Label("Enter Guess:");

		VBox gameLayout = new VBox();
		gameLayout.getChildren().addAll(
				categoryLabel, categoryField,
				guessLabel, guessField,
				submitButton, resultLabel
		);

		gameScene = new Scene(gameLayout, 500, 400);
	}

	private void connectToServer(int port) {
		try {
			socket = new Socket("127.0.0.1", port);
			outputStream = new ObjectOutputStream(socket.getOutputStream());
			inputStream = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
			// Handle the connection failure, e.g., show an error message to the user
		}
	}

//	private void submitGuess() {
//		// Implement guess submission logic here
//		String category = categoryField.getText().toUpperCase();
//		String guess = guessField.getText().toUpperCase();
//		String message = "GUESS:" + category + ":" + guess;
//
//		try {
//			// Send the guess to the server
//			outputStream.writeObject(message);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

}
