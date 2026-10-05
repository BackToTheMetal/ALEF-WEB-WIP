package com.webawt.portable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Desktop;
import java.awt.desktop.AppForegroundEvent;
import java.awt.desktop.AppForegroundListener;
import java.awt.desktop.AppHiddenEvent;
import java.awt.desktop.AppHiddenListener;
import java.net.URI;
import org.junit.Test;
import org.webawt.internal.peer.DesktopPeer;
import org.webawt.internal.peer.PeerDesktopEvent;
import org.webawt.internal.peer.PeerDesktopEventHandler;
import org.webawt.internal.peer.WebAwtRuntime;

public final class DesktopJ2clTest {
    @Test
    public void mapsBrowseMailVisibilityAndQuitToTheDesktopPeer() throws Exception {
        RecordingDesktopPeer peer = new RecordingDesktopPeer();
        WebAwtRuntime.installDesktopPeer(peer);
        Desktop desktop = Desktop.getDesktop();
        int[] foregroundEvents = new int[2];
        int[] hiddenEvents = new int[2];
        AppForegroundListener foregroundListener = new AppForegroundListener() {
            @Override
            public void appRaisedToForeground(AppForegroundEvent event) {
                foregroundEvents[0]++;
            }
            @Override
            public void appMovedToBackground(AppForegroundEvent event) {
                foregroundEvents[1]++;
            }
        };
        AppHiddenListener hiddenListener = new AppHiddenListener() {
            @Override
            public void appHidden(AppHiddenEvent event) {
                hiddenEvents[0]++;
            }
            @Override
            public void appUnhidden(AppHiddenEvent event) {
                hiddenEvents[1]++;
            }
        };
        desktop.addAppEventListener(foregroundListener);
        desktop.addAppEventListener(hiddenListener);
        desktop.browse(URI.create("https://example.test/path"));
        desktop.mail(URI.create("mailto:webawt@example.test"));
        peer.dispatch(PeerDesktopEvent.APP_FOREGROUND);
        peer.dispatch(PeerDesktopEvent.APP_BACKGROUND);
        peer.dispatch(PeerDesktopEvent.APP_HIDDEN);
        peer.dispatch(PeerDesktopEvent.APP_UNHIDDEN);
        desktop.setQuitHandler((event, response) -> response.cancelQuit());
        assertTrue(peer.dispatch(PeerDesktopEvent.QUIT_REQUEST));
        desktop.enableSuddenTermination();
        assertFalse(peer.dispatch(PeerDesktopEvent.QUIT_REQUEST));
        desktop.disableSuddenTermination();
        desktop.setQuitHandler(null);
        desktop.removeAppEventListener(foregroundListener);
        desktop.removeAppEventListener(hiddenListener);
        assertEquals("https://example.test/path", peer.browseUri);
        assertEquals("mailto:webawt@example.test", peer.mailUri);
        assertEquals(1, foregroundEvents[0]);
        assertEquals(1, foregroundEvents[1]);
        assertEquals(1, hiddenEvents[0]);
        assertEquals(1, hiddenEvents[1]);
        assertTrue(desktop.isSupported(Desktop.Action.BROWSE));
        assertFalse(desktop.isSupported(Desktop.Action.PRINT));
    }

    private static final class RecordingDesktopPeer implements DesktopPeer {
        private PeerDesktopEventHandler handler;
        private String browseUri;
        private String mailUri;

        @Override
        public boolean browse(String uri) {
            browseUri = uri;
            return true;
        }

        @Override
        public boolean mail(String uri) {
            mailUri = uri;
            return true;
        }

        @Override
        public void requestForeground() {
        }

        @Override
        public void openHelpViewer() {
        }

        @Override
        public void setEventHandler(PeerDesktopEventHandler handler) {
            this.handler = handler;
        }

        private boolean dispatch(int type) {
            return handler.handle(type);
        }
    }
}
