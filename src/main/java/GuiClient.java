
import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.control.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Set;

public class GuiClient extends Application{

	DataExchange receiveStatus = new DataExchange("none", '0');
	private ObjectInputStream inputStream;
	ListView<String> listItems, listItems2;
	private ObjectOutputStream outputStream;
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
	private TextField ipField = new TextField();
	private Label resultLabel = new Label();
	private Button submitButton = new Button("Submit Guess");
	private Stage primaryStage;
	private Client client;
	private Socket socket;
	
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
			} catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
	}

	private Scene createLoginScene() {
		Label ipLabel = new Label("Please Enter IP Address:");
		Label portLabel = new Label("Please Enter Port Number to Connect to:");

		VBox loginLayout = new VBox(10); // 10 is the spacing between children
		loginLayout.getChildren().addAll(
				ipLabel, ipField, // Add IP address field
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

	private char selectedLetter = '\0'; // Initialize with a default value

	private Scene createGameScene() {
		Button[] letterButtons = createLetterButtons();
		Button submitButton = new Button("Submit Guess");

		HBox row1 = new HBox(10, letterButtons[0], letterButtons[1], letterButtons[2], letterButtons[3], letterButtons[4], letterButtons[5], letterButtons[6], letterButtons[7], letterButtons[8], letterButtons[9]);
		HBox row2 = new HBox(10, letterButtons[10], letterButtons[11], letterButtons[12], letterButtons[13], letterButtons[14], letterButtons[15], letterButtons[16], letterButtons[17], letterButtons[18], letterButtons[19]);
		HBox row3 = new HBox(10, letterButtons[20], letterButtons[21], letterButtons[22], letterButtons[23], letterButtons[24], letterButtons[25]);

		row1.setAlignment(Pos.CENTER);
		row2.setAlignment(Pos.CENTER);
		row3.setAlignment(Pos.CENTER);

		VBox keyboardLayout = new VBox(10, row1, row2, row3, submitButton);
		keyboardLayout.setAlignment(Pos.BOTTOM_CENTER);

		submitButton.setOnAction(event -> {
			submitGuess(String.valueOf(selectedLetter));

			for (Button button : letterButtons) {
				if (button.getText().equals(String.valueOf(selectedLetter))) {
					button.setDisable(true);
					break;
				}
			}
			selectedLetter = '\0';

			handleGuessSubmit(selectedLetter);
		});

		return new Scene(keyboardLayout, 500, 400);
	}

	private Button[] createLetterButtons() {
		Button[] letterButtons = new Button[26];
		for (int i = 0; i < 26; i++) {
			char letter = (char) ('A' + i);
			letterButtons[i] = new Button(String.valueOf(letter));

			letterButtons[i].setOnAction(event -> {
				selectedLetter = letter;
				for (Button button : letterButtons) {
					button.setStyle("");
				}
				((Button) event.getSource()).setStyle("-fx-background-color: lightgray");
			});
		}
		return letterButtons;
	}

	private void handleCategoryButtonClick(String selectedCategory) {
		// send message to server
		DataExchange sendCategory = new DataExchange(selectedCategory, '0');
		sendCategory.sendMessage(outputStream);

		primaryStage.setScene(createGameScene());
	}

	private boolean connectToServer(int port) throws InterruptedException {
		client = new Client(port, data -> {
			System.out.println("Received data: " + data);
		});

		client.start();
		Thread.sleep(300);

		if (client.isConnected()) {
			outputStream = client.getOut();
			inputStream = client.getIn();
			return true;
		} else {
			return false;
		}
	}

	private void showAlert(String title, String content) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.showAndWait();
	}

	private void submitGuess(String letter) {
		try {
			outputStream.writeObject(letter);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void handleGuessSubmit(char c){
		DataExchange sendChar = new DataExchange("none", c);
		sendChar.sendMessage(outputStream);

		// process guess from server
		receiveStatus = DataExchange.receiveMessage(inputStream);
		assert receiveStatus != null;

		boolean isLetterFound = receiveStatus.isLetterFound(); // is letter found or not
		int remainingGuesses = receiveStatus.getRemainingGuesses(); // user's guesses left
		int numToGuess = receiveStatus.getNumToGuess(); // total letters in the word to guess
		int numGuessed = receiveStatus.getNumGuessed(); // letters that are correctly guessed
		Set<Integer> indices = receiveStatus.getIndices(); // indices of that letter in the string

		System.out.println("received status");


	}



}
