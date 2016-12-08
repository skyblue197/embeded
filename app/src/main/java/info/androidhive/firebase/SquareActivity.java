package info.androidhive.firebase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import info.androidhive.firebase.app.AppConfig;

public class SquareActivity extends AppCompatActivity {
    private EditText editTextAdd;
    private int previousBuf=0;
    private int readOrWrite=0;
    private Button button1;

    private int peopleLength;
    private int count=0;
    private static int randomNumber;

    private SimpleAdapter adapter;

    private String name;
    ListView list;
    ChatReceiveThread chatReceiveThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_square);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#676767")));

        button1 = (Button)findViewById(R.id.readOrWrite);
        list = (ListView)findViewById(R.id.listView);
        randomNumber = (int)(Math.random() * 1000 + 1);

        name="명지 " + String.valueOf(randomNumber);

        chatReceiveThread = new ChatReceiveThread();
        editTextAdd = (EditText) findViewById(R.id.address);
        chatReceiveThread.start();
    }

    public void insert(View view){
        String address = editTextAdd.getText().toString();
        AppConfig appConfig = new AppConfig();
        ChatData chatData = new ChatData();

        //insertToDatabase(name, address);
        insertToDatabase(name, address);


    }

    public void exit(View view){
        try {
            chatReceiveThread.stop();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void insertToDatabase(String name, String address){

        class InsertData extends AsyncTask<String, Void, String>{
            ProgressDialog loading;



            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(SquareActivity.this, "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
            }

            @Override
            protected String doInBackground(String... params) {

                try{
                    String name = (String)params[0];
                    String address = (String)params[1];

                    String link="http://219.254.60.31/chat2/write.php";
                    String data  = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8");
                    data += "&" + URLEncoder.encode("msg", "UTF-8") + "=" + URLEncoder.encode(address, "UTF-8");

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write( data );
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while((line = reader.readLine()) != null)
                    {
                        sb.append(line);
                        break;
                    }
                    return sb.toString();
                }
                catch(Exception e){
                    return new String("Exception: " + e.getMessage());
                }
            }
        }

        InsertData task = new InsertData();
        task.execute(name,address);
    }

    class ChatReceiveThread extends Thread{
        String myJSON;


        private static final String TAG_RESULTS="result";
        private static final String TAG_NAME = "name";
        private static final String TAG_ADD ="message";
        JSONArray peoples = null;

        ArrayList<HashMap<String, String>> personList;

        ListView list;

//        public ChatReceiveThread(ListView list1) {
//            list = list1;
//        }

        public void run(){
            while(true){
                try {
                    Thread.sleep(500);
                    list = (ListView) findViewById(R.id.listView);
                    personList = new ArrayList<HashMap<String, String>>();
                    getData("http://219.254.60.31/chat2/GetChat.php");


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        protected void showList(){
            try {
                JSONObject jsonObj = new JSONObject(myJSON);
                peoples = jsonObj.getJSONArray(TAG_RESULTS);

                for (int i = 0; i < peoples.length(); i++) {
                    JSONObject c = peoples.getJSONObject(i);
                    String name = c.getString(TAG_NAME);
                    String address = c.getString(TAG_ADD);

                    HashMap<String, String> persons = new HashMap<String, String>();

                    persons.put(TAG_NAME, name);
                    persons.put(TAG_ADD, address);

                    personList.add(persons);
                }
                adapter = new SimpleAdapter(
                        SquareActivity.this, personList, R.layout.list_item,
                        new String[]{TAG_NAME, TAG_ADD},
                        new int[]{R.id.name, R.id.address}
                );
                if((previousBuf!=peoples.length())&&(readOrWrite==0)) {
                    list.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    previousBuf = peoples.length();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        public void getData(String url){
            class GetDataJSON extends AsyncTask<String, Void, String>{

                @Override
                protected String doInBackground(String... params) {

                    String uri = params[0];

                    BufferedReader bufferedReader = null;
                    try {
                        URL url = new URL(uri);
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        StringBuilder sb = new StringBuilder();

                        bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                        String json;
                        while((json = bufferedReader.readLine())!= null){
                                sb.append(json + "\n");
                        }
                        return sb.toString().trim();

                    }catch(Exception e){
                        return null;
                    }



                }

                @Override
                protected void onPostExecute(String result){
                    myJSON=result;
                    showList();
                }
            }
            GetDataJSON g = new GetDataJSON();
            g.execute(url);
        }
    }
    
    public void readOrWrite(View view){
        if(readOrWrite==0)
            readOrWrite = 1;
        else
            readOrWrite = 0;
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}


