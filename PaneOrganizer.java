package pacman;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public class PaneOrganizer {
    BorderPane root;
    Pane gamePane;
    Pane scorePane;
    Game game;
    public PaneOrganizer(){
        this.root = new BorderPane();
        this.createGamePane();
        this.createButtonPane();
        this.createScorePane();
        this.game = new Game(this.gamePane, this.scorePane);
    }

    public BorderPane getRoot(){
        return this.root;

    }
    private void createGamePane(){
        this.gamePane = new Pane();
        this.root.setCenter(this.gamePane);
        this.gamePane.setOnKeyPressed(keyEvent -> this.game.handleKeyPress(keyEvent));
        this.gamePane.setFocusTraversable(true);
    }

    private void createScorePane(){
        this.scorePane = new Pane();
        this.root.setTop(this.scorePane);
        this.scorePane.setStyle("-fx-background-color: #252525");
        this.scorePane.setFocusTraversable(false);
    }

    public void createButtonPane(){
        Pane buttonPane = new Pane(); //This instantiates the button pane
        buttonPane.setPrefSize(50, 35);
        buttonPane.setStyle("-fx-background-color: #252525");
        Button button = new Button("Quit"); //This instantiates the button and sets the text
        button.setLayoutX(Constants.SCENE_WIDTH/2 - 20);
        buttonPane.getChildren().add(button);
        this.setButtonActions(buttonPane, button);
    }

    public void setButtonActions(Pane pane, Button button){
        button.setOnAction((ActionEvent e) -> System.exit(0));
        this.root.setBottom(pane);
        pane.setFocusTraversable(false);
        button.setFocusTraversable(false);
    }
}
