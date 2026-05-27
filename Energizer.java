package pacman;

import cs15.fnl.pacmanSupport.CS15SquareType;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;

public class Energizer implements Collidable{
    Circle energizer;
    Pane pane;
    /**
     * This is the constructor for the energizer class. It creates the circle that represents
     * the energizer based on the x and y coordinates that are passed in, and adds the
     * energizer to the game pane.
     */
    public Energizer(int row, int col, Color color, Pane p){
        this.energizer = new Circle(col * Constants.SQUARE_WIDTH + Constants.OFFSET,
                row * Constants.SQUARE_WIDTH + Constants.OFFSET, Constants.ENERGIZER_SIZE);
        this.energizer.setFill(color);
        this.pane = p;
        this.pane.getChildren().add(this.energizer);
    }

    /**
     * This method returns the row that the energizer is currently on.
     */
    public int getRow(){
        return (int)((this.energizer.getCenterY()-Constants.OFFSET)/Constants.SQUARE_WIDTH);
    }

    /**
     * This method returns the column that the energizer is currently on.
     */
    public int getCol(){
        return (int)((this.energizer.getCenterX()-Constants.OFFSET)/Constants.SQUARE_WIDTH);
    }

    /**
     * This method returns the CS15SquareType of the collidable. In this case, it is
     * energizer.
     */
    @Override
    public CS15SquareType getCollidable() {
        return CS15SquareType.ENERGIZER;
    }

    /**
     * This method is called when the pacman collides with an energizer. In this case, it will
     * update the score, and remove the energizer both graphically and logically.
     */
    @Override
    public void collision(Game game, ArrayList<Collidable> collidableList) {
        game.updateScore(Constants.ENERGIZER_SCORE);
        this.pane.getChildren().remove(this.energizer);
        collidableList.remove(this);
    }
}
