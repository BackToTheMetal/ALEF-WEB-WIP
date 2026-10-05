package javax.accessibility;

import java.util.Vector;

public class AccessibleRelationSet {
    protected Vector<AccessibleRelation> relations;

    public AccessibleRelationSet() {
    }

    public AccessibleRelationSet(AccessibleRelation[] relations) {
        addAll(relations);
    }

    public boolean add(AccessibleRelation relation) {
        if (relations == null) {
            relations = new Vector<>();
        }
        AccessibleRelation existingRelation = get(relation.getKey());
        if (existingRelation == null) {
            relations.addElement(relation);
            return true;
        }
        Object[] existingTargets = existingRelation.getTarget();
        Object[] addedTargets = relation.getTarget();
        Object[] mergedTargets = new Object[existingTargets.length + addedTargets.length];
        copy(existingTargets, mergedTargets, 0);
        copy(addedTargets, mergedTargets, existingTargets.length);
        existingRelation.setTarget(mergedTargets);
        return true;
    }

    public void addAll(AccessibleRelation[] relations) {
        for (AccessibleRelation relation : relations) {
            add(relation);
        }
    }

    public boolean remove(AccessibleRelation relation) {
        return relations != null && relations.removeElement(relation);
    }

    public void clear() {
        if (relations != null) {
            relations.removeAllElements();
        }
    }

    public int size() {
        return relations == null ? 0 : relations.size();
    }

    public boolean contains(String key) {
        return get(key) != null;
    }

    public AccessibleRelation get(String key) {
        if (relations == null) {
            return null;
        }
        for (AccessibleRelation relation : relations) {
            if (relation != null && relation.getKey().equals(key)) {
                return relation;
            }
        }
        return null;
    }

    public AccessibleRelation[] toArray() {
        if (relations == null) {
            return new AccessibleRelation[0];
        }
        AccessibleRelation[] result = new AccessibleRelation[relations.size()];
        for (int index = 0; index < result.length; index++) {
            result[index] = relations.elementAt(index);
        }
        return result;
    }

    @Override
    public String toString() {
        if (relations == null || relations.isEmpty()) {
            return "";
        }
        StringBuilder result = new StringBuilder(relations.elementAt(0).toDisplayString());
        for (int index = 1; index < relations.size(); index++) {
            result.append(',').append(relations.elementAt(index).toDisplayString());
        }
        return result.toString();
    }

    private static void copy(Object[] source, Object[] target, int targetOffset) {
        for (int index = 0; index < source.length; index++) {
            target[targetOffset + index] = source[index];
        }
    }
}
