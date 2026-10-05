package java.awt;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import org.webawt.internal.peer.WebAwtRuntime;

public class EventQueue {
    private static long mostRecentEventTime;
    private static AWTEvent currentEvent;
    private final List<AWTEvent> events = new ArrayList<>();
    private EventQueue pushedQueue;
    private EventQueue previousQueue;
    private boolean drainScheduled;

    public EventQueue() {
    }

    public static long getMostRecentEventTime() {
        return mostRecentEventTime;
    }

    public static AWTEvent getCurrentEvent() {
        return currentEvent;
    }

    public static boolean isDispatchThread() {
        return true;
    }

    public static void invokeLater(Runnable runnable) {
        if (runnable == null) {
            throw new NullPointerException("runnable");
        }
        WebAwtRuntime.schedule(() -> {
            mostRecentEventTime = System.currentTimeMillis();
            runnable.run();
        });
    }

    public void postEvent(AWTEvent event) {
        if (event == null) {
            throw new NullPointerException("event");
        }
        EventQueue activeQueue = activeQueue();
        if (event.getSource() instanceof Component) {
            for (int index = 0; index < activeQueue.events.size(); index++) {
                AWTEvent previous = activeQueue.events.get(index);
                if (previous.getSource() == event.getSource() && previous.getID() == event.getID()) {
                    AWTEvent merged = ((Component) event.getSource()).coalesceEvents(previous, event);
                    if (merged != null) {
                        activeQueue.events.set(index, merged);
                        return;
                    }
                }
            }
        }
        activeQueue.events.add(event);
        activeQueue.scheduleDrain();
    }

    public AWTEvent getNextEvent() throws InterruptedException {
        EventQueue activeQueue = activeQueue();
        if (activeQueue.events.isEmpty()) {
            throw new IllegalStateException("Queue is empty");
        }
        return activeQueue.events.remove(0);
    }

    public AWTEvent peekEvent() {
        EventQueue activeQueue = activeQueue();
        return activeQueue.events.isEmpty() ? null : activeQueue.events.get(0);
    }

    public AWTEvent peekEvent(int id) {
        EventQueue activeQueue = activeQueue();
        for (AWTEvent event : activeQueue.events) {
            if (event.getID() == id) {
                return event;
            }
        }
        return null;
    }

    public void push(EventQueue newEventQueue) {
        if (newEventQueue == null) {
            throw new NullPointerException("newEventQueue");
        }
        EventQueue activeQueue = activeQueue();
        if (newEventQueue.previousQueue != null || newEventQueue.pushedQueue != null || newEventQueue == activeQueue) {
            throw new IllegalArgumentException("Queue is already part of a stack");
        }
        activeQueue.pushedQueue = newEventQueue;
        newEventQueue.previousQueue = activeQueue;
        newEventQueue.events.addAll(activeQueue.events);
        activeQueue.events.clear();
        if (!newEventQueue.events.isEmpty()) {
            newEventQueue.scheduleDrain();
        }
    }

    protected void pop() throws EmptyStackException {
        EventQueue activeQueue = activeQueue();
        EventQueue previous = activeQueue.previousQueue;
        if (previous == null) {
            throw new EmptyStackException();
        }
        previous.events.addAll(activeQueue.events);
        activeQueue.events.clear();
        previous.pushedQueue = null;
        activeQueue.previousQueue = null;
        if (!previous.events.isEmpty()) {
            previous.scheduleDrain();
        }
    }

    private EventQueue activeQueue() {
        EventQueue queue = this;
        while (queue.pushedQueue != null) {
            queue = queue.pushedQueue;
        }
        return queue;
    }

    private void scheduleDrain() {
        if (drainScheduled) {
            return;
        }
        drainScheduled = true;
        WebAwtRuntime.schedule(this::drain, () -> {
            events.clear();
            drainScheduled = false;
        });
    }

    private void drain() {
        try {
            while (!events.isEmpty()) {
                dispatchEvent(events.remove(0));
            }
        } finally {
            drainScheduled = false;
            if (!events.isEmpty()) {
                scheduleDrain();
            }
        }
    }

    protected void dispatchEvent(AWTEvent event) {
        AWTEvent previousEvent = currentEvent;
        currentEvent = event;
        mostRecentEventTime = System.currentTimeMillis();
        try {
            if (event instanceof ActiveEvent) {
                ((ActiveEvent) event).dispatch();
            } else if (event.getSource() instanceof Component) {
                ((Component) event.getSource()).dispatchEvent(event);
            } else if (event.getSource() instanceof MenuComponent) {
                ((MenuComponent) event.getSource()).dispatchEvent(event);
            } else {
                Toolkit.notifyAWTEventListeners(event);
            }
        } finally {
            currentEvent = previousEvent;
        }
    }
}
