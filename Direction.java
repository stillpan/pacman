package pacman;

public enum Direction {
    LEFT, RIGHT, UP, DOWN, NONE;
    /**
     * This method returns the opposite direction.
     */
    public Direction getOpposite(){
        switch (this){
            case LEFT:
                return RIGHT;
            case RIGHT:
                return LEFT;
            case UP:
                return DOWN;
            case DOWN:
                return UP;
            default:
                return NONE;
        }
    }
    /**
     * This method returns the change in rows depending on the direction.
     */
    public int getNewRow(){
        switch (this) {
            case UP:
                return -1;
            case DOWN:
                return 1;
            default:
                return 0;
        }
    }
    /**
     * This method returns the change in rows depending on the direction.
     */
    public int getNewCol(){
        switch (this) {
            case LEFT:
                return -1;
            case RIGHT:
                return 1;
            default:
                return 0;
        }
    }

}
