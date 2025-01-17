package org.eclipse.kura.web.client.ui.settings;

import java.util.List;

import org.eclipse.kura.web.client.ui.EntryClassUi;
import org.eclipse.kura.web.client.util.FailureHandler;
import org.eclipse.kura.web.shared.model.GwtXSRFToken;
import org.eclipse.kura.web.shared.service.GwtSecurityTokenService;
import org.eclipse.kura.web.shared.service.GwtSecurityTokenServiceAsync;
import org.eclipse.kura.web.shared.service.GwtSnapshotService;
import org.eclipse.kura.web.shared.service.GwtSnapshotServiceAsync;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ExperimentalSnapshotRollbackModal extends SnapshotGenericModal {

    private final GwtSecurityTokenServiceAsync gwtXSRFService = GWT.create(GwtSecurityTokenService.class);
    private final GwtSnapshotServiceAsync gwtSnapshotService = GWT.create(GwtSnapshotService.class);

    Button confirmButton;
    Button cancelButton;

    @Override
    void show(Long snapshotId, List<String> snapshotConfigs) {
        initSnapshotPidList(snapshotConfigs);
        initSelectedPidCounter();
        initSnapshotSelectAllAnchor();

        initTitleAndDescription();
        initFooter();

        initEventButtons(snapshotId);

        this.snapshotModal.show();
    }

    @Override
    void initTitleAndDescription() {
        this.snapshotModal.setTitle(MSGS.deviceSnapshotRollbackTitle());
        this.snapshotModalDescription.setText(MSGS.deviceSnapshotRollbackConfirm());
        this.snapshotModalHint.setText(MSGS.deviceSnapshotRollbackHint());
    }

    @Override
    void initEventButtons(Long snapshotId) {
        this.cancelButton.addClickHandler(e -> {
            this.snapshotModal.hide();
            resetScrollPanel();
        });

        this.confirmButton.addClickHandler(e -> onRollbackConfirmation(snapshotId));

    }

    @Override
    void initFooter() {
        this.snapshotFooter.clear();

        this.cancelButton = new Button(MSGS.cancelButton());
        this.cancelButton.addStyleName("fa");
        this.cancelButton.setType(ButtonType.PRIMARY);
        this.snapshotFooter.add(cancelButton);

        this.confirmButton = new Button(MSGS.confirm());
        this.confirmButton.addStyleName("fa");
        this.confirmButton.setType(ButtonType.PRIMARY);
        this.snapshotFooter.add(confirmButton);

    }

    @Override
    void initHiddenFields() {
        // No hidden field required at the moment
    }

    private void onRollbackConfirmation(Long snapshotId) {
        EntryClassUi.showWaitModal();
        this.gwtXSRFService.generateSecurityToken(new AsyncCallback<GwtXSRFToken>() {

            @Override
            public void onFailure(Throwable ex) {
                EntryClassUi.hideWaitModal();
                FailureHandler.handle(ex);
            }

            @Override
            public void onSuccess(GwtXSRFToken token) {
                gwtSnapshotService.rollbackDeviceSnapshot(token, snapshotId, new AsyncCallback<Void>() {

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

        });

        this.snapshotModal.hide();
    }

}
