import Datalayer.GameStatistic;
import Datalayer.SaveGame;
import Datalayer.StatisticManager;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import domain.GameSession;
import inputreader.UserInput;
import view.*;

import java.io.IOException;

public class Main {
    private Terminal terminal;
    private Screen screen;
    private GameSession gameSession;
    private UserInput userInput;
    private Print print;
    private MyPanel mainMenu;

    public static void main(String[] args) {
        Main game = new Main();
        game.initializeGame();
        while (true) {
            switch (game.gameSession.getGameState()) {
                case START_MENU -> {
                    game.mainMenu.showPanel();
                    game.mainMenu.userInputForPanel();
                }
                case IN_GAME -> {
                    game.gameSession.compileBoard();
                    game.print.drawGame();
                    game.gameSession.game();
                }
                case GAME_OVER -> {
                    game.createGameOverMenu();

                }
                case RECORDS -> {
                    game.print.drawGame();
                    game.userInputForRecords();
                }
                case PAUSE -> {
                    game.createPauseMenu();
                }
                case BACKPACK -> {
                    game.print.drawGame();
                    switch (game.gameSession.getHero().getBackpack().getState()) {
                        case SHOW_BACKPACK -> {
                            game.gameSession.readInputForBackpack();
                        }
                        case SHOW_DIALOG_MENU_FOR_ITEM -> {
                            game.gameSession.dialogMenuForBackpack();
                        }
                    }
                }
            }
        }
    }

    private void initializeGame() {
        initializeTerminal();
        createNewGame();
        mainMenu = createStartMenu();
    }

    private void createNewGame() {
        userInput = new UserInput(terminal);
        gameSession = new GameSession(userInput);
        print = new Print(screen, terminal, screen.getTerminalSize().getColumns(), screen.getTerminalSize().getRows(), gameSession);
        screen.clear();
        gameSession.initBoard();
        try {
            screen.refresh();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initializeTerminal() {
        try {
            terminal = new DefaultTerminalFactory().setInitialTerminalSize(new TerminalSize(70, 24)).createTerminal();
            screen = new TerminalScreen(terminal);
            screen.startScreen();
        } catch (IOException e) {
            System.out.println("терминал в говне");
        }
    }

    public MyPanel createStartMenu() {
        MyPanel startMenu = new MyPanel(screen, terminal);
        addButtonsForMenu(startMenu);
        startMenu.setThemeStyle(new MyTheme(TextColor.ANSI.YELLOW, TextColor.ANSI.WHITE_BRIGHT));
        startMenu.setTopLabel(new GameEvent.ColoredText("RogueLike", TextColor.ANSI.BLACK, TextColor.ANSI.YELLOW));
        startMenu.setBottomLabel(new GameEvent.ColoredText("Created by Pizzatom & Arbokboe", TextColor.ANSI.BLACK, TextColor.ANSI.YELLOW));
        startMenu.centredPanel();
        return startMenu;
    }

    public void createPauseMenu() {
        MyPanel pauseMenu = new MyPanel(screen, terminal);
        addButtonsForPauseMenu(pauseMenu);
        pauseMenu.setThemeStyle(new MyTheme(TextColor.ANSI.YELLOW, TextColor.ANSI.WHITE_BRIGHT));
        pauseMenu.setTopLabel(new GameEvent.ColoredText("Pause", TextColor.ANSI.BLACK, TextColor.ANSI.YELLOW));
        pauseMenu.centredPanel();
        do {
            pauseMenu.showPanel();
            pauseMenu.userInputForPanel();
        } while (gameSession.getGameState() == GameSession.GameKa.PAUSE);
        pauseMenu.clearPanel(false);
    }

    public void addButtonsForPauseMenu(MyPanel pauseMenu) {
        MyButton resumeGame = new MyButton(new GameEvent.ColoredText("Возобновить игру", TextColor.ANSI.BLACK, TextColor.ANSI.WHITE_BRIGHT), TextColor.ANSI.GREEN, () -> {
            gameSession.setGameState(GameSession.GameKa.IN_GAME);
        });

        MyButton exitToMenu = new MyButton(new GameEvent.ColoredText("Выйти с сохранением", TextColor.ANSI.BLACK, TextColor.ANSI.WHITE_BRIGHT), TextColor.ANSI.RED, () -> {
            SaveGame saveGame = new SaveGame(gameSession);
            saveGame.saveGame();
            gameSession.setGameState(GameSession.GameKa.START_MENU);
        });
        pauseMenu.addButton(resumeGame);
        pauseMenu.addButton(exitToMenu);
    }

    public void createInputReaderPanel() {
        InputReaderPanel inputReaderPanel = new InputReaderPanel(screen, terminal);
        inputReaderPanel.setPanelSize(new TerminalSize(18, 3));
        inputReaderPanel.setThemeStyle(new MyTheme(TextColor.ANSI.YELLOW, TextColor.ANSI.WHITE_BRIGHT));
        inputReaderPanel.setTopLabel(new GameEvent.ColoredText("Введите имя персонажа", TextColor.ANSI.BLACK, TextColor.ANSI.YELLOW));
        inputReaderPanel.addMessage(new GameEvent.ColoredText("Имя должно содержать", TextColor.ANSI.BLACK, TextColor.ANSI.WHITE_BRIGHT));
        inputReaderPanel.addMessage(new GameEvent.ColoredText("не менее 5 символов", TextColor.ANSI.BLACK, TextColor.ANSI.WHITE_BRIGHT));
        inputReaderPanel.centred();

        InteractivePanelString interactivePanelString = new InteractivePanelString(17, () -> {
            gameSession.getHero().setName(inputReaderPanel.getInputString());
            gameSession.setGameState(GameSession.GameKa.IN_GAME);
        });
        inputReaderPanel.addInteractiveStringToPanel(interactivePanelString);

        do {
            inputReaderPanel.showPanel();
            inputReaderPanel.readInput();
        } while (gameSession.getGameState() == GameSession.GameKa.DIALOG_MENU);
        inputReaderPanel.clearPanel(false);
    }

    public void createDialogMenu(String label, GameSession.GameKa state) {
        MyPanel dialogMenu = new MyPanel(screen, terminal);
        dialogMenu.setThemeStyle(new MyTheme(TextColor.ANSI.RED, TextColor.ANSI.WHITE_BRIGHT));
        dialogMenu.setTopLabel(new GameEvent.ColoredText(label, TextColor.ANSI.BLACK, TextColor.ANSI.RED));
        dialogMenu.setPlane(MyPanel.Plane.HORIZONTAL);
        dialogMenu.centredPanel();
        addButtonsForDialogMenu(dialogMenu, state);
        do {
            dialogMenu.showPanel();
            dialogMenu.userInputForPanel();
        } while (gameSession.getGameState() == GameSession.GameKa.DIALOG_MENU);
        dialogMenu.clearPanel(false);
    }

    private void addButtonsForDialogMenu(MyPanel dialogMenu, GameSession.GameKa lastState) {
        MyButton yes = new MyButton(
                new GameEvent.ColoredText("YES", TextColor.ANSI.BLACK, TextColor.ANSI.WHITE_BRIGHT), TextColor.ANSI.RED,
                this::exit
        );
        MyButton no = new MyButton(
                new GameEvent.ColoredText("NO", TextColor.ANSI.BLACK, TextColor.ANSI.WHITE_BRIGHT), TextColor.ANSI.GREEN,
                () -> {
                    gameSession.setGameState(lastState);
                }
        );

        dialogMenu.addButton(yes);
        dialogMenu.addButton(no);
    }

    public void addButtonsForMenu(MyPanel startMenu) {
        MyButton startGame = new MyButton(
                new GameEvent.ColoredText("Start game", TextColor.ANSI.BLACK, TextColor.ANSI.WHITE_BRIGHT), TextColor.ANSI.GREEN,
                () -> {
                    mainMenu.clearPanel(false);
                    gameSession.setGameState(GameSession.GameKa.IN_GAME);
                    if (gameSession.getHero().getName() == null) {
                        gameSession.setGameState(GameSession.GameKa.DIALOG_MENU);
                        createInputReaderPanel();
                    }
                }
        );

        MyButton records = new MyButton(
                new GameEvent.ColoredText("Records", TextColor.ANSI.BLACK, TextColor.ANSI.WHITE_BRIGHT), TextColor.ANSI.GREEN,
                () -> {
                    mainMenu.clearPanel(false);
                gameSession.setGameState(GameSession.GameKa.RECORDS);
                });

        MyButton exit = new MyButton(
                new GameEvent.ColoredText("Exit", TextColor.ANSI.BLACK, TextColor.ANSI.WHITE_BRIGHT), TextColor.ANSI.RED, () -> {
            GameSession.GameKa lastState = gameSession.getGameState();
            gameSession.setGameState(GameSession.GameKa.DIALOG_MENU);
            createDialogMenu("Вы действительно хотите выйти из игры?", lastState);
        }
        );

        startMenu.addButton(startGame);
        startMenu.addButton(records);
        startMenu.addButton(exit);
    }

    public void createGameOverMenu() {
        MyPanel gameOver = new MyPanel(screen, terminal);
        addButtonsForGameOverMenu(gameOver);
        gameOver.setThemeStyle(new MyTheme(TextColor.ANSI.RED, TextColor.ANSI.WHITE_BRIGHT));
        gameOver.setTopLabel(new GameEvent.ColoredText("Game over", TextColor.ANSI.BLACK, TextColor.ANSI.RED));
        gameOver.centredPanel();
        StatisticManager.saveStat(gameSession);
        SaveGame.deleteFile();
        do {
            gameOver.showPanel();
            gameOver.userInputForPanel();
        } while (gameSession.getGameState() == GameSession.GameKa.GAME_OVER);
        gameOver.clearPanel(false);
    }

    public void addButtonsForGameOverMenu(MyPanel gameOver) {
        MyButton exitToMenu = new MyButton(new GameEvent.ColoredText("Exit to menu", TextColor.ANSI.BLACK, TextColor.ANSI.WHITE_BRIGHT), TextColor.ANSI.RED, () -> {
            gameSession.setGameState(GameSession.GameKa.START_MENU);
            createNewGame();
        });
        gameOver.addButton(exitToMenu);
    }

    public void userInputForRecords(){
        KeyStroke keyStroke = new UserInput(terminal).getInput();
        switch (keyStroke.getKeyType()){
            case KeyType.Escape -> {
                gameSession.setGameState(GameSession.GameKa.START_MENU);
            }
        }
    }

    public void exit() {
        try {
            screen.stopScreen();
            terminal.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
