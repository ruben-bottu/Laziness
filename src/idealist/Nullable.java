package idealist;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
// TODO remove and use org.checkerframework.checker.nullness.qual.Nullable instead
public @interface Nullable {
}
