package com.baidu.tb_robot.api.walk;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * zhangy
 * Created by zhangy on 2017/12/18.
 */

public class Test {

    public static final short robot_status_info_req=  2002; //重定位请求
    public static final short robot_serial_number=  1;

    public static void main(String args[]) {
        String str="{\"x\":10.0,\"y\":3.0,\"angle\":0}";
        byte[] bytes=str.getBytes();

        File file = new File("test1.txt");
        testDataOutputStream(file,bytes);
        readFile(file,bytes);
    }

    private static void testDataOutputStream(File file, byte[] bytes ) {
        try {

            DataOutputStream out = new DataOutputStream(new FileOutputStream(file));

            pack(out, robot_status_info_req,robot_serial_number,bytes.length,bytes);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void pack(DataOutputStream out, short type, short serial_number, int data_length, byte[] bytes) {
        try {
            int index = 0;
            byte[] buf_str = new byte[16 + data_length];

            buf_str[index++] = 0x5a;
            ///< 协议版本  // NOLINT
            buf_str[index++] = 0x01;

            ///< 序号，2字节  // NOLINT
            buf_str[index++] = (byte) (serial_number >> 8);
            buf_str[index++] = (byte) (serial_number >> 0);

            ///< 数据区长度，4字节  // NOLINT   低位在前，高位在后
            buf_str[index++] = (byte) (data_length >> 24);
            buf_str[index++] = (byte) (data_length >> 16);
            buf_str[index++] = (byte) (data_length >> 8);
            buf_str[index++] = (byte) (data_length >> 0);

//          < 报文类型，2字节  // type是short类型  高位在前，低位在后

            buf_str[index++] = (byte) ((type & 0xFF00) >> 8);
            buf_str[index++] = (byte) (type & 0xFF);
//            buf_str[index++] = (byte) (type >> 8);
//            buf_str[index++] = (byte) (type >> 0);

//            ///< 保留区域， 6字节  // NOLINT
            buf_str[index++] = 0x00;
            buf_str[index++] = 0x00;
            buf_str[index++] = 0x00;
            buf_str[index++] = 0x00;
            buf_str[index++] = 0x00;
            buf_str[index++] = 0x00;

            //数据内容
            for (int i = 0; i < data_length; i++) {
                buf_str[index++] = bytes[i];
            }

            StringBuilder stringBuilder = new StringBuilder();
            for (int z = 0; z < buf_str.length; z++) {
                stringBuilder.append(Integer.toHexString(buf_str[z]) + ",");
            }
            System.out.println(stringBuilder.toString());
            out.write(buf_str);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static void readFile(File file, byte[] bytes){
        if(file != null){
            try {
                FileInputStream fis = new FileInputStream(file);
                DataInputStream dis = new DataInputStream(fis);

               for(int i=0;i<16+bytes.length;i++) {
                   System.out.print(dis.readByte());
               }

                while(dis.available() != 0){
                    System.out.print((char)dis.readByte());
                }

//                for(int i=0;i<16+bytes.length;i++){
//                    System.out.printf("%x\n",dis.readByte());
//                }
                dis.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }
}
