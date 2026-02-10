package com.github.idelstak.flopless.view;

import javafx.scene.*;
import javafx.scene.control.*;

public final class ScrollTo {

    public void reveal(ScrollPane pane, Node item) {
        var content = (Parent) pane.getContent();
        content.applyCss();
        content.layout();
        var itemBounds = item.getBoundsInParent();
        var contentBounds = content.getBoundsInLocal();
        var viewport = pane.getViewportBounds().getHeight();
        var target = itemBounds.getMinY() - (viewport - itemBounds.getHeight()) / 2;
        pane.setVvalue(target / contentBounds.getHeight());
    }
}
