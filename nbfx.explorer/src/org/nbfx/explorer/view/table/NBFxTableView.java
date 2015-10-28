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
package org.nbfx.explorer.view.table;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.nbfx.explorer.view.NodeView;
import org.nbfx.nodes.wrapper.NodeWrapper;
import org.nbfx.util.NBFxUtilities;
import org.openide.nodes.Node;
import org.openide.util.Parameters;

public class NBFxTableView extends NodeView<NodeWrapper> {

    private final TableView<NodeWrapper> tableView = new TableView<>();

    public NBFxTableView() {
        setCenter(tableView);
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
            NBFxUtilities.FX.runLater(() -> tableView.getItems().setAll(wrappers));
        };

        tableView.selectionModelProperty().addListener(selModelListener);
        selModelListener.changed(null, null, tableView.getSelectionModel());

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

    public void setColumns(final TableColumnDefinition<?>... tcds) {
        final List<TableColumnDefinition<?>> defs = new ArrayList<>();
        boolean hasNodeColumn = false;

        if ((null != tcds) && (0 != tcds.length)) {
            for (final TableColumnDefinition<?> def : tcds) {
                if (null == def) {
                    continue;
                }

                if (Node.class.equals(def.dataClass)) {
                    if (!hasNodeColumn) {
                        hasNodeColumn = true;
                        defs.add(def);
                    }
                } else {
                    defs.add(def);
                }
            }
        }

        if (!hasNodeColumn) {
//            defs.add(0, TableColumnDefinition.createNodeColumn());
        }

        NBFxUtilities.FX.runLater(() -> tableView.getColumns().setAll(defs));
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

    public final void setTableMenuButtonVisible(final boolean visible) {
        NBFxUtilities.FX.runLater(() -> tableView.setTableMenuButtonVisible(visible));
    }

    public static class TableColumnDefinition<D> extends TableColumn<NodeWrapper, D> {

        private static final String NODE_COLUMN_NAME = Node.class.getName();
        private final Class<D> dataClass;

        public TableColumnDefinition(final String title, final String name, final Class<D> dataClass) {
            super(title);
            Parameters.notNull("name", name);
            this.dataClass = dataClass;

            if (!NODE_COLUMN_NAME.equals(name)) {
                Parameters.notNull("dataClass", dataClass);
                this.setCellValueFactory(p -> NodeWrapper.getValue((null == p) ? null : p.getValue(), name, dataClass));
            }
        }
    }
}
