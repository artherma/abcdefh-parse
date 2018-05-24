package main.test;

import java.util.HashSet;

public class TestAny {
    public static void main(String[] args) {
        HashSet<String> set = new HashSet<>();
        set.add("assdf");
        set.add("assdf");
        set.add("assdf");
        System.out.println(set.toString());
    }
}
