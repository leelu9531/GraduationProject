import java.util.ArrayList;
...

public class MainActivity extends Activity {

    final String url = "http://r2internotes.ihost.tw/mysql/test.php";// 要加上"http://" 否則會連線失敗

    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        text = (TextView)findViewById(R.id.textView1);

        new Thread(runnable).start();//啟動執行序runnable
    }

    Handler handler_Success = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("key");//取出key中的字串存入val
            text.setText(val);
        }
    };

    Handler handler_Error = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("key");
            Toast.makeText(getApplicationContext(), val, Toast.LENGTH_LONG).show();
        }
    };

    Handler handler_Nodata = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("key");
            Toast.makeText(getApplicationContext(), val, Toast.LENGTH_LONG).show();
        }
    };

    Runnable runnable = new Runnable(){
        @Override
        public void run() {
            //
            // TODO: http request.
            //
            Message msg = new Message();
            Bundle data = new Bundle();
            msg.setData(data);
            try
            {
                //連線到 url網址
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost method = new HttpPost(url);

                //傳值給PHP
                List< NameValuePair> vars=new ArrayList< NameValuePair>();
                vars.add(new BasicNameValuePair("number","1"));
                method.setEntity(new UrlEncodedFormEntity(vars,HTTP.UTF_8));

                //接收PHP回傳的資料
                HttpResponse response = httpclient.execute(method);
                HttpEntity entity = response.getEntity();

                if(entity != null){
                    data.putString("key",EntityUtils.toString(entity,"utf-8"));//如果成功將網頁內容存入key
                    handler_Success.sendMessage(msg);
                }
                else{
                    data.putString("key","無資料");
                    handler_Nodata.sendMessage(msg);
                }
            }
            catch(Exception e){
                data.putString("key","連線失敗");
                handler_Error.sendMessage(msg);
            }

        }
    };
}