package java.awt;

import java.awt.image.ImageObserver;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MediaTracker implements Serializable {
    public static final int LOADING = 1;
    public static final int ABORTED = 2;
    public static final int ERRORED = 4;
    public static final int COMPLETE = 8;
    private static final int DONE = ABORTED | ERRORED | COMPLETE;
    private static final long serialVersionUID = -483174189758638095L;
    private final List<TrackedImage> images = new ArrayList<TrackedImage>();
    private final Component target;

    public MediaTracker(Component component) {
        target = component;
    }

    public void addImage(Image image, int identifier) {
        addImage(image, identifier, -1, -1);
    }

    public synchronized void addImage(Image image, int identifier, int width, int height) {
        images.add(new TrackedImage(image, identifier, width, height));
    }

    public boolean checkAll() {
        return checkAll(false);
    }

    public synchronized boolean checkAll(boolean load) {
        boolean complete = true;
        for (TrackedImage image : images) {
            if ((image.getStatus(load) & DONE) == 0) {
                complete = false;
            }
        }
        return complete;
    }

    public synchronized boolean isErrorAny() {
        for (TrackedImage image : images) {
            if ((image.getStatus(false) & ERRORED) != 0) {
                return true;
            }
        }
        return false;
    }

    public synchronized Object[] getErrorsAny() {
        return errorsFor(null);
    }

    public void waitForAll() throws InterruptedException {
        waitForAll(0);
    }

    public synchronized boolean waitForAll(long milliseconds) throws InterruptedException {
        return statusAll(true) == COMPLETE;
    }

    public synchronized int statusAll(boolean load) {
        int status = 0;
        for (TrackedImage image : images) {
            status |= image.getStatus(load);
        }
        return status;
    }

    public boolean checkID(int identifier) {
        return checkID(identifier, false);
    }

    public synchronized boolean checkID(int identifier, boolean load) {
        boolean complete = true;
        for (TrackedImage image : images) {
            if (image.identifier == identifier && (image.getStatus(load) & DONE) == 0) {
                complete = false;
            }
        }
        return complete;
    }

    public synchronized boolean isErrorID(int identifier) {
        for (TrackedImage image : images) {
            if (image.identifier == identifier && (image.getStatus(false) & ERRORED) != 0) {
                return true;
            }
        }
        return false;
    }

    public synchronized Object[] getErrorsID(int identifier) {
        return errorsFor(Integer.valueOf(identifier));
    }

    public void waitForID(int identifier) throws InterruptedException {
        waitForID(identifier, 0);
    }

    public synchronized boolean waitForID(int identifier, long milliseconds) throws InterruptedException {
        return statusID(identifier, true) == COMPLETE;
    }

    public synchronized int statusID(int identifier, boolean load) {
        int status = 0;
        for (TrackedImage image : images) {
            if (image.identifier == identifier) {
                status |= image.getStatus(load);
            }
        }
        return status;
    }

    public synchronized void removeImage(Image image) {
        removeMatching(image, null, null, null);
    }

    public synchronized void removeImage(Image image, int identifier) {
        removeMatching(image, Integer.valueOf(identifier), null, null);
    }

    public synchronized void removeImage(Image image, int identifier, int width, int height) {
        removeMatching(image, Integer.valueOf(identifier), Integer.valueOf(width), Integer.valueOf(height));
    }

    private Object[] errorsFor(Integer identifier) {
        List<Object> errors = new ArrayList<Object>();
        for (TrackedImage image : images) {
            if ((identifier == null || image.identifier == identifier.intValue()) && (image.getStatus(false) & ERRORED) != 0) {
                errors.add(image.image);
            }
        }
        return errors.isEmpty() ? null : errors.toArray(new Object[0]);
    }

    private void removeMatching(Image image, Integer identifier, Integer width, Integer height) {
        for (int index = images.size() - 1; index >= 0; index--) {
            TrackedImage candidate = images.get(index);
            if (candidate.image == image && (identifier == null || candidate.identifier == identifier.intValue()) && (width == null || candidate.width == width.intValue()) && (height == null || candidate.height == height.intValue())) {
                images.remove(index);
            }
        }
    }

    private final class TrackedImage implements ImageObserver {
        private final int height;
        private final int identifier;
        private final Image image;
        private final int width;
        private int status;

        private TrackedImage(Image image, int identifier, int width, int height) {
            this.image = image;
            this.identifier = identifier;
            this.width = width;
            this.height = height;
        }

        private int getStatus(boolean load) {
            if ((status & DONE) != 0) {
                return status;
            }
            int imageStatus = target.checkImage(image, width, height, this);
            updateStatus(imageStatus);
            if ((status & DONE) == 0 && load) {
                status = LOADING;
                if (target.prepareImage(image, width, height, this)) {
                    status = COMPLETE;
                }
            }
            return status;
        }

        @Override
        public boolean imageUpdate(Image image, int flags, int x, int y, int width, int height) {
            updateStatus(flags);
            return (status & LOADING) != 0;
        }

        private void updateStatus(int flags) {
            if ((flags & ImageObserver.ERROR) != 0) {
                status = ERRORED;
            } else if ((flags & ImageObserver.ABORT) != 0) {
                status = ABORTED;
            } else if ((flags & ImageObserver.ALLBITS) != 0) {
                status = COMPLETE;
            } else if (flags != 0 && (status & DONE) == 0) {
                status = LOADING;
            }
        }
    }
}
