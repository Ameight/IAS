package Scenes;
// ?????????? ??????????? ??????????

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class AuthPageScene extends Scene {

    private final Stage _parent;

    private double width, height;

    private final ChangeListener<Number> widthListener = new ChangeListener<Number>() {
        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            width = newValue.doubleValue();
            _resize();
        }
    };

    private final ChangeListener<Number> heightListener = new ChangeListener<Number>() {
        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            height = newValue.doubleValue();
            _resize();
        }
    };

    public AuthPageScene(Parent root, Stage parent) {
        super(root);
        _parent = parent;
        _parent.widthProperty().addListener(widthListener);
        _parent.heightProperty().addListener(heightListener);
    }


    private void _resize() {
        GridPane pane = null;
        ObservableList<Node> children = getRoot().getChildrenUnmodifiable();
        for (Node node : children) {
            if (node instanceof GridPane) {
                pane = (GridPane) node;
                break;
            }
        }
        pane.setPrefWidth(width);
        pane.setPrefHeight(height);

        int sizeColumn = pane.getColumnConstraints().size();
        pane.getColumnConstraints().forEach(column -> {
            column.setMinWidth(width / sizeColumn);
        });
    }
}
