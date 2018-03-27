package baiduaudio.tangdi.com.audiotest.asrfinishjson;

import baiduaudio.tangdi.com.audiotest.asrfinishjson.OriginResult;

/**
 * zhangy
 * Created by td on 2017/10/20.
 */

public class AsrFinishJsonData {

    private String error;
    private String sub_error;
    private String desc;
    private OriginResult origin_result;

    public String getError() {
        return error;
    }

    public String getSub_error() {
        return sub_error;
    }

    public String getDesc() {
        return desc;
    }

    public OriginResult getOrigin_result() {
        return origin_result;
    }
}
