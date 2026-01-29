package domain.enemy;

import com.fasterxml.jackson.annotation.*;
import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import domain.Coordinate;
import domain.GameSession;
import domain.Hero;
import domain.Point;
import domain.Room;

import java.util.Random;


@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Ghost.class, name = "ghost"),
        @JsonSubTypes.Type(value = Ogre.class, name = "ogre"),
        @JsonSubTypes.Type(value = SerpentMage.class, name = "serpentMage"),
        @JsonSubTypes.Type(value = Vampire.class, name = "vampire"),
        @JsonSubTypes.Type(value = Zombie.class, name = "zombie")
})


public abstract class Enemy {

    public static final int GHOST = 1;
    public static final int OGRE = 2;
    public static final int SERPENTMAGE = 3;
    public static final int VAMPIRE = 4;
    public static final int ZOMBIE = 5;


    //Characteristic
    protected int agility;
    protected int strength;
    protected int hostility;
    protected int health;

    private String type;
    private TextCharacter enemySymbol;
    private GameSession gameSession;
    private TextColor color;
    //Location
    private Point point;
    private int currentRoom;
    private Coordinate startCoordinate;
    private AIKa state = AIKa.IDLE;

    enum AIKa {
        IDLE, CHASE, RETURNING, ATTACK
    }

    Enemy() {
    }                                                                   // @JsonCreator указывает Jackson, какой конструктор использовать для создания экземпляра объекта.

                                                                      // @JsonProperty указывает Jackson, какое JSON-поле соответствует какому параметру конструктора.
    @JsonCreator
    Enemy(@JsonProperty("coordinate") Coordinate coordinate,
          @JsonProperty("type") String type,
          @JsonProperty("enemySymbol") char enemySymbol,
          @JsonProperty("agility") int agility,
          @JsonProperty("strength") int strength,
          @JsonProperty("hostility") int hostility,
          @JsonProperty("health") int health,
          @JsonProperty("currentRoom") int currentRoom,
          @JsonProperty("visibility") boolean visibility,
          TextColor color) {

        point = new Point(coordinate, visibility);
        startCoordinate = new Coordinate(point.getCoordinate().getX(), point.getCoordinate().getY());
        this.type = type;
        this.hostility = hostility;
        this.health = health;
        this.enemySymbol = TextCharacter.fromCharacter(enemySymbol, TextColor.ANSI.RED, TextColor.ANSI.DEFAULT)[0];
        this.agility = agility;
        this.strength = strength;
        this.currentRoom = currentRoom;
        this.color = color;
    }

    public void takeDamage(int damage) {
        setHealthMinusDamage(damage);
    }
    /// Расчет вероятности попадания
    private boolean hitProbCal() {
        Random random = new Random();
        Hero hero = gameSession.getHero();
        double baseChance = 0.5, agilityFactor = 0.01, minHitChance = 0.1, maxHitChance = 0.9;
        double hitChance = baseChance + (agility - hero.getStats().getAgility() * agilityFactor);
        hitChance = Math.min(maxHitChance, Math.max(minHitChance, hitChance));
        return random.nextDouble() < hitChance;
    }

    /// Абстрактный метод расчета урона для enemy
    public abstract int damageCalc();

    /// Обновляет состояния ботов
    public void update(int heroPosX, int heroPosY, GameSession gameSession) {
        this.gameSession = gameSession;
        int distance = checkDistance(heroPosX, heroPosY);
        boolean heroVisible = heroOnVision(heroPosX, heroPosY);
        switch (state) {
            case IDLE -> {
                if (heroVisible) {
                    state = AIKa.CHASE;
                } else if (point.getCoordinate().getX() != startCoordinate.getX()
                        || point.getCoordinate().getY() != startCoordinate.getY()) {
                    state = AIKa.RETURNING;
                }
            }

            case RETURNING -> {
                if (point.getCoordinate().getX() == startCoordinate.getX()
                        && point.getCoordinate().getY() == startCoordinate.getY()) {
                    state = AIKa.IDLE;
                } else {
                    moveEnemyTowards(startCoordinate.getX(), startCoordinate.getY());
                }

            }
            case CHASE -> {
                if (heroVisible) {
                    moveEnemyTowards(heroPosX, heroPosY);
                    if (distance <= 1) {
                        state = AIKa.ATTACK;
                    }
                } else {
                    state = AIKa.IDLE;
                }
            }

            case ATTACK -> {
                attackToHero(gameSession);
                if (distance > 1) {
                    state = AIKa.CHASE;
                } else if (!heroVisible) {
                    state = AIKa.IDLE;

                }

            }
            ///
        }

    }

    /// Атака противником игрока
    public void attackToHero(GameSession gameSession) {
        boolean hitChance = hitProbCal();
        if (hitChance) {
            double damage = damageCalc();
            gameSession.getGameEvent().addDamageEvent(this, String.format("%s %.0f %s", "Получено", damage, "урона от "));
            gameSession.getHero().takeDamage(damage);
        } else {
            gameSession.getGameEvent().addDamageEvent(this, "MISS!!! ");
        }
    }

    /// Пошаговое передвжиение по карте для enemy
    private void moveEnemyTowards(int x, int y) {
        int deltaX = x - point.getCoordinate().getX();
        int deltaY = y - point.getCoordinate().getY();

        int moveX = 0;
        int moveY = 0;

        if (Math.abs(deltaX) > Math.abs(deltaY)) {
            moveX = deltaX > 0 ? 1 : -1;
        } else if (Math.abs(deltaY) > 0) {
            moveY = deltaY > 0 ? 1 : -1;
        }
        if (validateEnemyMove(moveX, moveY)) {
            enemyMove(moveX, moveY);
        }
    }

    public boolean validateEnemyMove(int dx, int dy) {
        int newX = dx + this.point.getCoordinate().getX();
        int newY = dy + this.point.getCoordinate().getY();

        if (newX >= Room.MAP_WIDTH || newY >= Room.MAP_HEIGHT || newX < 0 || newY < 0 || newX == gameSession.getHero().getCoordinate().getX() || newY == gameSession.getHero().getCoordinate().getY()) {
            return false;
        }

        char newBoardCell = gameSession.getCell(newY, newX).getCharacterString().charAt(0);

        return newBoardCell == '.' || newBoardCell == Symbols.SPADES
                || newBoardCell == Symbols.INVERSE_BULLET || newBoardCell == Symbols.HEART
                || newBoardCell == '$' || newBoardCell == '✉' || newBoardCell == 'a' || newBoardCell == 's';
    }

    public int checkDistance(int heroPosX, int heroPosY) {
        int deltaX = heroPosX - point.getCoordinate().getX();
        int deltaY = heroPosY - point.getCoordinate().getY();

        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        return (int) distance;
    }

    public void enemyMove(int dx, int dy) {
        point.getCoordinate().setNewPlusOld(dx, dy);
    }

    private boolean heroOnVision(int posX, int posY) {
        int heroRoom = gameSession.getCurrentRoomForEntity(new Coordinate(posX, posY));
        return heroRoom == currentRoom && heroRoom != -1;
    }

    //SET
    public void setVisibility(boolean visibility) {
        this.point.setVisibility(visibility);
    }

    public void setCurrentRoom(int currentRoom) {
        this.currentRoom = currentRoom;
    }

    public void setHealthMinusDamage(int damage) {
        this.health = health - damage;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setHostility(int hostility) {
        this.hostility = hostility;
    }

    public void setStartCoordinate(Coordinate coordinate) {
        this.startCoordinate = coordinate;
    }

    public void setState(AIKa state) {
        this.state = state;
    }

    //GET
    @JsonIgnore
    public boolean getVisibility() {
        return point.getVisibility();
    }

    public TextColor getColor() {
        return color;
    }

    public Point getPoint() {
        return point;
    }

    public int getHostility() {
        return hostility;
    }

    @JsonIgnore
    public String getType() {
        return type;
    }

    public int getStrength() {
        return strength;
    }

    public int getHealth() {
        return health;
    }

    public int getCurrentRoom() {
        return currentRoom;
    }

    public int getAgility() {
        return agility;
    }

    public Coordinate getStartCoordinate() {
        return startCoordinate;
    }

    public AIKa getState() {
        return state;
    }
}