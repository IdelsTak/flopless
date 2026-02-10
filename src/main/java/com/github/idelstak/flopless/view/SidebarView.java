package com.github.idelstak.flopless.view;

import com.github.idelstak.flopless.poker.player.*;
import com.github.idelstak.flopless.poker.table.*;
import com.github.idelstak.flopless.state.*;
import com.github.idelstak.flopless.state.api.*;
import io.reactivex.rxjava3.observers.*;
import java.net.*;
import java.util.*;
import javafx.application.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.stage.*;

public final class SidebarView implements Initializable {

    private final Stage stage;
    private final FloplessLoop loop;
    private DisposableObserver<History<FloplessState>> observe;
    @FXML
    private ToggleGroup tableTypeGroup;
    @FXML
    private ToggleGroup heroPositionGroup;
    @FXML
    private ToggleGroup scenarioGroup;
    @FXML
    private ToggleGroup blindsGroup;
    @FXML
    private RadioButton sixMaxRadio;
    @FXML
    private RadioButton nineMaxRadio;
    @FXML
    private RadioButton bb100Radio;
    @FXML
    private RadioButton bb200Radio;
    @FXML
    private RadioButton oneTwoBlindsRadio;
    @FXML
    private RadioButton twoFourBlindsRadio;
    @FXML
    private RadioButton utgRadio;
    @FXML
    private RadioButton utg1Radio;
    @FXML
    private RadioButton utg2Radio;
    @FXML
    private RadioButton ljRadio;
    @FXML
    private RadioButton hjRadio;
    @FXML
    private RadioButton coRadio;
    @FXML
    private RadioButton btnRadio;
    @FXML
    private RadioButton sbRadio;
    @FXML
    private RadioButton bbRadio;
    @FXML
    private RadioButton openRadio;
    @FXML
    private RadioButton vsUtgRadio;
    @FXML
    private RadioButton vsUtg1Radio;
    @FXML
    private RadioButton vsUtg2Radio;
    @FXML
    private RadioButton vsLjRadio;
    @FXML
    private RadioButton vsHjRadio;
    @FXML
    private RadioButton vsCoRadio;
    @FXML
    private RadioButton vsBtnRadio;
    @FXML
    private RadioButton vsSbRadio;
    @FXML
    private RadioButton vs3bRadio;
    @FXML
    private RadioButton vs4bRadio;
    @FXML
    private RadioButton vs5bRadio;
    @FXML
    private RadioButton vsAllinRadio;
    @FXML
    private RadioButton withLimpersRadio;
    @FXML
    private ToggleGroup stacksGroup;
    private Map<Position, RadioButton> raised;

    public SidebarView(Stage stage, FloplessLoop loop) {
        this.stage = stage;
        this.loop = loop;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        raised = Map.of(
          new Position.Utg(), vsUtgRadio,
          new Position.Utg1(), vsUtg1Radio,
          new Position.Utg2(), vsUtg2Radio,
          new Position.Lj(), vsLjRadio,
          new Position.Hj(), vsHjRadio,
          new Position.Co(), vsCoRadio,
          new Position.Btn(), vsBtnRadio,
          new Position.Sb(), vsSbRadio
        );

        setupActions();
        setupSubscription();
    }

    private void setupActions() {
        EventHandler<ActionEvent> pickTable = (ActionEvent _) ->
          loop.accept(new Action.User.TableTypePick(tableType()));
        sixMaxRadio.setOnAction(pickTable);
        nineMaxRadio.setOnAction(pickTable);
        bb100Radio.setOnAction(pickTable);
        bb200Radio.setOnAction(pickTable);
        oneTwoBlindsRadio.setOnAction(pickTable);
        twoFourBlindsRadio.setOnAction(pickTable);

        EventHandler<ActionEvent> pickPosition = (ActionEvent _) ->
          loop.accept(new Action.User.PositionPick(position()));

        for (var toggle : heroPositionGroup.getToggles()) {
            ((ButtonBase) toggle).setOnAction(pickPosition);
        }

        withLimpersRadio.setOnAction(_ -> {
            loop.accept(new Action.User.ToggleLimpersSqueeze());
        });
    }

    private TableType tableType() {
        return sixMaxRadio.isSelected()
                 ? new TableType.SixMax(stack(), blinds())
                 : new TableType.NineMax(stack(), blinds());
    }

    private StackDepth stack() {
        return bb100Radio.isSelected() ? new StackDepth.Bb100() : new StackDepth.Bb200();
    }

    private Blinds blinds() {
        return oneTwoBlindsRadio.isSelected() ? new Blinds.OneSbTwoBB() : new Blinds.TwoSbFourBB();
    }

    private Position position() {
        var radio = (Labeled) heroPositionGroup.getSelectedToggle();
        return switch (radio.getText()) {
            case "UTG" ->
                new Position.Utg();
            case "UTG1" ->
                new Position.Utg1();
            case "UTG2" ->
                new Position.Utg2();
            case "LJ" ->
                new Position.Lj();
            case "HJ" ->
                new Position.Hj();
            case "CO" ->
                new Position.Co();
            case "BTN" ->
                new Position.Btn();
            case "SB" ->
                new Position.Sb();
            case "BB" ->
                new Position.Bb();
            default ->
                throw new IllegalStateException("Unexpected value: " + (radio.getText()));
        };
    }

    private void setupSubscription() {
        observe = new DisposableObserver<>() {
            @Override
            public void onNext(History<FloplessState> history) {
                Platform.runLater(() -> render(history.present()));
            }

            @Override
            public void onError(Throwable e) {
                throw new IllegalStateException(e);
            }

            @Override
            public void onComplete() {
                // Not supported
            }
        };
        loop.subscribe(observe);
        stage.setOnHiding(_ -> dispose());
    }

    private void render(FloplessState state) {
        var tableType = state.tableType();
        var isSixMax = tableType instanceof TableType.SixMax;
        sixMaxRadio.setSelected(isSixMax);
        nineMaxRadio.setSelected(!isSixMax);

        var isBb100 = tableType.stack() instanceof StackDepth.Bb100;
        bb100Radio.setSelected(isBb100);
        bb200Radio.setSelected(!isBb100);

        var is1Sb2bb = tableType.blinds() instanceof Blinds.OneSbTwoBB;
        oneTwoBlindsRadio.setSelected(is1Sb2bb);
        twoFourBlindsRadio.setSelected(!is1Sb2bb);

        var position = state.position();
        for (var toggle : heroPositionGroup.getToggles()) {
            var isSelectedPosition = ((Labeled) toggle).getText().equalsIgnoreCase(position.getClass().getSimpleName());
            toggle.setSelected(isSelectedPosition);
        }

        var hero = position;
        openRadio.setDisable(hero instanceof Position.Bb);

        raised.forEach((villain, radio) ->
          radio.setDisable(villain.index() >= hero.index())
        );

        withLimpersRadio.setDisable(hero instanceof Position.Utg);
        withLimpersRadio.setSelected(state.squeezeLimpers());
    }

    private void dispose() {
        if (observe != null && !observe.isDisposed()) {
            observe.dispose();
        }
    }
}
