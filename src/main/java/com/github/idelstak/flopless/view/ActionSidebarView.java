package com.github.idelstak.flopless.view;

import java.net.*;
import java.util.*;
import javafx.fxml.*;
import javafx.scene.control.*;

public final class ActionSidebarView implements Initializable {

    @FXML
    private RadioButton foldRadio;
    @FXML
    private ToggleGroup actionGroup;
    @FXML
    private RadioButton checkRadio;
    @FXML
    private RadioButton callRadio;
    @FXML
    private RadioButton raiseRadio;
    @FXML
    private RadioButton allinRadio;
    @FXML
    private TextField limperAmountField;
    @FXML
    private Button decrementActionAmountButton;
    @FXML
    private TextField actionAmountField;
    @FXML
    private Button incrementActionAmountButton;
    @FXML
    private Button decrementLimperAmountButton;
    @FXML
    private Button incrementLimperAmountButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
}
