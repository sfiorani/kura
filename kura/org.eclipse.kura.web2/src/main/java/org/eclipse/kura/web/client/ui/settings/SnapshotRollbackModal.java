package org.eclipse.kura.web.client.ui.settings;

import java.util.List;

import org.eclipse.kura.web.client.ui.EntryClassUi;
import org.eclipse.kura.web.client.util.FailureHandler;
import org.eclipse.kura.web.shared.model.GwtSnapshot;
import org.eclipse.kura.web.shared.model.GwtXSRFToken;
import org.eclipse.kura.web.shared.service.GwtSecurityTokenService;
import org.eclipse.kura.web.shared.service.GwtSecurityTokenServiceAsync;
import org.eclipse.kura.web.shared.service.GwtSnapshotService;
import org.eclipse.kura.web.shared.service.GwtSnapshotServiceAsync;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.constants.ButtonType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SnapshotRollbackModal extends SnapshotSelectorModal {

    private static final String FONT_AWESOME_STYLE_NAME = "fa";

    private final GwtSecurityTokenServiceAsync gwtXSRFService = GWT.create(GwtSecurityTokenService.class);
    private final GwtSnapshotServiceAsync gwtSnapshotService = GWT.create(GwtSnapshotService.class);

    SnapshotSelectorActionButton cancelButton;
    SnapshotSelectorActionButton rollbackButton;

    @Override
    protected void customiseModal(long snapshotId) {
        clearClickHandlers();

        this.cancelButton = new SnapshotSelectorActionButton(MSGS.cancelButton(), FONT_AWESOME_STYLE_NAME,
                ButtonType.PRIMARY, e -> hideAndReset());

        this.rollbackButton = new SnapshotSelectorActionButton(MSGS.rollback(), FONT_AWESOME_STYLE_NAME,
                ButtonType.PRIMARY, e -> onRollbackConfirmation(snapshotId, getSelectedPidsList()));

        addFooterButton(this.cancelButton);
        addFooterButton(this.rollbackButton);

        setTitleDescriptionAndHints(MSGS.deviceSnapshotRollbackTitle(), MSGS.deviceSnapshotRollbackConfirm(),
                MSGS.deviceSnapshotRollbackHint());

        setAdvancedModePanel(true);

        setAdvancedModeHintText(MSGS.snapshotRollbackAdvancedModeHint());

        setAdvancedModeClickHandler(this::onAdvancedModeClick);

    }

    /*
     * OnEvent Methods
     */

    private void onRollbackConfirmation(long snapshotId, List<String> selectedPids) {
        EntryClassUi.showWaitModal();
        this.gwtXSRFService.generateSecurityToken(new AsyncCallback<GwtXSRFToken>() {

            @Override
            public void onFailure(Throwable ex) {
                EntryClassUi.hideWaitModal();
                FailureHandler.handle(ex);

            }

            @Override
            public void onSuccess(GwtXSRFToken token) {
                gwtSnapshotService.findDeviceSnapshotFromSid(token, snapshotId, new AsyncCallback<List<GwtSnapshot>>() {

                    @Override
                    public void onFailure(Throwable ex) {
                        EntryClassUi.hideWaitModal();
                        FailureHandler.handle(ex);

                    }

                    @Override
                    public void onSuccess(List<GwtSnapshot> snapshot) {
                        if (advancedModeCheckbox.getValue().booleanValue()) {
                            snapshtoRollback(token, snapshot);
                        } else {
                            configurationRollback(token, snapshot, selectedPids);
                        }
                    }

                });
            }
        });
        hideAndReset();
    }

    private void onAdvancedModeClick(ClickEvent clickHandler) {
        boolean currentAdvancedModeState = this.advancedModeCheckbox.getValue().booleanValue();

        this.pidPanel.forEach(widget -> {
            CheckBox box = (CheckBox) widget;
            box.setValue(true);
            box.setEnabled(!currentAdvancedModeState);
        });

        setAnchorEnable(!this.advancedModeCheckbox.getValue().booleanValue());
        setAdvancedModeHintVisibility(currentAdvancedModeState);

    }

    /*
     * Rollback utils
     */

    private void configurationRollback(GwtXSRFToken token, List<GwtSnapshot> snapshot, List<String> selectedPids) {
        if (!snapshot.isEmpty()) {
            FailureHandler.showErrorMessage("SnapshotId not found");
        }

        gwtSnapshotService.configurationRollbackDeviceSnapshot(token, snapshot.get(0), selectedPids,
                new AsyncCallback<Void>() {

                    @Override
                    public void onFailure(Throwable ex) {
                        EntryClassUi.hideWaitModal();
                        FailureHandler.handle(ex);
                    }

                    @Override
                    public void onSuccess(Void result) {
                        Window.Location.reload();
                    }
                });
    }

    private void snapshtoRollback(GwtXSRFToken token, List<GwtSnapshot> snapshot) {
        if (!snapshot.isEmpty()) {
            FailureHandler.showErrorMessage("SnapshotId not found");
        }

        gwtSnapshotService.rollbackDeviceSnapshot(token, snapshot.get(0), new AsyncCallback<Void>() {

            @Override
            public void onFailure(Throwable ex) {
                EntryClassUi.hideWaitModal();
                FailureHandler.handle(ex);
            }

            @Override
            public void onSuccess(Void result) {
                Window.Location.reload();
            }
        });
    }

    /*
     * Utils
     */

    private void clearClickHandlers() {

        if (this.cancelButton != null) {
            this.cancelButton.removeClickHandler();
        }

        if (this.rollbackButton != null) {
            this.rollbackButton.removeClickHandler();
        }

        if (this.advancedModeClickHandler != null) {
            this.advancedModeClickHandler.removeHandler();
        }

    }
}
