package com.webawt.portable;

import java.awt.Desktop;
import java.awt.desktop.QuitStrategy;
import java.awt.desktop.UserSessionEvent;

public final class PortableDesktopScenarios {
    private PortableDesktopScenarios() {
    }

    public static String enumSurface() {
        Desktop.Action[] actions = Desktop.Action.values();
        UserSessionEvent.Reason[] reasons = UserSessionEvent.Reason.values();
        QuitStrategy[] strategies = QuitStrategy.values();
        return actions.length + ":" + actions[0] + ":" + actions[actions.length - 1] + ":" + reasons.length + ":" + reasons[0] + ":" + reasons[reasons.length - 1] + ":" + strategies.length;
    }
}
