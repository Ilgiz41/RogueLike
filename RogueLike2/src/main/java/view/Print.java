package view;

import Datalayer.GameStatistic;
import Datalayer.SaveGame;
import Datalayer.StatisticManager;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;
import domain.Backpack;
import domain.GameSession;
import domain.Room;
import domain.item.Item;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static Datalayer.StatisticManager.sortStat;
import static view.GameEvent.ColoredText.createColoredText;

public class Print {
    private int columns;
    private int rows;
    private Terminal terminal;
    private Screen screen;
    private TextGraphics textGraphics;
    private GameSession gameSession;

    public Print(Screen screen, Terminal terminal, int columns, int rows, GameSession gameSession) {
        this.gameSession = gameSession;
        this.columns = columns;
        this.rows = rows;
        this.terminal = terminal;
        this.screen = screen;
        textGraphics = screen.newTextGraphics();
        resizeListener();
    }

    private void resizeListener() {
        terminal.addResizeListener((terminal, newSize) -> {
            columns = newSize.getColumns();
            rows = newSize.getRows();
            screen.doResizeIfNecessary();
            drawGame();
        });
    }

    public void drawGame() {
        try {
            if (gameSession.getGameState() != GameSession.GameKa.START_MENU && gameSession.getGameState() != GameSession.GameKa.RECORDS) {
                drawBoard();
                drawHero();
                drawKillFeed();
                drawHeroStats();
            }
            switch (gameSession.getGameState()) {
                case RECORDS -> {
                    drawRecords();
                }
                case GameSession.GameKa.BACKPACK -> {
                    switch (gameSession.getHero().getBackpack().getState()) {
                        case Backpack.BackpackState.SHOW_BACKPACK -> {
                            drawBackpack();
                        }
                        case Backpack.BackpackState.SHOW_DIALOG_MENU_FOR_ITEM -> {
                            drawDialogMenuForBackpack();
                        }
                    }
                }
            }
            screen.refresh();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void drawHero() {
        int heroPosX = ((columns - Room.MAP_WIDTH) / 2) + gameSession.getHero().getCoordinate().getX();
        int heroPosY = ((rows - Room.MAP_HEIGHT) / 2) + gameSession.getHero().getCoordinate().getY();
        textGraphics.setCharacter(new TerminalPosition(heroPosX, heroPosY), gameSession.getHero().getHeroSymbol());
    }

    private void drawKillFeed() {
        int startYPos = (rows - Room.MAP_HEIGHT) / 2 + 1;
        for (GameEvent.TextPart event : gameSession.getGameEvent().getEvents()) {
            int xPos = (columns - Room.MAP_WIDTH - 100) / 2;
            for (GameEvent.ColoredText coloredText : event.getColoredTexts()) {
                textGraphics.setForegroundColor(coloredText.getColor());
                textGraphics.putString(xPos, startYPos, coloredText.getText());
                xPos += coloredText.getText().length();
            }
            startYPos++;
        }
        drawRectangle(textGraphics, TextColor.ANSI.YELLOW, TextColor.ANSI.DEFAULT, (columns - Room.MAP_WIDTH - 100) / 2 - 1, (rows - Room.MAP_HEIGHT) / 2, 41, 21);
    }

    public void drawRecords() {
        clearScreen();
        List<GameStatistic> gameStatistics = StatisticManager.getStat();
        sortStat(gameStatistics);
        int XCenterBoard = columns / 2 - getRecordsWidth(gameStatistics) / 2;
        int recordSize = Math.min(gameStatistics.size(), 15);
        int YCenterBoard = rows / 2 - recordSize / 2;
        //int startY = YCenterBoard;
        for (int i = 0; i < recordSize; i++) {
            textGraphics.setForegroundColor(TextColor.ANSI.YELLOW);
            textGraphics.setBackgroundColor(TextColor.ANSI.WHITE_BRIGHT);
            textGraphics.putString(XCenterBoard, YCenterBoard, String.format("%d. %s", i + 1, gameStatistics.get(i).toString()));
            YCenterBoard += 2;
        }
        //drawRectangle(textGraphics, TextColor.ANSI.YELLOW, TextColor.ANSI.DEFAULT, XCenterBoard - 2, startY - 1, 2 + getRecordsWidth(gameStatistics) + 5, XCenterBoard - 2);
        textGraphics.setBackgroundColor(TextColor.ANSI.BLACK);
    }

    private int getRecordsWidth(List<GameStatistic> gameStatistics) {
        int totalWidth = 0;
        for (GameStatistic gameStatistic : gameStatistics) {
            totalWidth = Math.max(gameStatistic.toString().length(), totalWidth);
        }
        return totalWidth;
    }

    private void drawHeroStats() {
        int posX = columns / 2 + Room.MAP_WIDTH / 2 + 1;
        int posY = rows / 2 - Room.MAP_HEIGHT / 3;
        GameEvent.ColoredText heroWeapon = createColoredText(gameSession.getHero().getHandsType());
        String maxHealth = String.valueOf(gameSession.getHero().getStats().getMaxHealth());
        String currentHealth = String.valueOf(gameSession.getHero().getStats().getCurrentHealth());
        TextColor currentHealthColor = maxHealth.equals(currentHealth) ? TextColor.ANSI.GREEN : TextColor.ANSI.RED;
        drawRectangle(textGraphics, TextColor.ANSI.YELLOW, TextColor.ANSI.DEFAULT, posX - 1, posY - 1, gameSession.getHero().getName().length() < 13 ? 19 : 8 + gameSession.getHero().getName().length(), 11);
        drawColoredString("Name: ", TextColor.ANSI.WHITE, posX, posY);
        drawColoredString(gameSession.getHero().getName(), TextColor.ANSI.BLUE, posX + "Name: ".length(), posY);
        drawColoredString("Health: ", TextColor.ANSI.WHITE, posX, posY + 1);
        drawColoredString(maxHealth, TextColor.ANSI.GREEN, posX + "Health: ".length(), posY + 1);
        drawColoredString(" / ", TextColor.ANSI.WHITE, posX + "Health: ".length() + maxHealth.length(), posY + 1);
        drawColoredString(currentHealth, currentHealthColor, posX + "Health: ".length() + " / ".length() + maxHealth.length(), posY + 1);
        drawColoredString("Agility: ", TextColor.ANSI.WHITE, posX, posY + 2);
        drawColoredString(String.valueOf(gameSession.getHero().getStats().getAgility()), TextColor.ANSI.GREEN, posX + "Agility: ".length(), posY + 2);
        drawColoredString("Strength: ", TextColor.ANSI.WHITE, posX, posY + 3);
        drawColoredString(String.valueOf(gameSession.getHero().getStats().getStrength()), TextColor.ANSI.RED, posX + "Strength: ".length(), posY + 3);
        drawColoredString("Coins: ", TextColor.ANSI.WHITE, posX, posY + 4);
        drawColoredString(String.valueOf(gameSession.getHero().getBackpack().getCoinCount()), TextColor.ANSI.YELLOW, posX + "Coins: ".length(), posY + 4);
        drawColoredString("Hands: ", TextColor.ANSI.WHITE, posX, posY + 5);
        drawColoredString(heroWeapon.getText(), heroWeapon.getColor(), posX + "Hands: ".length(), posY + 5);
        drawColoredString("Steps: ", TextColor.ANSI.WHITE, posX, posY + 6);
        drawColoredString(String.valueOf(gameSession.getHero().getSteps() + 1), TextColor.ANSI.YELLOW, posX + "Steps ".length(), posY + 6);
        drawColoredString("Level - ", TextColor.ANSI.WHITE, posX, posY + 7);
        drawColoredString(String.valueOf(gameSession.getLevel(gameSession.getCurrentIndexLevel()).getLevelNumber()), TextColor.ANSI.CYAN, posX + "Level - ".length(), posY + 7);
        drawColoredString("Kills - ", TextColor.ANSI.WHITE, posX, posY + 8);
        drawColoredString(String.valueOf(gameSession.getHero().getKills()), TextColor.ANSI.CYAN, posX + "Kills - ".length(), posY + 8);
    }

    private void drawColoredString(String text, TextColor color, int posX, int posY) {
        textGraphics.setForegroundColor(color);
        textGraphics.putString(posX, posY, text);
    }

    private void drawDialogMenuForBackpack() {
        GameEvent.ColoredText item = createColoredText(gameSession.getHero().getBackpack().getItems().get(gameSession.getHero().getBackpack().getCurrentItem()).getType());
        String message = "Выберите действие для: ";
        int posX = columns / 2 - (message.length() + item.getText().length()) / 2;
        int posY = 60;
        drawRectangle(textGraphics, item.getColor(), TextColor.ANSI.DEFAULT, posX - 15, posY - 1, 56, 5);
        drawColoredString(message, TextColor.ANSI.WHITE, posX, posY);
        drawColoredString(item.getText(), item.getColor(), posX + message.length(), posY);
        drawColoredString("1 - ", TextColor.ANSI.WHITE, posX - 14, posY + 2);
        drawColoredString("Использовать", TextColor.ANSI.GREEN, posX - 14 + "1 - ".length(), posY + 2);
        drawColoredString("2 - Отмена", TextColor.ANSI.WHITE, posX - 6 + "1 - использовать".length(), posY + 2);
        drawColoredString("3 - ", TextColor.ANSI.WHITE, posX + "1 - использовать 2 - отмена".length(), posY + 2);
        drawColoredString("Выбросить", TextColor.ANSI.RED, posX + "1 - использовать 2 - отмена - 3".length(), posY + 2);
    }

    private void drawBackpack() {
        int posY = 60;
        List<GameEvent.ColoredText> coloredItems = initializeColoredList();
        drawBackpackItemType(posY);
        int posX = getPosXForBackpack(coloredItems);
        drawItemsInBackpack(posX, posY, coloredItems);
        try {
            screen.refresh();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<GameEvent.ColoredText> initializeColoredList() {
        List<GameEvent.ColoredText> coloredItems = new ArrayList<>();
        List<Item> items = gameSession.getHero().getBackpack().getItems();
        for (int i = 0; i < 9; i++) {
            if (items.size() > i && items.get(i).getType() != null) {
                coloredItems.add(createColoredText(items.get(i).getType()));
            } else {
                coloredItems.add(createColoredText("Пусто"));
            }
        }
        return coloredItems;
    }

    private void drawItemsInBackpack(int posX, int posY, List<GameEvent.ColoredText> coloredItem) {
        int numberAboveItem = 0;
        int startPosX = posX;
        int lastPosY = posY;
        for (GameEvent.ColoredText item : coloredItem) {
            int newPosX = calculateNewPosX(item);
            numberAboveItem += 1;
            textGraphics.setForegroundColor(item.getText().equals("Пусто") ? TextColor.ANSI.RED : TextColor.ANSI.GREEN);
            textGraphics.putString(posX + newPosX / 2 - 1, posY - 2, String.valueOf(numberAboveItem));
            String[] temp = item.getText().split(" ");
            if (temp.length > 1) {
                lastPosY = posY + 1;
                for (int i = 0; i < temp.length; i++) {
                    textGraphics.setForegroundColor(item.getColor());
                    textGraphics.putString(posX, posY + i, temp[i]);
                }
            } else {
                textGraphics.setForegroundColor(item.getColor());
                textGraphics.putString(posX, posY, temp[0]);
            }
            posX += newPosX;
        }
        drawRectangle(textGraphics, createColoredText(gameSession.getHero().getBackpack().getBackpackType()).getColor(), TextColor.ANSI.DEFAULT, startPosX - 1, posY - 5, posX - startPosX + 1, lastPosY - posY + 7);
    }

    private int getPosXForBackpack(List<GameEvent.ColoredText> items) {
        int totalWidth = 0;
        for (GameEvent.ColoredText item : items) {
            totalWidth += calculateNewPosX(item);
        }

        return columns / 2 - totalWidth / 2;
    }

    private void drawBackpackItemType(int posY) {
        GameEvent.ColoredText backpackType = createColoredText(gameSession.getHero().getBackpack().getBackpackType());
        textGraphics.setForegroundColor(backpackType.getColor());
        int arrowLeftPosX = columns / 2 - backpackType.getText().length() - 1;
        int arrowRightPosX = backpackType.getText().length() % 2 == 0 ? columns / 2 + backpackType.getText().length() - 2 : columns / 2 + backpackType.getText().length() - 1;
        textGraphics.putString(columns / 2 - backpackType.getText().length() / 2 - 1, posY - 4, backpackType.getText());
        textGraphics.setCharacter(arrowLeftPosX, posY - 4, TextCharacter.fromCharacter(Character.forDigit(gameSession.getHero().getBackpack().getIter() + 1, 10), TextColor.ANSI.WHITE, TextColor.ANSI.DEFAULT)[0]);
        textGraphics.setCharacter(arrowLeftPosX - 1, posY - 4, TextCharacter.fromCharacter('<', TextColor.ANSI.WHITE, TextColor.ANSI.DEFAULT)[0]);
        textGraphics.setCharacter(arrowRightPosX + 1, posY - 4, TextCharacter.fromCharacter('>', TextColor.ANSI.WHITE, TextColor.ANSI.DEFAULT)[0]);
        textGraphics.setCharacter(arrowRightPosX, posY - 4, TextCharacter.fromCharacter('4', TextColor.ANSI.WHITE, TextColor.ANSI.DEFAULT)[0]);
    }

    private int calculateNewPosX(GameEvent.ColoredText item) {
        int maxWordLength = 0;
        String[] temp = item.getText().split(" ");
        for (String word : temp) {
            maxWordLength = Math.max(maxWordLength, word.length());
        }
        return maxWordLength + 1;
    }

    private void drawBoard() {
        int XCenterBoard = (columns - Room.MAP_WIDTH) / 2;
        int YCenterBoard = (rows - Room.MAP_HEIGHT) / 2;

        clearScreen();

        for (int i = 0; i < Room.MAP_HEIGHT; i++) {
            for (int j = 0; j < Room.MAP_WIDTH; j++) {
                textGraphics.setCharacter(new TerminalPosition(XCenterBoard + j, YCenterBoard + i), gameSession.getCell(i, j));
            }
        }
        drawRectangle(textGraphics, TextColor.ANSI.YELLOW, TextColor.ANSI.DEFAULT, XCenterBoard, YCenterBoard, Room.MAP_WIDTH, Room.MAP_HEIGHT);
        drawRectangle(textGraphics, TextColor.ANSI.YELLOW, TextColor.ANSI.DEFAULT, 0, 0, columns, rows);
    }

    private void clearScreen(){
        textGraphics.fillRectangle(new TerminalPosition(0, 0), new TerminalSize(columns, rows), ' ');
    }

    public static void drawRectangle(TextGraphics textGraphics, TextColor textColor, TextColor backgroundColor, int x, int y, int width, int height) {
        /// Верхняя линия
        textGraphics.setCharacter(x, y, TextCharacter.fromCharacter('┌', textColor, backgroundColor)[0]);
        for (int i = 1; i < width - 1; i++) {
            textGraphics.setCharacter(x + i, y, TextCharacter.fromCharacter('─', textColor, backgroundColor)[0]);
        }
        textGraphics.setCharacter(x + width - 1, y, TextCharacter.fromCharacter('┐', textColor, backgroundColor)[0]);

        /// Боковые линии
        for (int i = 1; i < height - 1; i++) {
            textGraphics.setCharacter(x, y + i, TextCharacter.fromCharacter('│', textColor, backgroundColor)[0]);
            textGraphics.setCharacter(x + width - 1, y + i, TextCharacter.fromCharacter('│', textColor, backgroundColor)[0]);
        }

        /// Нижняя линия
        textGraphics.setCharacter(x, y + height - 1, TextCharacter.fromCharacter('└', textColor, backgroundColor)[0]);
        for (int i = 1; i < width - 1; i++) {
            textGraphics.setCharacter(x + i, y + height - 1, TextCharacter.fromCharacter('─', textColor, backgroundColor)[0]);
        }
        textGraphics.setCharacter(x + width - 1, y + height - 1, TextCharacter.fromCharacter('┘', textColor, backgroundColor)[0]);
    }
}
