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
    private boolean roundEnded = false;
    private boolean roundResult = false;

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

    // receiving msg
    public static DataExchange receiveMessage(ObjectInputStream in) {
        try {
            return (DataExchange) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error receiving message: " + e.getMessage());
            // Handle exception
            return null;
        }
    }

    public void sendMessage(ObjectOutputStream out) {
        try {
            out.writeObject(this);
        } catch (IOException e) {
            System.out.println("Error sending message: " + e.getMessage());
            // Handle exception
        }
    }

    // getters + setters for client
    public String getCategory() {
        return category;
    }

    public char getChar() { return character; }

    public boolean getRoundEnded() { return roundEnded; }

    public boolean getRoundResult() {return roundResult;}

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
    public void setCategory(String category) {
        this.category = category;
    }

    public void setCharacter(char character) {
        this.character = character;
    }

}
