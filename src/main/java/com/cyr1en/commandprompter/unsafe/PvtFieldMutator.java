package com.cyr1en.commandprompter.unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;

/**
 * A utility class that allows you to easily change the value of private final fields.
 * <p>
 * WARNING: Use this class with caution. Only use it for cases that justifies unsafe operations.
 * First, check if there's another way to accomplish
 *
 * Usage:
 * <pre>{@code
 *     // let's say there's a private final field named "targetField" in instanceOfTarget
 *     var instanceOfTarget = new Target();
 *
 *     var newFieldVal = new SomeObject();
 *
 *     var mutator = new PvtFieldMutator();
 *     mutator.forField("targetField").in(instanceOfTarget).with(newFieldVal);
 * }</pre>
 */
public class PvtFieldMutator {
    private String targetName;
    private Object targetInstance;

    private PvtFieldMutator() {
    }
    /**
     * Function that defines the name of the target.
     *
     * @param targetFieldName name of the target field.
     * @return instance of this class.
     */
    public static PvtFieldMutator forField(String targetFieldName) {
        PvtFieldMutator mutator = new PvtFieldMutator();
        mutator.targetName = targetFieldName;
        return mutator;
    }

    /**
     * Function that defines the instance of the target object.
     *
     * @param objInstance instance of the target object.
     * @return instance of this class.
     */
    public PvtFieldMutator in(Object objInstance) {
        this.targetInstance = objInstance;
        return this;
    }

    /**
     * Function that defines the new object for the target field
     * and executes the mutation
     *
     * @param newObject object to replace the private final field with.
     * @throws NullPointerException when one of the target field name or target instance is null.
     * @throws NoSuchFieldException when the target field cannot be found in the target instance.
     */
    public void replaceWith(Object newObject) throws NoSuchFieldException, IllegalStateException, IllegalAccessException {
        assertTargetNotNull();
        Field targetField = targetInstance.getClass().getDeclaredField(targetName);
        targetField.setAccessible(true);

        // Remove FINAL modifier
        int mod = targetField.getModifiers();
        if (Modifier.isFinal(mod)) {
            Field modifiers = targetField.getClass().getDeclaredField("modifiers");
            modifiers.setAccessible(true);
            modifiers.set(targetField, mod & ~Modifier.FINAL);
        }

        targetField.set(targetInstance, newObject);

        Object newFieldObject = targetField.get(targetInstance);

        if(Objects.isNull(newObject)) {
            if(!Objects.isNull(newFieldObject))
                throw new PvtFieldMutationException("null");
            return;
        }

        if (!newFieldObject.getClass().getCanonicalName().equals(newObject.getClass().getCanonicalName()))
            throw new PvtFieldMutationException(newObject.getClass());
    }

    /**
     * Helper function to easily get the current class name for the target field.
     *
     * <p>
     * Uses reflection to set the target field accessible from the instance of the defining
     * object.
     *
     * @return The canonical name of the current class of the target field.
     * @throws NullPointerException when one of the target field name or target instance is null.
     * @throws NoSuchFieldException when the target field cannot be found in the target instance.
     */
    public String getClassName() throws NoSuchFieldException, IllegalAccessException {
        assertTargetNotNull();
        Field targetField = targetInstance.getClass().getDeclaredField(targetName);
        targetField.setAccessible(true);
        Object ob = targetField.get(targetInstance);
        return ob.getClass().getCanonicalName();
    }

    public int getHashCode() throws NoSuchFieldException, IllegalAccessException {
        assertTargetNotNull();
        Field targetField = targetInstance.getClass().getDeclaredField(targetName);
        targetField.setAccessible(true);
        Object ob = targetField.get(targetInstance);
        return ob.hashCode();
    }

    /**
     * Asserts that both target field name and target instance is not null.
     *
     * @throws NullPointerException when the target field cannot be found in the target instance.
     */
    private void assertTargetNotNull() throws IllegalStateException {
        if (this.targetInstance == null || this.targetName == null)
            throw new IllegalStateException("Target field name or target instance is null.");
    }
}
