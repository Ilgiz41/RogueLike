package domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.lanterna.TextColor;
import domain.enemy.*;
import domain.item.*;
import domain.item.coin.Coin;
import domain.item.eat.Chapman;
import domain.item.eat.Meat;
import domain.item.elixir.*;
import domain.item.scrol.*;
import domain.item.weapon.*;


import static domain.item.Item.*;

import java.util.*;


public class Level {
    @JsonIgnore
    public final int ROOMS_PER_SIDE = 3;
    @JsonIgnore
    public final int NUM_ROOMS = 9;
    @JsonIgnore
    private final int MIN_ENEMY_LEVEL = 5;


    private int enemyCount;
    private int itemCount;
    private int levelNumber;
    private Point exitLevel;
    private List<Item> items = new ArrayList<>();
    private List<Enemy> enemies = new ArrayList<>();
    private List<Room> rooms = new ArrayList<>();
    private List<Corridor> corridors = new ArrayList<>();
    private List<Door> doors = new ArrayList<>();

    private boolean[][] connections = new boolean[NUM_ROOMS][NUM_ROOMS];


    Level() {
    }


    public Level(int levelNumber) {
        this.levelNumber = levelNumber;
        generateRooms();
        generateConnections();
        createCorridors();
        createDoors();
        enemyCount = generateRandomCountEnemy();
        itemCount = generateRandomItemCount();
        generateEnemies();
        generateItems();
        placeExitLevel();
    }

    private void placeExitLevel() {
        Random rand = new Random();
        int numRoom = rand.nextInt(3) + 6;
        Coordinate coordinate;
        while (true) {
            coordinate = generateRandomPos(numRoom);
            if (checkCell(numRoom, coordinate.getX(), coordinate.getY()) && !doorIsNear(coordinate)) {
                break;
            }
        }
        exitLevel = new Point(coordinate, false);
    }

    private boolean doorIsNear(Coordinate coordinate) {
        int x = coordinate.getX();
        int y = coordinate.getY();

        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                Coordinate temp = new Coordinate(i, j);
                for (Door door : doors) {
                    for (Point points : door.getPoint()) {
                        if (points.getCoordinate().equals(temp)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /// Генерирует количество предметов на уровень
    private int generateRandomItemCount() {
        Random random = new Random();
        int maxItems = 10;
        int minItems = 5;
        int baseItems = maxItems - (levelNumber - 1) * 2;
        baseItems = Math.max(baseItems, minItems);
        int randomExtra = random.nextInt(4);
        return baseItems + randomExtra;
    }

    /// Генерирует рандомный тип предмета
    private void generateItems() {
        while (items.size() < itemCount) {
            int roomIndex = generateRoomNum(0);
            Coordinate coordinate = generateRandomPos(roomIndex);
            if (checkCell(roomIndex, coordinate.getX(), coordinate.getY())) {
                items.add(generateTypeItem(coordinate));
            }
        }
    }

    /// Вспомогательный метод для генератора предметов
    private Item generateTypeItem(Coordinate coordinate) {
        int itemType = chooseItemType();

        return switch (itemType) {
            case SWORD -> new Sword(coordinate, false, TextColor.ANSI.GREEN);
            case AXE -> new Axe(coordinate, false, TextColor.ANSI.RED);
            case ELIXIRBLUE -> new ElixirBlue(coordinate, false, TextColor.ANSI.BLUE);
            case ELIXIRRED -> new ElixirRed(coordinate, false, TextColor.ANSI.RED);
            case ELIXIRGREEN -> new ElixirGreen(coordinate, false, TextColor.ANSI.GREEN);
            case ELIXIRYELLOW -> new ElixirYellow(coordinate, false, TextColor.ANSI.YELLOW);
            case MEAT -> new Meat(coordinate, false, TextColor.ANSI.CYAN);
            case CHAPMAN -> new Chapman(coordinate, false, TextColor.ANSI.CYAN);
            case COIN -> new Coin(coordinate, Coin.generateAmountCoin(), false, TextColor.ANSI.YELLOW);
            case SCROLDEATH -> new ScrollDeath(coordinate, false, TextColor.ANSI.WHITE);
            case SCROLSTRENGTH -> new ScrollStrength(coordinate, false, TextColor.ANSI.RED);
            default -> new ScrollAgility(coordinate, false,  TextColor.ANSI.GREEN);
        };
    }

    /// Возвращает тип предмета
    private int chooseItemType() {

        Random random = new Random();
        /// Шанс в % на выпадение предмета
        int sword = 100;
        int axe = 100;
        int elixirBlue = 9;
        int elixirRED = 9;
        int elixirGreen = 9;
        int elixirYellow = 9;
        int meat = 9;
        int chapman = 9;
        int coin = 9;
        int scrollDeath = 9;
        int scrollStrength = 9;
        int scrollAgility = 9;

        int totalProbability = sword + axe + elixirBlue + elixirRED + elixirGreen + elixirYellow + elixirBlue + meat + chapman + coin + scrollDeath + scrollStrength + scrollAgility;
        int randomNumber = random.nextInt(totalProbability);

        if (randomNumber < sword) {
            return SWORD;
        } else if (randomNumber < sword + axe) {
            return AXE;
        } else if (randomNumber < sword + axe + elixirBlue) {
            return ELIXIRBLUE;
        } else if (randomNumber < sword + axe + elixirBlue + elixirRED) {
            return ELIXIRRED;
        } else if (randomNumber < sword + axe + elixirBlue + elixirRED + elixirGreen) {
            return ELIXIRGREEN;
        } else if (randomNumber < sword + axe + elixirBlue + elixirRED + elixirGreen + elixirYellow) {
            return ELIXIRYELLOW;
        } else if (randomNumber < sword + axe + elixirBlue + elixirRED + elixirGreen + elixirYellow + meat) {
            return MEAT;
        } else if (randomNumber < sword + axe + elixirBlue + elixirRED + elixirGreen + elixirYellow + meat + chapman) {
            return CHAPMAN;
        } else if (randomNumber < sword + axe + elixirBlue + elixirRED + elixirGreen + elixirYellow + meat + chapman + coin) {
            return COIN;
        } else if (randomNumber < sword + axe + elixirBlue + elixirRED + elixirGreen + elixirYellow + meat + chapman + coin + scrollDeath) {
            return SCROLDEATH;
        } else if (randomNumber < sword + axe + elixirBlue + elixirRED + elixirGreen + elixirYellow + meat + chapman + coin + scrollDeath + scrollStrength) {
            return SCROLSTRENGTH;
        }
        return SCROLAGILITY;
    }


    /// Генерация противников
    private void generateEnemies() {
        while (enemies.size() < enemyCount) {
            int roomIndex = generateRoomNum(1);
            Coordinate coordinate = generateRandomPos(roomIndex);
            if (checkCell(roomIndex, coordinate.getX(), coordinate.getY())) {
                enemies.add(generateTypeEnemy(coordinate, roomIndex));
            }
        }
    }

    /// Генерация типа противника
    public Enemy generateTypeEnemy(Coordinate coordinate, int roomIndex) {
        int enemyType = chooseEnemyType(levelNumber);

        return switch (enemyType) {
            case Enemy.ZOMBIE ->
                    new Zombie(coordinate, "Zombie", 'Z', 20 + levelNumber, 40 + levelNumber, 20, 59, roomIndex,
                            false, TextColor.ANSI.GREEN);
            case Enemy.OGRE -> new Ogre(coordinate, "Ogre", 'O', 20 + levelNumber, 60 + levelNumber, 20, 79, roomIndex,
                    false, TextColor.ANSI.YELLOW);
            case Enemy.GHOST ->
                    new Ghost(coordinate, "Ghost", 'G', 60 + levelNumber, 20 + levelNumber, 10, 30, roomIndex,
                            false,  TextColor.ANSI.WHITE);
            case Enemy.VAMPIRE ->
                    new Vampire(coordinate, "Vampire", 'V', 45 + levelNumber, 40 + levelNumber, 40, 59, roomIndex,
                            false, TextColor.ANSI.RED);
            default -> new SerpentMage(coordinate, "Serpent Mage", 'S', 60 + levelNumber, 40 + levelNumber, 40, 65,
                    roomIndex, false, TextColor.ANSI.WHITE);
        };
    }

    /// Возвращает тип противника в зависимости от уровня
    private int chooseEnemyType(int level) {

        Random random = new Random();
        /// Вероятности появления каждого типа противника (в процентах)
        int zombieProbability = 60;
        int ogreProbability = 30;
        int ghostProbability = 20;
        int vampireProbability = 5;
        int serpentMageProbability = 1;

        /// Корректировка вероятности в зависимости от уровня
        if (level > 10) {
            zombieProbability = 20;
            ogreProbability = 30;
            ghostProbability = 40;
            vampireProbability = 40;
            serpentMageProbability = 50;
        } else if (level > 5) {
            zombieProbability = 40;
            ogreProbability = 22;
            ghostProbability = 18;
            vampireProbability = 12;
            serpentMageProbability = 8;
        }
        int totalProbability = zombieProbability + ogreProbability + ghostProbability +
                vampireProbability + serpentMageProbability;
        int randomNumber = random.nextInt(totalProbability);
        if (randomNumber < zombieProbability) {
            return Enemy.ZOMBIE;
        } else if (randomNumber < zombieProbability + ogreProbability) {
            return Enemy.OGRE;
        } else if (randomNumber < zombieProbability + ogreProbability + ghostProbability) {
            return Enemy.GHOST;
        } else if (randomNumber < zombieProbability + ogreProbability + ghostProbability + vampireProbability) {
            return Enemy.VAMPIRE;
        } else {
            return Enemy.SERPENTMAGE;
        }
    }

    /// Генерация координат в комнате по индексу
    Coordinate generateRandomPos(int roomIndex) {
        Random random = new Random();
        int x = rooms.get(roomIndex).getTopLeft().getX() + random.nextInt(rooms.get(roomIndex).getWidth() - 2) + 1;
        int y = rooms.get(roomIndex).getTopLeft().getY() + random.nextInt(rooms.get(roomIndex).getHeight() - 2) + 1;
        return new Coordinate(x, y);
    }

    /// Проверка, что клетка на карте не занята
    private boolean checkCell(int roomIndex, int x, int y) {
        for (Point wall : rooms.get(roomIndex).getWallPoint()) {
            if (wall.getCoordinate().getX() == x && wall.getCoordinate().getY() == y) {
                return false;
            }
        }
        if (!enemies.isEmpty()) {
            for (Enemy enemy : enemies) {
                if (enemy.getPoint().getCoordinate().getX() == x && enemy.getPoint().getCoordinate().getY() == y) {
                    return false;
                }
            }
        }
        if (!items.isEmpty()) {
            for (Item item : items) {
                if (item.getPoint().getCoordinate().getX() == x && item.getPoint().getCoordinate().getY() == y) {
                    return false;
                }
            }
        }
        return true;
    }

    /// Генерация номера комнаты
    private int generateRoomNum(int type) {
        Random random = new Random();
        /// Для enemy
        if (type == 1) {
            return random.nextInt(8) + 1;
        } else {
            ///  Для item
            return random.nextInt(9);
        }
    }

    /// Генерация общего кол-ва противников на один уровень (кол-во зависит от текущего уровня)
    private int generateRandomCountEnemy() {
        Random random = new Random();
        int maxEnemies = 12;
        int currentEnemies = Math.min(MIN_ENEMY_LEVEL + levelNumber - 1, maxEnemies);

        return random.nextInt(currentEnemies - MIN_ENEMY_LEVEL + 1) + MIN_ENEMY_LEVEL;
    }

    /// Генерация 9 комнат на игровой 1 уровень
    private void generateRooms() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                rooms.add(new Room(i, j));
            }
        }
    }

    /// Генерация соединения комнат
    private void generateConnections() {
        generateMST();
        addRandomConnections();
        if (!isConnected()) {
            generateConnections();
        }
    }

    /// Алгоритм минимального остового дерева
    private void generateMST() {
        boolean[] visited = new boolean[NUM_ROOMS];
        Random random = new Random();

        // Начинаем с первой комнаты
        visited[0] = true;

        // Пока не соединим все комнаты
        while (true) {
            boolean added = false;
            for (int i = 0; i < NUM_ROOMS; i++) {
                if (visited[i]) {
                    for (int j = 0; j < NUM_ROOMS; j++) {
                        // Проверяем, что комнаты находятся рядом в сетке
                        if (!visited[j] && isAdjacent(i, j) && random.nextBoolean()) {
                            connections[i][j] = true;
                            connections[j][i] = true;
                            visited[j] = true;
                            added = true;
                        }
                    }
                }
            }
            if (!added) break; // Если ничего не добавили, выходим
        }
    }

    /// Добавление случайных туннелей
    private void addRandomConnections() {
        Random random = new Random();
        int extraTunnels = random.nextInt(5);
        for (int i = 0; i < extraTunnels; i++) {

            int room1 = random.nextInt(NUM_ROOMS);
            int room2 = random.nextInt(NUM_ROOMS);
            if (room1 != room2 && !connections[room1][room2] && isAdjacent(room1, room2)) {
                connections[room1][room2] = true;
                connections[room2][room1] = true;
            }
        }
    }

    /// Проверка связанности графа
    private boolean isConnected() {
        boolean[] visited = new boolean[NUM_ROOMS];
        Queue<Integer> queue = new LinkedList<>();

        // Начинаем с первой комнаты
        queue.add(0);
        visited[0] = true;

        // Поиск в ширину (BFS)
        while (!queue.isEmpty()) {
            int current = queue.poll();
            for (int i = 0; i < NUM_ROOMS; i++) {
                if (connections[current][i] && !visited[i]) {
                    visited[i] = true;
                    queue.add(i);
                }
            }
        }

        // Проверяем, все ли комнаты посещены
        for (boolean v : visited) {
            if (!v) return false;
        }
        return true;
    }

    /// Высчитывает расстояние между двуями комнатами
    private boolean isAdjacent(int room1, int room2) {
        int row1 = room1 / ROOMS_PER_SIDE;
        int col1 = room1 % ROOMS_PER_SIDE;
        int row2 = room2 / ROOMS_PER_SIDE;
        int col2 = room2 % ROOMS_PER_SIDE;
        return Math.abs(row1 - row2) + Math.abs(col1 - col2) == 1;
    }

    /// Генерация координат начала и  конца корридора
    private Coordinate generateBeginEndCorridor(int direction, Room room) {
        Random random = new Random();
        int x = 0;
        int y = 0;

        if (direction == GameSession.UP) {
            y = room.getTopLeft().getY(); // Верхняя граница комнаты
            x = random.nextInt(room.getWidth() - 2) + 1 + room.getTopLeft().getX(); // Случайная координата x внутри комнаты
        } else if (direction == GameSession.RIGHT) {
            x = room.getBotRight().getX(); // Правая граница комнаты
            y = random.nextInt(room.getHeight() - 2) + 1 + room.getTopLeft().getY(); // Случайная координата y внутри комнаты
        } else if (direction == GameSession.DOWN) {
            y = room.getBotRight().getY(); // Нижняя граница комнаты
            x = random.nextInt(room.getWidth() - 2) + 1 + room.getTopLeft().getX(); // Случайная координата x внутри комнаты
        } else if (direction == GameSession.LEFT) {
            x = room.getTopLeft().getX(); // Левая граница комнаты
            y = random.nextInt(room.getHeight() - 2) + 1 + room.getTopLeft().getY(); // Случайная координата y внутри комнаты
        }

        return new Coordinate(x, y);
    }

    /// Определение направление для корридора
    private int direction(int room1, int room2) {
        int row1 = room1 / ROOMS_PER_SIDE;
        int col1 = room1 % ROOMS_PER_SIDE;
        int row2 = room2 / ROOMS_PER_SIDE;
        int col2 = room2 % ROOMS_PER_SIDE;

        if (row1 < row2) return GameSession.DOWN;
        if (row1 > row2) return GameSession.UP;
        if (col1 < col2) return GameSession.RIGHT;
        if (col1 > col2) return GameSession.LEFT;
        return -1;
    }

    /// Заполнение списка корридоров
    private void createCorridors() {
        for (int i = 0; i < NUM_ROOMS; i++) {
            for (int j = 0; j < NUM_ROOMS; j++) {
                if (connections[i][j]) {
                    if (j < i) continue;
                    Coordinate begin = generateBeginEndCorridor(direction(i, j), rooms.get(i));
                    Coordinate end = generateBeginEndCorridor(direction(j, i), rooms.get(j));
                    corridors.add(new Corridor(begin, end, direction(i, j)));
                }
            }
        }
    }

    /// Обновляет статус видимости обьектов на карте
    public void ProcessVisible(int x, int y) {
        visibleRoomPointsAndWall(x, y);
        visibleCorridorsPoints(x, y);
        visibleDoorPoints();
        visibleEnemyPoints(x, y);
        visibleItemPoints(x, y);
        visibleExitLevel(x, y);

    }

    private void visibleExitLevel(int x, int y) {
        Room playerRoom = findRoom(x, y);
        if (playerRoom != null && playerRoom.inRoom(exitLevel.getCoordinate().getX(), exitLevel.getCoordinate().getY())) {
            exitLevel.setVisibility(true);
        } else {
            exitLevel.setVisibility(false);
        }
    }


    private Room findRoom(int x, int y) {
        for (Room room : rooms) {
            if (room.inRoom(x, y) || room.inDoor(x, y)) {
                return room;
            }
        }
        return null;
    }

    private void visibleItemPoints(int x, int y) {
        for (Room room : rooms) {
            boolean isVisible = room.inRoom(x, y) || room.inDoor(x, y); // Проверяем видимость комнаты

            for (Point points : room.getRoomPoint()) {
                for (Item item : items) {
                    int xRoom = points.getCoordinate().getX();
                    int yRoom = points.getCoordinate().getY();
                    Point pointEnemy = item.getPoint();
                    if (yRoom == pointEnemy.getCoordinate().getY() &&
                            xRoom == pointEnemy.getCoordinate().getX()) {
                        item.setVisibility(isVisible); // Устанавливаем видимость в зависимости от комнаты
                    }
                }
            }
        }
    }

    /// Видимость для противников
    private void visibleEnemyPoints(int x, int y) {
        for (Room room : rooms) {
            boolean isVisible = room.inRoom(x, y) || room.inDoor(x, y); // Проверяем видимость комнаты

            for (Point points : room.getRoomPoint()) {
                for (Enemy enemy : enemies) {
                    int xRoom = points.getCoordinate().getX();
                    int yRoom = points.getCoordinate().getY();
                    Point pointEnemy = enemy.getPoint();
                    if (yRoom == pointEnemy.getCoordinate().getY() &&
                            xRoom == pointEnemy.getCoordinate().getX()) {
                        enemy.setVisibility(isVisible); // Устанавливаем видимость в зависимости от комнаты
                    }
                }
            }
        }
    }

    /// Видимость для дверей
    private void visibleDoorPoints() {
        for (Room room : rooms) {
            if (room.getVisibleRoom()) {
                for (Corridor corridor : corridors) {
                    for (Point point : corridor.getPoint()) {
                        if (room.inDoor(point.getCoordinate().getX(), point.getCoordinate().getY()))
                            point.setVisibility(true);
                    }
                }
            }
        }
    }

    /// Видимость для точек комнаты
    private void visibleRoomPointsAndWall(int x, int y) {
        for (Room room : rooms) {
            if (room.inRoom(x, y) || room.inDoor(x, y)) {
                room.setVisibleRoom(true);
                room.setVisibleWalls(true);
                room.setVisibleRoomPoints(true);

            } else {
                room.setVisibleRoomPoints(false);
            }
        }
    }

    /// Видимость для корридоров
    private void visibleCorridorsPoints(int x, int y) {
        for (Corridor corridor : corridors) {
            for (Point point : corridor.getPoint()) {
                int pointX = point.getCoordinate().getX();
                int pointY = point.getCoordinate().getY();

                if (Math.abs(pointX - x) + Math.abs(pointY - y) == 1) {
                    point.setVisibility(true);
                }
            }
        }
    }

    /// Чит
    public void allVisible() {

        for (Room room : rooms) {
            room.setVisibleRoom(true);
            room.setVisibleWalls(true);
            room.setVisibleRoomPoints(true);
        }

        for (Corridor corridor : corridors) {
            for (Point point : corridor.getPoint()) {
                point.setVisibility(true);
            }
        }

        for (Room room : rooms) {
            if (room.getVisibleRoom()) {
                for (Corridor corridor : corridors) {
                    for (Point point : corridor.getPoint()) {
                        point.setVisibility(true);
                    }
                }
            }
        }

        for (Enemy enemy : enemies) {
            Point pointEnemy = enemy.getPoint();
            pointEnemy.setVisibility(true);
        }

        for (Item item : items) {
            Point points = item.getPoint();
            points.setVisibility(true);
        }

        Point point = getExitLevel();
        point.setVisibility(true);


    }


    /// Создание дверей (координаты конца корридора это дверь)
    private void createDoors() {
        for (Corridor corridor : corridors) {
            List<Point> corridorPoints = corridor.getPoint();
            Point begin = corridorPoints.get(0);
            Point end = corridorPoints.get(corridorPoints.size() - 1);
            begin.setVisibility(false);
            end.setVisibility(false);
            List<Point> doorPoints = new ArrayList<>();
            doorPoints.add(begin);
            doorPoints.add(end);
            doors.add(new Door(doorPoints));
        }
    }

    //SET
    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    public void setCorridors(List<Corridor> corridors) {
        this.corridors = corridors;
    }

    public void setDoors(List<Door> doors) {
        this.doors = doors;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public void setExitLevel(Point exitLevel) {
        this.exitLevel = exitLevel;
    }

    public void setEnemies(List<Enemy> enemies) {
        this.enemies = enemies;
    }

    public void setEnemyCount(int enemyCount) {
        this.enemyCount = enemyCount;
    }

    public void setLevelNumber(int levelNumber) {
        this.levelNumber = levelNumber;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }


    //GET
    public int getLevelNumber() {
        return levelNumber;
    }

    public int getItemCount() {
        return itemCount;
    }

    public int getEnemyCount() {
        return enemyCount;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public List<Corridor> getCorridors() {
        return corridors;
    }

    public List<Door> getDoors() {
        return doors;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public List<Item> getItems() {
        return items;
    }

    public Point getExitLevel() {
        return exitLevel;
    }
}











