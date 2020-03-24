package cn.com.erayton.testGateWay;

import io.reactivex.functions.Consumer;

public abstract class BaseRequestBean<T> implements Supplier<String>, Consumer<String>,SupplierHeader<String> {

    abstract String getRequsetData();

    abstract T parseData(String data);

    public T resultBean; //解析结果的bean

    public String method;   //标记请求

    abstract String methods();   //  标记请求

   public BaseRequestBean()

    {
        method = methods();
    }
//   public BaseRequestBean<T>()
//
//    {
//        method = methods();
//    }
    public BaseRequestBean(T resultBean) {
        this.resultBean = resultBean;
        method = methods();
    }

    /**
     * socket 发送数据的回掉接口
     *
     * @return 需要发送的数据
     */
    @Override
    public String get() {
        return getRequsetData();
    }

    /**
     * 接收socket 返回的数据回掉；
     *
     * @param s
     * @throws Exception
     */
    @Override
    public void accept(String s) throws Exception {
        parseData(s);
    }

    /**
     * Socket请求头接口
     *
     * @return socket报文头
     */
    @Override
    public String getHeader() {
        return null;
    }
}