package com.chess;

import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class GameView {
    public Stage stage;
    public static final int SCREEN_HEIGHT = Chess.SCREEN_HEIGHT;
    public static final int SCREEN_WIDTH = Chess.SCREEN_WIDTH;
    public static final int TILE_SIZE = (SCREEN_HEIGHT) / 8;
    public static final int WIDTH = 8, HEIGHT = 8;
    private final Group tileGroup = new Group();
    private final Group pieceGroup = new Group();
    private final Group highlightGroup = new Group();
    private final Group lastMoveHighlightGroup = new Group();
    private final Rectangle highlight = new Rectangle(GameView.TILE_SIZE, GameView.TILE_SIZE);
    private final Rectangle highlight_start = new Rectangle(GameView.TILE_SIZE, GameView.TILE_SIZE);
    private final Rectangle highlight_end = new Rectangle(GameView.TILE_SIZE, GameView.TILE_SIZE);
    public boolean humanPlaying;
    public boolean playWhite;

    public void gameView(Stage stage, Chess mainView, boolean playWhite, boolean humanPlaying, boolean whiteToMove) {

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #2D2D2D;");
        Scene scene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT);
        this.stage = stage;
        this.humanPlaying = humanPlaying;
        this.playWhite = playWhite;

        GameEngine game = new GameEngine(playWhite, "classic", whiteToMove, scene, humanPlaying, this);

        highlight.setFill(javafx.scene.paint.Color.rgb(245, 214, 5, 0.4));
        highlight_start.setFill(javafx.scene.paint.Color.rgb(245, 214, 5, 0.4));
        highlight_start.setVisible(false);
        highlight_end.setFill(javafx.scene.paint.Color.rgb(245, 214, 5, 0.4));
        highlight_end.setVisible(false);
        if (!lastMoveHighlightGroup.getChildren().contains(highlight_start)) {
            lastMoveHighlightGroup.getChildren().add(highlight_start);
        }
        if (!lastMoveHighlightGroup.getChildren().contains(highlight_end)) {
            lastMoveHighlightGroup.getChildren().add(highlight_end);
        }

        drawBoard(game);

        root.getChildren().addAll(tileGroup, lastMoveHighlightGroup, highlightGroup, pieceGroup);

        // Buttons
        VBox buttonBox = new VBox();

        Button menuButton = new Button("Menu");
        menuButton.setPrefSize(300, 70);
        buttonBox.getChildren().add(menuButton);

        buttonBox.setAlignment(Pos.BOTTOM_RIGHT);
        buttonBox.setSpacing(10);

        buttonBox.setPadding(new Insets(0, 50, 30, 0));
        root.setRight(buttonBox);

        menuButton.setOnAction(actionEvent -> {
            tileGroup.getChildren().clear();
            highlightGroup.getChildren().clear();
            pieceGroup.getChildren().clear();
            //textGroup.getChildren().clear();
            game.clickBuffer = null;
            game.gameEnded = true;
            game.loop.stop();
            root.setTop(null);   // edit or remove this if doing overlay pane for end of game
            mainView.start(stage);
        });

        stage.setScene(scene);
        stage.show();
    }

    private void drawBoard(GameEngine game) {
        String piece;
        ImageView image;
        for (int c = 0; c < HEIGHT; c++) {
            for (int r = 0; r < WIDTH; r++) {

                Tile tile = new Tile((r+c) % 2 == 0, r, c);
                tileGroup.getChildren().add(tile);
                piece = game.startBoard[r][c];

                if (!piece.equals("--")) {
                    Piece.Color color = piece.charAt(0) == 'w' ? Piece.Color.WHITE : Piece.Color.BLACK;
                    Piece.Type type = switch (piece.charAt(1)) {
                        case 'P' -> Piece.Type.PAWN;
                        case 'N' -> Piece.Type.KNIGHT;
                        case 'B' -> Piece.Type.BISHOP;
                        case 'Q' -> Piece.Type.QUEEN;
                        case 'K' -> Piece.Type.KING;
                        case 'R' -> Piece.Type.ROOK;
                        default -> throw new IllegalStateException("Unexpected value: " + piece.charAt(1));
                    };

                    image = createImage(piece);
                    Piece newPiece = new Piece(type, color, image, r, c, game);
                    game.board[r][c] = newPiece;
                }
            }
        }
    }

    public void drawHighlights(Piece piece, int hx, int hy, GameEngine game) {
        // Drawing highlight
        highlight.setX(hx);
        highlight.setY(hy);
        if (!highlightGroup.getChildren().contains(highlight)){highlightGroup.getChildren().add(highlight);}

        // Drawing possible moves
        int r, c;
        List<Move> validMoves = game.getLegalMovesForPiece(piece);
        for (Move validMove : validMoves) {
            Circle dot = new Circle(15);
            dot.setFill(javafx.scene.paint.Color.rgb(0, 0, 0, .2));
            r = validMove.endR * TILE_SIZE + (TILE_SIZE / 2);
            c = validMove.endC * TILE_SIZE + (TILE_SIZE / 2);
            dot.setCenterX(c);
            dot.setCenterY(r);
            // Checking last piece for larger dot
            Piece checkCapturePiece = game.getPieceAt(validMove.endR, validMove.endC);
            if (checkCapturePiece != null && checkCapturePiece.color != piece.color) {
                dot.setRadius(40);
                dot.setStroke(javafx.scene.paint.Color.rgb(0, 0, 0, .2));
                dot.setStrokeWidth(7);
                dot.setFill(Color.TRANSPARENT);
            }

            if (!highlightGroup.getChildren().contains(dot)) {highlightGroup.getChildren().add(dot);}
        }
    }

    public void drawLastMoveHighlights(GameEngine game) {
        highlight_start.setVisible(true);
        highlight_end.setVisible(true);
        if (game.lastMove != null) {
            // Start square
            highlight_start.setX(game.lastMove.startC * TILE_SIZE);
            highlight_start.setY(game.lastMove.startR * TILE_SIZE);
            // End square
            highlight_end.setX(game.lastMove.endC * TILE_SIZE);
            highlight_end.setY(game.lastMove.endR * TILE_SIZE);
        }
    }

    public void hideHighlights() {
        highlightGroup.getChildren().clear();
    }

    public ImageView createImage(String piece) {
        Image image = new Image(getClass().getResource("/images/pieceStyle1/" + piece + ".png").toExternalForm());
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(TILE_SIZE);
        imageView.setFitHeight(TILE_SIZE);
        pieceGroup.getChildren().add(imageView);
        return imageView;
    }

    public void removeImage(ImageView image) {
        pieceGroup.getChildren().remove(image);
    }

    public String openPromoteWin(String color, AnimationTimer loop) {
        loop.stop();
        Stage promoteWinStage = new Stage();
        promoteWinStage.initOwner(this.stage);
        promoteWinStage.initModality(Modality.APPLICATION_MODAL);
        promoteWinStage.setResizable(false);
        promoteWinStage.setTitle("Promotion");

        HBox hbox = new HBox();

        // Load images for buttons
        ImageView queen = createImage(color + "Q");
        ImageView bishop = createImage(color + "B");
        ImageView knight = createImage(color + "N");
        ImageView rook = createImage(color + "R");

        // Buttons
        Button queenButton = new Button("", queen);
        Button bishopButton = new Button("", bishop);
        Button knightButton = new Button("", knight);
        Button rookButton = new Button("", rook);

        // Button Size
        double buttonSize = 50;
        queenButton.setPrefSize(buttonSize, buttonSize);
        bishopButton.setPrefSize(buttonSize, buttonSize);
        knightButton.setPrefSize(buttonSize, buttonSize);
        rookButton.setPrefSize(buttonSize, buttonSize);

        hbox.getChildren().addAll(queenButton, bishopButton, knightButton, rookButton);

        //Scene promoteScene = new Scene(hbox, 464, 107);
        Scene promoteScene = new Scene(hbox, 500, 150);
        promoteWinStage.setScene(promoteScene);

        // Use AtomicReference for a reference that can be final but still allow its contents to be changed
        AtomicReference<String> selectedImage = new AtomicReference<>("Q");

        // Event handlers for each button
        queenButton.setOnAction(e -> {
            promoteWinStage.close();
        });

        bishopButton.setOnAction(e -> {
            selectedImage.set("B");
            promoteWinStage.close();
        });

        knightButton.setOnAction(e -> {
            selectedImage.set("N");
            promoteWinStage.close();
        });

        rookButton.setOnAction(e -> {
            selectedImage.set("R");
            promoteWinStage.close();
        });

        promoteWinStage.showAndWait();
        loop.start();
        return selectedImage.get();
    }

    private String getPieceNotation(Piece piece) {
        String colorNotation = piece.color == Piece.Color.WHITE ? "w" : "b";
        String typeNotation;
        switch (piece.type) {
            case PAWN: typeNotation = "P"; break;
            case KNIGHT: typeNotation = "N"; break;
            case BISHOP: typeNotation = "B"; break;
            case ROOK: typeNotation = "R"; break;
            case QUEEN: typeNotation = "Q"; break;
            case KING: typeNotation = "K"; break;
            default: throw new IllegalStateException("Unexpected piece type: " + piece.type);
        }
        return colorNotation + typeNotation;
    }


}
