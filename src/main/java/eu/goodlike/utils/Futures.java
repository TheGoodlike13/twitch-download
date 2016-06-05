package eu.goodlike.utils;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class Futures {

    public static <Result, Error> BiConsumer<Result, Error> completionHandler(Consumer<Result> resultConsumer,
                                                                              Consumer<Error> errorConsumer) {
        return (result, error) ->  {
            if (error == null)
                resultConsumer.accept(result);
            else
                errorConsumer.accept(error);
        };
    }

    // PRIVATE

    private Futures() {
        throw new AssertionError("Do not instantiate, use static methods!");
    }

}
