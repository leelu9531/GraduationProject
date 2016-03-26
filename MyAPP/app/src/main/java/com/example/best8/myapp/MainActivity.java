package com.example.best8.myapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.ScanResult;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Menu;
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
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    WifiManager wifi;
    ListView listView;
    TextView textStatus;
    Button startBtn;
    int size = 0 ;
    List<ScanResult> resultList;
    ArrayList arrayList = new ArrayList();
    SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        textStatus = (TextView) findViewById(R.id.textView2);
        startBtn = (Button) findViewById(R.id.startBtn);
        listView = (ListView) findViewById(R.id.listView1);

        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        /*check wifi turn on*/
        /*if wifi is disable , turn on */
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


        this.adapter = new SimpleAdapter(MainActivity.this, arrayList, R.layout.list, new String[] {"SSID","power","MAC"}, new int[] {R.id.SSID, R.id.power, R.id.MAC});
        listView.setAdapter(adapter);

        registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                resultList = wifi.getScanResults();
                size = resultList.size();
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        /*Start Btn clicked*/
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                arrayList.clear();
                wifi.startScan();

                Toast.makeText(MainActivity.this , "Scanning..." + size, Toast.LENGTH_LONG);

                try {
                    size = size - 1 ;
                    while(size >= 0 ){
                        HashMap item = new HashMap();
                        item.put("SSID", resultList.get(size).SSID);
                        item.put("power", resultList.get(size).level+"dBm");
                        item.put("MAC", resultList.get(size).BSSID);
                        arrayList.add(item);
                        size--;
                        adapter.notifyDataSetChanged();
                    }


                    Collections.sort(arrayList, new Comparator (){

                        public int compare(HashMap l, HashMap r) {
                            return ((String) lhs.get("power")).compareTo((String) rhs.get("power"));
                        }
                    });



                    //textStatus.setText(arrayList.get(0).SSID);
                }catch(Exception e) {

                }
            }
        });



    }

/*
    @Override
    public boolean onCreateOptionMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main , menu);
        return true;
    }
*/
}
