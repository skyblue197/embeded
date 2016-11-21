package info.androidhive.firebase;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChatRoomActivity extends AppCompatActivity {

    private EditText editText;
    private EditText roomeditText;
    private ListView listView;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ArrayAdapter adapter;
    Dialog dialog;
    Button roomButton;
    Spinner spinner1;
    Spinner spinner2;
    Spinner Contentspinner;
    Spinner Majorspinner;

    String roomKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);


        spinner1 = (Spinner)findViewById(R.id.spinner1);
        ArrayAdapter adapter1 = ArrayAdapter.createFromResource(
                this, R.array.contents_array, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner1.setAdapter(adapter1);

        spinner2 = (Spinner)findViewById(R.id.spinner2);
        ArrayAdapter adapter2 = ArrayAdapter.createFromResource(
                this, R.array.department_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner2.setAdapter(adapter2);


        listView = (ListView)findViewById(R.id.listView);

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.activity_addroom);
        dialog.setTitle("Create Room");

        roomButton  = (Button) dialog.findViewById(R.id.makeButton);
        roomeditText = (EditText) dialog.findViewById(R.id.roomeditText);
        Contentspinner = (Spinner) dialog.findViewById(R.id.MySpinner1);
        Majorspinner = (Spinner) dialog.findViewById(R.id.MySpinner2);


        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                android.R.id.text1);

        databaseReference.child("room").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatRoomData chatRoomData = dataSnapshot.getValue(ChatRoomData.class);
                if(dataSnapshot.exists()) {
                    roomKey = dataSnapshot.getKey();

                    adapter.add(chatRoomData.getRoomName());
                    adapter.add(chatRoomData.getContents());
                    adapter.add(chatRoomData.getMajor());

                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s)
            {
//                ChatRoomData chatRoomData = dataSnapshot.getValue(ChatRoomData.class);
//                if(dataSnapshot.exists())
//                {
//                    roomKey = dataSnapshot.getKey();
//                    adapter.add(chatRoomData.getRoomName());
//
//                    adapter.notifyDataSetChanged();
//                }
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                //intent.putExtra("room_key", roomKey);
                Object a = adapterView.getItemAtPosition(i);
                adapterView.getSelectedItemPosition();
                long b = adapterView.getId();
                //String.valueOf(a);
                intent.putExtra("room_key",adapter.getItemId(i));
                startActivity(intent);
            }
        });
    }

    public void pressmakeRoom(View view)
    {
        dialog.show();
    }

    public void pressmakeButton(View view)
    {
        String roomName = roomeditText.getText().toString();
        String cspinner = String.valueOf(Contentspinner.getSelectedItem());
        String mspinner = String.valueOf(Majorspinner.getSelectedItem());

        ChatRoomData chatRoomData = new ChatRoomData(roomName, cspinner, mspinner);
        databaseReference.child("room").push().setValue(chatRoomData);
        roomeditText.setText("");
        dialog.dismiss();
    }
}
