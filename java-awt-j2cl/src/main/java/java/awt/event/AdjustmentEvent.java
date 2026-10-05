package java.awt.event;

import java.awt.AWTEvent;
import java.awt.Adjustable;

public class AdjustmentEvent extends AWTEvent {
    private static final long serialVersionUID = 5700290645205279921L;
    public static final int ADJUSTMENT_FIRST = 601;
    public static final int ADJUSTMENT_LAST = 601;
    public static final int ADJUSTMENT_VALUE_CHANGED = 601;
    public static final int UNIT_INCREMENT = 1;
    public static final int UNIT_DECREMENT = 2;
    public static final int BLOCK_DECREMENT = 3;
    public static final int BLOCK_INCREMENT = 4;
    public static final int TRACK = 5;
    private final int adjustmentType;
    private final int value;
    private final boolean valueIsAdjusting;

    public AdjustmentEvent(Adjustable source, int id, int type, int value) {
        this(source, id, type, value, false);
    }

    public AdjustmentEvent(Adjustable source, int id, int type, int value, boolean valueIsAdjusting) {
        super(source, id);
        adjustmentType = type;
        this.value = value;
        this.valueIsAdjusting = valueIsAdjusting;
    }

    public Adjustable getAdjustable() {
        return (Adjustable) getSource();
    }

    public int getValue() {
        return value;
    }

    public int getAdjustmentType() {
        return adjustmentType;
    }

    public boolean getValueIsAdjusting() {
        return valueIsAdjusting;
    }

    @Override
    public String paramString() {
        String eventName = id == ADJUSTMENT_VALUE_CHANGED ? "ADJUSTMENT_VALUE_CHANGED" : "unknown type";
        return eventName + ",adjType=" + adjustmentName() + ",value=" + value + ",isAdjusting=" + valueIsAdjusting;
    }

    private String adjustmentName() {
        switch (adjustmentType) {
            case UNIT_INCREMENT:
                return "UNIT_INCREMENT";
            case UNIT_DECREMENT:
                return "UNIT_DECREMENT";
            case BLOCK_DECREMENT:
                return "BLOCK_DECREMENT";
            case BLOCK_INCREMENT:
                return "BLOCK_INCREMENT";
            case TRACK:
                return "TRACK";
            default:
                return "unknown type";
        }
    }
}
