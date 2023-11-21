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
	private Consumer<Serializable> callback;
	
	Client(Consumer<Serializable> call, int serverPort, String serverA){
		callback = call;
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
				String message = in.readObject().toString();
				handleServerMessage(message);
//			callback.accept(message);
			}
			catch(Exception e) {}
		}
	
    }
	
	public void send(String data) {
		
		try {
			out.writeObject(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void handleServerMessage(String message){
		callback.accept(message);
	}


}
