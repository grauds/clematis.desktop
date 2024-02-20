package jworkspace.utils;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * A collection of stream utils
 *
 * @author Anton Troshin
 */
public final class StreamUtils {

    private StreamUtils() {}

    /**
     * Adds index argument to forEach
     *
     * @param source iterable collection of objects
     * @param consumer bespoke {@link  BiConsumer} with one integer argument
     * @param <T> type of the input collection
     */
    static <T> void forEach(Iterable<T> source, BiConsumer<Integer, T> consumer) {
        int i = 0;
        for (T item : source) {
            consumer.accept(i, item);
            i++;
        }
    }

    /**
     * Modifies consumer by wrapping {@link BiConsumer}
     *
     * @param consumer bespoke {@link  BiConsumer} with one integer argument
     * @return combined consumer with counter
     * @param <T> type of the input collection
     */
    public static <T> Consumer<T> withCounter(BiConsumer<Integer, T> consumer) {
        AtomicInteger counter = new AtomicInteger(0);
        return item -> consumer.accept(counter.getAndIncrement(), item);
    }
}
