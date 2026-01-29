package view;

import com.googlecode.lanterna.TextColor;

public record MyButton(GameEvent.ColoredText coloredText, TextColor actionColor, OnClick onClick) {

    public void run() {
        onClick.onClick();
    }
}