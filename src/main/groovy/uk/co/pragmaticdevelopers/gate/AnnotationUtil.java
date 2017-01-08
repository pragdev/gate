package uk.co.pragmaticdevelopers.gate;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class AnnotationUtil {

    public static boolean hasAnnotation(Class annotation, Field field) {
        Annotation found = field.getAnnotation(annotation);
        return found != null;
    }

    public static String fieldName(Field field) {
        return field.getName();
    }

}
