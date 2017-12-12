package uppd.com.vrec.util;

import io.reactivex.functions.Predicate;

/**
 * Created by o.rabinovych on 12/9/17.
 */

public class Safety {
    public static <T> java.util.function.Predicate<T> safePredicate(Predicate<T> predicate) {
        return t -> {
            try {
                return predicate.test(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}
