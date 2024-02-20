package jworkspace.utils;
/**
 * Bi consumer to use there it usually takes one argument to do the job
 *
 * @author Anton Troshin
 * @param <T>
 * @param <U>
 */
@FunctionalInterface
public interface BiConsumer<T, U> {
    void accept(T t, U u);
}