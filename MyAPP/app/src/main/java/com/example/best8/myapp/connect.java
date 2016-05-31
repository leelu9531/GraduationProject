package com.example.best8.myapp;

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
import org.apache.http.util.EntityUtils;

import javax.crypto.Mac;

public class connect {
    public void sentTo(){

    }

    public void toSendData(){
        //connect with server
        try{

            String uriAPI = "http://59.125.213.198/collect.php";//php位置
            //HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpRequest  = new HttpPost(uriAPI);

            //傳值給PHP
            List<NameValuePair> datas=new ArrayList<NameValuePair>();
            datas.add(new BasicNameValuePair("MAC", "MACtest"));
            datas.add(new BasicNameValuePair("RSSI","RSSITest"));
            datas.add(new BasicNameValuePair("SSID", "SSIDTest"));

        try {
                httpRequest.setEntity(new UrlEncodedFormEntity(datas, HTTP.UTF_8));
                HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);

                if (httpResponse.getStatusLine().getStatusCode()==200){
                    String strResult = EntityUtils.toString(httpResponse.getEntity());
                    System.out.println(strResult);
                }
                else
                    System.out.println("QQ");

            }catch(Exception e){
                e.printStackTrace();
            }


            //HttpResponse response = httpclient.execute(method);


        }catch(Exception e){
            e.printStackTrace();
        }
    }



}
