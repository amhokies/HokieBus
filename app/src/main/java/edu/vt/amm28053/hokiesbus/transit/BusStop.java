package edu.vt.amm28053.hokiesbus.transit;

/**
 * Created by alex on 8/12/15.
 */
public class BusStop {
    private final String name;
    private final int code;

    public BusStop(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }
}
