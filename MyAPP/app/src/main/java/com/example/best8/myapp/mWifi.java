package com.example.best8.myapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class mWifi extends AppCompatActivity {

    private WifiManager wifi;
    private List<ScanResult> resultList;
    private ArrayList detectWifiList = new ArrayList();
    private WifiInfo xlist[] ;
    private int size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
    }

    public void checkWifiState(){
        if(wifi.isWifiEnabled() == false){
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Remind");
            dialog.setMessage("Your Wi-Fi not turn on, turn on?");
            dialog.setIcon(android.R.drawable.ic_dialog_info);
            dialog.setCancelable(false);
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    wifi.setWifiEnabled(true);
                    Toast.makeText(getApplicationContext(), "wifi is disabled", Toast.LENGTH_LONG).show();
                }
            });
            dialog.show();
        }
    }

    private Runnable detectWifiLoop = new Runnable() {

        public void run(){

            //allWifiList.clear();

            wifi.startScan();
            resultList = wifi.getScanResults();
            size = resultList.size();

            xlist = new WifiInfo[size];


            //取得現在系統時間
            Time nowTime = new Time("GMT+8");
            nowTime.setToNow();



            try {

                int index = 0 ;

                while( index < size ){
                    HashMap item = new HashMap();

                    String time;
                    if (nowTime.minute < 10 )
                        time = nowTime.hour + ":0" + nowTime.minute;
                    else
                        time = nowTime.hour + ":"+nowTime.minute;

                    // Get the info we need from resultList and add to detectWifiList
                    item.put("detectTime",time);
                    item.put("SSID", resultList.get(index).SSID);          //SSID
                    item.put("power", resultList.get(index).level + "dBm");  //RSSI
                    item.put("MAC", resultList.get(index).BSSID);          //MAC Address
                    detectWifiList.add(item);

                    //
                    WifiInfo temp = new WifiInfo(time,resultList.get(index).SSID,resultList.get(index).level,resultList.get(index).BSSID);
                    xlist[index] = temp;

                    index++;
                }

                //Sort detect result by power (RSSI)
                Collections.sort(detectWifiList, new Comparator<HashMap>() {
                    public int compare(HashMap lhs, HashMap rhs) {
                        return ((String) lhs.get("power")).compareTo((String) rhs.get("power"));
                    }
                });

                // 傳回最強者
                //arrayList.add(allWifiList.get(0));
                Intent intent = new Intent();
                intent.setClass(mWifi.this,MainActivity.class);

                //new一個Bundle物件，並將要傳遞的資料傳入
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("detectTime",detectWifiList);
                bundle.putStringArrayList("SSID",detectWifiList);
                bundle.putStringArrayList("power",detectWifiList);
                bundle.putStringArrayList("MAC",detectWifiList);



                //將Bundle物件傳給intent
                intent.putExtras(bundle);

                //切換Activity
                startActivity(intent);



                //Bundle bundle = new Bundle();
                //bundle.putDouble("age",age );//傳遞Double
                //bundle.putString("name",name);//傳遞String


                detectWifiList.clear();


                /*
                counter ++ ;
                UI_handler.post(UI_display);
                Log.v("HEYHU", "delayed.....");
                mThreadHandler.postDelayed(this,5000);
                */

            }catch(Exception e) {}
        }
    };

}
