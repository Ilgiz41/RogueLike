package view;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;
import inputreader.UserInput;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyPanel {
    protected MyTheme themeStyle;
    private List<MyButton> myButtonList = new ArrayList<>();
    protected GameEvent.ColoredText topLabel;
    private GameEvent.ColoredText bottomLabel;
    protected TerminalSize panelSize = new TerminalSize(0, 0);
    protected TerminalPosition panelPosition = new TerminalPosition(30, 30);
    protected TextGraphics textBuffer;
    private int currentButton = 0;
    protected UserInput userInput;
    protected Screen screen;
    protected Terminal terminal;
    private boolean positionIsCenter = false;
    private boolean isActive = false;

    public Plane getPlane() {
        return plane;
    }

    public void setPlane(Plane plane) {
        this.plane = plane;
    }

    public enum Plane {
        VERTICAL, HORIZONTAL
    }

    private Plane plane;

    public MyPanel(Screen screen, Terminal terminal) {
        themeStyle = new MyTheme(TextColor.ANSI.RED, TextColor.ANSI.WHITE);
        topLabel = new GameEvent.ColoredText("My panel", TextColor.ANSI.BLACK, TextColor.ANSI.RED);
        plane = Plane.VERTICAL;
        this.screen = screen;
        this.terminal = terminal;
        resizeListener();
    }

    private void resizeListener() {
        terminal.addResizeListener((terminal, newSize) -> {
            if (positionIsCenter && isActive){
                centredPanel();
            }
            if (isActive){
                showPanel();
            }
        });
    }

    public void showPanel() {
        textBuffer = screen.newTextGraphics();
        drawPanel();
        showLabel();
        if (plane == Plane.HORIZONTAL) {
            showButtonsOnHorizontal();
        } else {
            showButtonsOnVertical();
        }
        try {
            screen.refresh();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void showButtonsOnHorizontal() {
        int posX = panelPosition.getColumn() + 2;
        int buttonsLength = getButtonsLength();
        for (int i = 0;i < myButtonList.size();i++) {
            int availableWidth = panelSize.getColumns() - buttonsLength - 2 * (myButtonList.size());
            int realSpacing = myButtonList.size() > 1 ? availableWidth / (myButtonList.size() - 1) : 0;
            TextColor backgroundColor = i == currentButton ? myButtonList.get(currentButton).actionColor() : myButtonList.get(i).coloredText().getBackgroundColor();
            textBuffer.setForegroundColor(myButtonList.get(i).coloredText().getColor());
            textBuffer.setBackgroundColor(backgroundColor);
            textBuffer.putString(new TerminalPosition(posX, panelPosition.getRow() + 2), myButtonList.get(i).coloredText().getText());
            posX += myButtonList.get(i).coloredText().getText().length() + realSpacing;
        }
    }

    private void showButtonsOnVertical() {
        int posY = panelPosition.getRow() + 2;
        for (int i = 0;i < myButtonList.size();i++){
            TextColor backgroundColor = i == currentButton ? myButtonList.get(currentButton).actionColor() : myButtonList.get(i).coloredText().getBackgroundColor();
            textBuffer.setForegroundColor(myButtonList.get(i).coloredText().getColor());
            textBuffer.setBackgroundColor(backgroundColor);
            textBuffer.putString(new TerminalPosition(panelPosition.getColumn() + (panelSize.getColumns() - myButtonList.get(i).coloredText().getText().length()) / 2, posY), myButtonList.get(i).coloredText().getText());
            posY += 2;
        }
    }

    private void drawPanel() {
        panelSize = panelSize.getColumns() <= 0 || panelSize.getRows() <= 0 ? calculatePanelSize() : panelSize;
        clearPanel(true);
        textBuffer.drawRectangle(panelPosition, panelSize, TextCharacter.fromCharacter(' ', themeStyle.foreground(), themeStyle.foreground())[0]);
        textBuffer.fillRectangle(new TerminalPosition(panelPosition.getColumn() + 1, panelPosition.getRow() + 1), new TerminalSize(panelSize.getColumns() - 2, panelSize.getRows() - 2), TextCharacter.fromCharacter(' ', themeStyle.background(), themeStyle.background())[0]);
        Print.drawRectangle(textBuffer,themeStyle.foreground(), TextColor.ANSI.DEFAULT, 0, 0, screen.getTerminalSize().getColumns(), screen.getTerminalSize().getRows());
    }

    private void showLabel() {
        textBuffer.setForegroundColor(topLabel.getColor());
        textBuffer.setBackgroundColor(topLabel.getBackgroundColor());
        textBuffer.putString(new TerminalPosition(panelPosition.getColumn() + (panelSize.getColumns() - topLabel.getText().length()) / 2, panelPosition.getRow()), topLabel.getText());
        if (bottomLabel != null){
            textBuffer.setForegroundColor(bottomLabel.getColor());
            textBuffer.setBackgroundColor(bottomLabel.getBackgroundColor());
            textBuffer.putString(new TerminalPosition(panelPosition.getColumn() + (panelSize.getColumns() - bottomLabel.getText().length()) / 2, panelPosition.getRow() + panelSize.getRows() - 1), bottomLabel.getText());
        }
    }

    private TerminalSize calculatePanelSize() {
        int totalWidth = 0;
        int totalHeight = 0;
        int maxLabelLength = bottomLabel == null ? topLabel.getText().length() : Math.max(topLabel.getText().length(), bottomLabel.getText().length());
        if (plane == Plane.VERTICAL) {
            for (MyButton button : myButtonList) {
                String temp = button.coloredText().getText();
                totalWidth = Math.max(totalWidth, temp.length());
            }
            totalWidth = Math.max(totalWidth, maxLabelLength);
            totalHeight = 2 * myButtonList.size() + 3;
            return new TerminalSize(totalWidth + 4, totalHeight);
        } else {
            for (MyButton button : myButtonList) {
                String temp = button.coloredText().getText();
                totalWidth += temp.length() + 2;
                totalHeight = 5;
            }
            totalWidth = Math.max(totalWidth, maxLabelLength);
            return new TerminalSize(totalWidth + 2, totalHeight);
        }
    }

    public void userInputForPanel() {
        KeyStroke keyStroke = new UserInput(terminal).getInput();

        switch (keyStroke.getKeyType()) {
            case KeyType.ArrowUp, KeyType.ArrowRight -> {
                currentButton -= 1;
                if (currentButton < 0) {
                    currentButton = myButtonList.size() - 1;
                }
            }
            case KeyType.ArrowDown, KeyType.ArrowLeft -> {
                currentButton += 1;
                if (currentButton >= myButtonList.size()) {
                    currentButton = 0;
                }
            }
            case KeyType.Enter -> {
                myButtonList.get(currentButton).run();
            }
        }
    }

    public void centredPanel() {
        panelSize = calculatePanelSize();
        int centerX = screen.getTerminalSize().getColumns() / 2 - panelSize.getColumns() / 2;
        int centerY = screen.getTerminalSize().getRows() / 2 - panelSize.getRows() / 2;
        panelPosition = new TerminalPosition(centerX, centerY);
        positionIsCenter = true;
    }

    public int getButtonsLength(){
        int totalLength = 0;
        for (MyButton button : myButtonList){
            totalLength += button.coloredText().getText().length();
        }
        return totalLength;
    }

    public void addButton(MyButton button) {
        myButtonList.add(button);
    }

    public void clearPanel(boolean isActive) {
        this.isActive = isActive;
        textBuffer.fillRectangle(new TerminalPosition(0, 0), new TerminalSize(screen.getTerminalSize().getColumns(), screen.getTerminalSize().getRows()), TextCharacter.fromCharacter(' ', TextColor.ANSI.DEFAULT, TextColor.ANSI.DEFAULT)[0]);
        if (!isActive) {
            try {
                screen.refresh();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void setPanelSize(TerminalSize panelSize) {
        this.panelSize = panelSize;
        this.panelSize = calculatePanelSize();
    }

    public void setPanelPosition(TerminalPosition panelPosition) {
        positionIsCenter = false;
        this.panelPosition = panelPosition;
    }

    public TerminalSize getPanelSize() {
        return panelSize;
    }

    public TerminalPosition getPanelPosition() {
        return panelPosition;
    }

    public void setTopLabel(GameEvent.ColoredText topLabel) {
        this.topLabel = topLabel;
    }

    public void setBottomLabel(GameEvent.ColoredText bottomLabel){
        this.bottomLabel = bottomLabel;
    }

    public void setThemeStyle(MyTheme themeStyle) {
        this.themeStyle = themeStyle;
    }
}
