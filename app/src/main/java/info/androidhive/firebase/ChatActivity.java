package info.androidhive.firebase;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChatActivity extends AppCompatActivity {

    private ListView listView;
    private EditText editText;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ArrayAdapter adapter;
    String room_name;
    String temp_key;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        listView = (ListView)findViewById(R.id.listView);
        editText = (EditText)findViewById(R.id.editText);
        temp_key = getIntent().getExtras().get("room_key").toString();
        firebaseDatabase = FirebaseDatabase.getInstance();
//        room_name = getIntent().getExtras().get("room_Name").toString();
        databaseReference = firebaseDatabase.getReference().child(temp_key);
        //databaseReference = firebaseDatabase.getReference();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                android.R.id.text1);

        databaseReference.child("room").child(temp_key).child("message").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatData chatData = dataSnapshot.getValue(ChatData.class);
                adapter.add(chatData.getUserName() + ": " + chatData.getMessage());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                ChatData chatData = dataSnapshot.getValue(ChatData.class);
//                adapter.add(chatData.getUserName() + ": " + chatData.getMessage());
//                adapter.notifyDataSetChanged();
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
        listView.setAdapter(adapter);
    }

    public void pressSendButton(View view)
    {
//        DatabaseReference message_root = databaseReference.child(temp_key);
        String userName = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        ChatData chatData = new ChatData(userName, editText.getText().toString());
        databaseReference.child("room").child(temp_key).child("message").push().setValue(chatData);
        editText.setText("");
    }

}
