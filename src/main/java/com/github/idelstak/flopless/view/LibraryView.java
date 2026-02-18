package com.github.idelstak.flopless.view;

import com.github.idelstak.flopless.io.*;
import com.github.idelstak.flopless.state.*;
import com.github.idelstak.flopless.state.api.*;
import io.reactivex.rxjava3.observers.*;
import java.net.*;
import java.util.*;
import javafx.application.*;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import org.kordamp.ikonli.javafx.*;

public final class LibraryView implements Initializable {

    private final Stage stage;
    private final FloplessLoop loop;
    private final Persistence persistence;
    private final Strategy strategy;
    private DisposableObserver<History<FloplessState>> observe;
    private String activeStateName;
    private String searchTerm;
    private final ObservableList<FloplessState> ranges = FXCollections.observableArrayList();
    @FXML
    private ListView<FloplessState> rangesListView;
    @FXML
    private Button newRangeButton;
    @FXML
    private TextField chartsSearchField;
    @FXML
    private StackPane clearSearchPane;

    public LibraryView(Stage stage, FloplessLoop loop, Persistence persistence) {
        this.persistence = persistence;
        this.stage = stage;
        this.loop = loop;
        strategy = new Strategy();

        activeStateName = "";
        searchTerm = "";
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        var savedStates = persistence.loadAll();
        setupListView();
        Platform.runLater(() -> renderLibrary(savedStates, searchTerm));
        clearSearchPane.setVisible(false);
        clearSearchPane.setManaged(false);
        setupActions();
        setupSubscription();

        if (!savedStates.isEmpty()) {
            Platform.runLater(() -> {
                loop.accept(new Action.User.LoadState(savedStates.getFirst()));
            });
        }
    }

    private void setupListView() {
        rangesListView.setItems(ranges);
        rangesListView.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.DELETE || e.getCode() == KeyCode.BACK_SPACE) {
                var selected = rangesListView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    requestDelete(selected);
                    e.consume();
                }
            }
        });
        rangesListView.setCellFactory(_ -> new ListCell<>() {
            private final HBox item = new HBox();
            private final Label label = new Label();
            private final Region spacer = new Region();
            private final Button deleteButton = new Button();

            {
                item.getStyleClass().add("grid-item");
                item.maxWidthProperty().bind(rangesListView.widthProperty().multiply(0.93));
                label.setWrapText(true);
                label.setMinHeight(55);
                HBox.setHgrow(spacer, Priority.ALWAYS);

                var deleteIcon = new FontIcon("jam-trash");
                deleteIcon.getStyleClass().add("delete-chart-icon");
                deleteButton.setGraphic(deleteIcon);
                deleteButton.getStyleClass().add("delete-chart-btn");
                deleteButton.setFocusTraversable(false);
                deleteButton.setOnAction(e -> {
                    e.consume();
                    var state = getItem();
                    if (state != null) {
                        requestDelete(state);
                    }
                });

                item.getChildren().addAll(label, spacer, deleteButton);
                item.setOnMouseClicked(e -> {
                    var state = getItem();
                    if (state != null) {
                        e.consume();
                        loop.accept(new Action.User.LoadState(state));
                    }
                });
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            }

            @Override
            protected void updateItem(FloplessState state, boolean empty) {
                super.updateItem(state, empty);
                if (empty || state == null) {
                    setGraphic(null);
                    return;
                }

                var name = strategy.name(state);
                label.setText(name);
                item.getStyleClass().remove("active");
                if (name.equals(activeStateName)) {
                    item.getStyleClass().add("active");
                }
                setGraphic(item);
            }
        });
    }

    private void requestDelete(FloplessState state) {
        loop.accept(new Action.Effect.DeleteStateConfirmRequested(state));
    }

    private void renderLibrary(List<FloplessState> states, String searchTerm) {
        var filtered = filterStates(states, searchTerm);
        int activeIndex = -1;
        for (int i = 0; i < filtered.size(); i++) {
            if (strategy.name(filtered.get(i)).equals(activeStateName)) {
                activeIndex = i;
                break;
            }
        }

        ranges.setAll(filtered);
        rangesListView.refresh();
        if (activeIndex >= 0) {
            int indexToScroll = activeIndex;
            Platform.runLater(() -> rangesListView.scrollTo(indexToScroll));
        }
    }

    private void setupActions() {
        newRangeButton.setOnAction(_ ->
          loop.accept(new Action.User.LoadState(FloplessState.initial())));

        chartsSearchField.textProperty().addListener((_, _, nextText) ->
          loop.accept(new Action.User.LibrarySearchTerm(normalizeSearchTerm(nextText))));

        clearSearchPane.setOnMouseClicked(_ ->
          loop.accept(new Action.User.ClearLibrarySearch()));
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
        activeStateName = strategy.name(state);
        searchTerm = normalizeSearchTerm(state.librarySearchTerm());

        if (!searchTerm.equals(chartsSearchField.getText())) {
            chartsSearchField.setText(searchTerm);
        }

        var hasSearch = !searchTerm.isBlank();
        clearSearchPane.setVisible(hasSearch);
        clearSearchPane.setManaged(hasSearch);

        var savedStates = persistence.loadAll();
        renderLibrary(savedStates, searchTerm);
    }

    private String normalizeSearchTerm(String text) {
        return text == null ? "" : text;
    }

    private List<FloplessState> filterStates(List<FloplessState> states, String searchTerm) {
        if (searchTerm.isBlank()) {
            return states;
        }
        var query = searchTerm.trim().toLowerCase(Locale.ROOT);
        return states.stream()
          .filter(state -> strategy.name(state).toLowerCase(Locale.ROOT).contains(query))
          .toList();
    }

    private void dispose() {
        if (observe != null && !observe.isDisposed()) {
            observe.dispose();
        }
    }
}
