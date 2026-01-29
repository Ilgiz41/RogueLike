package inputreader;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;


public class UserInput {

    private KeyStroke keyStroke;
    private Terminal terminal;

    public UserInput(Terminal terminal){
        this.terminal = terminal;
    }

    public KeyStroke getInput() {
        try {
            keyStroke = terminal.readInput();
            return keyStroke;
        } catch (IOException e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    public char getCharacterFromKeyStroke(KeyStroke keyStroke) {
        if (keyStroke != null && keyStroke.getKeyType() == KeyType.Character) {
            return keyStroke.getCharacter();
        }
        return '\0';
    }

}
