package de.tud.stg.popart.eclipse.core.debug.annotations;

@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(value = { java.lang.annotation.ElementType.METHOD })
public abstract @interface PopartType {

    @SuppressWarnings("rawtypes")
    public abstract Class clazz();

    public abstract int breakpointPossible();

}
