package java.awt;

import java.awt.image.BufferStrategy;
import java.util.Locale;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import org.webawt.internal.graphics.CanvasGraphics2D;
import org.webawt.internal.peer.CanvasPeer;

public class Canvas extends Component implements Accessible {
    private WebBufferStrategy bufferStrategy;

    public Canvas() {
        super("canvas");
    }

    public Canvas(GraphicsConfiguration configuration) {
        this();
        setGraphicsConfiguration(configuration);
    }

    @Override
    public void addNotify() {
        super.addNotify();
    }

    @Override
    public Graphics getGraphics() {
        return getPeer() instanceof CanvasPeer ? new CanvasGraphics2D((CanvasPeer) getPeer()) : null;
    }

    public void createBufferStrategy(int bufferCount) {
        createBufferStrategyInternal(bufferCount, new BufferCapabilities(new ImageCapabilities(false), new ImageCapabilities(false), null));
    }

    public void createBufferStrategy(int bufferCount, BufferCapabilities capabilities) throws AWTException {
        if (capabilities == null) {
            throw new IllegalArgumentException("Buffer capabilities cannot be null");
        }
        if (capabilities.isPageFlipping() || capabilities.getFrontBufferCapabilities().isAccelerated() || capabilities.getBackBufferCapabilities().isAccelerated()) {
            throw new AWTException("The browser canvas does not support the requested buffer capabilities");
        }
        createBufferStrategyInternal(bufferCount, capabilities);
    }

    public BufferStrategy getBufferStrategy() {
        return bufferStrategy;
    }

    void clearBufferStrategy(WebBufferStrategy strategy) {
        if (bufferStrategy == strategy) {
            bufferStrategy = null;
        }
    }

    @Override
    public void paint(Graphics graphics) {
    }

    @Override
    public void update(Graphics graphics) {
        graphics.clearRect(0, 0, getWidth(), getHeight());
        paint(graphics);
    }

    @Override
    public void removeNotify() {
        if (bufferStrategy != null) {
            bufferStrategy.dispose();
        }
        super.removeNotify();
    }

    private void createBufferStrategyInternal(int bufferCount, BufferCapabilities capabilities) {
        if (bufferCount < 1) {
            throw new IllegalArgumentException("Buffer count must be at least one");
        }
        if (!isDisplayable()) {
            throw new IllegalStateException("Component must be displayable");
        }
        if (bufferStrategy != null) {
            bufferStrategy.dispose();
        }
        bufferStrategy = new WebBufferStrategy(this, capabilities);
    }

    @Override
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleAWTCanvas();
        }
        return accessibleContext;
    }

    protected class AccessibleAWTCanvas extends AccessibleAWTComponent {
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.CANVAS;
        }

        @Override
        public AccessibleStateSet getAccessibleStateSet() {
            AccessibleStateSet states = new AccessibleStateSet();
            if (isEnabled()) {
                states.add(AccessibleState.ENABLED);
            }
            if (isVisible()) {
                states.add(AccessibleState.VISIBLE);
            }
            return states;
        }

        @Override
        public int getAccessibleIndexInParent() {
            return getParent() == null ? -1 : getParent().getComponentZOrder(Canvas.this);
        }

        @Override
        public int getAccessibleChildrenCount() {
            return 0;
        }

        @Override
        public Accessible getAccessibleChild(int index) {
            return null;
        }

        @Override
        public Locale getLocale() {
            return Canvas.this.getLocale();
        }
    }
}
