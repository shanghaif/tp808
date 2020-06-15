package net.rayton.usprotocol;


/**
 * 解析器
 * */
public class ObjParser {
    private static ObjParser instance ;

    private ObjParser() {}
    public static ObjParser getInstance(){
        if (instance == null){
            instance = new ObjParser() ;
        }
        return instance ;
    }

    public ObjectHeader parse(byte[] datas){
        ObjectHeader objects = null ;
        try {
            objects = ObjectFactory.getInstance().getObject(datas) ;
            //  数据直接解析，未加密数据
            objects.inflatePackageBody(datas);
//            //  数据需解密，加密数据 eg
//            objects.bytesToObj(MessageUtils.encryptAndDecryptMess(datas)) ;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return objects ;
    }
}
