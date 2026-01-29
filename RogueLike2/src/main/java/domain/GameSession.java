package domain;

import static com.googlecode.lanterna.TextColor.ANSI.*;

import Datalayer.SaveGame;
import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.TextCharacter;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import domain.enemy.*;
import domain.item.*;
import domain.item.coin.*;
import domain.item.eat.*;
import domain.item.elixir.*;
import domain.item.scrol.*;
import domain.item.weapon.*;

import inputreader.UserInput;

import view.GameEvent;

import java.util.ArrayList;
import java.util.List;


public class GameSession {

    public static final int UP = 0;
    public static final int RIGHT = 1;
    public static final int DOWN = 2;
    public static final int LEFT = 3;

    private TextCharacter[][] gameBoard = new TextCharacter[Room.MAP_HEIGHT][Room.MAP_WIDTH];
    private List<Level> levels = new ArrayList<>();
    private Hero hero;
    private UserInput userInput;
    private int currentIndexLevel;
    private int numLevelGenerate;
    private int levelNumber;
    private GameEvent gameEvent;
    private GameKa gameState = GameKa.START_MENU;

    public enum GameKa {
        START_MENU, BACKPACK, GAME_OVER, IN_GAME, PAUSE, LOAD_SAVE_GAME, RECORDS, SAVE_GAME, DIALOG_MENU
    }

    public GameSession(UserInput userInput) {
        try {
            SaveGame.loadGame(this);
            gameEvent = new GameEvent(19);
            this.userInput = userInput;
            numLevelGenerate = 21 - (currentIndexLevel + 1);
            this.levelNumber = currentIndexLevel + 1;
            currentIndexLevel = 0;
            initLevels(numLevelGenerate, levelNumber);
        } catch (Exception e) {
            initBaseGameSession(userInput);
        }
    }

    private void initBaseGameSession(UserInput userInput) {

        currentIndexLevel = 0;
        numLevelGenerate = 21;
        this.levelNumber = 0;
        initLevels(numLevelGenerate, levelNumber);
        hero = new Hero(
                new Coordinate(levels.get(currentIndexLevel).
                        getRooms().getFirst().getXCenter() + 1, levels.get(currentIndexLevel).
                        getRooms().getFirst().getYCenter() + 1), 0);
        gameEvent = new GameEvent(19);
        this.userInput = userInput;
    }

    /// Генерация 21 уровня
    private void initLevels(int numOfLevels, int levelNumber) {
        for (int i = 0; i < numOfLevels; i++) {
            levelNumber++;
            levels.add(new Level(levelNumber));
        }
    }

    /// Основной игровой цикл
    public void game() {
        handleInput();
        moveEnemies();
        heroInItem();
        checkGame();
        checkHealth();
    }


    private void checkHealth() {
        if (hero.getStats().getCurrentHealth() <= 0) {
            gameState = GameKa.GAME_OVER;
        }
    }

    private void checkGame() {
        if (isExit(getHero().getCoordinate())) {
            nextLevel();
        }
    }

    private void heroInItem() {
        List<Item> items = levels.get(currentIndexLevel).getItems();
        for (int i = 0; i < items.size(); i++) {
            Point point = items.get(i).getPoint();
            if (point.getCoordinate().getX() == hero.getCoordinate().getX() && point.getCoordinate().getY() == hero.getCoordinate().getY()) {
                boolean itemAdded = addItemBackpack(items.get(i));
                if (itemAdded) items.remove(i);
            }
        }
    }

    private boolean addItemBackpack(Item item) {
        boolean itemAdded = hero.getBackpack().addItem(item);
        if (itemAdded) {
            gameEvent.addEvent("Вы подобрали предмет: ", item);
            return true;
        } else {
            gameEvent.addEvent("В рюкзаке нет места для: ", item);
            return false;
        }
    }


    /// Движение противников
    private void moveEnemies() {
        for (Enemy enemy : levels.get(currentIndexLevel).getEnemies()) {
            enemy.update(hero.getCoordinate().getX(), hero.getCoordinate().getY(), this);
        }
    }

    /// Ввод пользователя
    private void handleInput() {
        KeyStroke keyStroke = userInput.getInput();
        switch (keyStroke.getKeyType()) {
            case Escape -> {
                setGameState(GameKa.PAUSE);
            }
            default -> {
                char moveSymbol = Character.toLowerCase(userInput.getCharacterFromKeyStroke(keyStroke));
                if (moveSymbol == 'w' && validatePlayerMove(0, -1)) {
                    hero.movePlayer(0, -1, this);
                    hero.setStepsPlus();
                }
                if (moveSymbol == 's' && validatePlayerMove(0, 1)) {
                    hero.movePlayer(0, 1, this);
                    hero.setStepsPlus();
                }
                if (moveSymbol == 'a' && validatePlayerMove(-1, 0)) {
                    hero.movePlayer(-1, 0, this);
                    hero.setStepsPlus();
                }
                if (moveSymbol == 'd' && validatePlayerMove(1, 0)) {
                    hero.movePlayer(1, 0, this);
                    hero.setStepsPlus();
                }
                if (moveSymbol == 'p') {
                    levels.get(currentIndexLevel).allVisible();
                }
                if (moveSymbol == 'i') {
                    gameState = GameKa.BACKPACK;
                }
                if (moveSymbol == 'z') {
                    currentIndexLevel++;
                    initBoard();
                    compileBoard();
                }
                if (moveSymbol == '.') {
                    gameState = GameKa.PAUSE;
                }
            }
        }
    }

    private void dropItem(int itemIndex) {
        Coordinate coordinate = searChFreeCell();
        if (coordinate != null) {
            int xFree = coordinate.getX();
            int yFree = coordinate.getY();
            hero.getBackpack().getItems().get(itemIndex).getPoint().getCoordinate().setNew(xFree, yFree);
            levels.get(currentIndexLevel).getItems().add(hero.getBackpack().getItems().get(itemIndex));
            gameEvent.addEvent("Вы выбросили предмет: ", hero.getBackpack().getItems().get(itemIndex));
        } else {
            gameEvent.addEvent("Вы потеряли предмет: ", hero.getBackpack().getItems().get(itemIndex));
        }
        hero.getBackpack().getItems().remove(itemIndex);
        hero.getBackpack().setState(Backpack.BackpackState.SHOW_BACKPACK);
        compileBoard();
    }

    private Coordinate searChFreeCell() {
        int xHero = hero.getCoordinate().getX();
        int yHero = hero.getCoordinate().getY();
        Coordinate coordinate = null;

        for (int i = xHero - 1; i <= xHero + 1; i++) {
            for (int j = yHero - 1; j <= yHero + 1; j++) {
                if (getCell(j, i).getCharacterString().charAt(0) == '.') {
                    coordinate = new Coordinate(i, j);
                    break;
                }
            }
        }
        return coordinate;
    }


    public void readInputForBackpack() {
        KeyStroke keyType = userInput.getInput();

        switch (keyType.getKeyType()) {
            case KeyType.ArrowLeft -> hero.getBackpack().backIter();
            case KeyType.ArrowRight -> hero.getBackpack().nextIter();
            default -> {
                char key = Character.toLowerCase(userInput.getCharacterFromKeyStroke(keyType));
                switch (key) {
                    case '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                        int itemIndex = Integer.parseInt(String.valueOf(key)) - 1;
                        if (itemIndex < hero.getBackpack().getItems().size()) {
                            hero.getBackpack().setState(Backpack.BackpackState.SHOW_DIALOG_MENU_FOR_ITEM);
                            hero.getBackpack().setCurrentItem(Integer.parseInt(String.valueOf(key)) - 1);
                        } else {
                            gameEvent.addColoredMessage("Выбранный предмет отсутствует в рюкзаке", ANSI.RED);
                        }
                    }
                    case 'i' -> gameState = GameKa.IN_GAME;
                }
            }
        }
    }

    private void useCurrentItem(Backpack backpack){
        Item item = backpack.getItems().get(backpack.getCurrentItem());
        switch (item){
            case UsableItem usableItem -> {
                usableItem.apply(hero.getStats());
                hero.getBackpack().getItems().remove(backpack.getCurrentItem());
                gameEvent.addEvent("Вы использовали: ", item);
            }
            case EquippableItem equippableItem -> {
                equippableItem.equip(hero.getStats(), hero.getEquipment(), hero.getBackpack());
                hero.getBackpack().getItems().remove(backpack.getCurrentItem());
                gameEvent.addEvent("Вы экипировали: ", item);
            }
            default -> throw new IllegalStateException("Unexpected value: " + item);
        }
        backpack.setState(Backpack.BackpackState.SHOW_BACKPACK);
    }

    public void dialogMenuForBackpack() {
        char key = Character.toLowerCase(userInput.getCharacterFromKeyStroke(userInput.getInput()));
        switch (key) {
            case '1' -> useCurrentItem(hero.getBackpack());
            case '2' -> hero.getBackpack().setState(Backpack.BackpackState.SHOW_BACKPACK);
            case '3' -> dropItem(hero.getBackpack().getCurrentItem());
        }
    }

    /// Проверка корректности перемещения игрока
    private boolean validatePlayerMove(int dx, int dy) {
        int newX = dx + hero.getCoordinate().getX();
        int newY = dy + hero.getCoordinate().getY();

        if (newX >= Room.MAP_WIDTH || newY >= Room.MAP_HEIGHT || newX < 0 || newY < 0) {
            return false;
        }

        char newBoardCell = getCell(newY, newX).getCharacterString().charAt(0);
        return newBoardCell == '.' || newBoardCell == 'D'
                || newBoardCell == '#' || newBoardCell == Symbols.SPADES || newBoardCell == Symbols.INVERSE_BULLET
                || newBoardCell == Symbols.HEART || newBoardCell == '$' || newBoardCell == '✉'
                || newBoardCell == 'a' || newBoardCell == 's' || newBoardCell == '+' || newBoardCell == 'Z'
                || newBoardCell == 'G' || newBoardCell == 'S' || newBoardCell == 'V' || newBoardCell == 'O';
    }

    ///  Заполнение игрового поля
    public void compileBoard() {
        levels.get(currentIndexLevel).ProcessVisible(hero.getCoordinate().getX(), hero.getCoordinate().getY());
        compileRoomPointAndWall();
        compileCorridorAndDoor();
        compileEnemy();
        compileItem();
        compileExitLevel();
    }

    /// Вспомогательный метод compileBoard
    private void compileExitLevel() {
        int exitLevelX = levels.get(currentIndexLevel).getExitLevel().getCoordinate().getX();
        int exitLevelY = levels.get(currentIndexLevel).getExitLevel().getCoordinate().getY();

        if (levels.get(currentIndexLevel).getExitLevel().getVisibility()) {
            gameBoard[exitLevelY][exitLevelX] = TextCharacter.fromCharacter('+', YELLOW_BRIGHT, DEFAULT)[0];
        }
    }

    /// Вспомогательный метод compileBoard
    private void compileItem() {
        for (Item item : levels.get(currentIndexLevel).getItems()) {
            if (item.getPoint().getVisibility()) {
                int Xitem = item.getPoint().getCoordinate().getX();
                int Yitem = item.getPoint().getCoordinate().getY();
                if (item instanceof ElixirBlue) {
                    gameBoard[Yitem][Xitem] = TextCharacter.fromCharacter(Symbols.SPADES, BLUE, DEFAULT)[0];
                } else if (item instanceof ElixirGreen) {
                    gameBoard[Yitem][Xitem] = TextCharacter.fromCharacter(Symbols.SPADES, GREEN, DEFAULT)[0];
                } else if (item instanceof ElixirRed) {
                    gameBoard[Yitem][Xitem] = TextCharacter.fromCharacter(Symbols.SPADES, RED, DEFAULT)[0];
                } else if (item instanceof ElixirYellow) {
                    gameBoard[Yitem][Xitem] = TextCharacter.fromCharacter(Symbols.SPADES, YELLOW, DEFAULT)[0];
                } else if (item instanceof Chapman) {
                    gameBoard[Yitem][Xitem] = TextCharacter.fromCharacter(Symbols.INVERSE_BULLET, RED, DEFAULT)[0];
                } else if (item instanceof Meat) {
                    gameBoard[Yitem][Xitem] = TextCharacter.fromCharacter(Symbols.HEART, RED_BRIGHT, DEFAULT)[0];
                } else if (item instanceof Coin) {
                    gameBoard[Yitem][Xitem] = TextCharacter.fromCharacter('$', YELLOW_BRIGHT, DEFAULT)[0];
                } else if (item instanceof ScrollDeath) {
                    gameBoard[Yitem][Xitem] = TextCharacter.fromCharacter('✉', MAGENTA_BRIGHT, DEFAULT)[0];
                } else if (item instanceof ScrollAgility) {
                    gameBoard[Yitem][Xitem] = TextCharacter.fromCharacter('✉', MAGENTA, DEFAULT)[0];
                } else if (item instanceof ScrollStrength) {
                    gameBoard[Yitem][Xitem] = TextCharacter.fromCharacter('✉', CYAN, DEFAULT)[0];
                } else if (item instanceof Axe) {
                    gameBoard[Yitem][Xitem] = TextCharacter.fromCharacter('a', YELLOW_BRIGHT, DEFAULT)[0];
                } else if (item instanceof Sword) {
                    gameBoard[Yitem][Xitem] = TextCharacter.fromCharacter('s', YELLOW_BRIGHT, DEFAULT)[0];
                }
            }
        }
    }

    /// Вспомогательный метод compileBoard
    private void compileEnemy() {
        for (Enemy enemy : levels.get(currentIndexLevel).getEnemies()) {
            int y = enemy.getPoint().getCoordinate().getY();
            int x = enemy.getPoint().getCoordinate().getX();
            if (enemy.getVisibility()) {
                switch (enemy) {
                    case Ghost ghost -> gameBoard[y][x] = TextCharacter.fromCharacter('G', WHITE_BRIGHT, DEFAULT)[0];
                    case Ogre ogre -> gameBoard[y][x] = TextCharacter.fromCharacter('O', YELLOW_BRIGHT, DEFAULT)[0];
                    case SerpentMage serpentMage ->
                            gameBoard[y][x] = TextCharacter.fromCharacter('S', WHITE_BRIGHT, DEFAULT)[0];
                    case Vampire vampire -> gameBoard[y][x] = TextCharacter.fromCharacter('V', RED_BRIGHT, DEFAULT)[0];
                    case Zombie zombie -> gameBoard[y][x] = TextCharacter.fromCharacter('Z', GREEN_BRIGHT, DEFAULT)[0];
                    default -> {
                    }
                }
            }
        }
    }

    /// Вспомогательный метод compileBoard
    private void compileCorridorAndDoor() {
        for (Corridor corridor : levels.get(currentIndexLevel).getCorridors()) {
            for (Point points : corridor.getPoint()) {
                if (points.getVisibility()) {
                    gameBoard[points.getCoordinate().getY()][points.getCoordinate().getX()] = TextCharacter.fromCharacter('#', YELLOW, DEFAULT)[0];
                }
            }
        }
        for (Door door : levels.get(currentIndexLevel).getDoors()) {
            for (Point points : door.getPoint()) {
                if (points.getVisibility()) {
                    gameBoard[points.getCoordinate().getY()][points.getCoordinate().getX()] = TextCharacter.fromCharacter('D', YELLOW_BRIGHT, DEFAULT)[0];
                }
            }
        }
    }

    /// Вспомогательный метод compileBoard
    private void compileRoomPointAndWall() {
        for (Room room : levels.get(currentIndexLevel).getRooms()) {
            for (Point points : room.getRoomPoint()) {
                if (points.getVisibility()) {
                    gameBoard[points.getCoordinate().getY()][points.getCoordinate().getX()] = TextCharacter.fromCharacter('.', YELLOW, DEFAULT)[0];
                } else {
                    gameBoard[points.getCoordinate().getY()][points.getCoordinate().getX()] = TextCharacter.fromCharacter(' ', YELLOW, DEFAULT)[0];
                }
            }
            for (Point points : room.getWallPoint()) {

                int x = points.getCoordinate().getX();
                int y = points.getCoordinate().getY();

                if (points.getVisibility()) {
                    if (y == room.getBotRight().getY() && x == room.getBotRight().getX()) {
                        gameBoard[y][x] = TextCharacter.fromCharacter(Symbols.DOUBLE_LINE_BOTTOM_RIGHT_CORNER, YELLOW, DEFAULT)[0];
                    } else if (y == room.getTopLeft().getY() && x == room.getTopLeft().getX()) {
                        gameBoard[y][x] = TextCharacter.fromCharacter(Symbols.DOUBLE_LINE_TOP_LEFT_CORNER, YELLOW, DEFAULT)[0];
                    } else if (y == room.getBotLeft().getY() && x == room.getBotLeft().getX()) {
                        gameBoard[y][x] = TextCharacter.fromCharacter(Symbols.DOUBLE_LINE_BOTTOM_LEFT_CORNER, YELLOW, DEFAULT)[0];
                    } else if (y == room.getTopRight().getY() && x == room.getTopRight().getX()) {
                        gameBoard[y][x] = TextCharacter.fromCharacter(Symbols.DOUBLE_LINE_TOP_RIGHT_CORNER, YELLOW, DEFAULT)[0];
                    } else if (y == room.getBotRight().getY()) {
                        gameBoard[y][x] = TextCharacter.fromCharacter(Symbols.DOUBLE_LINE_HORIZONTAL, YELLOW, DEFAULT)[0];
                    } else if (y == room.getTopLeft().getY()) {
                        gameBoard[y][x] = TextCharacter.fromCharacter(Symbols.DOUBLE_LINE_HORIZONTAL, YELLOW, DEFAULT)[0];
                    } else if (x == room.getTopLeft().getX()) {
                        gameBoard[y][x] = TextCharacter.fromCharacter(Symbols.DOUBLE_LINE_VERTICAL, YELLOW, DEFAULT)[0];
                    } else if (x == room.getTopRight().getX()) {
                        gameBoard[y][x] = TextCharacter.fromCharacter(Symbols.DOUBLE_LINE_VERTICAL, YELLOW, DEFAULT)[0];
                    }
                }
            }
        }
    }

    ///  Заполнения игрового поля пустыми символами
    public void initBoard() {
        for (int i = 0; i < Room.MAP_HEIGHT; i++) {
            for (int j = 0; j < Room.MAP_WIDTH; j++) {
                gameBoard[i][j] = TextCharacter.fromCharacter(' ', DEFAULT, DEFAULT)[0];

            }
        }
    }

    public Integer getCurrentRoomForEntity(Coordinate coordinate) {
        for (int i = 1; i < 9; i++) {
            Room room = levels.get(currentIndexLevel).getRooms().get(i);
            if (room.inRoom(coordinate.getX(), coordinate.getY()) || room.inDoor(coordinate.getX(),
                    coordinate.getY())) {
                return i;
            }
        }
        return -1;
    }

    /// Проверка на совпадение координат игрока с координатами выхода из уровня
    private boolean isExit(Coordinate coordinate) {
        int exitLevelX = levels.get(currentIndexLevel).getExitLevel().getCoordinate().getX();
        int exitLevelY = levels.get(currentIndexLevel).getExitLevel().getCoordinate().getY();
        if (coordinate.getX() == exitLevelX && coordinate.getY() == exitLevelY) {
            return true;
        }
        return false;
    }

    /// Переход на след уровень
    private void nextLevel() {
        initBoard();
        currentIndexLevel++;
        Coordinate coordinate = levels.get(currentIndexLevel).generateRandomPos(0);
        hero.setNewPos(coordinate.getX(), coordinate.getY());

    }

    //GET
    public Level getLevel(int levelNum) {
        return levels.get(levelNum);
    }

    public List<Level> getLevels() {
        return levels;
    }

    public int getCurrentIndexLevel() {
        return currentIndexLevel;
    }

    public TextCharacter getCell(int posY, int posX) {
        return gameBoard[posY][posX];
    }

    public Hero getHero() {
        return hero;
    }

    public GameEvent getGameEvent() {
        return gameEvent;
    }

    public GameKa getGameState() {
        return gameState;
    }

    //SET
    public void setGameState(GameKa state) {
        gameState = state;
    }

    public void setCurrentIndexLevel(int currentIndexLevel) {
        this.currentIndexLevel = currentIndexLevel;
    }

    public void setHero(Hero hero) {
        this.hero = hero;
    }

    public void setLevels(List<Level> levels) {
        this.levels = levels;
    }

    public void setLevel(Level level) {
        levels.add(level);
    }
}