package pacman;

import cs15.fnl.pacmanSupport.CS15SquareType;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Ghost implements Collidable{
    Rectangle ghost;
    Mode ghostMode;
    Pane pane;
    Color originalColor;
    Direction direction;
    Direction nextDirection;
    int initialX;
    int initialY;
    int frightenedModeCounter;
    /**
     * This is the constructor of the ghost class. It sets the inital x and y locations based on what is
     * passed in and creates the ghosts, which is simply a rectangle. It also takes the color that is
     * passed in and sets the ghost color as that color.
     */
    public Ghost(int row, int col, Color color, Pane p) {
        this.initialX = col * Constants.SQUARE_WIDTH;
        this.initialY = row * Constants.SQUARE_WIDTH;
        this.setUpGhost(initialX, initialY, color, p);
        this.direction = Direction.DOWN;
        this.nextDirection = Direction.NONE;
        this.originalColor = color;
        this.frightenedModeCounter = 0;
    }

    /**
     * This is a helper class that creates the actual ghost based on the coordinates and color
     * passed in. Then it adds the ghost to the pane that is passed in.
     */
    private void setUpGhost(int x, int y, Color color, Pane p){
        this.ghost = new Rectangle(x,
                y, Constants.GHOST_SIZE, Constants.GHOST_SIZE);
        this.ghost.setFill(color);
        this.pane = p;
        this.pane.getChildren().add(this.ghost);
    }
    /**
     * This method sets the direction that the ghost will move in based on the ghost mode.
     * For chase and scatter mode, the ghosts will travel in the direction that will take
     * them on the shortest path to their target using a breadth first search algorithm.
     * During frightened mode, the ghosts will travel randomly.
     */
    public void setDirection(Mode mode, BoardCoordinate target, Square[][] board,
                             Game game){
        this.ghostMode = mode;
        switch (ghostMode) {
            case CHASE:
                this.direction = bfs(target, board);
                break;
            case SCATTER:
                this.direction = bfs(target, board);
                break;
            case FRIGHTENED:
                this.runFrightenedMode(target, board);
                break;
        }
        this.moveGhost(board);
    }
    /**
     * This method moves the ghost based on the direction that it is going in. It also makes sure
     * to remove the ghost from the collidable list on the original square and adds the
     * ghost to the collidable list on the new square that it is going to move to. This method
     * also takes care of the ghost wrapping.
     */
    private void moveGhost(Square[][] board){
        board[this.getRow()][this.getCol()].getList().remove(this);
        switch (this.direction){
            case UP:
                this.moveUp();
                this.ghost.toFront();
                break;
            case DOWN:
                this.moveDown();
                this.ghost.toFront();
                break;
            case RIGHT:
                if (this.getCol() == 22){
                    this.ghost.setX(0*Constants.SQUARE_WIDTH);
                }
                else{
                    this.moveRight();
                }
                this.ghost.toFront();
                break;
            case LEFT:
                if (this.getCol() == 0){
                    this.ghost.setX(22*Constants.SQUARE_WIDTH);
                }
                else{
                    this.moveLeft();
                }
                ghost.toFront();
                break;
            default:
                break;
        }
        board[this.getRow()][this.getCol()].getList().add(this);
    }

    /**
     * This method graphically moves the ghost up one square.
     */
    private void moveUp(){
        int newY = (int) (this.ghost.getY() - Constants.SQUARE_WIDTH);
        this.ghost.setY(newY);
    }

    /**
     * This method graphically moves the ghost down one square.
     */
    private void moveDown(){
        int newY = (int) (this.ghost.getY() + Constants.SQUARE_WIDTH);
        this.ghost.setY(newY);
    }

    /**
     * This method graphically moves the ghost to the right.
     */
    private void moveRight(){
        int newX = (int) (this.ghost.getX() + Constants.SQUARE_WIDTH);
        this.ghost.setX(newX);
    }

    /**
     * This method graphically moves the ghost to the left.
     */
    private void moveLeft(){
        int newX = (int) (this.ghost.getX() - Constants.SQUARE_WIDTH);
        this.ghost.setX(newX);
    }

    /**
     * This method is called when the ghost mode is frightened. It updates the frightened
     * mode counter. It the counter is less than the duration of the mode, then it sets
     * the ghost colors as light blue and sets the direction as a random valid direction.
     * If the frightened mode counter is more than the duration, it sets the frightened mode
     * counter as 0, and sets the ghosts to the original color.
     */
    private void runFrightenedMode(BoardCoordinate target, Square[][] board){
        this.frightenedModeCounter++;
        if (this.frightenedModeCounter <= Constants.FRIGHTENED_END_TIME){
            this.ghost.setFill(Color.LIGHTBLUE);
            this.direction = randomDirection(board);
        }
        else {
            this.frightenedModeCounter = 0;
            this.ghost.setFill(this.originalColor);
        }
    }

    /**
     * This method returns a random direction based on the possible valid
     * directions that the ghost can take.
     */
    private Direction randomDirection(Square[][] board){
        ArrayList<Direction> possibleDir = new ArrayList<>();
        for (Direction direction : Direction.values()){
            int newRow = this.getRow() + direction.getNewRow();
            int newCol = this.getCol() + direction.getNewCol();
            if (direction == Direction.NONE){
                continue;
            }
            boolean validDirection = this.checkDirectionValidity(direction,
                    this.direction.getOpposite(),newRow,newCol, board);
            if (validDirection){
                possibleDir.add(direction);
            }
        }
        int randomIndex = (int)(Math.random()* possibleDir.size()-1);
        return possibleDir.get(randomIndex);
    }

    /**
     * This is a breadth first search algorithm that calculates the shortest path to
     * take to get to the target, and returns the next direction that the ghost
     * should take. It does this by creating a map of all the possible directions that
     * the ghost can take then checks to see which one will result in the shortest
     * path. Then it returns the direction the ghost should take to get to the target
     * in the shortest time.
     */
    private Direction bfs(BoardCoordinate target, Square[][] board){
        Queue<BoardCoordinate> possibleDir = new LinkedList<>();
        Direction[][] directions = new Direction[Constants.BOARD_ROWS][Constants.BOARD_COLUMNS];
        BoardCoordinate currentLoc = new BoardCoordinate(this.getRow(), this.getCol(), false);
        this.updateNeighbors(possibleDir, directions, currentLoc, board, true);
        double shortestDist = Double.POSITIVE_INFINITY;
        Direction nextDir = null;
        while (!possibleDir.isEmpty()){
            currentLoc = possibleDir.remove();
            Direction tempDir = directions[currentLoc.getRow()][currentLoc.getColumn()];
            double dist = this.calcDistance(currentLoc.getRow(),currentLoc.getColumn(),
                    target.getRow(), target.getColumn());
            if (newMinCheck(dist, shortestDist)){
                shortestDist = dist;
                nextDir = tempDir;
            }
            this.updateNeighbors(possibleDir, directions, currentLoc, board, false);
        }
        return nextDir;
    }
    /**
     * This method checks to see if the first number passed in is smaller than the second number
     * that is passed in.
     */
    private boolean newMinCheck(double num1, double num2){
        if (num1 <= num2){
            return true;
        }
        return false;
    }

    /**
     * This method adds the possible neighboring squares to the queue and then adds the directions to
     * the map of directions. It also checks to see if the neighboring square can be moved to
     * or not and checks if the square has already been added.
     */
    private void updateNeighbors(Queue<BoardCoordinate> possibleDir, Direction[][] dir,
                                 BoardCoordinate initialLoc, Square[][] board, Boolean visited){
        Direction oppDirection = this.direction.getOpposite();
        Direction currDirection = dir[initialLoc.getRow()][initialLoc.getColumn()];
        for (Direction direction : Direction.values()){
            int newRow = initialLoc.getRow() + direction.getNewRow();
            int newCol = initialLoc.getColumn() + direction.getNewCol();
            if (direction == oppDirection && !visited){
                continue;
            }
            if (direction == Direction.NONE){
                continue;
            }
            if (newCol == -1){
                newCol = 22;
            } else if (newCol == 23){
                newCol = 0;
            }
            if (checkDirectionValidity(direction, oppDirection, newRow, newCol, board) &&
                    dir[newRow][newCol] == null){
                if (!visited){
                    dir[newRow][newCol] = currDirection;
                }
                else {
                    dir[newRow][newCol] = direction;
                }
                possibleDir.add(new BoardCoordinate(newRow, newCol, false));

            }

        }

    }
    /**
     * This method checks if a direction is valid by checking if the next square is out of bounds,
     * if the direction is opposite to the current direction, and if it is a wall. If any of
     * these conditions are met, then the direction is not valid. Otherwise, it is.
     */
    private boolean checkDirectionValidity(Direction direction, Direction oppDir,
                                           int newRow, int newCol, Square[][] board){
        if (newCol == -1){
            newCol = 22;
        }
        if (newCol == 23){
            newCol = 0;
        }
        if (direction == oppDir){
            return false;
        }
        if (direction == oppDir){
            return false;
        }
        else if (newRow >= Constants.BOARD_ROWS || newRow < 0 ||
                newCol >= Constants.BOARD_COLUMNS || newCol < 0){
            return false;
        }
        else if (board[newRow][newCol].getSquare().getFill() == Color.BLUE){
            return false;
        }

        else {
            return true;
        }
    }
    /**
     * This method calculates the distance between two squares by using the distance formula.
     */
    private double calcDistance(int firstRow, int firstCol, int secondRow, int secondCol){
        return Math.sqrt(Math.pow(Math.abs(secondRow - firstRow), 2) +
                Math.pow(Math.abs(secondCol - firstCol), 2));
    }

    /**
     * This method returns the CS15SquareType for the collidable.
     */
    @Override
    public CS15SquareType getCollidable() {
        return CS15SquareType.GHOST_START_LOCATION;
    }
    /**
     * This method runs the necessary steps to be taken when pacman collides
     * with a ghost.
     */
    @Override
    public void collision(Game game, ArrayList<Collidable> collidableList) {
        collidableList.remove(this);
        game.getBoard()[this.getRow()][this.getCol()].getList().remove(this);
        if (this.ghostMode == Mode.FRIGHTENED){
            game.updateScore(Constants.GHOST_SCORE);
            this.resetLocHelper();
        }
        else{
            game.updateLives(1);
            game.resetGhostLoc();
            game.reset();
        }
    }

    /**
     * This method resets the ghost location to its original location.
     */
    public void resetLoc(Square[][] board){
        board[this.getRow()][this.getCol()].getList().remove(this);
        this.resetLocHelper();
        board[this.getRow()][this.getCol()].getList().add(this);
    }

    public void resetLocHelper(){
        this.ghost.setX(this.initialX);
        this.ghost.setY(this.initialY);
    }

    /**
     * This method removes the ghost from the pen and places it outside the pen.
     */
    public void removeGhost(){
        int x = Constants.GHOST_OUT_LOC_COL*Constants.SQUARE_WIDTH;
        int y = Constants.GHOST_OUT_LOC_ROW*Constants.SQUARE_WIDTH;
        this.ghost.setX(x);
        this.ghost.setX(y);
    }

    /**
     * This method returns the ghost when called, which is the rectangle created in the
     * constructor.
     */
    public Rectangle getGhost(){
        return this.ghost;
    }

    /**
     * This method returns the row that the ghost is currently on.
     */
    @Override
    public int getRow() {
        return (int) (this.ghost.getY()/Constants.SQUARE_WIDTH);
    }

    /**
     * This method returns the column that the ghost is currently on.
     */
    @Override
    public int getCol() {
        return (int) (this.ghost.getX()/Constants.SQUARE_WIDTH);
    }
}
