import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Set;

public class DataExchange implements Serializable {
    private static final long serialVersionUID = 1L;

    // receive from the client
    private String category;
    private char character;
    private String playOrQuit;

    // send to the client
    private boolean letterFound;
    private int remainingGuesses;
    private int numToGuess;
    private int numGuessed;
    private Set<Integer> indices;
//    private int gameWins;


    public DataExchange(String category, char character) {
        this.category = category;
        this.character = character;
    }

    public DataExchange(boolean letterFound, int remainingGuesses, int numToGuess, int numGuessed, Set<Integer> indices){
        this.letterFound = letterFound;
        this.remainingGuesses = remainingGuesses;
        this.numToGuess = numToGuess;
        this.numGuessed = numGuessed;
        this.indices = indices;
    }

    public DataExchange(String playOrQuit){
        this.playOrQuit = playOrQuit;
    }

    // receive/send methods
    public void sendMessage(ObjectOutputStream out) {
        try {
            out.reset(); // Reset the stream to clear any internal caches of written objects
            out.writeObject(this);
            out.flush(); // Flush the stream to ensure all data is sent
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

    // getters for server
    public String getCategory() {
        return category;
    }

    public char getChar() { return character; }
    public String getPlayOrQuit() { return playOrQuit; }

    // getters for the client
    public boolean isLetterFound(){ return letterFound; }
    public int getRemainingGuesses(){
        return remainingGuesses;
    }

    public int getNumToGuess(){
        return numToGuess;
    }

    public int getNumGuessed(){
        return numGuessed;
    }

    public Set<Integer> getIndices() { return indices; }



    //  setters for server
    public void setLetterFound(boolean letterFound) {this.letterFound = letterFound;}

    public void setRemainingGuesses(int remainingGuesses) {this.remainingGuesses = remainingGuesses;}

    public void setNumToGuess(int numToGuess) {this.numToGuess = numToGuess;}

    public void setNumGuessed(int numGuessed) {this.numGuessed = numGuessed;}

    public void setIndices(Set<Integer> indices) {this.indices = indices;}
    public void setPlayOrQuit(String playOrQuit){ this.playOrQuit = playOrQuit; }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setCharacter(char character) {
        this.character = character;
    }

//    public int getGameResult() {
//        return gameWins;
//    }
//
//    public void setGameResult(int gameResult) {
//        this.gameWins = gameResult;
//    }

}
