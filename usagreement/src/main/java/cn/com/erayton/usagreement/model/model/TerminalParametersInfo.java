package cn.com.erayton.usagreement.model.model;

public class TerminalParametersInfo {
        //    应答流水号     WORD
        private int serialNumber ;
        //  指令数
        private int instructCount ;
        //  参数项
        private String itemParamenter ;
        //  GPS 休眠上传间隔      DWORD 休眠时汇报时间间隔，单位为秒（s），>0
        private int GpsSleepInterval ;
        //  GPS 缺省上传间隔      DWORD 缺省时间汇报间隔，单位为秒（s），>0
        private int GpsDefInterval ;

        //  汇报策略        DWORD 位置汇报策略，0：定时汇报；1：定距汇报；2：定时和定距汇报
        private int strategy ;
        //  汇报方案        DWORD 位置汇报方案，0：根据 ACC 状态；
        //                          1：根据登录状态和 ACC 状态， 先判断登录状态，若登录再根据 ACC 状态
        private int plan ;
        //  驾驶员未登录时间间隔      DWORD 驾驶员未登录汇报时间间隔，单位为秒（s），>0
        private int loggedOnInterval ;
        //  紧急报警时间间隔    DWORD 紧急报警时汇报时间间隔，单位为秒（s），>0
        private int alarmInterval ;
        //  拐点补偿角度          DWORD 拐点补传角度，< 180
        private int angelInflection ;
        //  非法位移阀值          WORD 电子围栏半径（非法位移阈值），单位为米
        private int threshold ;

        public int getSerialNumber() {
            return serialNumber;
        }

        public void setSerialNumber(int serialNumber) {
            this.serialNumber = serialNumber;
        }

        public int getInstructCount() {
            return instructCount;
        }

        public void setInstructCount(int instructCount) {
            this.instructCount = instructCount;
        }

        public String getItemParamenter() {
            return itemParamenter;
        }

        public void setItemParamenter(String itemParamenter) {
            this.itemParamenter = itemParamenter;
        }

        public int getGpsSleepInterval() {
            return GpsSleepInterval;
        }

        public void setGpsSleepInterval(int gpsSleepInterval) {
            GpsSleepInterval = gpsSleepInterval;
        }

        public int getGpsDefInterval() {
            return GpsDefInterval;
        }

        public void setGpsDefInterval(int gpsDefInterval) {
            GpsDefInterval = gpsDefInterval;
        }

        public int getStrategy() {
            return strategy;
        }

        public void setStrategy(int strategy) {
            this.strategy = strategy;
        }

        public int getPlan() {
            return plan;
        }

        public void setPlan(int plan) {
            this.plan = plan;
        }

        public int getLoggedOnInterval() {
            return loggedOnInterval;
        }

        public void setLoggedOnInterval(int loggedOnInterval) {
            this.loggedOnInterval = loggedOnInterval;
        }

        public int getAlarmInterval() {
            return alarmInterval;
        }

        public void setAlarmInterval(int alarmInterval) {
            this.alarmInterval = alarmInterval;
        }

        public int getAngelInflection() {
            return angelInflection;
        }

        public void setAngelInflection(int angelInflection) {
            this.angelInflection = angelInflection;
        }

        public int getThreshold() {
            return threshold;
        }

        public void setThreshold(int threshold) {
            this.threshold = threshold;
        }
}
