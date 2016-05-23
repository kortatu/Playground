package net.kortsoft.playground.monads;

import static java.util.Arrays.asList;

import org.junit.Test;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Created by alvaro on 5/23/16.
 */
public class MonadicTest {

    public static void main(String[] args) {
        new MonadicTest().testFirstLawMonadicStream();
        new MonadicTest().testSecondLawMonadicStream();
        new MonadicTest().testThirdLawMonadicStream();
        new MonadicTest().testDo();
    }

    public void testFirstLawMonadicStream() {
        final String initialString = "hola";
        final Function<String, Stream<String>> f = myString -> _return(myString + " concat");

        _return(initialString).flatMap(f).forEach(print());
        f.apply(initialString).forEach(print());
    }

    public void testSecondLawMonadicStream() {
        final String initialString = "hola";

        _return(initialString).forEach(print());
        _return(initialString).flatMap(this::_return).forEach(print());
    }

    public void testThirdLawMonadicStream() {
        final String initialString = "hola";

        final Function<String, Stream<String>> f = myString -> _return(myString + " concat");
        final Function<String, Stream<String>> g = myString -> _return(myString + " second");

        _return(initialString).flatMap(f).flatMap(g).forEach(print());

        _return(initialString).flatMap(myString -> f.apply(myString).flatMap(g)).forEach(print());

    }

    public void testDo() {
        System.out.println("TEST do");
        final Function<String, Stream<String>> f = myString -> _return(myString + " concat");
        final Function<String, Stream<String>> g = myString -> _return(myString + " second");
        final Function<String, Stream<Integer>> h = myString -> _return(myString.length());

        final Function<Stream<String>, Stream<String>> lifted = lift((String x) -> x.toUpperCase());
        final Stream<Object> main = _do(
                x -> _return("hola","adios","test"),
                myString -> _return(myString + " concat"),
                myString -> _return(myString + " second"),
                (String myString) -> _return(myString.length())
        );
        System.out.println(main.reduce("", (x,y) -> String.valueOf(x)+y+ " "));
    }

    private Consumer<String> print() {
        return System.out::println;
    }

    private <T> Stream<T> _return(T... value) {
        return Arrays.asList(value).stream();
    }

    private <T,S> Stream<S> bind(Stream<T> m,  Function<T, Stream<S>> f) {
        return m.flatMap(f);
    }

    private <T,S> Function<Stream<T>, Stream<S>> lift(Function<T,S> f) {
        return st -> st.map(f);
    }

    private <T> Stream<T> _do(Function<?, ? extends Stream<? extends Serializable>>... statements) {
        Stream stream = Stream.of(this);
        for (Function<?, ? extends Stream<? extends Serializable>> f : statements) {
            stream = stream.flatMap(f);
        }
        return stream;
    }
}
