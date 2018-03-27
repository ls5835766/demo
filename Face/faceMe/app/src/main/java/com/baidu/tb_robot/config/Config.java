package com.baidu.tb_robot.config;

import android.os.Environment;

import com.baidu.speech.asr.SpeechConstant;

import java.nio.charset.Charset;

/**
 * zhangy
 * Created by zhenglibin on 17/5/31.
 */

public class Config {

    public static Charset DEFAULT_CHARSET = Charset.forName("GB18030");

    public static String apiKey = "38TGqd1lGuwBwL1PTswrZk4l";
    public static String secretKey = "b31lD7EsWCNoTzhyBvZVd01FgiPj2vIC";
    public static String licenseID = "tangbao20180120-face-android";
    public static String licenseFileName = "idl-license.face-android";

    public static String groupID = "Turesite001";

    /**
     * Usb的配置
     */
    public interface Usb {
        int BAUD_RATE = 115200;     //波特率
        byte STOP_BIT = 1;
        byte DATA_BIT = 8;
        byte PARITY = 0;
        byte FLOW_CONTROL = 0;
    }

    /**
     * 升级服务器
     */
    public interface Server {
        String BASE_IP = "http://192.168.95.4:18088";
        String UPDATE = BASE_IP + "/tdcctp/APK001.tran";
        String DOWNLOAD_APK = BASE_IP + "/tdcctp/APK002.tran";
        String EXCEPTION_LOG_UPLOAD = BASE_IP + "/tdcctp/AL0001.tran";
    }


    /**
     * 仙知机器人API
     */
    public static final int STAUTS_PORT = 19204; //机器人状态API端口
    public static final int CONTROL_PORT =19205 ; //机器人控制API端口
    public static final int TASK_PORT = 19206;//机器人任务API端口
    public static final int CONFIG_PORT = 19207;//机器人配置API端口
    public static final int KERNEL_PORT = 19208;//机器人核心API端口
    public static final int OTHER_PORT = 19210;//机器人其他API端口

    public static final short robot_serial_number=  1;

    public static final short robot_task_turn_req= 3056;  //旋转
    public static final short robot_task_gopoint_req=3050;  //自由导航
    public static final short robot_task_gotarget_req= 3051;  //固定路径导航
    public static final short robot_task_patrol_req=  3052;  //巡检
    public static final short robot_control_reloc_req= 2002; //重定位请求
    public static final short robot_status_info_req=1000;//查询机器人信息
    public static final short robot_status_mode_req=1003;//查询机器人运行模式
    public static final short robot_status_loc_req=1004;//查询机器人的位置
    public static final short robot_status_block_req=1006;//查询机器人的被阻挡状态
    public static final short robot_status_task_req=1020;//查询机器人任务状态






}
