package cn.erayton.easytoolsandroid.constants;

public interface PublicConstants {
    static class FileStrongConstants{
     // 临时文件位置  /Android/data/packageName/
        private static final String TempFileStrong = "";
    }
    static class TimeFormatConstants{
//        yyyy-MM-dd HH:mm:ss
        public static final String yMdHmssE = "yyyy-MM-dd HH:mm:ss SSS";
//        yyyyMMddHHmmss
        public static final String yMdHmssA = "yyyyMMddHHmmssSSS";
//        yyyy年-MM月dd日-HH时mm分ss秒
        public static final String yMdHmssC = "yyyy年-MM月dd日-HH时mm分ss秒 SSS";

//        yyyy-MM-dd HH:mm:ss
        public static final String yMdHmsE = "yyyy-MM-dd HH:mm:ss";
//        yyyyMMddHHmmss
        public static final String yMdHmsA = "yyyyMMddHHmmss";
//        yyyy年-MM月dd日-HH时mm分ss秒
        public static final String yMdHmsC = "yyyy年-MM月dd日-HH时mm分ss秒";

//        yyyy-MM-dd HH:mm
        public static final String yMdHmE = "yyyy-MM-dd HH:mm";
//        yyyyMMddHHmm
        public static final String yMdHmA = "yyyyMMddHHmm";
//        yyyy年-MM月dd日-HH时mm分
        public static final String yMdHmC = "yyyy年-MM月dd日-HH时mm分";

//        yyyy-MM-dd HH
        public static final String yMdHE = "yyyy-MM-dd HH";
//        yyyyMMddHH
        public static final String yMdHA = "yyyyMMddHH";
//        yyyy年-MM月dd日-HH时
        public static final String yMdHC = "yyyy年-MM月dd日-HH时";

//        yyyy-MM-dd
        public static final String TIME_STANDARD = "yyyy-MM-dd";
//        yyyyMMdd
        public static final String yMdA = "yyyyMMdd";
//        yyyy年-MM月dd日
        public static final String TIME_STANDARD_CHINESE = "yyyy年-MM月dd日";
    }
}
