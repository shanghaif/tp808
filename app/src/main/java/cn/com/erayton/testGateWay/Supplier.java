package cn.com.erayton.testGateWay;

public interface Supplier<T>  {
    /**
     * Gets a result.
     * @return a result
     */
    T get();
}
