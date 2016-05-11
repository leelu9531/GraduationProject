package com.example.best8.myapp;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

/*
 * Created by best8 on 2016/5/6.
 */
public class connect {

    connect(){

    }

    public void sentTo(){

    }

    public void connect(){
        //connect with server
        try {
            String url = "59.125.213.198";//php位置
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost method = new HttpPost(url);

            //傳值給PHP
            List<NameValuePair> vars=new ArrayList<NameValuePair>();
            vars.add(new BasicNameValuePair("number", "1"));
            method.setEntity(new UrlEncodedFormEntity(vars, HTTP.UTF_8));


            //
            HttpResponse response = httpclient.execute(method);
            HttpEntity entity = response.getEntity();




            /*
            URL url = new URL("59.125.213.198");//php位置
            HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection(); //對資料庫打開連結
            urlConnection.setRequestMethod("POST");
            urlConnection.connect();//接通資料庫
            InputStream is = urlConnection.getInputStream();//從DB開啟stream

            BufferedReader br = new BufferedReader(new InputStreamReader(is,"utf-8"),8);
            String line = null;
            while((line = br.readLine())!=null){

            }*/

        }catch(Exception e){
            e.printStackTrace();
        }
    }

   public void sendDataToServer(){

   }



}
