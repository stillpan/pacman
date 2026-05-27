package pacman;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import cs15.fnl.pacmanSupport.CS15SupportMap;
import cs15.fnl.pacmanSupport.CS15SquareType;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;


public class Game {
    private Square[][] board;
    private Timeline timeline;
    private Pane gamePane;
    private Pane scorePane;
    private Label scoreLabel, livesLabel, tokenLabel;
    private Pacman pacman;
    private Ghost blinky;
    private Ghost pinky;
    private Ghost inky;
    private Ghost clyde;
    private BoardCoordinate blinkyTarget;
    private BoardCoordinate pinkyTarget;
    private BoardCoordinate inkyTarget;
    private BoardCoordinate clydeTarget;
    private Mode ghostMode;
    private Queue<Ghost> ghostQueue;
    private int ghostModeCounter;
    private int frightenedModeCounter;
    private int ghostLeavePenCounter;
    private int score;
    private int lives;
    private int edibleCount;
    private int modeChangeTokens;
    private int difficulty;
    private int difficultyCounter;

    private Direction pacmanDir;
    public Game(Pane p, Pane scoreP){
        this.board = new Square[Constants.BOARD_ROWS][Constants.BOARD_COLUMNS];
        this.gamePane = p;
        this.scorePane = scoreP;
        this.score = 0;
        this.lives = 3;
        this.edibleCount = 0;
        this.ghostQueue = new LinkedList<>();
        this.generateBoard();
        this.createScoreBoard();
        this.generateGhosts();
        this.pacmanDir = Direction.NONE;
        this.ghostMode = Mode.CHASE;
        this.ghostModeCounter = 0;
        this.frightenedModeCounter = 0;
        this.ghostLeavePenCounter = 0;
        this.modeChangeTokens = 3;
        this.difficulty = Constants.GAME_DIFFICULTY;
        this.difficultyCounter = 0;
        this.setUpTimeline();
    }

    /**
     * This method generates the Board by taking the SupportMap, which has locations
     * of different objects to generate the board. Collidables like energizers and
     * dots are added to a list of collidables on the square it is located on.
     */

    private void generateBoard(){
        CS15SquareType[][] map = CS15SupportMap.getSupportMap();
        for (int i = 0; i < Constants.BOARD_ROWS; i++){
            for (int j = 0; j < Constants.BOARD_COLUMNS; j++){
                switch(map[i][j]){
                    case WALL:
                        this.board[i][j] = new Square(i, j, Color.BLUE, gamePane);
                        break;
                    case FREE:
                        this.board[i][j] = new Square(i, j, Color.BLACK, gamePane);
                        break;
                    case DOT:
                        this.board[i][j] = new Square(i, j, Color.BLACK, gamePane);
                        Dot dot = new Dot(i, j, Color.WHITE, gamePane);
                        this.board[i][j].getList().add(dot);
                        break;
                    case ENERGIZER:
                        this.board[i][j] = new Square(i, j, Color.BLACK, gamePane);
                        Energizer energizer = new Energizer(i, j, Color.WHITE, gamePane);
                        this.board[i][j].getList().add(energizer);
                        break;
                    case PACMAN_START_LOCATION:
                        this.board[i][j] = new Square(i, j, Color.BLACK, gamePane);
                        this.pacman = new Pacman(i, j, gamePane);
                        break;
                }
            }
        }

    }

    /**
     * Ghosts have to be generated differently because otherwise when the for loop iterates
     * past the location of the ghost, it will cover the ghost that is located in the
     * square next to the ghost location square. This method creates each of the four
     * ghosts, adds them to the collidable list on the square that they are located
     * on, and adds each of them to the ghost queue.
     */

    private void generateGhosts(){
        CS15SquareType[][] map = CS15SupportMap.getSupportMap();
        for (int i = 0; i < Constants.BOARD_ROWS; i++) {
            for (int j = 0; j < Constants.BOARD_COLUMNS; j++) {
                switch (map[i][j]) {
                    case GHOST_START_LOCATION:
                        this.board[i][j] = new Square(i, j, Color.BLACK, gamePane);
                        this.inky = new Ghost(i, j-1, Color.TURQUOISE, gamePane);
                        this.pinky = new Ghost(i, j, Color.PINK, gamePane);
                        this.clyde = new Ghost(i, j+1, Color.ORANGE, gamePane);
                        this.blinky = new Ghost(i-2, j, Color.RED, gamePane);
                        this.board[i][j-1].getList().add(this.inky);
                        this.board[i][j].getList().add(this.pinky);
                        this.board[i][j+1].getList().add(this.clyde);
                        this.board[i-2][j].getList().add(this.blinky);
                        this.ghostQueue.add(this.pinky);
                        this.ghostQueue.add(this.inky);
                        this.ghostQueue.add(this.clyde);
                        break;
                }
            }
        }
    }

    /**
     * This method takes care of user input. When a key is pressed, it moves Pacman by checking if
     * the move is valid. It also sets the ghost mode by pressing c, s, or f. You can also switch the
     * difficulty with D.
     */
    public void handleKeyPress(KeyEvent e) {
        KeyCode keyPressed = e.getCode();
        switch(keyPressed){
            case LEFT:
                if (this.pacman.getCol() == 0){
                    this.pacmanDir = Direction.LEFT;
                }
                else if (this.pacman.checkValidity(0,-1,this.board)) {
                    this.pacmanDir = Direction.LEFT;
                }
                break;
            case RIGHT:
                if (this.pacman.getCol() == 22){
                    this.pacmanDir = Direction.RIGHT;
                }
                else if (this.pacman.checkValidity(0,1,this.board)) {
                    this.pacmanDir = Direction.RIGHT;
                }
                break;
            case UP:
                if (this.pacman.getCol() == 0){
                    this.pacmanDir = Direction.UP;
                }
                else if (this.pacman.checkValidity(-1,0,this.board)) {
                    this.pacmanDir = Direction.UP;
                }
                break;
            case DOWN:
                if (this.pacman.getCol() == 22){
                    this.pacmanDir = Direction.DOWN;
                }
                else if (this.pacman.checkValidity(1,0,this.board)) {
                    this.pacmanDir = Direction.DOWN;
                }
                break;
            case C:
                if (this.modeChangeTokens > 0){
                    this.ghostMode = Mode.CHASE;
                    this.ghostModeCounter = 0;
                    this.modeChangeTokens--;
                }
                else{
                    this.noMoreTokens();
                }
                break;
            case S:
                if (this.modeChangeTokens > 0){
                    this.ghostMode = Mode.SCATTER;
                    this.ghostModeCounter = 100;
                    this.modeChangeTokens--;
                }
                else{
                    this.noMoreTokens();
                }
                break;
            case F:
                if (this.modeChangeTokens > 0){
                    this.ghostMode = Mode.FRIGHTENED;
                    this.modeChangeTokens--;
                }
                else{
                    this.noMoreTokens();
                }
                break;
            case D:
                if (this.difficultyCounter%2 == 0){
                    this.timeline.stop();
                    this.difficulty = Constants.GAME_DIFFICULTY;
                    this.setUpTimeline();
                }
                else {
                    this.timeline.stop();
                    this.difficulty = Constants.GAME_HARD_MODE;
                    this.setUpTimeline();
                }
                this.difficultyCounter++;

                break;
        }
    }

    private void noMoreTokens(){
        this.tokenLabel = new Label("No More Tokens!");
        this.setLabelFont(this.tokenLabel, Constants.SCENE_WIDTH/2 - 80, Constants.SCENE_HEIGHT/2 - 10
                , "-fx-text-fill: #FFFFFF");
        this.gamePane.getChildren().add(tokenLabel);
    }

    /**
     * This method sets up the timeline of the game. The keyframe duration is set to half a second.
     * The keyframe calls the method doTimeline every half a second, and the timeline
     * goes on indefinitely.
     */

    private void setUpTimeline(){
        KeyFrame kf = new KeyFrame(Duration.millis(this.difficulty), (ActionEvent e) ->
                this.doTimeline());
        this.timeline = new Timeline(kf);
        this.timeline.setCycleCount(Animation.INDEFINITE);
        this.timeline.play();
    }


    /**
     * This is the doTimeline method which is the main timeline helper. Every half a second, this
     * method is run to move pacman, the ghosts, check for collisions, and take care of any
     * necessary game updates.
     */
    private void doTimeline(){
        this.pacman.move(this.pacmanDir, this.board);
        this.checkCollision();
        this.ghostTimelineHelper();
        this.checkCollision();
        this.gameUpdates();
    }
    /**
     * This is the gameUpdates helper method which calls all the necessary methods
     * other than checking for collision and moving the pacman or the ghosts. This includes
     * taking care of having the ghosts leave the pen, and updating the game status.
     */
    private void gameUpdates(){
        this.ghostsLeavePen();
        this.ghostLeavePenCounter++;
        this.checkGameStatus();
    }

    /**
     * This method checks the game status to see if the game is over or if the player has
     * won. It takes the ediblesCount, which increases every time pacman collides with an
     * energizer or dot, and compares it to the total number of edibles. If they are equal,
     * then that means that pacman has eaten all the energizers and dots. However,
     * if pacman has no lives left, then that means that the player has lost.
     */
    private void checkGameStatus(){
        if (this.edibleCount == Constants.TOTAL_EDIBLES){
            this.gameOver();
            this.createWinGameLabel();
        }
        if (this.lives == 0){
            this.gameOver();
            createGameOverLabel();
        }
    }

    /**
     * This method stops the timeline to stop any movement, and to prevent user input on
     * the gamePane.
     */
    private void gameOver(){
        this.timeline.stop();
        this.gamePane.setFocusTraversable(false);
    }
    /**
     * This method creates the label when the game is won.
     */
    private void createWinGameLabel(){
        Label winLabel = new Label("Congrats!");
        this.setLabelFont(winLabel, Constants.SCENE_WIDTH/2 - 50, Constants.SCENE_HEIGHT/2 - 10
                , "-fx-text-fill: #FFFFFF");
        this.gamePane.getChildren().add(winLabel);
    }
    /**
     * This method creates the label when the game is over.
     */
    private void createGameOverLabel(){
        Label gameOverLabel = new Label("Game Over!");
        this.setLabelFont(gameOverLabel, Constants.SCENE_WIDTH/2 - 53,
                Constants.SCENE_HEIGHT/2 - 10, "-fx-text-fill: #FFFFFF");
        this.gamePane.getChildren().add(gameOverLabel);
    }
    /**
     * This is the helper method that organizes the method that needs to be called
     * for proper ghost movement.
     */
    private void ghostTimelineHelper(){
        this.changeGhostMode();
        this.moveGhosts();
    }
    /**
     * This method moves the ghosts by calling the setDirection method in the Ghost class.
     * It feeds in the current ghost mode, the target, the board, and the game. The
     * setDirection method essentially tells the ghosts the direction to move in.
     */
    private void moveGhosts(){
        this.blinky.setDirection(this.ghostMode, this.blinkyTarget, this.board,this);
        this.pinky.setDirection(this.ghostMode, this.pinkyTarget, this.board,this);
        this.inky.setDirection(this.ghostMode, this.inkyTarget, this.board,this);
        this.clyde.setDirection(this.ghostMode, this.clydeTarget, this.board,this);
    }
    /**
     * This method sets the ghosts targets. During chase mode, the target is pacman. During
     * scatter move, the target is the corners.
     */
    private void setGhostTargets(){
        switch (this.ghostMode){
            case CHASE:
                this.blinkyTarget = new BoardCoordinate(this.pacman.getRow(), this.pacman.getCol(),
                        true);
                this.pinkyTarget = new BoardCoordinate(this.pacman.getRow(), this.pacman.getCol()+2,
                        true);
                this.inkyTarget = new BoardCoordinate(this.pacman.getRow()-4, this.pacman.getCol(),
                        true);
                this.clydeTarget = new BoardCoordinate(this.pacman.getRow()+1, this.pacman.getCol()-3,
                        true);
                break;
            case SCATTER:
                this.blinkyTarget = new BoardCoordinate(0, 0, true);
                this.pinkyTarget = new BoardCoordinate(0, 22, true);
                this.inkyTarget = new BoardCoordinate(22, 0, true);
                this.clydeTarget = new BoardCoordinate(22,22,true);
                break;
            default:
                break;
        }
    }
    /**
     * This method takes care of ghost mode changes. It updates the ghost mode counter, sets ghost modes and counters, and
     * takes care of alternating between scatter mode and chase mode. It also makes sure to
     * call the relevant methods when the mode is changed to frightened.
     */
    private void changeGhostMode(){
        this.ghostModeCounter++;
        this.setGhostTargets();
        if(this.ghostMode != Mode.FRIGHTENED){
            if (this.ghostModeCounter >= Constants.SCATTER_START_TIME &&
            this.ghostModeCounter < Constants.SCATTER_END_TIME){
                this.ghostMode = Mode.SCATTER;
            } else if (this.ghostModeCounter == Constants.SCATTER_END_TIME) {
                this.ghostModeCounter = 0;
                this.ghostMode = Mode.CHASE;
            }
        }
        else {
            this.frightenedModeCounter++;
            if (this.frightenedModeCounter >= Constants.FRIGHTENED_END_TIME){
                this.ghostModeCounter = 0;
                this.frightenedModeCounter = 0;
                this.resetGhostColors();
                this.ghostMode = Mode.CHASE;
            }
        }
    }
    /**
     * This is called in the timeline helper method to take care of having the ghosts leave
     * the pen one by one by removing it from a queue.
     */
    private void ghostsLeavePen(){
        switch (this.ghostLeavePenCounter){
            case 40:
            case 60:
            case 80:
            case 100:
                if (!this.ghostQueue.isEmpty()){
                    this.ghostQueue.remove().removeGhost();
                }
        }
    }

    /**
     * This method resets the colors of the ghosts to their original colors.
     */
    private void resetGhostColors(){
        this.blinky.getGhost().setFill(Color.RED);
        this.pinky.getGhost().setFill(Color.PINK);
        this.inky.getGhost().setFill(Color.TURQUOISE);
        this.clyde.getGhost().setFill(Color.ORANGE);
    }

    /**
     * This method resets the locations of the ghosts to their original locations.
     */
    public void resetGhostLoc(){
        this.resetGhostQueue();
        this.blinky.resetLoc(this.board);
        this.pinky.resetLoc(this.board);
        this.inky.resetLoc(this.board);
        this.clyde.resetLoc(this.board);
    }

    /**
     * This method sets the pacman direction.
     */
    public void setPacmanDir(Direction d){
        this.pacmanDir = d;
    }
    /**
     * This method updates the score.
     */
    public void updateScore(int dScore){
        this.score += dScore;
        this.scoreLabel.setText("Score: " + this.score);
    }
    /**
     * This method updates the lives.
     */
    public void updateLives(int dLives){
        this.lives -= dLives;
        this.livesLabel.setText("Lives: " + this.lives);
    }
    /**
     * This method checks for any collisions, and calls the collision method in
     * each of the collidable's classes, which takes care of what happens when pacman
     * collides into each of the collidables.
     */
    private void checkCollision(){
        int pacCol = this.pacman.getCol();
        int pacRow = this.pacman.getRow();
        ArrayList<Collidable> collidableList = this.board[pacRow][pacCol].getList();
        for (int i = 0; i < collidableList.size(); i++){
            Collidable collidable = collidableList.get(i);
            collidable.collision(this, collidableList);
            switch (collidable.getCollidable()) {
                case DOT:
                    this.edibleCount++;
                    break;
                case ENERGIZER:
                    this.edibleCount++;
                    this.ghostMode = Mode.FRIGHTENED;
                    break;
                default:
                    break;
            }
        }
    }
    /**
     * This method resets pacman to its original location.
     */
    public void reset(){
        this.pacman.reset(this);
    }
    /**
     * This method resets the ghost queue to its full list.
     */
    private void resetGhostQueue(){
        while (!ghostQueue.isEmpty()){
            this.ghostQueue.remove();
        }
        this.ghostQueue = new LinkedList<>();
        this.ghostQueue.add(this.pinky);
        this.ghostQueue.add(this.inky);
        this.ghostQueue.add(this.clyde);
    }
    /**
     * This method returns the board.
     */
    public Square[][] getBoard(){
        return this.board;
    }

    public Mode getGhostMode(){
        return ghostMode;
    }
    /**
     * This method creates the score board by creating the score label and the lives label.
     * It also calls a helper method to set up the label font. Then it adds the labels to
     * the score pane.
     */
    private void createScoreBoard(){
        this.scoreLabel = new Label("Score: " + this.score);
        this.livesLabel = new Label("Lives: " + this.lives);
        this.setLabelFont(scoreLabel, Constants.SCENE_WIDTH/3 - 43, 3, "-fx-text-fill: #FFFFFF");
        this.setLabelFont(livesLabel, (Constants.SCENE_WIDTH/3)*2 - 43, 3, "-fx-text-fill: #FFFFFF");
        this.scorePane.getChildren().addAll(scoreLabel, livesLabel);
    }
    /**
     * This is a helper method that helps to set the font and location of the label.
     */
    public void setLabelFont(Label label, int x, int y, String color){
        label.setFont(Font.font("Courier New",20));
        label.setLayoutX(x);
        label.setLayoutY(y);
        label.setStyle(color);
    }



}
