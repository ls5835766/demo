package com.baidu.tb_robot.config;

/**
 * Created by zhenglibin on 17/5/31.
 * 关闭语音输出模式        0x02            0x43 0x30 成功  0x43 0x31 失败  （麦克风录不进去音）
 * 开启语音输出模式        0x03            0x44 0x30 成功  0x44 0x31 失败
 * 查询唤醒角度           0x04
 * 查询是否被唤醒         0x06             0x42 0x30 没有被唤醒，没有语音输出  0x42 0x31 被唤醒，有语音输出
 */
public class Constants {

    public static final String QUERY_WAKE_UP_STATUS = "0x06";

    public static final String QUERY_WAKE_UP_ANGLE = "0x04";

    public static final String WAKE_UP_SUCCESS = "";

    public static final String WAKE_UP_FAILED = "";

    public static final String CLOSE_VOICE_OUTPUT = "0x02";

    public static final String CLOSE_VOICE_OUTPUT_SUCCESS = "";

    public static final String CLOSE_VOICE_OUTPUT_FAILED = "";

    public static final String OPEN_VOICE_OUTPUT = "0x03";

    public static final String OPEN_VOICE_OUTPUT_SUCCESS = "";

    public static final String OPEN_VOICE_OUTPUT_FAILED = "0x44 0x31";


    /**
     * 1001 TDStartASR
     * 1002 TDStopASR
     * 1003 TDStartTTS
     * 1004 TDStopTTS
     * 1005  SetArryAngle
     */
    public static final String TD_START_ASR = "1001";   // 开启语音识别

    public static final String TD_STOP_ASR = "1002";    //关闭语音识别

    public static final String TD_START_TTS = "1003";   //开启语音合成

    public static final String TD_STOP_TTS = "1004";    //关闭语音合成

    public static final String SET_ARRY_ANGLE = "1005"; //设置唤醒角度角度

    public static final String QUERY_ANGLE = "|1200";  //返回角度

    public static final String TTS_RESULT = "|1201"; //语音合成结果

    public static final String ASR_RESULT = "|1202"; //语音识别结果

    public static final String ASR_EVERY_RESULT = "|1203"; //语音合成逐字结果


}
