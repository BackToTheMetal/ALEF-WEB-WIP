package com.webawt.portable;

import java.awt.AWTEvent;
import java.awt.ActiveEvent;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import org.junit.After;
import org.junit.Test;
import org.webawt.internal.peer.BrowserScheduler;
import org.webawt.internal.peer.WebAwtRuntime;

public final class EventQueueJ2clTest {
    @After
    public void resetScheduler() {
        WebAwtRuntime.installScheduler(Runnable::run);
    }

    @Test
    public void shutdownCancelsWorkAndAllowsQueueReuse() {
        RecordingScheduler scheduler = new RecordingScheduler();
        WebAwtRuntime.installScheduler(scheduler);
        EventQueue queue = new EventQueue();
        RecordingEvent event = new RecordingEvent(77);
        queue.postEvent(event);
        EventQueue.invokeLater(() -> { throw new AssertionError("cancelled invokeLater"); });
        WebAwtRuntime.shutdown();
        scheduler.runPending();
        assertEquals(0, event.dispatchCount);
        assertSame(null, queue.peekEvent());
        assertEquals(0, WebAwtRuntime.pendingTaskCount());
        queue.postEvent(event);
        assertEquals(1, event.dispatchCount);
    }

    @Test
    public void delegatesInvokeLaterToInstalledScheduler() {
        RecordingScheduler scheduler = new RecordingScheduler();
        WebAwtRuntime.installScheduler(scheduler);
        int[] calls = new int[1];
        EventQueue.invokeLater(() -> calls[0]++);
        assertEquals(0, calls[0]);
        scheduler.runPending();
        assertEquals(1, calls[0]);
        assertTrue(EventQueue.getMostRecentEventTime() > 0);
        assertTrue(EventQueue.isDispatchThread());
    }

    @Test
    public void queuesInspectsPushesAndDispatchesActiveEvents() throws Exception {
        RecordingScheduler scheduler = new RecordingScheduler();
        WebAwtRuntime.installScheduler(scheduler);
        EventQueue queue = new EventQueue();
        EventQueue pushed = new EventQueue();
        RecordingEvent event = new RecordingEvent(41);
        queue.push(pushed);
        queue.postEvent(event);
        assertSame(event, queue.peekEvent());
        assertSame(event, queue.peekEvent(41));
        assertSame(null, queue.peekEvent(42));
        scheduler.runPending();
        assertEquals(1, event.dispatchCount);
        assertSame(event, event.observedCurrentEvent);
        assertSame(null, queue.peekEvent());
        RecordingEvent removed = new RecordingEvent(42);
        queue.postEvent(removed);
        assertSame(removed, queue.getNextEvent());
        assertSame(null, queue.peekEvent());
    }

    @Test
    public void dispatchesThroughSubclassAndRestoresQueuedEventsOnPop() {
        RecordingScheduler scheduler = new RecordingScheduler();
        WebAwtRuntime.installScheduler(scheduler);
        InspectableQueue base = new InspectableQueue();
        InspectableQueue pushed = new InspectableQueue();
        RecordingEvent beforePush = new RecordingEvent(51);
        base.postEvent(beforePush);
        base.push(pushed);
        scheduler.runPending();
        assertEquals(0, base.dispatchCount);
        assertEquals(1, pushed.dispatchCount);
        assertEquals(1, beforePush.dispatchCount);
        RecordingEvent beforePop = new RecordingEvent(52);
        base.postEvent(beforePop);
        pushed.removeTop();
        assertSame(beforePop, base.peekEvent());
        assertSame(null, pushed.peekEvent());
        scheduler.runPending();
        assertEquals(1, base.dispatchCount);
        assertEquals(1, pushed.dispatchCount);
        assertEquals(1, beforePop.dispatchCount);
        assertSame(null, EventQueue.getCurrentEvent());
    }

    @Test
    public void popsTopQueueFromNestedStackAndAllowsReuse() {
        RecordingScheduler scheduler = new RecordingScheduler();
        WebAwtRuntime.installScheduler(scheduler);
        InspectableQueue base = new InspectableQueue();
        InspectableQueue middle = new InspectableQueue();
        InspectableQueue top = new InspectableQueue();
        base.push(middle);
        base.push(top);
        RecordingEvent event = new RecordingEvent(61);
        base.postEvent(event);
        middle.removeTop();
        assertSame(event, middle.peekEvent());
        scheduler.runPending();
        assertEquals(1, middle.dispatchCount);
        assertEquals(0, top.dispatchCount);
        middle.removeTop();
        base.push(top);
        base.postEvent(new RecordingEvent(62));
        scheduler.runPending();
        assertEquals(1, top.dispatchCount);
    }

    @Test
    public void rejectsPopWithoutPreviousQueue() {
        try {
            new InspectableQueue().removeTop();
            throw new AssertionError("Expected EmptyStackException");
        } catch (EmptyStackException expected) {
            assertSame(null, EventQueue.getCurrentEvent());
        }
    }

    @Test
    public void restoresCurrentEventAfterNestedDispatch() {
        InspectableQueue queue = new InspectableQueue();
        RecordingEvent inner = new RecordingEvent(72);
        RecordingEvent outer = new RecordingEvent(71) {
            @Override
            public void dispatch() {
                assertSame(this, EventQueue.getCurrentEvent());
                queue.dispatchDirectly(inner);
                assertSame(this, EventQueue.getCurrentEvent());
            }
        };
        queue.dispatchDirectly(outer);
        assertSame(inner, inner.observedCurrentEvent);
        assertSame(null, EventQueue.getCurrentEvent());
    }

    @Test
    public void continuesPendingEventsAfterDispatchFailure() {
        RecordingScheduler scheduler = new RecordingScheduler();
        WebAwtRuntime.installScheduler(scheduler);
        InspectableQueue queue = new InspectableQueue();
        RecordingEvent next = new RecordingEvent(82);
        queue.postEvent(new RecordingEvent(81) {
            @Override
            public void dispatch() {
                throw new IllegalStateException("Event failed");
            }
        });
        queue.postEvent(next);
        try {
            scheduler.runPending();
            throw new AssertionError("Expected event failure");
        } catch (IllegalStateException expected) {
            assertSame(null, EventQueue.getCurrentEvent());
        }
        scheduler.runPending();
        assertEquals(1, next.dispatchCount);
    }

    private static void assertEquals(int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError("Expected " + expected + " but was " + actual);
        }
    }

    private static void assertTrue(boolean value) {
        if (!value) {
            throw new AssertionError("Expected true");
        }
    }

    private static void assertSame(Object expected, Object actual) {
        if (expected != actual) {
            throw new AssertionError("Expected the same object");
        }
    }

    private static class RecordingEvent extends AWTEvent implements ActiveEvent {
        private int dispatchCount;
        private AWTEvent observedCurrentEvent;

        private RecordingEvent(int id) {
            super(new Object(), id);
        }

        @Override
        public void dispatch() {
            dispatchCount++;
            observedCurrentEvent = EventQueue.getCurrentEvent();
        }
    }

    private static final class InspectableQueue extends EventQueue {
        private int dispatchCount;

        @Override
        protected void dispatchEvent(AWTEvent event) {
            dispatchCount++;
            super.dispatchEvent(event);
        }

        void dispatchDirectly(AWTEvent event) {
            dispatchEvent(event);
        }

        void removeTop() {
            pop();
        }
    }

    private static final class RecordingScheduler implements BrowserScheduler {
        private final List<Runnable> pending = new ArrayList<>();

        @Override
        public void schedule(Runnable task) {
            pending.add(task);
        }

        void runPending() {
            while (!pending.isEmpty()) {
                pending.remove(0).run();
            }
        }
    }
}
