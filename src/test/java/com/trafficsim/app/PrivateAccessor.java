package com.trafficsim.app;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import junit.framework.Assert;

/**
 * Provides access to private members in classes.
 */
public class PrivateAccessor {
  public static Object getPrivateField (Object o,
  String fieldName) {
    /* Check we have valid arguments */
    Assert.assertNotNull(o);
    Assert.assertNotNull(fieldName);

    /* Go and find the private field... */
    final Field fields[] = o.getClass().getDeclaredFields();
    for (int i = 0; i < fields.length; ++i) {
      if (fieldName.equals(fields[i].getName())) {
        try {
          fields[i].setAccessible(true);
          return fields[i].get(o);
        } catch (IllegalAccessException ex) {
          Assert.fail ("IllegalAccessException accessing " +
            fieldName);
        }
      }
    }

    Assert.fail ("Field '" + fieldName + "' not found");
    return null;
  }

  public static Method getPrivateMethod (Class<?> c, String methodName) {
    /* Check we have valid arguments */
    Assert.assertNotNull(c);
    Assert.assertNotNull(methodName);

    Method[] methods = c.getDeclaredMethods();
    for (int i=0; i<methods.length; i++) {
        if (methodName.equals(methods[i].getName())) {
            methods[i].setAccessible(true);
            return methods[i];
        }
    }
    
    Assert.fail ("Method '" + methodName + "' not found");
    return null;
  }

  public static Object runPrivateMethod(Method m, Object target, Object... args) {
    try {
        return m.invoke(target, args);
    } catch (IllegalAccessException ex) {
        Assert.fail("IllegalAccessException accessing " + m.getName());
    } catch (InvocationTargetException ex) {
        Assert.fail("InvocationTargetException accessing " + m.getName() + "\nCause: " + ex.getCause().toString());
    }

    return null;
  }

  public static Object runPrivateMethodUnsafe(Method m, Object target, Object... args) throws InvocationTargetException {
    try {
        return m.invoke(target, args);
    } catch (IllegalAccessException ex) {
        Assert.fail("IllegalAccessException accessing " + m.getName());
    }

    return null;
  }


  public static Object getAndRunPrivateMethod (Class<?> c, String methodName, Object target, Object... args) {
    Method m = getPrivateMethod(c, methodName);
    return runPrivateMethod(m, target, args);
  }
}
