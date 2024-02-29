package com.chess;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class Chess extends Application {

    public static final int SCREEN_WIDTH = 1200;
    public static final int SCREEN_HEIGHT = 800;
    private final GameView gameView = new GameView();
    private boolean playAsWhite;
    private boolean humanPlaying;
    public static Slider slider = new Slider();

    @Override
    public void start(Stage stage) {
        BorderPane borderPane = new BorderPane();
        borderPane.setStyle("-fx-background-color: #2D2D2D;");


        // Styles
        String buttonStyle = "-fx-background-color: #4a4a4a; " +
                "-fx-text-fill: white; " +
                "-fx-border-color: white; " +
                "-fx-border-radius: 15; " +
                "-fx-background-radius: 15; " +
                "-fx-font-size: 26px;";
        String titleStyle = "-fx-font-family: 'Palatino'; " +
                "-fx-font-size: 80px; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold;";
        String settingsStyle = "-fx-font-family: 'Palatino'; " +
                "-fx-font-size: 40px; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold;";
        String radioButtonStyle = "-fx-padding: 5px 22px 5px 22px; " +
                "-fx-background-color: #4a4a4a; " +
                "-fx-text-fill: white; " +
                "-fx-border-color: white; " +
                "-fx-border-radius: 15; " +
                "-fx-background-radius: 15; " +
                "-fx-font-size: 18px; " +
                "-fx-min-width: 170px;";
        String sliderStyle = "-fx-control-inner-background: #4a4a4a; " +
                "-fx-accent: #FFFFFF; " +
                "-fx-focus-color: #FFFFFF; " +
                "-fx-faint-focus-color: #FFFFFF; " +
                "-fx-border-radius: 0; " +
                "-fx-text-fill: #FFFFFF; ";




        // Center Column

        VBox centerVBox = new VBox();
        centerVBox.setAlignment(Pos.CENTER);

        Label title = new Label("Chess");
        title.setStyle(titleStyle);

        Button playClassicButton = new Button("Play");
        playClassicButton.setPrefSize(350, 75);
        playClassicButton.setStyle(buttonStyle);

        Button loadGameButton = new Button("Load Game");
        loadGameButton.setPrefSize(350, 75);
        loadGameButton.setStyle(buttonStyle);

        Image image = new Image(getClass().getResource("/images/mainChessView.png").toExternalForm());
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(420);
        imageView.setFitWidth(420);

        VBox.setMargin(title, new Insets(-20, 0, 40, 0));
        VBox.setMargin(imageView, new Insets(-80, 0, 0, 0));
        VBox.setMargin(playClassicButton, new Insets(-5, 0, 25, 0));

        centerVBox.setPadding(new Insets(0, -350, 0, 0));
        centerVBox.getChildren().addAll(title, imageView, playClassicButton, loadGameButton);
        borderPane.setCenter(centerVBox);


        // Right Column - Settings

        VBox rightVBox = new VBox();
        rightVBox.setAlignment(Pos.CENTER);

        Label settings = new Label("Settings");
        settings.setStyle(settingsStyle);

        RadioButton onePlayerButton = new RadioButton("One Player");
        onePlayerButton.setStyle(radioButtonStyle);
        RadioButton aiPlaysButton = new RadioButton("AI Plays");
        aiPlaysButton.setStyle(radioButtonStyle);

        ToggleGroup playerGroup = new ToggleGroup();
        onePlayerButton.setToggleGroup(playerGroup);
        aiPlaysButton.setToggleGroup(playerGroup);
        onePlayerButton.setSelected(true);

        HBox playerBox = new HBox(10);
        playerBox.setAlignment(Pos.CENTER_LEFT);
        playerBox.getChildren().addAll(onePlayerButton, aiPlaysButton);

        RadioButton whiteButton = new RadioButton("White");
        whiteButton.setStyle(radioButtonStyle);
        RadioButton blackButton = new RadioButton("Black");
        blackButton.setStyle(radioButtonStyle);

        ToggleGroup colorGroup = new ToggleGroup();
        whiteButton.setToggleGroup(colorGroup);
        blackButton.setToggleGroup(colorGroup);
        whiteButton.setSelected(true);

        playAsWhite = whiteButton.isSelected();
        humanPlaying = onePlayerButton.isSelected();

        colorGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == whiteButton) {
                playAsWhite = true;
            } else if (newValue == blackButton) {
                playAsWhite = false;
            }
        });

        if (playerGroup.getSelectedToggle() == onePlayerButton) {
            humanPlaying = true;
        } else if (playerGroup.getSelectedToggle() == aiPlaysButton) {
            humanPlaying = false;
        }

        playerGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == onePlayerButton) {
                humanPlaying = true;
            } else if (newValue == aiPlaysButton) {
                humanPlaying = false;
            }
        });

        HBox colorBox = new HBox(10);
        colorBox.setAlignment(Pos.CENTER_LEFT);
        colorBox.getChildren().addAll(whiteButton, blackButton);

        VBox.setMargin(playerBox, new Insets(10));
        VBox.setMargin(colorBox, new Insets(10));

        slider.setMin(0);
        slider.setMax(100);
        slider.setValue(25);
        slider.setMajorTickUnit(25);
        slider.setMinorTickCount(0);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setStyle(sliderStyle);

        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            double roundedValue = Math.round(newValue.doubleValue() / 25) * 25;
            slider.setValue(roundedValue);
        });


        rightVBox.setPadding(new Insets(0, 10, 0, 0));
        rightVBox.setSpacing(10);
        rightVBox.getChildren().addAll(settings, playerBox, colorBox, slider);
        borderPane.setRight(rightVBox);


        // Scene
        Scene scene = new Scene(borderPane, SCREEN_WIDTH, SCREEN_HEIGHT);
        stage.setTitle("Chess");
        stage.setScene(scene);
        stage.show();

        // Buttons
        String hoverStyle = "-fx-background-color: #6a6a6a; " +  // Slightly lighter shade for hover
                "-fx-text-fill: white; " +
                "-fx-border-color: white; " +
                "-fx-border-radius: 15; " +
                "-fx-background-radius: 15; " +
                "-fx-font-size: 26px;";

        String pressedStyle = "-fx-background-color: #2a2a2a; " +  // Slightly darker shade for pressed
                "-fx-text-fill: white; " +
                "-fx-border-color: white; " +
                "-fx-border-radius: 15; " +
                "-fx-background-radius: 15; " +
                "-fx-font-size: 26px;";

        // Hover effect
        playClassicButton.setOnMouseEntered(e -> playClassicButton.setStyle(hoverStyle));
        playClassicButton.setOnMouseExited(e -> playClassicButton.setStyle(buttonStyle));

        playClassicButton.setOnMousePressed(e -> playClassicButton.setStyle(pressedStyle));
        playClassicButton.setOnMouseReleased(e -> {
            playClassicButton.setStyle(buttonStyle);
            gameView.gameView(stage, this, playAsWhite, humanPlaying, true);
        });

    }

    public static void main(String[] args) {
        launch();
    }
}