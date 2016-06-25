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

    // GUI ITEM
    private ListView listView;
    private TextView textStatus;
    private Button startBtn;
    private Button stopBtn;
    private SimpleAdapter adapter;

    // WIFI
    private WifiManager wifi;
    private int size = 0 ;
    private List<ScanResult> resultList;
    private ArrayList arrayList = new ArrayList();
    private ArrayList detectWifiList = new ArrayList();

    // Thread
    private Handler UI_handler = new Handler();
    private Handler mThreadHandler ;
    private HandlerThread mThread;

    static private int counter = 0 ;

private int temp=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        textStatus = (TextView) findViewById(R.id.textView2);
        startBtn   = (Button) findViewById(R.id.startBtn);
        stopBtn    = (Button) findViewById(R.id.stopBtn);
        listView   = (ListView) findViewById(R.id.listView1);

        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        mThread = new HandlerThread("Name");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());

        /*check wifi turn on*/
        checkWifiState();

        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!  <<這邊應該就會送資料了阿QQQQQQQQQQ
        connect cn = new connect();
        cn.toSendData();

        /*Start Btn clicked*/
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter = new SimpleAdapter(MainActivity.this, arrayList, R.layout.list, new String[] {"detectTime","SSID","power","MAC"}, new int[] {R.id.detectTime,R.id.SSID, R.id.power, R.id.MAC});
                listView.setAdapter(adapter);
                mThreadHandler.post(detectWifiLoop);
            }
        });

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

                detectWifiList.clear();
                size = 0 ;
            }
        });
    }

    private Runnable detectWifiLoop = new Runnable() {

        public void run(){

            detectWifiList.clear();

            wifi.startScan();
            resultList = wifi.getScanResults();
            size = resultList.size();

            try {
                int index = 0 ;
                while(index < size){
                    // Get the info we need from resultList and add to detectWifiList
                    HashMap item = new HashMap();
                    item.put("detectTime",timeStr());
                    item.put("SSID", resultList.get(index).SSID); //SSID
                    item.put("power", resultList.get(index).level+"dBm"); //RSSI
                    item.put("MAC", resultList.get(index).BSSID); //MAC
                    detectWifiList.add(item);
                    index++ ;
                }

                //sort detectWifiList by power
                Collections.sort(detectWifiList, new Comparator<HashMap>() {
                    public int compare(HashMap lhs, HashMap rhs) {
                        return ((String) lhs.get("power")).compareTo((String) rhs.get("power"));
                    }
                });

                // send the strong signal to server



                // Add the strong signal to arrayList and clear detectWifiList;
                arrayList.add(detectWifiList.get(0));
                detectWifiList.clear();

                counter ++ ;
                UI_handler.post(UI_display);
                mThreadHandler.postDelayed(this,5000);

            }catch(Exception e) {}
        }
    };


    public String timeStr (){
        Time nowTime = new Time("GMT+8");
        nowTime.setToNow();
        String time;
        if (nowTime.minute < 10 )
            time = nowTime.hour + ":0" + nowTime.minute;
        else
            time = nowTime.hour + ":"+nowTime.minute;

        return time;
    }
    private Runnable UI_display = new Runnable() {
        @Override
        public void run() {

            startBtn.setText("click!");

            textStatus.setText("" + counter);

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
        //移除工人上的工作
        if (mThreadHandler != null) {
            mThreadHandler.removeCallbacks(detectWifiLoop);
        }

        //解聘工人 (關閉Thread)
        if (mThread != null) {
            mThread.quit();
        }
    }

}