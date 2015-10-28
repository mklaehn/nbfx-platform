/* 
 * The MIT License
 *
 * Copyright 2015 NBFx.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.nbfx.explorer.view.list;

import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.image.ImageView;
import org.nbfx.explorer.view.NodeView;
import org.nbfx.nodes.wrapper.NodeWrapper;
import org.nbfx.util.NBFxUtilities;
import org.openide.nodes.Node;

public class NBFxListView extends NodeView<NodeWrapper> {

    private final ListView<NodeWrapper> listView = new ListView<>();

    public NBFxListView() {
        setCenter(listView);
        listView.setCellFactory(param -> {
            final ListCell<NodeWrapper> cell = new ListCell<>();

            cell.itemProperty().addListener((ov, oldValue, newValue) -> {
                if (null != oldValue || null == newValue) {
                    cell.textProperty().unbind();
                    cell.textProperty().set(null);
                    cell.contextMenuProperty().unbind();
                    cell.contextMenuProperty().set(null);
                    cell.setGraphic(null);
                }

                if (null != newValue) {
                    final ImageView imageView = new ImageView();

                    imageView.imageProperty().bind(newValue.nodeIconProperty());
                    cell.textProperty().bind(newValue.displayNameProperty());
                    cell.contextMenuProperty().bind(newValue.contextMenuProperty());
                    cell.setGraphic(imageView);
                }
            });

            return cell;
        });

        final ChangeListener<MultipleSelectionModel<NodeWrapper>> selModelListener = (ov, oldValue, newValue) -> {
            if (null != oldValue) {
                oldValue.selectedItemProperty().removeListener(getSelectionListener());
            }

            if (null != newValue) {
                newValue.selectedItemProperty().addListener(getSelectionListener());
                getSelectionListener().changed(null, null, newValue.getSelectedItem());
            }
        };
        final ListChangeListener<? super Node> selectionListener = change -> {
            final List<NodeWrapper> wrappers = getRepresentations(change.getList().stream());
            NBFxUtilities.FX.runLater(() -> listView.getItems().setAll(wrappers));
        };

        listView.selectionModelProperty().addListener(selModelListener);
        selModelListener.changed(null, null, listView.getSelectionModel());

        setRepresentationFactory(nw -> nw);
        rootNodeProperty().addListener((ov, oldValue, newValue) -> {
            if (null != oldValue) {
                oldValue.childNodes().removeListener(selectionListener);
            }

            if (null != newValue) {
                newValue.childNodes().addListener(selectionListener);
                newValue.addNotify();
            }
        });
    }

    @Override
    protected Node getNode(final NodeWrapper t) {
        if (null == t) {
            return null;
        } else {
            t.contextMenuProperty(); // forces update of contextmenu
            return t.getValue();
        }
    }
}
