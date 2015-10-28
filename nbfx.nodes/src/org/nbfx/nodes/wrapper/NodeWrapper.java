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
package org.nbfx.nodes.wrapper;

import java.beans.BeanInfo;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import org.nbfx.actions.MenuItemsConverter;
import org.nbfx.util.NBFxImageUtilities;
import org.nbfx.nodes.properties.NBFxNodeProperty;
import org.nbfx.nodes.properties.NBFxNodePropertyUtility;
import org.nbfx.util.NBFxUtilities;
import org.nbfx.util.ParametrizedPCL;
import org.openide.nodes.Node;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.NodeAdapter;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;
import org.openide.util.RequestProcessor;

public class NodeWrapper extends FeatureDescriptorWrapper<Node> {

    private static final RequestProcessor RP = new RequestProcessor(NodeWrapper.class.getSimpleName(), 4);
    private static final int ICON_KEY = Integer.getInteger("NBFxNodeIcon", BeanInfo.ICON_COLOR_16x16);
    private final ObjectProperty<Image> nodeIconProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<Image> nodeIconOpenedProperty = new SimpleObjectProperty<>();
    private final ReadOnlyObjectWrapper<ContextMenu> contextMenuProperty = new ReadOnlyObjectWrapper<>();
    private final ObservableList<Node> childNodes = FXCollections.<Node>observableArrayList();

    public NodeWrapper(final Node node) {
        super(node);

        final ParametrizedPCL ppcl = new ParametrizedPCL();

        ppcl.addPropertyChangeListener(Node.PROP_DISPLAY_NAME, NBFxUtilities.FX, e -> displayNameProperty().set(node.getDisplayName()));
        ppcl.addPropertyChangeListener(Node.PROP_NAME, e -> nameProperty().set(node.getName()));
        ppcl.addPropertyChangeListener(Node.PROP_SHORT_DESCRIPTION, e -> shortDescriptionProperty().set(node.getShortDescription()));
        ppcl.addPropertyChangeListener(Node.PROP_ICON, NBFxUtilities.FX, e -> nodeIconProperty().set(NBFxImageUtilities.getImage((null == e.getNewValue())
                ? getValue().getIcon(ICON_KEY)
                : e.getNewValue())));
        ppcl.addPropertyChangeListener(Node.PROP_LEAF, e -> childNodes.setAll(Collections.<Node>emptyList()));
        ppcl.addPropertyChangeListener(Node.PROP_OPENED_ICON, NBFxUtilities.FX, e -> nodeIconOpenedProperty().set(NBFxImageUtilities.getImage((null == e.getNewValue())
                ? getValue().getOpenedIcon(ICON_KEY)
                : e.getNewValue())));

        node.addPropertyChangeListener(ppcl);

        node.addNodeListener(new NodeAdapter() {

            @Override
            public void childrenAdded(final NodeMemberEvent nme) {
                NBFxUtilities.SWING.ensureThread();
                childNodes.setAll(nme.getSnapshot());
            }

            @Override
            public void childrenRemoved(final NodeMemberEvent nme) {
                NBFxUtilities.SWING.ensureThread();
                childNodes.setAll(nme.getSnapshot());
            }

            @Override
            public void childrenReordered(final NodeReorderEvent nre) {
                NBFxUtilities.SWING.ensureThread();
                childNodes.setAll(nre.getSnapshot());
            }

            @Override
            public void nodeDestroyed(final NodeEvent ne) {
                NBFxUtilities.SWING.ensureThread();
                childNodes.remove(ne.getNode());
            }

            @Override
            public void propertyChange(final PropertyChangeEvent pce) {
                ppcl.propertyChange(pce);
                contextMenuProperty(); // forces init/update of contextmenu
            }
        });

        nodeIconProperty().set(NBFxImageUtilities.getImage(node.getIcon(ICON_KEY)));
        nodeIconOpenedProperty().set(NBFxImageUtilities.getImage(node.getOpenedIcon(ICON_KEY)));
    }

    public final ObjectProperty<Image> nodeIconOpenedProperty() {
        return nodeIconOpenedProperty;
    }

    public final ObjectProperty<Image> nodeIconProperty() {
        return nodeIconProperty;
    }

    public final ObservableList<Node> childNodes() {
        return childNodes;
    }

    public final void addNotify() {
        RP.post(() -> childNodes.setAll(getValue().getChildren().getNodes()));
    }

    public ReadOnlyObjectProperty<ContextMenu> contextMenuProperty() {
        synchronized (this) {
            if (null == contextMenuProperty.getValue()) {
                contextMenuProperty.setValue(NBFxUtilities.FX.isCurrentThread() ? new ContextMenu() : NBFxUtilities.FX.get(ContextMenu::new));
            }
        }

        // update the content of the ContextMenu
        NBFxUtilities.SWING.runLater(() -> {
            final Collection<? extends MenuItem> convertMenuItems = MenuItemsConverter.convertMenuItems(getValue());
            NBFxUtilities.FX.runLater(() -> contextMenuProperty.getValue().getItems().setAll(convertMenuItems));
        });

        return contextMenuProperty.getReadOnlyProperty();
    }

    public Map<String, List<NBFxNodeProperty<?>>> getNodeProperties() {
        return getNodeProperties((null == getValue())
                ? null
                : getValue().getPropertySets());
    }

    public static Map<String, List<NBFxNodeProperty<?>>> getNodeProperties(final PropertySet[] propertySets) {
        if ((null == propertySets) || (0 == propertySets.length)) {
            return Collections.<String, List<NBFxNodeProperty<?>>>emptyMap();
        }

        final Map<String, List<NBFxNodeProperty<?>>> groups = new LinkedHashMap<>();

        for (final Node.PropertySet propertySet : propertySets) {
            final List<NBFxNodeProperty<?>> properties = new ArrayList<>(propertySet.getProperties().length);

            for (org.openide.nodes.Node.Property<?> nodeProperty : propertySet.getProperties()) {
                properties.add(NBFxNodePropertyUtility.createNBFxNodeProperty(nodeProperty));
            }

            groups.put(propertySet.getDisplayName(), properties);
        }

        return groups;
    }

    public static <D> ObservableValue<D> getValue(final NodeWrapper nodeWrapper, final String name, final Class<D> dataClass) {
        return getValue((null == nodeWrapper) ? null : nodeWrapper.getValue(), name, dataClass);
    }

    public static <D> ObservableValue<D> getValue(final Node node, final String name, final Class<D> dataClass) {
        return getValue((null == node) ? null : node.getPropertySets(), name, dataClass);
    }

    public static <D> ObservableValue<D> getValue(final PropertySet[] propertySets, final String name, final Class<D> dataClass) {
        if ((null == propertySets) || (0 == propertySets.length)) {
            return null;
        }

        for (final PropertySet propertySet : propertySets) {
            if ((null == propertySet) || (0 == propertySet.getProperties().length)) {
                continue;
            }

            for (final Node.Property<?> property : propertySet.getProperties()) {
                if (name.equals(property.getName()) && dataClass.isAssignableFrom(property.getValueType())) {
                    @SuppressWarnings("unchecked")
                    final ObservableValue<D> ov = (ObservableValue<D>) NBFxNodePropertyUtility.createNBFxNodeProperty(property);
                    return ov;
                }
            }
        }

        return null;
    }
}
