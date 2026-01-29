package domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Room {

    public static final int MAP_HEIGHT = 21;
    public static final int MAP_WIDTH = 78;
    public static final int SECTOR_MAP_WIDTH = 26;
    public static final int SECTOR_MAP_HEIGHT = 7;
    private boolean visibleRoom;

    private List<Point> walls;
    private List<Point> roomPoint;

    private Coordinate topLeft;
    private Coordinate topRight;
    private Coordinate botRight;
    private Coordinate botLeft;

    private int width;
    private int height;
    private int xCentr;
    private int yCentr;

    Room() {
    }


    Room(int i, int j) {
        Random random = new Random();
        this.visibleRoom = false;
        this.width = generateWidth(random);
        this.height = generateHeight(random);
        this.topLeft = generateCoordinateTopLeft(i, j);
        this.topRight = generateCoordinateTopRight();
        this.botLeft = generateCoordinateBotLeft();
        this.botRight = generateCoordinateBotRight();
        this.walls = generateWalls();
        this.roomPoint = generateRoom();
        this.xCentr = (topLeft.getX() + botRight.getX()) / 2;
        this.yCentr = (topLeft.getY() + botRight.getY()) / 2;
    }

    /// Рандомная генерация ширины комнаты
    private int generateWidth(Random random) {
        return random.nextInt(SECTOR_MAP_WIDTH - 8) + 7;
    }

    /// Рандомная генерация высоты комнаты
    private int generateHeight(Random random) {
        return random.nextInt(SECTOR_MAP_HEIGHT - 5) + 4;
    }

    /// Рандомная генерация X и Y левого верхнего угла комнаты
    private Coordinate generateCoordinateTopLeft(int i, int j) {
        Random random = new Random();
        int x;
        int y;
        if (this.width == 24) x = 1 + SECTOR_MAP_WIDTH * j;
        else x = random.nextInt(SECTOR_MAP_WIDTH - this.width - 2) + 1 + SECTOR_MAP_WIDTH * j;
        if (this.height == 5) y = 1 + SECTOR_MAP_HEIGHT * i;
        else y = random.nextInt(SECTOR_MAP_HEIGHT - this.height - 2) + 1 + SECTOR_MAP_HEIGHT * i;
        return new Coordinate(x, y);
    }

    /// Расчет координат правого нижнего угла комнаты на основе координат левого верхнего угла
    private Coordinate generateCoordinateBotRight() {
        return new Coordinate(this.topLeft.getX() + this.width - 1, this.topLeft.getY() + this.height - 1);
    }

    private Coordinate generateCoordinateBotLeft() {
        return new Coordinate(this.topLeft.getX(), this.topLeft.getY() + this.height - 1);
    }

    private Coordinate generateCoordinateTopRight() {
        return new Coordinate(this.topLeft.getX() + this.width - 1, this.topLeft.getY());
    }

    /// Генерация стен комнат
    private List<Point> generateWalls() {
        List<Point> walls = new ArrayList<>();
        for (int i = topLeft.getY(); i < topLeft.getY() + height; i++) {
            for (int j = topLeft.getX(); j < topLeft.getX() + width; j++) {
                if (i == topLeft.getY() || i == topLeft.getY() + height - 1 ||
                        j == topLeft.getX() || j == topLeft.getX() + width - 1) {
                    walls.add(new Point(new Coordinate(j, i), false));
                }
            }
        }
        return walls;
    }

    /// Генерация точек комнаты
    private List<Point> generateRoom() {
        List<Point> roomPoints = new ArrayList<>();
        for (int i = topLeft.getY(); i < topLeft.getY() + height; i++) {
            for (int j = topLeft.getX(); j < topLeft.getX() + width; j++) {
                if (i == topLeft.getY() || i == topLeft.getY() + height - 1 ||
                        j == topLeft.getX() || j == topLeft.getX() + width - 1) {
                } else {
                    roomPoints.add(new Point(new Coordinate(j, i), false));
                }
            }
        }
        return roomPoints;
    }

    /// Устанаввливает видимость стен
    public void setVisibleWalls(boolean visible) {
        for (Point point : walls) {
            point.setVisibility(visible);
        }
    }

    /// Устанавливает видимость точек комнаты
    public void setVisibleRoomPoints(boolean visible) {
        for (Point point : roomPoint) {
            point.setVisibility(visible);
        }
    }

    /// Метод проверки нахождения координат внутри комнаты
    public boolean inRoom(int x, int y) {
        for (Point roomPoint : roomPoint) {
            if (roomPoint.getCoordinate().getX() == x && roomPoint.getCoordinate().getY() == y) {
                return true;
            }
        }
        return false;
    }

    /// Метод проверки нахождения координат на двери
    public boolean inDoor(int x, int y) {
        for (Point wall : walls) {
            if (wall.getCoordinate().getX() == x && wall.getCoordinate().getY() == y)
                return true;
        }
        return false;
    }

    public Integer getRoomNumberForHero(int heroX, int heroY) {
        if (!inRoom(heroX, heroY) && !inDoor(heroX, heroY)) {
            return null; // Герой не в комнате и не в стене, вернуть null или обработать по-другому
        }

        int roomNumberJ = (heroX - 1) / SECTOR_MAP_WIDTH; // Вычисляем номер сектора по X координате
        int roomNumberI = (heroY - 1) / SECTOR_MAP_HEIGHT; // Вычисляем номер сектора по Y координате

        // Преобразуем номер комнаты в int, используя формулу: номер_строки * кол-во_столбцов + номер_столбца
        int numberOfColumns = MAP_WIDTH / SECTOR_MAP_WIDTH; // Количество секторов по горизонтали
        return roomNumberI * numberOfColumns + roomNumberJ;
    }

    public void setVisibleRoom(boolean visible) {
        this.visibleRoom = visible;
    }

    public void setRoomPoint(List<Point> roomPoint) {
        this.roomPoint = roomPoint;
    }

    public void setWallPoint(List<Point> wallPoint) {
        this.walls = wallPoint;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setBotRight(Coordinate botRight) {
        this.botRight = botRight;
    }

    public void setBotLeft(Coordinate botLeft) {
        this.botLeft = botLeft;
    }

    public void setTopRight(Coordinate topRight) {
        this.topRight = topRight;
    }

    public void setTopLeft(Coordinate topLeft) {
        this.topLeft = topLeft;
    }

    public void setXCenter(int xCentr) {
        this.xCentr = xCentr;
    }

    public void setYCenter(int yCentr) {
        this.yCentr = yCentr;
    }

    public boolean getVisibleRoom() {
        return visibleRoom;
    }

    public List<Point> getRoomPoint() {
        return roomPoint;
    }

    public List<Point> getWallPoint() {
        return walls;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Coordinate getBotRight() {
        return botRight;
    }

    public Coordinate getBotLeft() {
        return botLeft;
    }

    public Coordinate getTopLeft() {
        return topLeft;
    }

    public Coordinate getTopRight() {
        return topRight;
    }

    public int getXCenter() {
        return xCentr;
    }

    public int getYCenter() {
        return yCentr;

    }
}
