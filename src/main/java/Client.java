import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;

public class Client extends Thread{
	int port;
	Socket socketClient;
	ObjectOutputStream out;
	ObjectInputStream in;

	Consumer<Serializable> callback;

	Client(int serverPort, Consumer<Serializable> call){
		port = serverPort;
		callback = call;
	}

	public void run() {
		try {
			socketClient= new Socket("127.0.0.1", port);
			out = new ObjectOutputStream(socketClient.getOutputStream());
			in = new ObjectInputStream(socketClient.getInputStream());
			socketClient.setTcpNoDelay(true);
		} catch(Exception e) {
			System.out.println("couldnt connect: " + e);
		}

		while(true) {
			try {

			}
			catch(Exception e) {break;}
		}

    }

	public boolean isConnected() {
		return ((socketClient != null) && (!socketClient.isClosed()));
	}

	public ObjectOutputStream getOut(){
		return out;
	}

	public ObjectInputStream getIn(){
		return in;
	}



}
