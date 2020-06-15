package net.rayton.usprotocol;

import net.rayton.usprotocol.constants.MessIdConstants;
import net.rayton.usprotocol.model.TerminalGeneralMsg;

public class ObjectFactory {

    //  长度
    private Object[] OBJ_TABLES = new Object[10] ;
    private static ObjectFactory instance ;

    public static ObjectFactory getInstance() {
        if (instance == null){
            instance = new ObjectFactory() ;
        }
        return instance;
    }

    //  初始化消息ID
    private ObjectFactory() {
        OBJ_TABLES[MessIdConstants.SERVER_COMMOM_RSP] = TerminalGeneralMsg.class;
    }

    public ObjectHeader getObject(byte[] datas) throws InstantiationException, IllegalAccessException {
        //  估计是消息ID
        byte messId = 0 ;
        return ((Class<ObjectHeader>)OBJ_TABLES[messId]).newInstance();
    }
}
