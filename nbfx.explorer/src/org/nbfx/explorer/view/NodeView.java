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
package org.nbfx.explorer.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import org.nbfx.nodes.wrapper.NodeWrapper;
import org.nbfx.util.NBFxUtilities;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Parameters;

public abstract class NodeView<T> extends BorderPane implements ExplorerManager.Provider {

    private final EMListener<T> emListener = new EMListener<>(this);
    private ExplorerManager explorerManager = null;
    private Callback<NodeWrapper, T> representationFactory = null;
    private final ObjectProperty<T> rootNodeProperty = new SimpleObjectProperty<>(null);
    private final ChangeListener<T> selectionListener = (ov, oldValue, newValue) -> {
        if (null != getExplorerManager()) {
            final Node node = getNode(newValue);

            final Node[] nodes = (null == node)
                    ? new Node[0]
                    : new Node[]{node};

            try {
                getExplorerManager().setSelectedNodes(nodes);
            } catch (final PropertyVetoException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    };

    public ObjectProperty<T> rootNodeProperty() {
        return rootNodeProperty;
    }

    public final ChangeListener<T> getSelectionListener() {
        return selectionListener;
    }

    public void setRootNode(final org.openide.nodes.Node node) {
        setRootNode(new NodeWrapper(node));
    }

    public void setRootNode(final NodeWrapper nodeWrapper) {
        NBFxUtilities.FX.runLater(() -> rootNodeProperty.set(getRepresentation(nodeWrapper)));
    }

    public final void setExplorerManager(final ExplorerManager explorerManager) {
        emListener.detach(this.explorerManager);
        this.explorerManager = explorerManager;
        emListener.attach(this.explorerManager);
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }

    public Callback<NodeWrapper, T> getRepresentationFactory() {
        return representationFactory;
    }

    public void setRepresentationFactory(final Callback<NodeWrapper, T> representationFactory) {
        this.representationFactory = representationFactory;
    }

    public T getRepresentation(final NodeWrapper nodeWrapper) {
        if (null == representationFactory) {
            assert false : "RepresentationFactory is not set";
            return null;
        } else {
            return representationFactory.call(nodeWrapper);
        }
    }

    public List<T> getRepresentations(final Stream<? extends Node> nodeStream) {
        return nodeStream
                .map(NodeWrapper::new)
                .map(nw -> getRepresentation(nw))
                .filter(o -> null != o)
                .collect(Collectors.toList());
    }

    protected abstract Node getNode(final T t);

    private static class EMListener<T> implements PropertyChangeListener {

        private final NodeView<T> nodeView;

        private EMListener(final NodeView<T> nodeView) {
            Parameters.notNull("nodeView", nodeView);
            this.nodeView = nodeView;
        }

        @Override
        public void propertyChange(final PropertyChangeEvent pce) {
            if (ExplorerManager.PROP_ROOT_CONTEXT.equals(pce.getPropertyName())
                    && (pce.getNewValue() instanceof Node)) {
                update((Node) pce.getNewValue());
            }
        }

        public void attach(final ExplorerManager explorerManager) {
            if (null != explorerManager) {
                explorerManager.addPropertyChangeListener(this);
                update(explorerManager.getRootContext());
            } else {
                update(null);
            }
        }

        public void detach(final ExplorerManager explorerManager) {
            if (null != explorerManager) {
                explorerManager.removePropertyChangeListener(this);
            }
        }

        private void update(final Node node) {
            nodeView.setRootNode(node);
        }
    }

    @Override
    public String toString() {
        return super.toString() + "; w: " + getWidth() + "; h: " + getHeight() + "; v: " + isVisible() + "; d1: " + isDisable()+ "; d2: " + isDisabled();
    }
}
