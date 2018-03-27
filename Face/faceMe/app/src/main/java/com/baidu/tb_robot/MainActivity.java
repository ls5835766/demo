package com.baidu.tb_robot;

import android.Manifest;
import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends ListActivity {

    public static final String CATEGORY_SAMPLE_CODE = "com.baidu.speech.recognizerdemo.intent.category.SAMPLE_CODE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initPermission();

        setListAdapter(new SimpleAdapter(this, getData(), android.R.layout.simple_list_item_1,
                new String[]{
                        "title"
                }, new int[]{
                android.R.id.text1
        }));
    }

    /**
     * 获取Demo列表
     *
     * @return
     */
    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> myData = new ArrayList<Map<String, Object>>();

        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(CATEGORY_SAMPLE_CODE);

        PackageManager pm = getPackageManager();
        List<ResolveInfo> list = pm.queryIntentActivities(mainIntent, 0);

        if (null == list)
            return myData;

        int len = list.size();

        for (int i = 0; i < len; i++) {
            ResolveInfo info = list.get(i);
            if (!getPackageName().equalsIgnoreCase(info.activityInfo.packageName)) {
                continue;
            }
            CharSequence labelSeq = info.loadLabel(pm);
            CharSequence description = null;
            if (info.activityInfo.descriptionRes != 0) {
                description = pm.getText(info.activityInfo.packageName,
                        info.activityInfo.descriptionRes, null);
            }

            String label = labelSeq != null ? labelSeq.toString() : info.activityInfo.name;
            addItem(myData,
                    label,
                    activityIntent(info.activityInfo.applicationInfo.packageName,
                            info.activityInfo.name), description);
        }
        return myData;
    }

    private void addItem(List<Map<String, Object>> data, String name, Intent intent,
                         CharSequence description) {
        Map<String, Object> temp = new HashMap<String, Object>();
        temp.put("title", name);
        if (description != null) {
            temp.put("description", description.toString());
        }
        temp.put("intent", intent);
        data.add(temp);
    }

    private Intent activityIntent(String pkg, String componentName) {
        Intent result = new Intent();
        result.setClassName(pkg, componentName);
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Map<String, Object> map = (Map<String, Object>) l.getItemAtPosition(position);

        Intent intent = (Intent) map.get("intent");
        startActivity(intent);
    }

    /**
     * android 6.0 以上需要动态申请权限
     */
    private void initPermission() {
        String permissions[] = {Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        ArrayList<String> toApplyList = new ArrayList<String>();

        for (String perm :permissions){
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
                //进入到这里代表没有权限.

            }
        }
        String tmpList[] = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()){
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // 此处为android 6.0以上动态授权的回调，用户自行实现。
    }


}
