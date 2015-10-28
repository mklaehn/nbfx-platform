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
package org.nbfx.explorer.view.tree;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.nbfx.explorer.view.NodeView;
import org.openide.nodes.Node;

public final class NBFxTreeView extends NodeView<TreeItem<Node>> {

    private final TreeView<Node> treeView = new TreeView<>();

    public NBFxTreeView() {
        setCenter(treeView);
        treeView.setCellFactory(param -> {
            final TreeCell<Node> cell = new TreeCell<>();

            cell.treeItemProperty().addListener((ov, oldValue, newValue) -> {
                if (null != oldValue || null == newValue) {
                    cell.textProperty().unbind();
                    cell.textProperty().setValue(null);
                    cell.graphicProperty().unbind();
                    cell.graphicProperty().setValue(null);
                    cell.contextMenuProperty().unbind();
                    cell.contextMenuProperty().setValue(null);
                }

                if (null != newValue) {
                    if (newValue instanceof TreeNodeItem) {
                        cell.textProperty().bind(((TreeNodeItem) newValue).textProperty());
                        cell.contextMenuProperty().bind(((TreeNodeItem) cell.getTreeItem()).contextMenuProperty());
                    } else {
                        cell.textProperty().set(newValue.getValue().getDisplayName());
                    }

                    cell.graphicProperty().bind(newValue.graphicProperty());
                }
            });

            return cell;
        });

        final ChangeListener<MultipleSelectionModel<TreeItem<Node>>> selModelListener = (ov, oldValue, newValue) -> {
            if (null != oldValue) {
                oldValue.selectedItemProperty().removeListener(getSelectionListener());
            }

            if (null != newValue) {
                newValue.selectedItemProperty().addListener(getSelectionListener());
                getSelectionListener().changed(null, null, newValue.getSelectedItem());
            }
        };

        treeView.selectionModelProperty().addListener(selModelListener);
        selModelListener.changed(null, null, treeView.getSelectionModel());

        setRepresentationFactory(TreeNodeItem::new);
        rootNodeProperty().addListener((ov, oldValue, newValue) -> treeView.setRoot(newValue));
    }

    @Override
    protected Node getNode(final TreeItem<Node> t) {
        return (null == t)
                ? null
                : t.getValue();
    }
}
