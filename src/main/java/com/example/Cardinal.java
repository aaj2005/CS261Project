package com.example;

public enum Cardinal {
    N,E,S,W;

    /*
     * Checks if b is left of a
     * For a car coming in from the north, east is to the left, so isLeftOf(N, E) === true
     */
    public static boolean isLeftOf(Cardinal a, Cardinal b) {
        Cardinal[] validA = { Cardinal.N, Cardinal.E, Cardinal.S, Cardinal.W };
        Cardinal[] validB = { Cardinal.E, Cardinal.S, Cardinal.W, Cardinal.N };
        for (int i=0; i<4; i++) {
            if (a == validA[i] && b == validB[i]) {
                return true;
            }
        }
        return false;
    }

    /*
     * Checks if b is right of a
     * For a car coming in from the south, east is to the left, so isRightOf(S, E) === true
     */
    public static boolean isRightOf(Cardinal a, Cardinal b) {
        Cardinal[] validA = { Cardinal.N, Cardinal.E, Cardinal.S, Cardinal.W };
        Cardinal[] validB = { Cardinal.W, Cardinal.N, Cardinal.E, Cardinal.S };
        for (int i=0; i<4; i++) {
            if (a == validA[i] && b == validB[i]) {
                return true;
            }
        }
        return false;
    }

    public static Direction toDirection(Cardinal a) {
        switch (a) {
            case N: return Direction.TOP;
            case E: return Direction.RIGHT;
            case S: return Direction.BOTTOM;
            case W: return Direction.LEFT;
        }
        return null;
    }

    public static boolean isOpposite(Cardinal a, Cardinal b) {
        return a == N && b == S
            || a == S && b == N
            || a == E && b == W
            || a == W && b == E;
    }
}
