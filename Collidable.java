package pacman;

import cs15.fnl.pacmanSupport.CS15SquareType;

import java.util.ArrayList;

public interface Collidable {
    CS15SquareType getCollidable();
    void collision(Game game, ArrayList<Collidable> collidableList);

    int getRow();
    int getCol();
}
