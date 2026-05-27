package pacman;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public class Square {
    private Rectangle square;
    private Pane pane;
    private ArrayList<Collidable> squareList;
    public Square (int row, int col, Color color, Pane p) {
        this.squareList = new ArrayList<>();
        this.setUpSquare(col * Constants.SQUARE_WIDTH, row * Constants.SQUARE_WIDTH,p,color);
    }

    private void setUpSquare(double x, double y, Pane pane, Color color){
        this.square = new Rectangle(tetris.Constants.SQUARE_WIDTH, tetris.Constants.SQUARE_WIDTH);
        this.square.setX(x);
        this.square.setY(y);
        this.square.setFill(color);
        pane.getChildren().add(this.square);
    }
    public ArrayList<Collidable> getList(){
        return this.squareList;
    }
    public Rectangle getSquare(){
        return this.square;
    }
}
