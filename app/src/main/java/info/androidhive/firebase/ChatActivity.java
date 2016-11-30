package info.androidhive.firebase;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import static android.R.id.list;

public class ChatActivity extends AppCompatActivity {

    private ListView listView;
    private EditText editText;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    //private ArrayAdapter adapter;
    String temp_key;
    final static ArrayList<String> list = new ArrayList<String>();
    SimpleAdapter adapter;
    List<Map<String, String>> retDataList = new ArrayList<Map<String, String>>();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        listView = (ListView)findViewById(R.id.listView);
        editText = (EditText)findViewById(R.id.editText);

        temp_key = getIntent().getExtras().get("room_key").toString();





        listView.post(new Runnable() {
            @Override
            public void run() {
                listView.setSelection(adapter.getCount()-1);
            }
        });

        firebaseDatabase = FirebaseDatabase.getInstance();

        databaseReference = firebaseDatabase.getReference().getRoot().child("room").child(temp_key);





        databaseReference.child("message").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatData chatData = dataSnapshot.getValue(ChatData.class);
                Map<String, String> data = new HashMap<String, String>();
                data.put("title",chatData.getUserName() + ": " + chatData.getMessage());
                data.put("comment",chatData.getTime());
                retDataList.add(data);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        adapter = new SimpleAdapter(this, retDataList,
                android.R.layout.simple_list_item_2, new String[] { "title", "comment" },
                new int[] {android.R.id.text1, android.R.id.text2 }){

            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                text2.setTextColor(Color.GRAY);
                return view;
            };
        };

        listView.setAdapter(adapter);
    }

    public void pressSendButton(View view)
    {
        String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        if (userName == null) {
            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
            ChatData chatData = new ChatData(email, editText.getText().toString(), currentDateTimeString );
            databaseReference.child("message").push().setValue(chatData);
            editText.setText("");

            listView.post(new Runnable() {
                @Override
                public void run() {
                    listView.setSelection(adapter.getCount()-1);
                }
            });
        } else {
            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
            ChatData chatData = new ChatData(userName, editText.getText().toString(), currentDateTimeString );
            databaseReference.child("message").push().setValue(chatData);
            editText.setText("");

            listView.post(new Runnable() {
                @Override
                public void run() {
                    listView.setSelection(adapter.getCount()-1);
                }
            });
        }
    }

}
