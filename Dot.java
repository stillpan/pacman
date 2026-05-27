package pacman;

import cs15.fnl.pacmanSupport.CS15SquareType;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;

public class Dot implements Collidable {
    private Circle dot;
    private Pane pane;

    /**
     * This is the constructor of the dot class. It creates the circle that represents the dot
     * based on the row and column that is passed in, and the color that is passed in. It also
     * adds the dot to the gamePane.
     */
    public Dot(int row, int col, Color color, Pane p){
        this.dot = new Circle(col * Constants.SQUARE_WIDTH + Constants.OFFSET,
                row * Constants.SQUARE_WIDTH + Constants.OFFSET, Constants.DOT_SIZE);
        this.dot.setFill(color);
        this.pane = p;
        this.pane.getChildren().add(this.dot);
    }

    /**
     * This method returns the row that the dot is currently on.
     */
    @Override
    public int getRow(){
        return (int)((this.dot.getCenterY()-Constants.OFFSET)/Constants.SQUARE_WIDTH);
    }

    /**
     * This method returns the column that the dot is currently on.
     */
    @Override
    public int getCol(){
        return (int)((this.dot.getCenterX()-Constants.OFFSET)/Constants.SQUARE_WIDTH);
    }

    /**
     * This method returns the CS15SquareType of the collidable. In this case, it is dot.
     */
    @Override
    public CS15SquareType getCollidable() {
        return CS15SquareType.DOT;
    }

    /**
     * This method is called when the pacman collides with a dot. In this case, it will
     * update the score, and logically and graphically remove the dot.
     */
    @Override
    public void collision(Game game, ArrayList<Collidable> collidableList) {
        game.updateScore(Constants.DOT_SCORE);
        this.pane.getChildren().remove(this.dot);
        collidableList.remove(this);
    }
}
