/**
 * 
 */
package com.vaadin.terminal.gwt.client.ui.dd;

import com.vaadin.terminal.gwt.client.UIDL;

@ServerCriterion("com.vaadin.event.dd.acceptCriteria.OverTreeNode")
final class OverTreeNode implements VAcceptCriteria {
    public void accept(VDragEvent drag, UIDL configuration,
            VAcceptCallback callback) {
        Boolean containsKey = (Boolean) drag.getDropDetails().get(
                "itemIdOverIsNode");
        if (containsKey != null && containsKey.booleanValue()) {
            callback.accepted(drag);
            return;
        }
        return;
    }

    public boolean needsServerSideCheck(VDragEvent drag, UIDL criterioUIDL) {
        return false;
    }
}