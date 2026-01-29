package view;

import com.googlecode.lanterna.TextColor;
import domain.Hero;
import domain.enemy.Enemy;
import domain.item.Item;

import java.util.*;

public class GameEvent {

    private final int maxEventsSize;
    Deque<TextPart> events;

    public GameEvent(int maxEventsSize) {
        this.maxEventsSize = maxEventsSize;
        events = new ArrayDeque<>();
    }

    public void addEvent(String action, Object entity) {
        events.addFirst(createTextParts(entity, action));
        resizeIfNecessary();
    }

    public void addColoredMessage(String message, TextColor textColor) {
        List<ColoredText> coloredTexts = new ArrayList<>();
        coloredTexts.add(new ColoredText(message, textColor, TextColor.ANSI.DEFAULT));
        events.addFirst(new TextPart(coloredTexts));
        resizeIfNecessary();
    }

    public void addDamageEvent(Object entity, String action) {
        events.addFirst(createTextParts(entity, action));
        resizeIfNecessary();
    }

    private TextPart createTextParts(Object entity, String action) {
        List<ColoredText> coloredTexts = new ArrayList<>();
        coloredTexts.add(new ColoredText(action, TextColor.ANSI.WHITE, TextColor.ANSI.DEFAULT));
        coloredTexts.add(convertToColoredText(entity));
        return new TextPart(coloredTexts);
    }

    private ColoredText convertToColoredText(Object obj) {
        return switch (obj) {
            case Hero hero -> new ColoredText(hero.getName(), TextColor.ANSI.WHITE, TextColor.ANSI.DEFAULT);
            case Item item -> new ColoredText(item.getType(), item.getColor(), TextColor.ANSI.DEFAULT);
            case Enemy enemy -> new ColoredText(enemy.getType(), enemy.getColor(), TextColor.ANSI.DEFAULT);
        default -> throw new IllegalStateException("Unexpected value: " + obj);
    };
}

private void resizeIfNecessary() {
    if (events.size() > maxEventsSize) {
        events.removeLast();
    }
}

public Deque<TextPart> getEvents() {
    return events;
}

public static class TextPart {
    List<ColoredText> coloredTexts;

    public TextPart(List<ColoredText> coloredTexts) {
        this.coloredTexts = coloredTexts;
    }

    public List<ColoredText> getColoredTexts() {
        return coloredTexts;
    }
}

public static class ColoredText {
    String text;
    TextColor color;
    TextColor backgroundColor;

    public ColoredText(String text, TextColor textColor, TextColor backgroundColor) {
        this.text = text;
        this.color = textColor;
        this.backgroundColor = backgroundColor;
    }

    public static ColoredText createColoredText(String text) {
        TextColor textColor;
        if (text == null) {
            return new ColoredText("Пусто", TextColor.ANSI.WHITE, TextColor.ANSI.DEFAULT);
        }
        textColor = switch (text) {
            case "elixir", "Эликсиры", "blue", "blue elixir" -> TextColor.ANSI.BLUE;
            case "scroll", "yellow", "Свитки", "yellow elixir" -> TextColor.ANSI.YELLOW;
            case "meat", "chapman" -> TextColor.ANSI.CYAN;
            case "green", "Еда", "sword", "agility", "green elixir", "scroll agility" -> TextColor.ANSI.GREEN;
            case "Оружие", "axe", "red", "strength", "red elixir", "scroll strength" -> TextColor.ANSI.RED;
            default -> TextColor.ANSI.WHITE;
        };
        return new ColoredText(text, textColor, TextColor.ANSI.WHITE);
    }

    public String getText() {
        return text;
    }

    public TextColor getColor() {
        return color;
    }

    public TextColor getBackgroundColor() {
        return backgroundColor;
    }
}
}