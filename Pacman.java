package pacman;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Pacman {
    Circle pacman;
    int speedx;
    int speedy;
    int initialX;
    int initialY;
    Direction direction;
    /**
     * This is the constructor of the pacman class. It sets the inital x and y locations based on what is
     * passed in and creates the pacman, which is simply a yellow circle.
     */
    public Pacman(int row, int col, Pane p){
        this.initialX = col * Constants.SQUARE_WIDTH + Constants.OFFSET;
        this.initialY = row * Constants.SQUARE_WIDTH + Constants.OFFSET;
        this.setUpPacman(p);
        this.speedx = 0;
        this.speedy = 0;
    }
    /**
     * This is a helper method that helps to create pacman.
     */
    private void setUpPacman(Pane p){
        this.pacman = new Circle(initialX, initialY, Constants.PACMAN_SIZE);
        this.pacman.setFill(Color.YELLOW);
        p.getChildren().add(this.pacman);
    }
    /**
     * This method moves the pacman based on the direction that is passed in.
     */
    public void move(Direction d, Square[][] board){
        this.direction = d;
        switch(this.direction){
            case LEFT:
                if (this.getCol() == 0) {
                    int newX = Constants.BOARD_COLUMNS * Constants.SQUARE_WIDTH - Constants.OFFSET;
                    this.pacman.setCenterX(newX);
                }
                else if (this.checkValidity(0,-1, board)){
                    this.moveLeft();
                }
                this.pacman.toFront();
                break;
            case RIGHT:
                this.pacman.toFront();
                if (this.getCol() == 22) {
                    int newX = Constants.SQUARE_WIDTH - Constants.OFFSET;
                    this.pacman.setCenterX(newX);
                }
                else if (this.checkValidity(0,1, board)){
                    this.moveRight();
                }
                this.pacman.toFront();
                break;
            case UP:
                if (this.getRow() == 0) {
                    int newY = Constants.BOARD_ROWS * Constants.SQUARE_WIDTH - Constants.OFFSET;
                    this.pacman.setCenterX(newY);
                }
                else if (this.checkValidity(-1,0, board)){
                    this.moveUp();
                }
                this.pacman.toFront();
                break;
            case DOWN:
                if (this.getRow() == 22) {
                    int newY = Constants.SQUARE_WIDTH + Constants.OFFSET;
                    this.pacman.setCenterX(newY);
                }
                else if (this.checkValidity(1,0, board)){
                    this.moveDown();
                }
                this.pacman.toFront();
                break;
            case NONE:
                break;
        }
    }
    /**
     * This method moves the pacman left by adjusting its x value.
     */
    public void moveLeft(){
        double newX = this.pacman.getCenterX()-Constants.SQUARE_WIDTH;
        this.pacman.setCenterX(newX);
    }
    /**
     * This method moves the pacman right by adjusting its x value.
     */
    public void moveRight(){
        double newX = this.pacman.getCenterX()+Constants.SQUARE_WIDTH;
        this.pacman.setCenterX(newX);
    }
    /**
     * This method moves the pacman right by adjusting its y value.
     */
    public void moveUp(){
        double newY = this.pacman.getCenterY()-Constants.SQUARE_WIDTH;
        this.pacman.setCenterY(newY);

    }
    /**
     * This method moves the pacman right by adjusting its y value.
     */
    public void moveDown(){
        double newY = this.pacman.getCenterY()+Constants.SQUARE_WIDTH;
        this.pacman.setCenterY(newY);
    }
    /**
     * This method resets the location of pacman and sets the direction to none.
     */
    public void reset(Game game){
        this.pacman.setCenterX(initialX);
        this.pacman.setCenterY(initialY);
        game.setPacmanDir(Direction.NONE);
    }

    /**
     * This method checks if the row inputed into the method is valid or not.
     */
    public boolean checkValidity(int dRow, int dCol, Square[][] board){
        if (board[this.getRow() + dRow][this.getCol() + dCol].getSquare().getFill() != Color.BLUE){
            return true;
        }
        return false;
    }

    /**
     * This method returns the row that the pacman is on.
     */
    public int getRow(){
        return (int)((this.pacman.getCenterY()-Constants.OFFSET)/Constants.SQUARE_WIDTH);
    }

    /**
     * This method returns the column that the pacman is on.
     */
    public int getCol(){
        return (int)((this.pacman.getCenterX()-Constants.OFFSET)/Constants.SQUARE_WIDTH);
    }

}
