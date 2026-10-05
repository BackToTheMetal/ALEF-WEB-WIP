package javax.accessibility;

public class AccessibleRelation extends AccessibleBundle {
    public static final String LABEL_FOR = "labelFor";
    public static final String LABELED_BY = "labeledBy";
    public static final String MEMBER_OF = "memberOf";
    public static final String CONTROLLER_FOR = "controllerFor";
    public static final String CONTROLLED_BY = "controlledBy";
    public static final String FLOWS_TO = "flowsTo";
    public static final String FLOWS_FROM = "flowsFrom";
    public static final String SUBWINDOW_OF = "subwindowOf";
    public static final String PARENT_WINDOW_OF = "parentWindowOf";
    public static final String EMBEDS = "embeds";
    public static final String EMBEDDED_BY = "embeddedBy";
    public static final String CHILD_NODE_OF = "childNodeOf";
    public static final String LABEL_FOR_PROPERTY = "labelForProperty";
    public static final String LABELED_BY_PROPERTY = "labeledByProperty";
    public static final String MEMBER_OF_PROPERTY = "memberOfProperty";
    public static final String CONTROLLER_FOR_PROPERTY = "controllerForProperty";
    public static final String CONTROLLED_BY_PROPERTY = "controlledByProperty";
    public static final String FLOWS_TO_PROPERTY = "flowsToProperty";
    public static final String FLOWS_FROM_PROPERTY = "flowsFromProperty";
    public static final String SUBWINDOW_OF_PROPERTY = "subwindowOfProperty";
    public static final String PARENT_WINDOW_OF_PROPERTY = "parentWindowOfProperty";
    public static final String EMBEDS_PROPERTY = "embedsProperty";
    public static final String EMBEDDED_BY_PROPERTY = "embeddedByProperty";
    public static final String CHILD_NODE_OF_PROPERTY = "childNodeOfProperty";
    private Object[] target;

    public AccessibleRelation(String key) {
        this.key = key;
    }

    public AccessibleRelation(String key, Object target) {
        this.key = key;
        this.target = new Object[] {target};
    }

    public AccessibleRelation(String key, Object[] target) {
        this.key = key;
        this.target = target;
    }

    public String getKey() {
        return key;
    }

    public Object[] getTarget() {
        if (target == null) {
            return new Object[0];
        }
        Object[] result = new Object[target.length];
        for (int index = 0; index < target.length; index++) {
            result[index] = target[index];
        }
        return result;
    }

    public void setTarget(Object target) {
        this.target = new Object[] {target};
    }

    public void setTarget(Object[] target) {
        this.target = target;
    }
}
