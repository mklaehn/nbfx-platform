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

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import javax.swing.Action;
import org.nbfx.nodes.wrapper.NodeWrapper;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;

final class TreeNodeItem extends TreeItem<Node> {

    private final Map<Node, TreeNodeItem> convertedNodes = new ConcurrentHashMap<>();
    private final NodeWrapper nodeWrapper;
    private final ImageView imageView = new ImageView();

    public TreeNodeItem(final NodeWrapper nodeWrapper) {
        super(nodeWrapper.getValue());

        this.nodeWrapper = nodeWrapper;

        setGraphic(imageView);

        // children
        nodeWrapper.childNodes().addListener((ListChangeListener.Change<? extends Node> change) -> {
            if (isExpanded() || isLeaf()) {
                final ObservableList<? extends Node> ol = change.getList();
                getChildren().setAll((ol.size() > 10 ? ol.parallelStream() : ol.stream())
                        .filter(o -> null != o)
                        .map(node -> convertedNodes.computeIfAbsent(node, n -> new TreeNodeItem(new NodeWrapper(n))))
                        .collect(Collectors.toList()));
            }
        });

        // expanded && image
        expandedProperty().addListener((ov, oldValue, newValue) -> {
            nodeWrapper.addNotify();
            updateIcon(newValue);
        });

        updateIcon(isExpanded());

        // leaf
        if (!getValue().isLeaf()) {
            getChildren().setAll(Collections.singleton(new TreeNodeItem(new NodeWrapper(createWaitNode()))));
        }
    }

    public StringProperty textProperty() {
        return nodeWrapper.displayNameProperty();
    }

    public ReadOnlyObjectProperty<ContextMenu> contextMenuProperty() {
        return nodeWrapper.contextMenuProperty();
    }

    private void updateIcon(final Boolean isExpanded) {
        imageView.imageProperty().unbind();

        if (leafProperty().get() || Boolean.FALSE.equals(isExpanded)) {
            imageView.imageProperty().bind(nodeWrapper.nodeIconProperty());
        } else if (Boolean.TRUE.equals(isExpanded)) {
            imageView.imageProperty().bind(nodeWrapper.nodeIconOpenedProperty());
        }
    }

    @Override
    public boolean equals(final Object o) {
        return (o instanceof TreeNodeItem)
                && getValue().equals(((TreeNodeItem) o).getValue());
    }

    @Override
    public int hashCode() {
        int hash = 37;
        hash = 67 * hash + getValue().hashCode();
        return hash;
    }

    private static Node createWaitNode() {
        final AbstractNode an = new AbstractNode(Children.LEAF) {

            @Override
            public Action[] getActions(boolean context) {
                return new Action[0];
            }
        };

        an.setDisplayName(NbBundle.getMessage(ChildFactory.class, "LBL_WAIT")); //NOI18N
        an.setIconBaseWithExtension("org/openide/nodes/wait.gif"); //NOI18N

        return an;
    }
}
