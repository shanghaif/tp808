package net.rayton.usprotocol;

import java.io.Serializable;

/**
 * 数据包抽象类，协议头
 * */
public abstract class ObjectHeader implements Serializable {
    // 消息ID WORD byte[0-1]
    protected int msgId;
    /////// ========消息体属性
    // byte[2-3]
    protected int msgBodyPropsField;
    // 消息体长度 bit[0-9]
    protected int msgBodyLength;
    // 数据加密方式 bit[10-12]
    protected int encryptionType;
    // 是否分包,true==>有消息包封装项 bit[13]
    protected boolean hasSubPackage;
    // 保留位 bit[14-15]
    protected int reservedBit;
    /////// ========消息体属性
    // 终端手机号 BCD[6] byte[4-9]
    protected String terminalPhone;
    // 流水号 byte[10-11]
    protected int flowId;
    //////// =====消息包封装项
    // byte[12-15]
    protected int packageInfoField;
    // 消息包总数(word(16))
    protected long totalSubPackage;
    // 包序号(word(16))这次发送的这个消息包是分包中的第几个消息包, 从 1 开始
    protected long subPackageSeq;
    //////// =====消息包封装项


    /**
     * 校验码 1byte
     */
    protected int checkSum;

    /**
     * 应答 ID
     * */
    protected int answerFlowId;

    public ObjectHeader() {
        totalSubPackage = 1 ;
        subPackageSeq = 1 ;
    }

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public int getMsgBodyPropsField() {
        return msgBodyPropsField;
    }

    public void setMsgBodyPropsField(int msgBodyPropsField) {
        this.msgBodyPropsField = msgBodyPropsField;
    }

    public int getMsgBodyLength() {
        return msgBodyLength;
    }

    public void setMsgBodyLength(int msgBodyLength) {
        this.msgBodyLength = msgBodyLength;
    }

    public int getEncryptionType() {
        return encryptionType;
    }

    public void setEncryptionType(int encryptionType) {
        this.encryptionType = encryptionType;
    }

    public boolean isHasSubPackage() {
        return hasSubPackage;
    }

    public void setHasSubPackage(boolean hasSubPackage) {
        this.hasSubPackage = hasSubPackage;
    }

    public int getReservedBit() {
        return reservedBit;
    }

    public void setReservedBit(int reservedBit) {
        this.reservedBit = reservedBit;
    }

    public String getTerminalPhone() {
        return terminalPhone;
    }

    public void setTerminalPhone(String terminalPhone) {
        this.terminalPhone = terminalPhone;
    }

    public int getFlowId() {
        return flowId;
    }

    public void setFlowId(int flowId) {
        this.flowId = flowId;
    }

    public int getPackageInfoField() {
        return packageInfoField;
    }

    public void setPackageInfoField(int packageInfoField) {
        this.packageInfoField = packageInfoField;
    }

    public long getTotalSubPackage() {
        return totalSubPackage;
    }

    public void setTotalSubPackage(long totalSubPackage) {
        this.totalSubPackage = totalSubPackage;
    }

    public long getSubPackageSeq() {
        return subPackageSeq;
    }

    public void setSubPackageSeq(long subPackageSeq) {
        this.subPackageSeq = subPackageSeq;
    }

    public int getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(int checkSum) {
        this.checkSum = checkSum;
    }

    public int getAnswerFlowId() {
        return answerFlowId;
    }

    public void setAnswerFlowId(int answerFlowId) {
        this.answerFlowId = answerFlowId;
    }

    /**
     * 将消息对象转换成字节数组。
     * @return			消息对象转换后的字节数组
     * @throws NullPointerException      参数出现空值
     * @throws MessageMistakenException  参数错误
     */
    public abstract byte[] packageDataBody2Byte()throws NullPointerException;


    /**
     * 将字节数组转换成消息对象。
     * @param datas		待转换完整消息数据<b>0xAA 0x7F ... 0x0D 0x0A</b>
     * @return			字节数组解析后的消息对象
     * @throws NullPointerException     datas		待转换数据为空
     * @throws CheckMistakenException     检验位错误
     */
    public abstract ObjectHeader inflatePackageBody(byte[] datas)throws NullPointerException;

}
