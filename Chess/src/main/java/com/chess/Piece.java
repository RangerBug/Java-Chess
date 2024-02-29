package com.chess;

import javafx.scene.image.ImageView;
import java.util.ArrayList;
import java.util.List;

public class Piece {
    public Type type;
    public Color color;
    public ImageView image;
    public int locC, locR;
    public final GameEngine game;

    public Piece (Type type, Color color, ImageView image, int locR, int locC, GameEngine game) {
        this.type = type;
        this.color = color;
        this.image = image;

        this.locC = locC;
        this.locR = locR;

        this.image.setX(locC * GameView.TILE_SIZE);
        this.image.setY(locR * GameView.TILE_SIZE);

        this.game = game;
        game.board[locR][locC] = this;
    }

    public List<Move> loadPsudoMoves() {
        return switch (this.type) {
            case PAWN -> getValidPawnMoves();
            case ROOK -> getValidRookMoves();
            case KNIGHT -> getValidKnightMoves();
            case BISHOP -> getValidBishopMoves();
            case QUEEN -> getValidQueenMoves();
            case KING -> getValidKingMoves();
        };
    }

    private List<Move> getValidPawnMoves() {
        List<Move> validPawnMoves = new ArrayList<>();
        int direction, startLine;

        // Determine pawn's direction and starting line
        if (game.playAsWhite) {
            direction = (this.color == Color.WHITE) ? -1 : 1;
            startLine = (this.color == Color.WHITE) ? 6 : 1;
        } else {
            direction = (this.color == Color.WHITE) ? 1 : -1;
            startLine = (this.color == Color.WHITE) ? 1 : 6;
        }

        // Move forward one square
        if (game.getPieceAt(this.locR + direction, this.locC) == null) {
            Move move = new Move(this, null, this.locR, this.locC, this.locR + direction, this.locC);
            if (this.locR + direction == 0 || this.locR + direction == 7) {
                move.promotion = true;
            }
            validPawnMoves.add(move);
        }

        // Move forward two squares
        if ((this.locR == startLine) && (game.getPieceAt(this.locR + 2*direction, this.locC) == null) && (game.getPieceAt(this.locR + direction, this.locC) == null)) {
            validPawnMoves.add(new Move(this, null, this.locR, this.locC, this.locR + 2*direction, this.locC));
        }

        // Capture diagonally
        for (int i = -1; i <= 1; i += 2) {
            if (withinBoard(this.locC + i, this.locR + direction)) {
                Piece capture = game.getPieceAt(this.locR + direction, this.locC + i);
                if (capture != null && capture.color != this.color) {
                    Move move = new Move(this, capture, this.locR, this.locC, this.locR + direction, this.locC + i);
                    if (this.locR + direction == 0 || this.locR + direction == 7) {
                        move.promotion = true;
                    }
                    validPawnMoves.add(move);
                }
            }
        }

        // En-Passant
        if (game.lastMove != null) {
            if ((game.lastMove.movedPiece.type == Type.PAWN)
                    && (Math.abs(game.lastMove.startR - game.lastMove.endR) == 2)
                    && (Math.abs(game.lastMove.endC - this.locC) == 1)
                    && (game.lastMove.endR == this.locR)) {

                // Set capture direction
                int captureDirection;
                if (game.playAsWhite) {
                    captureDirection = (this.color == Color.WHITE) ? -1 : 1;
                } else {
                    captureDirection = (this.color == Color.BLACK) ? -1 : 1;
                }

                // Add move
                validPawnMoves.add(new Move(this, game.lastMove.movedPiece, this.locR, this.locC, game.lastMove.endR + captureDirection, game.lastMove.endC));
            }
        }

        return validPawnMoves;
    }


    private List<Move> getValidRookMoves() {
        List<Move> validRookMoves = new ArrayList<>();
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        int endC, endR;

        for (int[] direction : directions) {
            int j = 1;  // Multiplier

            while (true) {
                endC = this.locC + direction[0] * j;
                endR = this.locR + direction[1] * j;

                // Break if the move is out of the board
                if (!withinBoard(endR, endC)) {
                    break;
                }

                Piece capture = game.getPieceAt(endR, endC);
                if (capture != null && capture.color == this.color) {
                    break;
                }

                validRookMoves.add(new Move(this, capture, this.locR, this.locC, endR, endC));

                // If there's a piece of the opposite color, break, you can't move past it
                if (capture != null && capture.color != this.color) {
                    break;
                }

                j++;
            }
        }

        return validRookMoves;
    }

    private List<Move> getValidKnightMoves() {
        List<Move> validKnightMoves = new ArrayList<>();
        int[][] directions = {{-2, -1}, {-2, 1}, {-1, -2}, {-1, 2}, {2, -1}, {2, 1}, {1, -2}, {1, 2}};
        int endC, endR;

        for (int[] direction : directions) {
            endC = direction[0] + this.locC;
            endR = direction[1] + this.locR;


            if (withinBoard(endR, endC)) {
                Piece capture = game.getPieceAt(endR, endC);

                if (capture == null || capture.color != this.color) {
                    validKnightMoves.add(new Move(this, capture, this.locR, this.locC, endR, endC));
                }
            }

        }
        return validKnightMoves;
    }

    private List<Move> getValidBishopMoves() {
        List<Move> validBishopMoves = new ArrayList<>();
        int[][] directions = {{1, 1}, {1, -1}, {-1, -1}, {-1, 1}};
        int endC, endR;

        for (int[] direction : directions) {
            int j = 1;  // Multiplier


            while (true) {
                endC = this.locC + direction[0] * j;
                endR = this.locR + direction[1] * j;

                // Break if the move is out of the board
                if (!withinBoard(endR, endC)) {
                    break;
                }

                Piece capture = game.getPieceAt(endR, endC);
                if (capture != null && capture.color == this.color) {
                    break;
                }

                validBishopMoves.add(new Move(this, capture, this.locR, this.locC, endR, endC));

                // If there's a piece of the opposite color, break, you can't move past it
                if (capture != null && capture.color != this.color) {
                    break;
                }

                j++;
            }

        }
        return validBishopMoves;
    }

    private List<Move> getValidQueenMoves() {
        List<Move> validQueenMoves = new ArrayList<>();
        validQueenMoves.addAll(getValidRookMoves());
        validQueenMoves.addAll(getValidBishopMoves());
        return validQueenMoves;
    }

    private List<Move> getValidKingMoves() {
        List<Move> validKingMoves = new ArrayList<>();
        int[][] directions = {{1, -1}, {1, 0}, {1, 1}, {0, 1}, {0, -1}, {-1, -1}, {-1, 0}, {-1, 1}};
        int endC, endR;

        for (int[] direction : directions) {
            endC = direction[0] + this.locC;
            endR = direction[1] + this.locR;

            if (withinBoard(endR, endC)) {
                Piece capture = game.getPieceAt(endR, endC);
                if ((capture == null || capture.color != this.color)) {
                    validKingMoves.add(new Move(this, capture, this.locR, this.locC, endR, endC));
                }
            }
        }
        return validKingMoves;
    }

    private boolean withinBoard(int r, int c) {
        return r >= 0 && r < 8 && c >= 0 && c < 8;
    }

    public enum Type {
        PAWN,
        ROOK,
        KNIGHT,
        BISHOP,
        QUEEN,
        KING
    }

    public enum Color {
        WHITE,
        BLACK
    }
}
