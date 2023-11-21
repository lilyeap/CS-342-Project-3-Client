
import java.io.IOException;

import javafx.application.Application;
import javafx.geometry.Pos;
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
	private Button b1 = new Button("Animals");
	private Button b2 = new Button("U.S. States");
	private Button b3 = new Button("Superheroes");

	private Scene loginScene;
	private Scene gameScene;
	private Scene cateScene;

	private TextField categoryField = new TextField();
	private TextField guessField = new TextField();
	private Label resultLabel = new Label();
	private Button submitButton = new Button("Submit Guess");
	private Stage primaryStage;
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		primaryStage.setTitle("Word Guessing Game - Client");

		createLoginScene();
		createCategoryScene();
		createGameScene();

		primaryStage.setScene(createLoginScene());

		primaryStage.show();

		loginButton.setOnAction(event -> {
			String portPass = portField.getText();
			try {
				int port = Integer.parseInt(portPass);

				if (connectToServer(port)) {
					primaryStage.setScene(createCategoryScene());
				} else {
					showAlert("Connection Failed", "Unable to connect to the server.");
				}
			} catch (NumberFormatException e) {
				showAlert("Invalid Port", "Please enter a valid port number.");
			}
		});
	}

	private Scene createLoginScene() {
		Label portLabel = new Label("Please Enter Port Number to Connect to:");

		VBox loginLayout = new VBox(10); // 10 is the spacing between children
		loginLayout.getChildren().addAll(
				portLabel, portField,
				loginButton
		);

		return new Scene(loginLayout, 500, 400);
	}

	private Scene createCategoryScene() {
		Label categoryLabel = new Label("Select Category:");

		VBox gameLayout = new VBox(10);
		gameLayout.getChildren().addAll(categoryLabel, b1, b2, b3);

		gameLayout.setAlignment(Pos.CENTER);

		b1.setOnAction(event -> handleCategoryButtonClick("Animals"));
		b2.setOnAction(event -> handleCategoryButtonClick("U.S. States"));
		b3.setOnAction(event -> handleCategoryButtonClick("Superheroes"));

		StackPane centerLayout = new StackPane();
		centerLayout.getChildren().add(gameLayout);
		StackPane.setAlignment(gameLayout, Pos.CENTER);

		return new Scene(centerLayout, 500, 400);
	}

	private Scene createGameScene() {
		Label promptLabel = new Label("Enter a letter to guess:");
		TextField letterField = new TextField();
		Button submitButton = new Button("Submit Guess");

		VBox guessLayout = new VBox(10);
		guessLayout.getChildren().addAll(promptLabel, letterField, submitButton);
		guessLayout.setAlignment(Pos.CENTER);

//		submitButton.setOnAction(event -> handleGuessSubmit(letterField.getText()));

		return new Scene(guessLayout, 500, 400);
	}

	private void handleCategoryButtonClick(String selectedCategory) {
		primaryStage.setScene(createGameScene());
	}

	private boolean connectToServer(int port) {
		try {
			socket = new Socket("127.0.0.1", port);
			outputStream = new ObjectOutputStream(socket.getOutputStream());
			inputStream = new ObjectInputStream(socket.getInputStream());
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
			// Handle the connection failure, e.g., show an error message to the user
		}
	}

	private void showAlert(String title, String content) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.showAndWait();
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
