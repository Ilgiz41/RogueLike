package view;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;
import inputreader.UserInput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class InputReaderPanel extends MyPanel {
    private InteractivePanelString interactivePanelString;
    private List<GameEvent.ColoredText> message;
    private Screen screen;
    private Terminal terminal;
    private TerminalPosition panelPosition;
    private TerminalSize panelSize;
    private boolean isActive;
    private boolean positionIsCenter;

    public InputReaderPanel(Screen screen, Terminal terminal) {
        super(screen, terminal);
        this.screen = screen;
        this.terminal = terminal;
        message = new ArrayList<>();
        resizeListener();
    }

    private void resizeListener() {
        terminal.addResizeListener((terminal, newSize) -> {
            if (positionIsCenter && isActive) {
                centred();
            }
            if (isActive) {
                showPanel();
            }
        });
    }

    public void showPanel() {
        isActive = true;
        textBuffer = screen.newTextGraphics();
        clearPanel(true);
        drawPanel();
        showLabel();
        drawMessage();
        drawInputString();
        try {
            screen.refresh();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void drawPanel() {
        textBuffer.drawRectangle(panelPosition, this.panelSize, TextCharacter.fromCharacter(' ', themeStyle.foreground(), themeStyle.foreground())[0]);
        textBuffer.fillRectangle(new TerminalPosition(panelPosition.getColumn() + 1, panelPosition.getRow() + 1), new TerminalSize(this.panelSize.getColumns() - 2, panelSize.getRows() - 2), TextCharacter.fromCharacter(' ', themeStyle.background(), themeStyle.background())[0]);
        Print.drawRectangle(textBuffer,themeStyle.foreground(), TextColor.ANSI.DEFAULT, 0, 0, screen.getTerminalSize().getColumns(), screen.getTerminalSize().getRows());
    }

    private void showLabel() {
        textBuffer.setForegroundColor(topLabel.getColor());
        textBuffer.setBackgroundColor(topLabel.getBackgroundColor());
        textBuffer.putString(new TerminalPosition(panelPosition.getColumn() + (panelSize.getColumns() - this.topLabel.getText().length()) / 2, panelPosition.getRow()), this.topLabel.getText());
    }

    private void drawInputString() {
        int posX = panelPosition.getColumn() + panelSize.getColumns() / 2 - interactivePanelString.getText().length() / 2;
        int posY = panelPosition.getRow() + message.size() + 1;
        addBackgroundColorToInputString();
        textBuffer.setBackgroundColor(TextColor.ANSI.WHITE);
        textBuffer.setModifiers(EnumSet.of(SGR.UNDERLINE));
        textBuffer.setForegroundColor(TextColor.ANSI.BLUE);
        textBuffer.setBackgroundColor(TextColor.ANSI.WHITE);
        textBuffer.putString(posX, posY, interactivePanelString.getText());
    }

    private void addBackgroundColorToInputString(){
        int posX = panelPosition.getColumn() + panelSize.getColumns() / 2 - interactivePanelString.getMaxStringSize() / 2;
        int posY = panelPosition.getRow() + message.size() + 1;
        StringBuilder repeat = new StringBuilder();
        repeat.repeat(' ', interactivePanelString.getMaxStringSize());
        textBuffer.setModifiers(EnumSet.of(SGR.CIRCLED));
        textBuffer.setBackgroundColor(TextColor.ANSI.WHITE);
        textBuffer.putString(posX, posY, repeat.toString());
    }

    public void addInteractiveStringToPanel(InteractivePanelString string) {
        this.interactivePanelString = string;
    }

    public void readInput() {
        KeyStroke keyStroke = new UserInput(terminal).getInput();
        switch (keyStroke.getKeyType()) {
            case Enter -> {
                if (interactivePanelString.getText().length() >= 5) {
                    interactivePanelString.run();
                }
            }
            case Backspace -> {
                if (!interactivePanelString.getText().isEmpty()) {
                    interactivePanelString.setText(interactivePanelString.getText().substring(0, interactivePanelString.getText().length() - 1));
                }
            }
            default -> {
                if (keyStroke.getCharacter() != null) {
                    char sym = keyStroke.getCharacter();
                    if (interactivePanelString.getText().length() < interactivePanelString.getMaxStringSize() && checkCharacter(sym)) {
                        interactivePanelString.setText(interactivePanelString.getText() + sym);
                    }
                }
            }
        }
    }

    private void drawMessage() {
            int posX = panelPosition.getColumn() + 2;
            int posY = panelPosition.getRow() + 1;
            for (GameEvent.ColoredText text : message){
                textBuffer.setForegroundColor(text.getColor());
                textBuffer.setBackgroundColor(text.getBackgroundColor());
                textBuffer.putString(posX, posY, text.getText());
                posY++;
            }
    }

    private TerminalSize fixPanelSize(){
        int totalWidth;
        int totalHeight;
        totalWidth = Math.max(panelSize.getColumns(), topLabel.getText().length() + 2);
        totalHeight = Math.max(panelSize.getRows(), message.size() + 3);
        return new TerminalSize(totalWidth, totalHeight);
    }

    public void centred() {
        panelSize = fixPanelSize();
        positionIsCenter = true;
        panelPosition = new TerminalPosition(screen.getTerminalSize().getColumns() / 2 - panelSize.getColumns() / 2, screen.getTerminalSize().getRows() / 2 - panelSize.getRows() / 2);
    }

    public void clearPanel(boolean isActive) {
        this.isActive = isActive;
        textBuffer.fillRectangle(new TerminalPosition(0, 0), new TerminalSize(screen.getTerminalSize().getColumns(), screen.getTerminalSize().getRows()), TextCharacter.fromCharacter(' ', TextColor.ANSI.DEFAULT, TextColor.ANSI.DEFAULT)[0]);
    }

    public boolean checkCharacter(char ch) {
        return (ch >= 65 && ch <= 90) || (ch >= 97 && ch <= 122) || ch == 32;
    }

    public void setPanelSize(TerminalSize panelSize) {
        this.panelSize = panelSize;
        this.panelSize = fixPanelSize();
    }

    public void addMessage(GameEvent.ColoredText message) {
        this.message.add(message);
    }

    public String getInputString() {
        return interactivePanelString.getText();
    }
}
