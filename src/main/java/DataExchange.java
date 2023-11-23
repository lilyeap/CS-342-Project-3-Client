import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class DataExchange implements Serializable {
    private static final long serialVersionUID = 1L;
    private String category;
    private char character;
    private int remainingGuesses;
    private char[] userGuess;
    boolean roundEnded = false;
    boolean roundResult = false;

    public DataExchange(String category, char character) {
        this.category = category;
        this.character = character;
    }

    public DataExchange(int remainingGuesses, char[] userGuess, boolean roundEnded, boolean roundResult){
        this.remainingGuesses = remainingGuesses;
        this.userGuess = userGuess;
        this.roundEnded = roundEnded;
        this.roundResult = roundResult;
    }

    // Method to send this message to a client
    public void sendMessage(ObjectOutputStream out) {
        try {
            out.writeObject(this);
        } catch (IOException e) {
            System.out.println("Error sending message: " + e.getMessage());
            // Handle exception
        }
    }
    public static DataExchange receiveMessage(ObjectInputStream in) {
        try {
            return (DataExchange) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error receiving message: " + e.getMessage());
            // Handle exception
            return null;
        }
    }

    // getters + setters for server
    public String getCategory() {
        return category;
    }

    public char getChar() { return character; }

    public void setRemainingGuesses(int remainingGuesses){
        this.remainingGuesses = remainingGuesses;
    }
    public void setUserGuess(char[] userGuess){
        this.userGuess = userGuess;
    }

    // getters + setters for the client
    public int getRemainingGuesses(){
        return remainingGuesses;
    }
    public char[] getUserGuess(){
        return userGuess;
    }

    public boolean getRoundEnded() {
        return roundEnded;
    }

    public boolean getRoundResult() {
        return roundResult;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setCharacter(char character) {
        this.character = character;
    }

}
