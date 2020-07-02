package cn.com.erayton.usagreement.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * public class RegexMatches {
 *
 * 	public static void main(String args[]) {
 * 		String str = "";
 * 		String pattern = "(\d+_){2,}\d+";
 *
 * 		Pattern r = Pattern.compile(pattern);
 * 		Matcher m = r.matcher(str); //  返回boolean, 有无匹配项
 * 		System.out.println(m.matches());
 *        }
 *
 * }
 * */
public class RegularUtils {
    //  是否包含 storage 字符串
    public static final String isStorage = ".*storage.*" ;

    /** 文件名规则，
     * 开始时间(YYMMDDHHmmss)_结束时间(YYMMDDHHmmss)_通道号_资源类型(音视频,音频,视频,)_码流类型(主,子码流)
     * 1. 全部为数字， 下划线在两位或者以上
     * (\d+_){2,}\d+
     */
    public static String videoRex = "(\\d+_){2,}\\d+" ;

    //  获取对应格式文件名
    public static String getOneResult(String str, String rex){
        String result = "";
        Pattern pattern = Pattern.compile(rex);
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            result = matcher.group();
        }
        return result;
    }



    /**
     *
     * @param str 匹配字符串
     * @param rex   正则表达式
     * @return  是否包含
     */
    public static boolean isContain(String str, String rex) {
        return Pattern.compile(rex).matcher(str).matches();
    }
}
