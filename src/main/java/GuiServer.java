
import java.io.IOException;
import java.util.HashMap;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.control.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GuiServer extends Application{

	private Socket socket;
	private ObjectInputStream inputStream;
	private ObjectOutputStream outputStream;

	private TextField usernameField = new TextField();
	private PasswordField passwordField = new PasswordField();
	private Button loginButton = new Button("Login");

	private Scene loginScene;
	private Scene gameScene;

	private TextField categoryField = new TextField();
	private TextField guessField = new TextField();
	private Label resultLabel = new Label();
	private Button submitButton = new Button("Submit Guess");
	
//	TextField s1,s2,s3,s4, c1;
//	Button serverChoice,clientChoice,b1;
//	HashMap<String, Scene> sceneMap;
//	GridPane grid;
//	HBox buttonBox;
//	VBox clientBox;
//	Scene startScene;
//	BorderPane startPane;
//	Server serverConnection;
//	Client clientConnection;
//
//	ListView<String> listItems, listItems2;
//
//
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
			connectToServer();
			primaryStage.setScene(gameScene);
		});

		submitButton.setOnAction(event -> submitGuess());
	}

	private void createLoginScene() {
		Label usernameLabel = new Label("Server:");
		Label passwordLabel = new Label("Password:");

		VBox loginLayout = new VBox(10); // 10 is the spacing between children
		loginLayout.getChildren().addAll(
				usernameLabel, usernameField,
				passwordLabel, passwordField,
				loginButton
		);

		loginScene = new Scene(loginLayout, 300, 200);
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

		gameScene = new Scene(gameLayout, 300, 200);
	}

	private void connectToServer() {
		try {
			socket = new Socket("localhost", 8888); // Replace with your server IP and port
			outputStream = new ObjectOutputStream(socket.getOutputStream());
			inputStream = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void submitGuess() {
		// implement guess submission logic here ?
	}
//
//	@Override
//	public void start(Stage primaryStage) throws Exception {
//		// TODO Auto-generated method stub
//		primaryStage.setTitle("The Networked Client/Server GUI Example");
//
//		this.serverChoice = new Button("Server");
//		this.serverChoice.setStyle("-fx-pref-width: 300px");
//		this.serverChoice.setStyle("-fx-pref-height: 300px");
//
//		this.serverChoice.setOnAction(e->{ primaryStage.setScene(sceneMap.get("server"));
//											primaryStage.setTitle("This is the Server");
//				serverConnection = new Server(data -> {
//					Platform.runLater(()->{
//						listItems.getItems().add(data.toString());
//					});
//
//				});
//
//		});
//
//
//		this.clientChoice = new Button("Client");
//		this.clientChoice.setStyle("-fx-pref-width: 300px");
//		this.clientChoice.setStyle("-fx-pref-height: 300px");
//
//		this.clientChoice.setOnAction(e-> {primaryStage.setScene(sceneMap.get("client"));
//											primaryStage.setTitle("This is a client");
//											clientConnection = new Client(data->{
//							Platform.runLater(()->{listItems2.getItems().add(data.toString());
//											});
//							});
//
//											clientConnection.start();
//		});
//
//		this.buttonBox = new HBox(400, serverChoice, clientChoice);
//		startPane = new BorderPane();
//		startPane.setPadding(new Insets(70));
//		startPane.setCenter(buttonBox);
//
//		startScene = new Scene(startPane, 800,800);
//
//		listItems = new ListView<String>();
//		listItems2 = new ListView<String>();
//
//		c1 = new TextField();
//		b1 = new Button("Send");
//		b1.setOnAction(e->{clientConnection.send(c1.getText()); c1.clear();});
//
//		sceneMap = new HashMap<String, Scene>();
//
//		sceneMap.put("server",  createServerGui());
//		sceneMap.put("client",  createClientGui());
//
//		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
//            @Override
//            public void handle(WindowEvent t) {
//                Platform.exit();
//                System.exit(0);
//            }
//        });
//
//
//
//		primaryStage.setScene(startScene);
//		primaryStage.show();
//
//	}
//
//	public Scene createServerGui() {
//
//		BorderPane pane = new BorderPane();
//		pane.setPadding(new Insets(70));
//		pane.setStyle("-fx-background-color: coral");
//
//		pane.setCenter(listItems);
//
//		return new Scene(pane, 500, 400);
//
//
//	}
//
//	public Scene createClientGui() {
//
//		clientBox = new VBox(10, c1,b1,listItems2);
//		clientBox.setStyle("-fx-background-color: blue");
//		return new Scene(clientBox, 400, 300);
//
//	}

}
