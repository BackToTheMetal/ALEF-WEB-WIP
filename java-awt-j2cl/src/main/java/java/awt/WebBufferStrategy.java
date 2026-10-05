package java.awt;

import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

final class WebBufferStrategy extends BufferStrategy {
    private final Component component;
    private final BufferCapabilities capabilities;
    private BufferedImage fallbackBuffer;
    private boolean restored;

    WebBufferStrategy(Component component, BufferCapabilities capabilities) {
        this.component = component;
        this.capabilities = capabilities;
    }

    @Override
    public BufferCapabilities getCapabilities() {
        return capabilities;
    }

    @Override
    public Graphics getDrawGraphics() {
        Graphics graphics = component.getGraphics();
        if (graphics != null) {
            restored = false;
            return graphics;
        }
        int width = Math.max(1, component.getWidth());
        int height = Math.max(1, component.getHeight());
        if (fallbackBuffer == null || fallbackBuffer.getWidth() != width || fallbackBuffer.getHeight() != height) {
            fallbackBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            restored = true;
        } else {
            restored = false;
        }
        return fallbackBuffer.createGraphics();
    }

    @Override
    public boolean contentsLost() {
        return false;
    }

    @Override
    public boolean contentsRestored() {
        boolean result = restored;
        restored = false;
        return result;
    }

    @Override
    public void show() {
    }

    @Override
    public void dispose() {
        fallbackBuffer = null;
        if (component instanceof Canvas) {
            ((Canvas) component).clearBufferStrategy(this);
        } else if (component instanceof Window) {
            ((Window) component).clearBufferStrategy(this);
        }
    }
}
