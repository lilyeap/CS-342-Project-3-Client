
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
	DataExchange sendData = new DataExchange("none", '0');

	DataExchange receiveStatus = new DataExchange("none", '0');
	private ObjectInputStream inputStream;
	private ObjectOutputStream outputStream;

	private TextField portField = new TextField();
	private Button loginButton = new Button("Login");
	private Button b1 = new Button("Animals");
	private Button b2 = new Button("U.S. States");
	private Button b3 = new Button("Superheroes");
	private TextField ipField = new TextField();
	private Stage primaryStage;
	private Client client;
	char selectedLetter = '\0';
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		primaryStage.setTitle("Word Guessing Game - Client");
		primaryStage.setScene(createLoginScene());

		primaryStage.show();

		loginButton.setOnAction(event -> {
			String portPass = portField.getText();
			try {
				int port = Integer.parseInt(portPass);

				if (connectToServer(port)) {
					primaryStage.setScene(createIntroScene());
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

	private Scene createIntroScene() {
		Label welcomeLabel = new Label("Welcome to the Word Guessing Game!");
		welcomeLabel.setAlignment(Pos.TOP_CENTER);
		welcomeLabel.setStyle("-fx-font-size: 18;"); // Set the font size

		Label instructionsLabel = new Label("Instructions:\n" +
				"Your goal is to guess 3 different words in 3 different categories to win!\n" +
				"First, you pick a category for the word guessing game.\n" +
				"You have a total of six letter guesses, one at a time.\n" +
				"To guess, press a letter and then 'Submit Guess'\n" +
				"A correct guess will tell you where the letter is in the word\n" +
				"A wrong guess will tell you that the letter is not in the word and how many guesses remain.\n" +
				"If you guess the word within 6 letter guesses, you must choose from the two remaining categories.\n" +
				"If you do not guess the word correctly, you may choose any of the three categories for another word.\n" +
				"You may guess at a maximum of three words per category.\n" +
				"If you do not make a correct guess within three attempts, the game is over.\n" +
				"The game is won when you successfully guess one word in each category.\n" +
				"When the game is over, you can either play again or quit.\n");

		instructionsLabel.setAlignment(Pos.CENTER);

		Button startPlayButton = new Button("Start Play");
		Button exitButton = new Button("Exit Game");

		// Set the action for the "Start Play" button
		startPlayButton.setOnAction(event -> {
			primaryStage.setScene(createCategoryScene());
		});

		exitButton.setOnAction(event -> {
			System.exit(0);
		});

		VBox introLayout = new VBox(10, welcomeLabel, instructionsLabel, startPlayButton, exitButton);
		introLayout.setAlignment(Pos.TOP_CENTER);

		return new Scene(introLayout, 600, 400);
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


	private void handleGuessSubmit(char c) {
		sendData.setCharacter(c);
		System.out.println(c + "from inside handle guess1");
		sendData.sendMessage(outputStream);

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
			for (Button button : letterButtons) {
				if (button.getText().equals(String.valueOf(selectedLetter))) {
					button.setDisable(true);
					break;
				}
			}
			System.out.println(selectedLetter);
			handleGuessSubmit(selectedLetter);
			selectedLetter = '\0';
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
		sendData.setCategory(selectedCategory);
		sendData.sendMessage(outputStream);

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







}
