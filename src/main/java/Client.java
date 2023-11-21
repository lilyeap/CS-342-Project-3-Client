import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;

public class Client extends Thread{
	String serverAddress;
	int port;
	Socket socketClient;
	ObjectOutputStream out;
	ObjectInputStream in;
	int remainingGuesses;
	char[] userGuess;
	boolean roundEnded;
	boolean roundResult;

	Client(String serverA, int serverPort){
		port = serverPort;
		serverAddress = serverA;
	}

	public void run() {
		try {
			socketClient= new Socket(serverAddress, port);
			out = new ObjectOutputStream(socketClient.getOutputStream());
			in = new ObjectInputStream(socketClient.getInputStream());
			socketClient.setTcpNoDelay(true);
		} catch(Exception e) {}

		while(true) {
			try {
				// read in stats of the game
//				DataExchange dataExchange = (DataExchange) in.readObject();
//				handleServerMessage(dataExchange);

				// idk if we need to check this here
				// prob need to check it in the guiclient?
//				if (dataExchange.getRoundEnded()){
//					if (dataExchange.getRoundResult()){
						// player won, change ui
//					} else {
						// player lost, change ui
//					}
//				}

				// need to add to dataexchange
				// s.t. we need to check whole game ended or not
			}
			catch(Exception e) {break;}
		}

    }

	public boolean isConnected() {
		return socketClient != null && !socketClient.isClosed();
	}

	public ObjectOutputStream getOut(){
		return out;
	}

	public ObjectInputStream getIn(){
		return in;
	}


	private void handleServerMessage(DataExchange dataExchange){
		remainingGuesses = dataExchange.getRemainingGuesses();
		userGuess = dataExchange.getUserGuess();
		roundEnded = dataExchange.getRoundEnded();
		roundResult = dataExchange.getRoundResult();
	}

	public int getRemainingGuesses(){
		return remainingGuesses;
	}
	public char[] getUserGuess(){
		return userGuess;
	}


}
