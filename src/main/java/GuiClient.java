
import java.io.IOException;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;

public class GuiClient extends Application{
	DataExchange sendData = new DataExchange("none", '0');
	DataExchange receiveStatus = new DataExchange("none", '0');

	private Text remainingGuessesText = new Text("6");
	private Text wordStateText = new Text();
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
	private Map<String, Integer> categoryRetries = new HashMap<>();
	int lostAni = 0;
	int lostUS = 0;
	int lostSuper = 0;
	int consecLoss = 0;

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
		ipLabel.setStyle("-fx-font-weight: bold;");
		ipLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #6A1B9A;");

		ipField.setPromptText("127.0.0.1");
		ipField.setStyle("-fx-border-color: #BDBDBD; -fx-border-radius: 3;");

		Label portLabel = new Label("Please Enter Port Number to Connect to:");
		portLabel.setStyle("-fx-font-weight: bold;");
		portLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #6A1B9A;");

		loginButton.setStyle("-fx-background-color: #C8A2C8; -fx-text-fill: white; -fx-border-radius: 5;");
		loginButton.setOnMouseEntered(e -> loginButton.setStyle("-fx-background-color: #B291B2; -fx-text-fill: white; -fx-border-radius: 5;"));
		loginButton.setOnMouseExited(e -> loginButton.setStyle("-fx-background-color: #C8A2C8; -fx-text-fill: white; -fx-border-radius: 5;"));

		VBox loginLayout = new VBox(10);
		loginLayout.getChildren().addAll(
				ipLabel, ipField,
				portLabel, portField,
				loginButton
		);
		loginLayout.setAlignment(Pos.CENTER);
		loginLayout.setStyle("-fx-background-color: #E9E0F8;");

		return new Scene(loginLayout, 500, 400);
	}

	private Scene createIntroScene() {
		Label welcomeLabel = new Label("Welcome to the Word Guessing Game!");
		welcomeLabel.setAlignment(Pos.TOP_CENTER);
		welcomeLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

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
		instructionsLabel.setWrapText(true);

		Button startPlayButton = new Button("Start Play");
		startPlayButton.setStyle("-fx-background-color: #8BC34A; -fx-text-fill: white; -fx-border-radius: 5;");
		startPlayButton.setOnMouseEntered(e -> startPlayButton.setStyle("-fx-background-color: #7CB342; -fx-text-fill: white; -fx-border-radius: 5;"));
		startPlayButton.setOnMouseExited(e -> startPlayButton.setStyle("-fx-background-color: #8BC34A; -fx-text-fill: white; -fx-border-radius: 5;"));

		Button exitButton = new Button("Exit Game");
		exitButton.setStyle("-fx-background-color: #E57373; -fx-text-fill: white; -fx-border-radius: 5;");
		exitButton.setOnMouseEntered(e -> exitButton.setStyle("-fx-background-color: #EF5350; -fx-text-fill: white; -fx-border-radius: 5;"));
		exitButton.setOnMouseExited(e -> exitButton.setStyle("-fx-background-color: #E57373; -fx-text-fill: white; -fx-border-radius: 5;"));

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

	// checks if all buttons are disabled
	private boolean areAllButtonsDisabled(Button... buttons) {
		for (Button button : buttons) {
			if (!button.isDisabled()) {
				return false;
			}
		}
		return true;
	}

	// checks if game ended
	private boolean didGameEnd() {
		if (areAllButtonsDisabled(b1, b2, b3)) {
			return true;
		}
		return false;
	}

	// send signal object whether user wants to play again or quit
	private void sendSignal(String signal) {
		DataExchange sendData = new DataExchange(signal);
		sendData.sendMessage(outputStream);
	}

	// play again or quit screen
	private Scene createPlayAgainScene(String winLoss) {
		Label questionLabel = new Label("You " + winLoss + "... Do you want to play again?");
		questionLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #F57C00;");
		questionLabel.setAlignment(Pos.CENTER);

		Button playAgainButton = new Button("Play Again");
		playQuitButtons(playAgainButton, true);

		Button quitButton = new Button("Quit");
		playQuitButtons(quitButton, false);

		// if the user wants to play again, reset
		playAgainButton.setOnAction(e -> {
			Platform.runLater(() -> {
				b1.setDisable(false);
				b2.setDisable(false);
				b3.setDisable(false);
				categoryRetries.clear();
				lostAni = 0;
				lostUS = 0;
				lostSuper = 0;
				consecLoss = 0;
				sendSignal("PLAY_AGAIN");
				primaryStage.setScene(createCategoryScene());
			});
		});

		// if the user wants to quit
		quitButton.setOnAction(e -> {
			sendSignal("QUIT");
			Platform.exit();
		});

		VBox layout = new VBox(10, questionLabel, playAgainButton, quitButton);
		layout.setAlignment(Pos.CENTER);
		layout.setStyle("-fx-background-color: #FFF3E0;");

		return new Scene(layout, 300, 200);
	}

	// style the play/quit buttons
	private void playQuitButtons(Button button, boolean isPlay) {
		// Soft green for play button
		String playStyle = "-fx-background-color: #FFB74D; -fx-text-fill: white; -fx-border-radius: 5;";
		String hoverPlay = "-fx-background-color: #FFA726; -fx-text-fill: white; -fx-border-radius: 5;";

		// Gentle red for quit button
		String quitStyle = "-fx-background-color: #E57373; -fx-text-fill: white; -fx-border-radius: 5;";
		String hoverQuit = "-fx-background-color: #EF5350; -fx-text-fill: white; -fx-border-radius: 5;";

		button.setStyle(isPlay ? playStyle : quitStyle);

		button.setOnMouseEntered(e -> {
			button.setStyle(isPlay ? hoverPlay : hoverQuit);
		});

		button.setOnMouseExited(e -> {
			button.setStyle(isPlay ? playStyle : quitStyle);
		});
	}


	// choose category screen
	private Scene createCategoryScene() {
		String backgroundStyle = "-fx-background-color: #E6F0FA;";

		if(didGameEnd()){
			Platform.runLater(() -> primaryStage.setScene(createPlayAgainScene("Won")));
		} else if (lostAni == 3 || lostUS == 3 || lostSuper == 3 || consecLoss == 3){
			Platform.runLater(() -> primaryStage.setScene(createPlayAgainScene("Lost")));
		}

		Label categoryLabel = new Label("Select Category:");
		categoryLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
		categoryLabel.setAlignment(Pos.CENTER);

		styleCategoryButton(b1, "Animals");
		styleCategoryButton(b2, "U.S. States");
		styleCategoryButton(b3, "Superheroes");

		VBox gameLayout = new VBox(10);
		gameLayout.getChildren().addAll(categoryLabel, b1, b2, b3);
		gameLayout.setAlignment(Pos.CENTER);
		gameLayout.setStyle(backgroundStyle);

		System.out.println("entered create category scene");

		StackPane centerLayout = new StackPane();
		centerLayout.getChildren().add(gameLayout);
		StackPane.setAlignment(gameLayout, Pos.CENTER);

		return new Scene(centerLayout, 500, 400);
	}

	// styling for category buttons
	private void styleCategoryButton(Button button, String category) {
		String enabledStyle = "-fx-background-color: #4DB6AC; -fx-text-fill: white; -fx-border-radius: 5;";
		String disabledStyle = "-fx-background-color: #757575; -fx-text-fill: white; -fx-border-radius: 5;";

		button.setStyle(button.isDisabled() ? disabledStyle : enabledStyle);

		button.disabledProperty().addListener((obs, wasDisabled, isDisabled) -> {
			button.setStyle(isDisabled ? disabledStyle : enabledStyle);
		});

		button.setOnMouseEntered(e -> {
			if (!button.isDisabled()) {
				button.setStyle("-fx-background-color: #26A69A; -fx-text-fill: white; -fx-border-radius: 5;");
			}
		});
		button.setOnMouseExited(e -> {
			if (!button.isDisabled()) {
				button.setStyle(enabledStyle);
			}
		});

		button.setOnAction(event -> handleCategoryButtonClick(category));
	}

	// update letters known based on indices so that we can update ui
	private void updateWordState(char guessedLetter, Set<Integer> indices) {
		StringBuilder currentWordState = new StringBuilder(wordStateText.getText());
		for (Integer index : indices) {
			currentWordState.setCharAt(index * 2, guessedLetter);
		}
		wordStateText.setText(currentWordState.toString());
		System.out.println(wordStateText.getText());
	}

	// check if user can retry categories
	private boolean canRetryCategories() {
		for (Map.Entry<String, Integer> entry : categoryRetries.entrySet()) {
			if (entry.getValue() <= 2) {
				return true; // player can retry this category
			}
		}
		return false; // player has exhausted retries for all categories
	}

	// disable category buttons if the user cannot play the category anymore
	private void disableButtonsForExhaustedCategories() {
		for (Map.Entry<String, Integer> entry : categoryRetries.entrySet()) {
			if (entry.getValue() >= 2) {
				// disable the button for the category if the player has exhausted retries
				switch (entry.getKey()) {
					case "Animals":
						b1.setDisable(true);
						lostAni += 3;
						break;
					case "U.S. States":
						b2.setDisable(true);
						lostUS += 3;
						break;
					case "Superheroes":
						b3.setDisable(true);
						lostSuper += 3;
						break;
				}
			}
		}
	}

	// disable all categories b/c user won
	private void disableButtonsForExhaustedCategoriesW(String category) {
		switch (category) {
			case "Animals":
				b1.setDisable(true);
				break;
			case "U.S. States":
				b2.setDisable(true);
				break;
			case "Superheroes":
				b3.setDisable(true);
				break;
		}
	}

	// when the user presses submit
	private void handleGuessSubmit(char c){
		// send the character
		DataExchange sendChar = new DataExchange("none", c);
		sendChar.sendMessage(outputStream);

		// receive results
		receiveStatus = DataExchange.receiveMessage(inputStream);
		assert receiveStatus != null;

		// error checking
		System.out.println("Indices: " + receiveStatus.getIndices().toString());
		System.out.println("Remaining Guesses: " + receiveStatus.getRemainingGuesses());
		System.out.println("Number To Guess: " + receiveStatus.getNumToGuess());
		System.out.println("User Character received: " + receiveStatus.getChar());
//		System.out.println("Disabling buttons for category: " + sendData.getCategory());

		// reset remaining guesses text
		remainingGuessesText.setText(Integer.toString(receiveStatus.getRemainingGuesses()));

		// get the indices of the letter
		Set<Integer> indices = receiveStatus.getIndices();
		if (indices != null && !indices.isEmpty()) {
			updateWordState(c, indices);
			StringBuilder message = new StringBuilder("Letter " + c + " was found at position(s): ");
			for (Integer index : indices) {
				message.append(index + 1).append(", ");
			}
			message.delete(message.length() - 2, message.length()); // Remove the trailing comma and space

			displayMessage(message.toString() + " of the word.");
		} else {
			displayMessage("Letter " + c + " was not found in the word.");
		}

		// check if the user won
		int lettersLeftToGuess = receiveStatus.getNumToGuess();
		if (lettersLeftToGuess == 0){
			Platform.runLater(() -> {
				String lossMessage = "Congratulations! You've guessed the word!";
				Runnable sceneChange = () -> {
					if (!didGameEnd()){
						consecLoss = 0;
						disableButtonsForExhaustedCategoriesW(sendData.getCategory());
						primaryStage.setScene(createCategoryScene());
					} else {
						primaryStage.setScene(createCategoryScene());
					}
				};
				showMessageAndWait(lossMessage, sceneChange);
			});
		}

		// check if the user lost
		int remainingGuesses = receiveStatus.getRemainingGuesses();
		if (remainingGuesses == 0){
			Platform.runLater(() -> {
				String lossMessage = "Sorry! You've run out of guesses.";
				Runnable sceneChange = () -> {
					if (!didGameEnd()){
						consecLoss += 1;
						categoryRetries.put(sendData.getCategory(), categoryRetries.getOrDefault(sendData.getCategory(), 0) + 1);
						if (!canRetryCategories()) {
							disableButtonsForExhaustedCategories();
							primaryStage.setScene(createCategoryScene());
						}else {
							primaryStage.setScene(createCategoryScene());
						}
					} else {
						primaryStage.setScene(createCategoryScene());
					}
				};
				showMessageAndWait(lossMessage, sceneChange);

			});
		}
	}

	// initialize the '_'s in the word for the ui
	private void initializeWordState(int wordLength) {
		StringBuilder initialWordState = new StringBuilder();
		for (int i = 0; i < wordLength; i++) {
			initialWordState.append("_ ");
		}

		// remove the trailing space
		initialWordState.deleteCharAt(initialWordState.length() - 1);
		wordStateText.setText(initialWordState.toString());
		System.out.println(wordStateText.getText());
	}

	// game scene
	private Scene createGameScene() {
		// set up screen
		Label promptLabel = new Label("Enter a letter to guess:");
		promptLabel.setStyle("-fx-font-weight: bold;");

		TextField letterField = new TextField();
		letterField.setStyle("-fx-border-color: #BDBDBD; -fx-border-radius: 3;");

		Button submitButton = new Button("Submit Guess");
		submitButton.setStyle("-fx-background-color: #81C784; -fx-text-fill: white; -fx-border-radius: 5;");
		submitButton.setOnMouseEntered(e -> submitButton.setStyle("-fx-background-color: #66BB6A ; -fx-text-fill: white; -fx-border-radius: 5;"));
		submitButton.setOnMouseExited(e -> submitButton.setStyle("-fx-background-color: #81C784; -fx-text-fill: white; -fx-border-radius: 5;"));

		Label guessedLettersLabel = new Label("Guessed Letters: ");
		guessedLettersLabel.setStyle("-fx-font-weight: bold;");

		Text guessedLettersText = new Text();
		guessedLettersText.setStyle("-fx-font-size: 14;");

		Label remainingGuessesLabel = new Label("Remaining Guesses: ");
		remainingGuessesLabel.setStyle("-fx-font-weight: bold;");

		remainingGuessesText.setText("6");
		remainingGuessesText.setStyle("-fx-font-size: 14;");

		List<Character> guessedLetters = new ArrayList<>();

		// receive the number of letters
		receiveStatus = DataExchange.receiveMessage(inputStream);
		assert receiveStatus != null;
		System.out.println(receiveStatus.getNumToGuess());
		initializeWordState(receiveStatus.getNumToGuess());

		// add them to the screen
		VBox guessLayout = new VBox(10);
		guessLayout.getChildren().addAll(
				wordStateText,
				promptLabel,
				letterField,
				submitButton,
				guessedLettersLabel,
				guessedLettersText,
				remainingGuessesLabel,
				remainingGuessesText
		);

		guessLayout.setAlignment(Pos.CENTER);

		// when user presses submit
		submitButton.setOnAction(event -> {
			String text = letterField.getText();

			// check if the text is exactly one character
			if (text.length() == 1 && text.matches("^[a-zA-Z]$")) {
				if (guessedLetters.contains(text.toUpperCase().charAt(0))) {
					showAlert("Invalid guess", "You've already guessed the letter " + text.toUpperCase().charAt(0));
					letterField.clear();
					return;
				}

				guessedLettersText.setText(guessedLettersText.getText() + " " + text.toUpperCase().charAt(0));
				guessedLetters.add(text.toUpperCase().charAt(0));

				new Thread(() -> {
					handleGuessSubmit(text.toUpperCase().charAt(0));
				}).start();
			} else {
				showAlert("Submission error", "Please enter a valid single letter.");
			}

			letterField.clear();
		});

		return new Scene(guessLayout, 500, 400);
	}

	// when the user clicks a category
	private void handleCategoryButtonClick(String selectedCategory) {
		// send message to server
		System.out.println("entered handle category button click");
		sendData.setCategory(selectedCategory);
		sendData.sendMessage(outputStream);
		System.out.println("success sent to server");
		primaryStage.setScene(createGameScene());
	}

	// trying to connect to the server
	private boolean connectToServer(int port) throws InterruptedException {
		client = new Client(port, data -> {
			System.out.println("Received data: " + data);
		});

		client.start();
		Thread.sleep(50);

		if (client.isConnected()) {
			outputStream = client.getOut();
			inputStream = client.getIn();
			return true;
		} else {
			return false;
		}
	}

	// used for error alerts
	private void showAlert(String title, String content) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.showAndWait();
	}

	// used for notifying if the letter was found or not
	private void displayMessage(String message) {
		Platform.runLater(() -> {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Guess Result");
			alert.setHeaderText(null);
			alert.setContentText(message);

			Timeline timeline = new Timeline(new KeyFrame(
					Duration.seconds(3),
					event -> alert.close()
			));
			timeline.setCycleCount(1);
			timeline.play();

			alert.showAndWait();
		});
	}

	// used to show win/loss alert
	private void showMessageAndWait(String message, Runnable postDisplayAction) {
		Platform.runLater(() -> {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Guess Result");
			alert.setHeaderText(null);
			alert.setContentText(message);

			Timeline timeline = new Timeline(new KeyFrame(
					Duration.seconds(3),
					ae -> alert.close()));
			timeline.setOnFinished(event -> {
				if (postDisplayAction != null) {
					postDisplayAction.run();
				}
			});
			timeline.play();

			alert.showAndWait();
		});
	}
}
