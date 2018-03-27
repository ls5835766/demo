package com.baidu.tb_robot.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.tb_robot.R;
import com.baidu.tb_robot.api.walk.BaseWalkPacke;
import com.baidu.tb_robot.config.Config;

public class WalkActivity extends AppCompatActivity {

    private static final String TAG=WalkActivity.class.getSimpleName();

    /**
     * 主 变量
     */
    // 主线程Handler
    // 用于将从服务器获取的消息显示出来
    private Handler mMainHandler;


    // 连接 断开连接 发送数据到服务器 的按钮变量
    private Button btnConnect, btnDisconnect, robot_task_turn_req_send,robot_status_mode_req_send,
            robot_status_info_req_send,robot_task_gopoint_req_send,robot_task_gotarget_req_send,
            robot_status_loc_req_send,robot_status_block_req_send,robot_task_patrol_req_send,
            robot_status_task_req_send,robot_task_chg_req_send;

    // 显示接收服务器消息 按钮
    private TextView receive_message;

    private BaseWalkPacke config,control,kernel,other,stauts,task;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk);

        // 初始化所有按钮
        btnConnect = (Button) findViewById(R.id.connect);
        btnDisconnect = (Button) findViewById(R.id.disconnect);
        robot_status_info_req_send= (Button) findViewById(R.id.robot_status_info_req_send);
        robot_task_turn_req_send= (Button) findViewById(R.id.robot_task_turn_req_send);
        robot_status_mode_req_send= (Button) findViewById(R.id.robot_status_mode_req_send);
        robot_task_gopoint_req_send= (Button) findViewById(R.id.robot_task_gopoint_req_send);
        robot_task_gotarget_req_send= (Button) findViewById(R.id.robot_task_gotarget_req_send);
        robot_status_loc_req_send= (Button) findViewById(R.id.robot_status_loc_req_send);
        robot_status_block_req_send= (Button) findViewById(R.id.robot_status_block_req_send);
        robot_task_patrol_req_send= (Button) findViewById(R.id.robot_task_patrol_req_send);
        robot_status_task_req_send= (Button) findViewById(R.id.robot_status_task_req_send);
        robot_task_chg_req_send= (Button) findViewById(R.id.robot_task_chg_req_send);
        receive_message = (TextView) findViewById(R.id.receive_message);


        // 实例化主线程,用于更新接收过来的消息
        mMainHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        receive_message.setText((String)msg.obj);
                        break;
                }
            }
        };
        /**
         * 创建客户端 & 服务器的连接
         */
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                config=new BaseWalkPacke(Config.CONFIG_PORT,mMainHandler);//机器人配置API端口
                control=new BaseWalkPacke(Config.CONTROL_PORT,mMainHandler);//机器人控制API端口
                kernel=new BaseWalkPacke(Config.KERNEL_PORT,mMainHandler);//机器人核心API端口
                other=new BaseWalkPacke(Config.OTHER_PORT,mMainHandler);//机器人其他API端口
                stauts=new BaseWalkPacke(Config.STAUTS_PORT,mMainHandler);//机器人状态API端口
                task=new BaseWalkPacke(Config.TASK_PORT,mMainHandler);//机器人任务API端口

            }
        });


        //机器人信息
        robot_status_info_req_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stauts.sendMessage(Config.robot_status_info_req, Config.robot_serial_number,null);
            }
        });

        //旋转
        robot_task_turn_req_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str="{\"angle\":1.25,\"vw\":1.0,\"mode\":0}";
                task.sendMessage(Config.robot_task_turn_req, Config.robot_serial_number,str.getBytes());
            }
        });

        //查询机器人运行模式
        robot_status_mode_req_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stauts.sendMessage(Config.robot_status_mode_req, Config.robot_serial_number,null);
            }
        });

        //自由导航
        robot_task_gopoint_req_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str="{\"id\":\"LM2\",\"angle\":0}";
                //String  str="{\"angle\":3.14,\"vw\":0.01,\"mode\":0}";
                task.sendMessage(Config.robot_task_gopoint_req,Config.robot_serial_number,str.getBytes());
            }
        });

        //固定导航
        robot_task_gotarget_req_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str="{\"id\":\"LM5\"}";
                task.sendMessage(Config.robot_task_gotarget_req,Config.robot_serial_number,str.getBytes());
            }
        });

        //充电
        robot_task_chg_req_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str="{\"id\":\"CP4\"}";
                task.sendMessage(Config.robot_task_gotarget_req,Config.robot_serial_number,str.getBytes());
            }
        });

        //获取机器人的位置
        robot_status_loc_req_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stauts.sendMessage(Config.robot_status_loc_req, Config.robot_serial_number,null);
            }
        });

        //获取障碍物信息
        robot_status_block_req_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stauts.sendMessage(Config.robot_status_block_req, Config.robot_serial_number,null);
            }
        });

        //巡检
        robot_task_patrol_req_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String str="{\"route\":\"test\"}";
                task.sendMessage(Config.robot_task_patrol_req,Config.robot_serial_number,str.getBytes());
            }
        });

        //查询任务状态
        robot_status_task_req_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stauts.sendMessage(Config.robot_status_task_req, Config.robot_serial_number,null);
            }
        });

        /**
         * 断开客户端 & 服务器的连接
         */
        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stauts.disConnect();
            }
        });
    }
}

