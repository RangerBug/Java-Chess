package com.chess;

import java.util.List;
import java.util.Random;

public class ChessAI {
    public int movesSearched = 0;
    public GameEngine game;
    public GameView gameView;
    public ChessAI(GameEngine game, GameView gameView) {
        this.game = game;
        this.gameView = gameView;
    }
    public Move calculateAIMove(List<Move> moves) {
        double difficulty = Chess.slider.getValue();
        if (difficulty > 76) {
            return getBestMove(moves, 4);
        } else if (difficulty > 74.9 && difficulty < 75.1) {
            return getBestMove(moves, 4);
        } else if (difficulty > 49.9 && difficulty < 50.1) {
            return getBestMove(moves, 2);
        } else if (difficulty < 25.1 && difficulty > 24.9) {
            return getBestMove(moves, 2);
        }
        return findRandomMove(moves);
    }
    public Move findRandomMove(List<Move> moves) {
        Random rand = new Random();
        int randomInt = rand.nextInt(moves.size());
        return moves.get(randomInt);
    }

    public Move getBestMove(List<Move> moves, int depth) {
        Move calculatedMove = maxValue(game, moves, depth, Integer.MIN_VALUE, Integer.MAX_VALUE).move;
        System.out.println(movesSearched);
        movesSearched = 0;

        if (!moves.contains(calculatedMove)) {
            System.out.println("Does not contain");
            return moves.get(0);
        }
        return calculatedMove;
    }
    public Pair maxValue(GameEngine gameState, List<Move> moves, int depth, int alpha, int beta) {
        if (depth == 0 || moves.isEmpty()) {
            return new Pair(null, scoreBoard());
        }

        Move bestMove = null;
        int maxScore = Integer.MIN_VALUE;

        for (Move move : moves) {
            movesSearched++;
            gameState.move(move.startR, move.startC, move.endR, move.endC, gameView, false);
            int score = minValue(gameState, gameState.getLegalMoves(), depth - 1, alpha, beta).value;
            gameState.undoMove();

            if (score > maxScore) {
                maxScore = score;
                bestMove = move;
            }

            alpha = Math.max(alpha, score);
            if (beta <= alpha) {
                break;
            }
        }
        return new Pair(bestMove, maxScore);
    }

    public Pair minValue(GameEngine gameState, List<Move> moves, int depth, int alpha, int beta) {
        if (depth == 0 || moves.isEmpty()) {
            return new Pair(null, scoreBoard());
        }

        Move bestMove = null;
        int minScore = Integer.MAX_VALUE;

        for (Move move : moves) {
            movesSearched++;
            gameState.move(move.startR, move.startC, move.endR, move.endC, gameView, false);
            int score = maxValue(gameState, gameState.getLegalMoves(), depth - 1, alpha, beta).value;
            gameState.undoMove();

            if (score < minScore) {
                minScore = score;
                bestMove = move;
            }

            beta = Math.min(beta, score);
            if (beta <= alpha) {
                break;
            }
        }
        return new Pair(bestMove, minScore);
    }

    private int scoreBoard() {
        int score = 0;
        if (game.checkMate) {
            return Integer.MAX_VALUE;
        } else if (game.staleMate) {
            return 0;
        }
        for (int c = 0; c < 8; c++) {
            for (int r = 0; r < 8; r++) {
                Piece piece = game.board[r][c];
                if (piece == null) continue;

                int pieceScore = getPieceScore(piece.type, r, c);
                if ((game.whiteToMove && piece.color == Piece.Color.WHITE) || (!game.whiteToMove && piece.color == Piece.Color.BLACK)) {
                    score += pieceScore;
                } else if ((!game.whiteToMove && piece.color == Piece.Color.WHITE) || (game.whiteToMove && piece.color == Piece.Color.BLACK)) {
                    score -= pieceScore;
                }
            }
        }
        return score;
    }

    private int getPieceScore(Piece.Type type, int r, int c) {
        return switch (type) {
            case KING -> game.whiteToMove ? kingAsWScores[r][c] : kingAsBScores[r][c];
            case QUEEN -> 100 + queenScores[r][c];
            case BISHOP -> 30 + bishopScores[r][c];
            case KNIGHT -> 30 + knightScores[r][c];
            case ROOK -> 50 + rookScores[r][c];
            case PAWN -> 10 + (game.playAsWhite ? playAsScores[r][c] : oppScores[r][c]);
            default -> 0;
        };
    }

    int[][] kingAsWScores = {
            {1, 1, 5, 1, 1, 1, 5, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 5, 1, 1, 1, 5, 1}};

    int[][] kingAsBScores = {
            {1, 5, 1, 1, 1, 5, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 5, 1, 1, 1, 5, 1, 1}};

    int[][] queenScores = {
            {4, 3, 2, 1, 1, 2, 3, 4},
            {3, 4, 3, 2, 2, 3, 4, 3},
            {2, 3, 3, 1, 1, 3, 3, 2},
            {1, 2, 1, 1, 1, 1, 2, 1},
            {1, 2, 1, 1, 1, 1, 2, 1},
            {2, 3, 3, 1, 1, 3, 3, 2},
            {3, 4, 3, 2, 2, 3, 4, 3},
            {4, 3, 2, 1, 1, 2, 3, 4}};

    int[][] knightScores = {
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 2, 2, 2, 2, 2, 2, 1},
            {1, 2, 3, 3, 3, 3, 2, 1},
            {1, 2, 3, 4, 4, 3, 2, 1},
            {1, 2, 3, 4, 4, 3, 2, 1},
            {1, 2, 3, 3, 3, 3, 2, 1},
            {1, 2, 2, 2, 2, 2, 2, 1},
            {1, 1, 1, 1, 1, 1, 1, 1}};

    int[][] rookScores = {
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1}};

    int[][] bishopScores = {
            {4, 3, 2, 1, 1, 2, 3, 4},
            {3, 4, 3, 2, 2, 3, 4, 3},
            {2, 3, 4, 3, 3, 4, 3, 2},
            {1, 2, 3, 4, 4, 3, 2, 1},
            {1, 2, 3, 4, 4, 3, 2, 1},
            {2, 3, 4, 3, 3, 4, 3, 2},
            {3, 4, 3, 2, 2, 3, 4, 3},
            {4, 3, 2, 1, 1, 2, 3, 4}};

    int[][] playAsScores = {
            {10, 10, 10, 10, 10, 10, 10, 10},
            {8, 8, 8, 8, 8, 8, 8, 8},
            {5, 6, 6, 7, 7, 6, 6, 5},
            {2, 3, 3, 5, 5, 3, 3, 2},
            {1, 2, 3, 5, 5, 3, 2, 1},
            {1, 1, 2, 2, 2, 2, 1, 1},
            {1, 1, 1, 0, 0, 1, 1, 1},
            {0, 0, 0, 0, 0, 0, 0, 0}};

    int[][] oppScores = {
            {0, 0, 0, 0, 0, 0, 0, 0},
            {1, 1, 1, 0, 0, 1, 1, 1},
            {1, 1, 2, 2, 2, 2, 1, 1},
            {1, 2, 3, 5, 5, 3, 2, 1},
            {2, 3, 3, 5, 5, 3, 3, 2},
            {5, 6, 6, 7, 7, 6, 6, 5},
            {8, 8, 8, 8, 8, 8, 8, 8},
            {10, 10, 10, 10, 10, 10, 10, 10}};

}
