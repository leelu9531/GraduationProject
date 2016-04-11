package com.example.best8.myapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiManager;
import android.net.wifi.ScanResult;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {

    private WifiManager wifi;
    private ListView listView;
    private TextView textStatus;
    private Button startBtn;
    private Button stopBtn;
    private int size = 0 ;
    private List<ScanResult> resultList;
    private ArrayList arrayList = new ArrayList();
    private ArrayList allWifiList = new ArrayList();
    private SimpleAdapter adapter;

    private Handler UI_handler = new Handler();
    private Handler mThreadHandler ;
    private HandlerThread mThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        textStatus= (TextView) findViewById(R.id.textStatus);
        listView  = (ListView) findViewById(R.id.wifiListView);
        startBtn  = (Button) findViewById(R.id.startBtn);
        stopBtn   = (Button) findViewById(R.id.stopBtn);

        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);


        mThread = new HandlerThread("Name");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());

        /*check wifi turn on*/
        checkWifiState();


        /*Start Btn clicked*/
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter = new SimpleAdapter(MainActivity.this, arrayList, R.layout.list, new String[] {"detectTime","SSID","power","MAC"}, new int[] {R.id.detectTime,R.id.SSID, R.id.power, R.id.MAC});
                listView.setAdapter(adapter);

                mThreadHandler.post(detectWifiLoop);
            }
        });

        /*Stop Btn clicked*/
        stopBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                textStatus.setText("STOP");

                if (mThreadHandler != null) {
                    mThreadHandler.removeCallbacks(detectWifiLoop);
                }

                //解聘工人 (關閉Thread)
                if (mThread != null) {
                    mThread.quit();
                }

                allWifiList.clear();
                size = 0 ;
            }
        });

    }


    private Runnable detectWifiLoop = new Runnable() {

        public void run(){

            allWifiList.clear();

            wifi.startScan();
            resultList = wifi.getScanResults();
            size = resultList.size();

            //取得現在系統時間
            Time nowTime = new Time("GMT+8");
            nowTime.setToNow();

            try {
                size = size - 1;
                while(size >= 0 ){
                    HashMap item = new HashMap();

                    String time;
                    if (nowTime.minute < 10 )
                            time = nowTime.hour + ":0" + nowTime.minute;
                    else
                            time = nowTime.hour + ":"+nowTime.minute;

                    item.put("detectTime",time);
                    item.put("SSID", resultList.get(size).SSID); //SSID
                    item.put("power", resultList.get(size).level+"dBm"); //RSSI
                    item.put("MAC", resultList.get(size).BSSID); //MAC
                    allWifiList.add(item);
                    size--;
                }

                Collections.sort(allWifiList, new Comparator<HashMap>() {
                    public int compare(HashMap lhs, HashMap rhs) {
                        return ((String) lhs.get("power")).compareTo((String) rhs.get("power"));
                    }
                });


                arrayList.add(allWifiList.get(0));
                allWifiList.clear();

                UI_handler.post(UI_display);
                mThreadHandler.postDelayed(this,5000);

            }catch(Exception e) {}
        }
    };


    private Runnable UI_display = new Runnable() {
        @Override
        public void run() {

            startBtn.setText("click!");

            textStatus.setText("第" + arrayList.size() + "次掃描");

            //更新listView內容
            adapter.notifyDataSetChanged();
        }
    };

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
                    Toast.makeText(getApplicationContext(),"wifi is disabled",Toast.LENGTH_LONG).show();
                }
            });
            dialog.show();
        }
    }

    protected void onDestroy() {

        super.onDestroy();
        allWifiList.clear();
        arrayList.clear();
        //移除工人上的工作
        if (mThreadHandler != null) {
            mThreadHandler.removeCallbacks(detectWifiLoop);
        }

        if (mThread != null) {
            mThread.quit();
        }

    }

}