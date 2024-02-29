package com.chess;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Tile extends Rectangle {
    public Tile(boolean light, int r, int c) {
        setWidth(GameView.TILE_SIZE);
        setHeight(GameView.TILE_SIZE);

        relocate(r * GameView.TILE_SIZE, c * GameView.TILE_SIZE);

        setFill(light ? Color.valueOf("#feb") : Color.valueOf("#582"));
    }
}
