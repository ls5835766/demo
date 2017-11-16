package com.zhangy.aidltest;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * zhangy
 * 用来处理客户端发来的请求，或者是定时推信息到客户端。
 * Created by td on 2017/10/25.
 */

public class AIDLService extends Service {
    private static final int WHAT_MSG = 10010;
    private final String TAG = "qdx";

    /**
     * 由于跨进程传输客户端的同一对象会在服务端生成不同的对象！
     * 上面这句话说明跨进程通讯的过程中，这个传递的对象载体并不是像寄快递一样，从客户端传给服务端。而是经过中间人狸猫换太子的一些手段传递的。
     * 不过传递过程中间人(binder)对象都是同一个，所以Android通过这个特性提供RemoteCallbackList，让我们用来存储监听接口集合。
     * 这个RemoteCallbackList内部自动实现了线程同步的功能，而且它的本质是一个ArrayMap，所以我们用它来绑定/解绑时，不需要做额外的线程同步操作。
     */
    private RemoteCallbackList<IDemandListener> demandList = new RemoteCallbackList<>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("zhangy","onBind is sucessifully");
        mHandler.sendEmptyMessageDelayed(WHAT_MSG, 3000);
        return demandManager;
    }

    //Stub内部继承Binder，具有跨进程传输能力
    IDemandManager.Stub demandManager = new IDemandManager.Stub() {
        @Override
        public MessageBean getDemand() throws RemoteException {
            MessageBean demand = new MessageBean("首先，看到我要敬礼", 1);
            return demand;
        }

        @Override
        public void setDemandIn(MessageBean msg) throws RemoteException {//客户端数据流向服务端
            Log.i(TAG, "程序员:" + msg.toString());
        }

        @Override
        public void setDemandOut(MessageBean msg) throws RemoteException {//服务端数据流向客户端
            Log.i(TAG, "程序员:" + msg.toString());//msg内容一定为空

            msg.setContent("我不想听解释，下班前把所有工作都搞好！");
            msg.setLevel(5);
        }

        @Override
        public void setDemandInOut(MessageBean msg) throws RemoteException {//数据互通
            Log.i(TAG, "程序员:" + msg.toString());

            msg.setContent("把用户交互颜色都改成粉色");
            msg.setLevel(3);
        }

        @Override
        public void registerListener(IDemandListener listener) throws RemoteException {
            demandList.register(listener);
        }

        @Override
        public void unregisterListener(IDemandListener listener) throws RemoteException {
           demandList.unregister(listener);
        }
    };


    /**
     * 我们通过Handler来进行定时发送消息。(handler并不能精准的做定时任务，因为handler在发送和接收的过程中会有时间损耗)
     * 另外我们需要通过beginBroadcast()来获取RemoteCallbackList中元素的个数，同时beginBroadcast()和finishBroadcast()必须要配对使用，
     * 哪怕仅仅只是获取一下这个集合的元素个数。
     */

    private int count = 1;
    private Handler mHandler=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (demandList != null) {
                int nums = demandList.beginBroadcast();
                for (int i = 0; i < nums; i++) {
                    MessageBean messageBean = new MessageBean("我丢", count);
                    count++;
                    try {
                        demandList.getBroadcastItem(i).onDemandReceiver(messageBean);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                demandList.finishBroadcast();
            }

            mHandler.sendEmptyMessageDelayed(WHAT_MSG, 3000);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(WHAT_MSG);
    }
}
