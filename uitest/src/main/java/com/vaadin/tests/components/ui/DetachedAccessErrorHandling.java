/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.components.ui;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.Constants;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.ErrorHandlingRunnable;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.UIDetachedException;

@Widgetset(Constants.DEFAULT_WIDGETSET)
public class DetachedAccessErrorHandling extends AbstractTestUI {

    private static final Runnable NOP = () -> {
    };

    private static final class ListErrorHandler implements ErrorHandler {
        private List<com.vaadin.server.ErrorEvent> errors = new CopyOnWriteArrayList<>();

        @Override
        public void error(com.vaadin.server.ErrorEvent event) {
            errors.add(event);
        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        ListErrorHandler errorHandler = ensureErrorHandlerSet();

        addComponent(new Button("Show errors", event -> {
            errorHandler.errors.forEach(error -> {
                Label errorLabel = new Label(error.getThrowable().getMessage());
                errorLabel.setStyleName("errorLabel");
                addComponent(errorLabel);
            });
        }), "show");

        addComponent(new Button("Add simple detach listener", event -> {
            addDetachListener(detachEvent -> access(NOP));
        }), "simple");

        addComponent(new Button("Add error handling detach listener", event -> {
            addDetachListener(detachEvent -> {
                access(new ErrorHandlingRunnable() {
                    @Override
                    public void run() {
                    }

                    @Override
                    public void handleError(Exception exception) {
                        if (exception instanceof UIDetachedException) {
                            UIDetachedException ignore = (UIDetachedException) exception;
                        } else {
                            throw new RuntimeException(exception);
                        }
                    }
                });
            });
        }), "handling");
    }

    private ListErrorHandler ensureErrorHandlerSet() {
        ErrorHandler currentErrorHandler = getSession().getErrorHandler();

        if (!(currentErrorHandler instanceof ListErrorHandler)) {
            currentErrorHandler = new ListErrorHandler();
            getSession().setErrorHandler(currentErrorHandler);
        }

        return (ListErrorHandler) currentErrorHandler;
    }

}
