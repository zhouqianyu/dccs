package test;

import java.util.HashMap;
import java.util.Map;

public class Test {
    public static void main(String[] args) {
        Map<String, String> map = new HashMap<>();
        map.put("1", "one");
        for (Map.Entry<String,String> item:map.entrySet()) {

        }
    }
}

class People{
    private String name = "a";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}