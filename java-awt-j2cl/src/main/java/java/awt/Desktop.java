package java.awt;

import java.awt.desktop.AboutHandler;
import java.awt.desktop.AppForegroundEvent;
import java.awt.desktop.AppForegroundListener;
import java.awt.desktop.AppHiddenEvent;
import java.awt.desktop.AppHiddenListener;
import java.awt.desktop.OpenFilesHandler;
import java.awt.desktop.OpenURIHandler;
import java.awt.desktop.PreferencesHandler;
import java.awt.desktop.PrintFilesHandler;
import java.awt.desktop.QuitEvent;
import java.awt.desktop.QuitHandler;
import java.awt.desktop.QuitResponse;
import java.awt.desktop.QuitStrategy;
import java.awt.desktop.SystemEventListener;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.webawt.internal.peer.PeerDesktopEvent;
import org.webawt.internal.peer.WebAwtRuntime;

public class Desktop {
    public enum Action {
        OPEN,
        EDIT,
        PRINT,
        MAIL,
        BROWSE,
        APP_EVENT_FOREGROUND,
        APP_EVENT_HIDDEN,
        APP_EVENT_REOPENED,
        APP_EVENT_SCREEN_SLEEP,
        APP_EVENT_SYSTEM_SLEEP,
        APP_EVENT_USER_SESSION,
        APP_ABOUT,
        APP_PREFERENCES,
        APP_OPEN_FILE,
        APP_PRINT_FILE,
        APP_OPEN_URI,
        APP_QUIT_HANDLER,
        APP_QUIT_STRATEGY,
        APP_SUDDEN_TERMINATION,
        APP_REQUEST_FOREGROUND,
        APP_HELP_VIEWER,
        APP_MENU_BAR,
        BROWSE_FILE_DIR,
        MOVE_TO_TRASH
    }

    private static final Desktop INSTANCE = new Desktop();
    static {
        WebAwtRuntime.addShutdownHook(() -> {
            INSTANCE.appEventListeners.clear();
            INSTANCE.quitHandler = null;
            INSTANCE.suddenTerminationEnabled = false;
        });
    }
    private final List<SystemEventListener> appEventListeners = new ArrayList<>();
    private QuitHandler quitHandler;
    private boolean suddenTerminationEnabled;

    private Desktop() {
        bindPeer();
    }

    public static synchronized Desktop getDesktop() {
        INSTANCE.bindPeer();
        return INSTANCE;
    }

    public static boolean isDesktopSupported() {
        return true;
    }

    public boolean isSupported(Action action) {
        if (action == null) {
            throw new NullPointerException("action");
        }
        return action == Action.BROWSE || action == Action.MAIL || action == Action.APP_EVENT_FOREGROUND || action == Action.APP_EVENT_HIDDEN || action == Action.APP_QUIT_HANDLER || action == Action.APP_SUDDEN_TERMINATION || action == Action.APP_REQUEST_FOREGROUND;
    }

    public void browse(URI uri) throws IOException {
        requireScheme(uri, "http", "https");
        if (!WebAwtRuntime.getDesktopPeer().browse(uri.toString())) {
            throw new IOException("The browser blocked the navigation window");
        }
    }

    public void mail() throws IOException {
        mail(URI.create("mailto:"));
    }

    public void mail(URI uri) throws IOException {
        requireScheme(uri, "mailto");
        if (!WebAwtRuntime.getDesktopPeer().mail(uri.toString())) {
            throw new IOException("The browser blocked the mail navigation");
        }
    }

    public void addAppEventListener(SystemEventListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener");
        }
        appEventListeners.add(listener);
        bindPeer();
    }

    public void removeAppEventListener(SystemEventListener listener) {
        appEventListeners.remove(listener);
    }

    public void setAboutHandler(AboutHandler handler) {
        rejectUnsupportedHandler(handler, Action.APP_ABOUT);
    }

    public void setPreferencesHandler(PreferencesHandler handler) {
        rejectUnsupportedHandler(handler, Action.APP_PREFERENCES);
    }

    public void setOpenFileHandler(OpenFilesHandler handler) {
        rejectUnsupportedHandler(handler, Action.APP_OPEN_FILE);
    }

    public void setPrintFileHandler(PrintFilesHandler handler) {
        rejectUnsupportedHandler(handler, Action.APP_PRINT_FILE);
    }

    public void setOpenURIHandler(OpenURIHandler handler) {
        rejectUnsupportedHandler(handler, Action.APP_OPEN_URI);
    }

    public void setQuitHandler(QuitHandler handler) {
        quitHandler = handler;
        bindPeer();
    }

    public void setQuitStrategy(QuitStrategy strategy) {
        if (strategy == null) {
            throw new NullPointerException("strategy");
        }
        throw new UnsupportedOperationException("Desktop action is unavailable in a browser: " + Action.APP_QUIT_STRATEGY);
    }

    public void enableSuddenTermination() {
        suddenTerminationEnabled = true;
    }

    public void disableSuddenTermination() {
        suddenTerminationEnabled = false;
    }

    public void requestForeground(boolean allWindows) {
        WebAwtRuntime.getDesktopPeer().requestForeground();
    }

    public void openHelpViewer() {
        throw new UnsupportedOperationException("Desktop action is unavailable in a browser: " + Action.APP_HELP_VIEWER);
    }

    private void bindPeer() {
        WebAwtRuntime.getDesktopPeer().setEventHandler(this::handlePeerEvent);
    }

    private boolean handlePeerEvent(int type) {
        if (type == PeerDesktopEvent.APP_FOREGROUND || type == PeerDesktopEvent.APP_BACKGROUND) {
            AppForegroundEvent event = new AppForegroundEvent();
            for (SystemEventListener listener : new ArrayList<>(appEventListeners)) {
                if (listener instanceof AppForegroundListener) {
                    AppForegroundListener foregroundListener = (AppForegroundListener) listener;
                    if (type == PeerDesktopEvent.APP_FOREGROUND) {
                        foregroundListener.appRaisedToForeground(event);
                    } else {
                        foregroundListener.appMovedToBackground(event);
                    }
                }
            }
        } else if (type == PeerDesktopEvent.APP_HIDDEN || type == PeerDesktopEvent.APP_UNHIDDEN) {
            dispatchHiddenEvent(type == PeerDesktopEvent.APP_HIDDEN);
        } else if (type == PeerDesktopEvent.QUIT_REQUEST) {
            return dispatchQuitRequest();
        }
        return false;
    }

    private void dispatchHiddenEvent(boolean hidden) {
        AppHiddenEvent event = new AppHiddenEvent();
        for (SystemEventListener listener : new ArrayList<>(appEventListeners)) {
            if (listener instanceof AppHiddenListener) {
                AppHiddenListener hiddenListener = (AppHiddenListener) listener;
                if (hidden) {
                    hiddenListener.appHidden(event);
                } else {
                    hiddenListener.appUnhidden(event);
                }
            }
        }
    }

    private boolean dispatchQuitRequest() {
        if (quitHandler == null || suddenTerminationEnabled) {
            return false;
        }
        BrowserQuitResponse response = new BrowserQuitResponse();
        quitHandler.handleQuitRequestWith(new QuitEvent(), response);
        return response.cancelled;
    }

    private static void requireScheme(URI uri, String... schemes) {
        if (uri == null) {
            throw new NullPointerException("uri");
        }
        for (String scheme : schemes) {
            if (scheme.equalsIgnoreCase(uri.getScheme())) {
                return;
            }
        }
        throw new IllegalArgumentException("Unsupported URI scheme: " + uri.getScheme());
    }

    private static void rejectUnsupportedHandler(Object handler, Action action) {
        if (handler != null) {
            throw new UnsupportedOperationException("Desktop action is unavailable in a browser: " + action);
        }
    }

    private static final class BrowserQuitResponse implements QuitResponse {
        private boolean cancelled;

        @Override
        public void performQuit() {
            cancelled = false;
        }

        @Override
        public void cancelQuit() {
            cancelled = true;
        }
    }
}
