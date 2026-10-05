package javax.accessibility;

public interface AccessibleAction {
    String TOGGLE_EXPAND = "toggleexpand";
    String INCREMENT = "increment";
    String DECREMENT = "decrement";
    String CLICK = "click";
    String TOGGLE_POPUP = "toggle popup";

    int getAccessibleActionCount();

    String getAccessibleActionDescription(int index);

    boolean doAccessibleAction(int index);
}
