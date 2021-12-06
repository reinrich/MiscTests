package test.misc;

import java.util.stream.Stream;

public class TestEmptyStream {

    public static void main(String[] args) {
        Object[] res = Stream.empty().toArray();
        System.out.println("res:" + res);
        System.out.println("res.length():" + res.length);
    }

}
