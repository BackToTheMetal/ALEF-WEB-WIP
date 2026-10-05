package java.awt;

import java.awt.event.WindowEvent;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;

public class Dialog extends Window {
    private static final int DECORATION_BORDER = 4;
    private static final int TITLE_HEIGHT = 26;
    public enum ModalityType {
        MODELESS,
        DOCUMENT_MODAL,
        APPLICATION_MODAL,
        TOOLKIT_MODAL
    }

    public enum ModalExclusionType {
        NO_EXCLUDE,
        APPLICATION_EXCLUDE,
        TOOLKIT_EXCLUDE
    }

    public static final ModalityType DEFAULT_MODALITY_TYPE = ModalityType.APPLICATION_MODAL;
    private ModalityType modalityType = ModalityType.MODELESS;
    private String title;
    private boolean resizable = true;
    private boolean undecorated;

    public Dialog(Frame owner) {
        this(owner, "", false);
    }

    public Dialog(Frame owner, boolean modal) {
        this(owner, "", modal);
    }

    public Dialog(Frame owner, String title) {
        this(owner, title, false);
    }

    public Dialog(Frame owner, String title, boolean modal) {
        this((Window) owner, title, modal ? DEFAULT_MODALITY_TYPE : ModalityType.MODELESS);
    }

    public Dialog(Frame owner, String title, boolean modal, GraphicsConfiguration configuration) {
        this((Window) owner, title, modal ? DEFAULT_MODALITY_TYPE : ModalityType.MODELESS, configuration);
    }

    public Dialog(Dialog owner) {
        this(owner, "", false);
    }

    public Dialog(Dialog owner, String title) {
        this(owner, title, false);
    }

    public Dialog(Dialog owner, String title, boolean modal) {
        this((Window) owner, title, modal ? DEFAULT_MODALITY_TYPE : ModalityType.MODELESS);
    }

    public Dialog(Dialog owner, String title, boolean modal, GraphicsConfiguration configuration) {
        this((Window) owner, title, modal ? DEFAULT_MODALITY_TYPE : ModalityType.MODELESS, configuration);
    }

    public Dialog(Window owner) {
        this(owner, "", ModalityType.MODELESS);
    }

    public Dialog(Window owner, String title) {
        this(owner, title, ModalityType.MODELESS);
    }

    public Dialog(Window owner, ModalityType modalityType) {
        this(owner, "", modalityType);
    }

    public Dialog(Window owner, String title, ModalityType modalityType) {
        this(owner, title, modalityType, null);
    }

    public Dialog(Window owner, String title, ModalityType modalityType, GraphicsConfiguration configuration) {
        super("dialog", requireDialogOwner(owner), configuration);
        this.title = title == null ? "" : title;
        setBackground(SystemColor.control);
        setModalityType(modalityType);
    }

    public boolean isModal() {
        return modalityType != ModalityType.MODELESS;
    }

    public void setModal(boolean modal) {
        setModalityType(modal ? DEFAULT_MODALITY_TYPE : ModalityType.MODELESS);
    }

    public ModalityType getModalityType() {
        return modalityType;
    }

    public void setModalityType(ModalityType modalityType) {
        if (modalityType == null) {
            modalityType = ModalityType.MODELESS;
        }
        this.modalityType = modalityType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? "" : title;
        synchronizeDecoration();
    }

    @Override
    public Insets getInsets() {
        return undecorated ? new Insets(0, 0, 0, 0) : new Insets(DECORATION_BORDER + TITLE_HEIGHT, DECORATION_BORDER, DECORATION_BORDER, DECORATION_BORDER);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        synchronizeDecoration();
    }

    private void synchronizeDecoration() {
        if (getPeer() != null) {
            getPeer().configureDialog(title, !undecorated, DECORATION_BORDER, TITLE_HEIGHT, () -> dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));
        }
    }

    public boolean isResizable() {
        return resizable;
    }

    public void setResizable(boolean resizable) {
        this.resizable = resizable;
    }

    public void setUndecorated(boolean undecorated) {
        if (isDisplayable()) {
            throw new IllegalComponentStateException("dialog is displayable");
        }
        this.undecorated = undecorated;
        invalidate();
    }

    public boolean isUndecorated() {
        return undecorated;
    }

    private static Window requireDialogOwner(Window owner) {
        if (owner != null && !(owner instanceof Frame) && !(owner instanceof Dialog)) {
            throw new IllegalArgumentException("owner must be a Frame or Dialog");
        }
        return owner;
    }

    @Override
    public AccessibleContext getAccessibleContext() {
        return new AccessibleAWTDialog();
    }

    protected class AccessibleAWTDialog extends AccessibleAWTWindow {
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.DIALOG;
        }
    }
}
