package com.chess;

public class Move {
    // Regular Move Vars
    public Piece movedPiece;
    public Piece capturedPiece;
    public int startC;
    public int startR;
    public int endC;
    public int endR;
    // Castling Vars
    public Piece rookMoved;
    public int rEndR;
    public int rEndC;
    public int rStartR;
    public int rStartC;
    // Promotion Var
    public boolean promotion;

    // Main move constructor
    public Move(Piece movedPiece, Piece capturedPiece, int startR, int startC, int endR, int endC) {
        this.movedPiece = movedPiece;
        this.capturedPiece = capturedPiece;
        this.startC = startC;
        this.startR = startR;
        this.endC = endC;
        this.endR = endR;
    }

    // Castle move constructor
    public Move(Piece movedPiece, Piece capturedPiece, int startR, int startC, int endR, int endC, Piece rookMoved, int rEndR, int rEndC, int rStartR, int rStartC) {
        this.movedPiece = movedPiece;
        this.capturedPiece = capturedPiece;
        this.startC = startC;
        this.startR = startR;
        this.endC = endC;
        this.endR = endR;
        this.rookMoved = rookMoved;
        this.rStartR = rStartR;
        this.rStartC = rStartC;
        this.rEndR = rEndR;
        this.rEndC = rEndC;
    }

    // Promotion Constructor
    public Move(Piece movedPiece, Piece capturedPiece, int startR, int startC, int endR, int endC, boolean promotion) {
        this.movedPiece = movedPiece;
        this.capturedPiece = capturedPiece;
        this.startC = startC;
        this.startR = startR;
        this.endC = endC;
        this.endR = endR;
        this.promotion = promotion;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Move move = (Move) obj;
        return startC == move.startC &&
                startR == move.startR &&
                endC == move.endC &&
                endR == move.endR &&
                movedPiece.equals(move.movedPiece) &&
                (capturedPiece == null ? move.capturedPiece == null : capturedPiece.equals(move.capturedPiece)) &&
                (rookMoved == null ? move.rookMoved == null : rookMoved.equals(move.rookMoved)) &&
                rEndR == move.rEndR &&
                rEndC == move.rEndC &&
                promotion == move.promotion;
    }
}
