package info.androidhive.firebase;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
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

import java.util.ArrayList;

public class ChatRoomActivity extends AppCompatActivity {

    private EditText roomeditText;
    private ListView listView;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ArrayAdapter adapter;
    final static ArrayList<String> items = new ArrayList<String>();
    Dialog dialog;
    Button roomButton;
    Spinner spinner1;
    Spinner spinner2;
    Spinner Contentspinner;
    Spinner Majorspinner;
    Intent intent;

    String roomKey;
    String roomName;
    String message;

    ChatRoomData chatRoomData;
    private ArrayList<String> childKeys = new ArrayList<>();
    ArrayAdapter adapter1;
    ArrayAdapter adapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);


        spinner1 = (Spinner) findViewById(R.id.spinner1);
        adapter1 = ArrayAdapter.createFromResource(
                this, R.array.contents_array, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner1.setAdapter(adapter1);
        spinner1.setOnItemSelectedListener(new MyOnItemSelectedListener());

        adapter2 = ArrayAdapter.createFromResource(
                this, R.array.department_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        spinner2.setAdapter(adapter2);
        spinner2.setOnItemSelectedListener(new MyOnItemSelectedListener());

        listView = (ListView) findViewById(R.id.listView);

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.activity_addroom);
        dialog.setTitle("Create Room");

        roomButton = (Button) dialog.findViewById(R.id.makeButton);
        roomeditText = (EditText) dialog.findViewById(R.id.roomeditText);
        Contentspinner = (Spinner) dialog.findViewById(R.id.MySpinner1);
        Majorspinner = (Spinner) dialog.findViewById(R.id.MySpinner2);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().getRoot();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice,
                items);

        databaseReference.child("room").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                chatRoomData = dataSnapshot.getValue(ChatRoomData.class);
                if (dataSnapshot.exists()) {
                    childKeys.add(dataSnapshot.getKey());
                    adapter.add(chatRoomData.getRoomName() + "   (" + chatRoomData.getContents() + ", " + chatRoomData.getMajor() + ")");
                    adapter.notifyDataSetChanged();
                    listView.setAdapter(adapter);


//                    spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                        @Override
//                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                            if (adapterView.getSelectedItem().equals(("Contents"))) {
//                                listView.setAdapter(adapter);
//                            } else if (adapterView.getSelectedItem().equals(("축구"))) {
//                                for (int j = 0; j < listView.getCount(); j++) {
//                                    if (listView.getItemAtPosition(j).toString().contains("축구")) {
//                                        listView.getChildAt(j).setBackgroundColor(Color.CYAN);
//                                    } else {
//                                        listView.getChildAt(j).setBackgroundColor(Color.WHITE);
//                                    }
//                                }
//                            } else if (adapterView.getSelectedItem().equals(("농구"))) {
//                                listView.setBackgroundColor(Color.WHITE);
//                                for (int j = 0; j < listView.getCount(); j++) {
//                                    if (listView.getItemAtPosition(j).toString().contains("농구")) {
//                                        listView.getChildAt(j).setBackgroundColor(Color.CYAN);
//                                    } else {
//                                        listView.getChildAt(j).setBackgroundColor(Color.WHITE);
//                                    }
//                                }
//                            } else if (adapterView.getSelectedItem().equals(("야구"))) {
//                                for (int j = 0; j < listView.getCount(); j++) {
//                                    if (listView.getItemAtPosition(j).toString().contains("야구")) {
//                                        listView.getChildAt(j).setBackgroundColor(Color.CYAN);
//                                    } else {
//                                        listView.getChildAt(j).setBackgroundColor(Color.WHITE);
//                                    }
//                                }
//                            } else if (adapterView.getSelectedItem().equals(("배구"))) {
//                                for (int j = 0; j < listView.getCount(); j++) {
//                                    if (listView.getItemAtPosition(j).toString().contains("배구")) {
//                                        listView.getChildAt(j).setBackgroundColor(Color.CYAN);
//                                    } else {
//                                        listView.getChildAt(j).setBackgroundColor(Color.WHITE);
//                                    }
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onNothingSelected(AdapterView<?> adapterView) {
//
//                        }
//                    });
//
//                    spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                        @Override
//                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                            if (adapterView.getSelectedItem().equals(("major"))) {
//                                listView.setAdapter(adapter);
//                            } else if (adapterView.getSelectedItem().equals(("컴공"))) {
//                                for (int j = 0; j < listView.getCount(); j++) {
//                                    if (listView.getItemAtPosition(j).toString().contains("컴공")) {
//                                        listView.getChildAt(j).setBackgroundColor(Color.CYAN);
//                                    } else {
//                                        listView.getChildAt(j).setBackgroundColor(Color.WHITE);
//                                    }
//                                }
//                            } else if (adapterView.getSelectedItem().equals(("화공"))) {
//                                listView.setBackgroundColor(Color.WHITE);
//                                for (int j = 0; j < listView.getCount(); j++) {
//                                    if (listView.getItemAtPosition(j).toString().contains("화공")) {
//                                        listView.getChildAt(j).setBackgroundColor(Color.CYAN);
//                                    } else {
//                                        listView.getChildAt(j).setBackgroundColor(Color.WHITE);
//                                    }
//                                }
//                            } else if (adapterView.getSelectedItem().equals(("기계"))) {
//                                for (int j = 0; j < listView.getCount(); j++) {
//                                    if (listView.getItemAtPosition(j).toString().contains("기계")) {
//                                        listView.getChildAt(j).setBackgroundColor(Color.CYAN);
//                                    } else {
//                                        listView.getChildAt(j).setBackgroundColor(Color.WHITE);
//                                    }
//                                }
//                            } else if (adapterView.getSelectedItem().equals(("체육"))) {
//                                for (int j = 0; j < listView.getCount(); j++) {
//                                    if (listView.getItemAtPosition(j).toString().contains("체육")) {
//                                        listView.getChildAt(j).setBackgroundColor(Color.CYAN);
//                                    } else {
//                                        listView.getChildAt(j).setBackgroundColor(Color.WHITE);
//                                    }
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onNothingSelected(AdapterView<?> adapterView) {
//
//                        }
//                    });



                }
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

    public void pressEnterButton(View view)
    {
        int count, checked;
        count = adapter.getCount();

        if(count > 0)
        {
            checked = listView.getCheckedItemPosition();
            if(checked > -1 && checked < count)
            {
                Intent intent = new Intent(this, ChatActivity.class);
                intent.putExtra("room_key", childKeys.get(checked));
                startActivity(intent);

                adapter.notifyDataSetChanged();
            }
        }
    }

    public void pressDeleteButton(View view)
    {
        int count, checked;
        count = adapter.getCount();

        if (count > 0) {
            checked = listView.getCheckedItemPosition();
            if (checked > -1 && checked < count) {
                adapter.getItem(checked);
                roomName = listView.getAdapter().getItem(checked).toString();
                message = String.valueOf(checked);
                items.remove(checked);
                String Key = childKeys.get(checked);
                databaseReference.child("room").child(Key).removeValue();

                childKeys.remove(checked);
                databaseReference.child(message).removeValue();
                listView.clearChoices();
                adapter.notifyDataSetChanged();
            }
        }
    }

    public void pressSettingButton(View view)
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    class MyOnItemSelectedListener implements AdapterView.OnItemSelectedListener
    {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
        {
            if(spinner1.getSelectedItem().equals("Contents") && spinner2.getSelectedItem().equals("major"))
            {
//                listView.setAdapter(adapter);
                listView.setBackgroundColor(Color.WHITE);
                adapter1.notifyDataSetChanged();
                adapter2.notifyDataSetChanged();
            }

            else if(spinner1.getSelectedItem().equals("축구") && spinner2.getSelectedItem().equals("컴공"))
            {
                for(int j=0; j<listView.getCount(); j++)
                {
                    if(listView.getItemAtPosition(j).toString().contains(("축구")) && listView.getItemAtPosition(j).toString().contains(("컴공")))
                    {
                        listView.getChildAt(j).setBackgroundColor(Color.CYAN);
                        adapter1.notifyDataSetChanged();
                        adapter2.notifyDataSetChanged();
                    }
                    else
                    {
                        listView.getChildAt(j).setBackgroundColor(Color.WHITE);
                        adapter1.notifyDataSetChanged();
                        adapter2.notifyDataSetChanged();
                    }
                }
            }
            else if(spinner1.getSelectedItem().equals("축구") && spinner2.getSelectedItem().equals("화공") )
            {
                for(int j=0; j<listView.getCount(); j++)
                {
                    if(listView.getItemAtPosition(j).toString().contains(("축구")) && listView.getItemAtPosition(j).toString().contains(("화공")))
                    {
                        listView.getChildAt(j).setBackgroundColor(Color.CYAN);
                        adapter1.notifyDataSetChanged();
                        adapter2.notifyDataSetChanged();
                    }
                    else
                    {
                        listView.getChildAt(j).setBackgroundColor(Color.WHITE);
                        adapter1.notifyDataSetChanged();
                        adapter2.notifyDataSetChanged();
                    }
                }
            }
            else if(adapterView.getSelectedItem().equals("축구") && adapterView.getSelectedItem().equals("기계") )
            {
                for(int j=0; j<listView.getCount(); j++)
                {
                    if(listView.getItemAtPosition(j).toString().contains(("축구")) && listView.getItemAtPosition(j).toString().contains(("기계")))
                    {
                        listView.getChildAt(j).setBackgroundColor(Color.CYAN);
                        adapter1.notifyDataSetChanged();
                        adapter2.notifyDataSetChanged();
                    }
                    else
                    {
                        listView.getChildAt(j).setBackgroundColor(Color.WHITE);
                        adapter1.notifyDataSetChanged();
                        adapter2.notifyDataSetChanged();
                    }
                }
            }
            else if(adapterView.getSelectedItem().equals("축구") && adapterView.getSelectedItem().equals("체육") )
            {
                for(int j=0; j<listView.getCount(); j++)
                {
                    if(listView.getItemAtPosition(j).toString().contains(("축구")) && listView.getItemAtPosition(j).toString().contains(("체육")))
                    {
                        listView.getChildAt(j).setBackgroundColor(Color.CYAN);
                        adapter1.notifyDataSetChanged();
                        adapter2.notifyDataSetChanged();
                    }
                    else
                    {
                        listView.getChildAt(j).setBackgroundColor(Color.WHITE);
                        adapter1.notifyDataSetChanged();
                        adapter2.notifyDataSetChanged();
                    }
                }
            }
            else if(spinner1.getSelectedItem().equals("농구") && spinner2.getSelectedItem().equals("컴공"))
            {
                for(int j=0; j<listView.getCount(); j++)
                {
                    if(listView.getItemAtPosition(j).toString().contains(("농구")) && listView.getItemAtPosition(j).toString().contains(("컴공")))
                    {
                        listView.getChildAt(j).setBackgroundColor(Color.CYAN);
                        adapter1.notifyDataSetChanged();
                        adapter2.notifyDataSetChanged();
                    }
                    else
                    {
                        listView.getChildAt(j).setBackgroundColor(Color.WHITE);
                        adapter1.notifyDataSetChanged();
                        adapter2.notifyDataSetChanged();
                    }
                }
            }
            else if(spinner1.getSelectedItem().equals("농구") && spinner2.getSelectedItem().equals("화공") )
            {
                for(int j=0; j<listView.getCount(); j++)
                {
                    if(listView.getItemAtPosition(j).toString().contains(("농구")) && listView.getItemAtPosition(j).toString().contains(("화공")))
                    {
                        listView.getChildAt(j).setBackgroundColor(Color.CYAN);
                        adapter1.notifyDataSetChanged();
                        adapter2.notifyDataSetChanged();
                    }
                    else
                    {
                        listView.getChildAt(j).setBackgroundColor(Color.WHITE);
                        adapter1.notifyDataSetChanged();
                        adapter2.notifyDataSetChanged();
                    }
                }
            }
            else if(adapterView.getSelectedItem().equals("농구") && adapterView.getSelectedItem().equals("기계") )
            {
                for(int j=0; j<listView.getCount(); j++)
                {
                    if(listView.getItemAtPosition(j).toString().contains(("농구")) && listView.getItemAtPosition(j).toString().contains(("기계")))
                    {
                        listView.getChildAt(j).setBackgroundColor(Color.CYAN);
                        adapter1.notifyDataSetChanged();
                        adapter2.notifyDataSetChanged();
                    }
                    else
                    {
                        listView.getChildAt(j).setBackgroundColor(Color.WHITE);
                        adapter1.notifyDataSetChanged();
                        adapter2.notifyDataSetChanged();
                    }
                }
            }
            else if(adapterView.getSelectedItem().equals("농구") && adapterView.getSelectedItem().equals("체육") )
            {
                for(int j=0; j<listView.getCount(); j++)
                {
                    if(listView.getItemAtPosition(j).toString().contains(("농구")) && listView.getItemAtPosition(j).toString().contains(("체육")))
                    {
                        listView.getChildAt(j).setBackgroundColor(Color.CYAN);
                        adapter1.notifyDataSetChanged();
                        adapter2.notifyDataSetChanged();
                    }
                    else
                    {
                        listView.getChildAt(j).setBackgroundColor(Color.WHITE);
                        adapter1.notifyDataSetChanged();
                        adapter2.notifyDataSetChanged();
                    }
                }
            }
            else if(spinner1.getSelectedItem().equals("야구") && spinner2.getSelectedItem().equals("컴공"))
            {
                for(int j=0; j<listView.getCount(); j++)
                {
                    if(listView.getItemAtPosition(j).toString().contains(("야구")) && listView.getItemAtPosition(j).toString().contains(("컴공")))
                    {
                        listView.getChildAt(j).setBackgroundColor(Color.CYAN);
                        adapter1.notifyDataSetChanged();
                        adapter2.notifyDataSetChanged();
                    }
                    else
                    {
                        listView.getChildAt(j).setBackgroundColor(Color.WHITE);
                        adapter1.notifyDataSetChanged();
                        adapter2.notifyDataSetChanged();
                    }
                }
            }
            else if(spinner1.getSelectedItem().equals("야구") && spinner2.getSelectedItem().equals("화공") )
            {
                for(int j=0; j<listView.getCount(); j++)
                {
                    if(listView.getItemAtPosition(j).toString().contains(("야구")) && listView.getItemAtPosition(j).toString().contains(("화공")))
                    {
                        listView.getChildAt(j).setBackgroundColor(Color.CYAN);
                        adapter1.notifyDataSetChanged();
                        adapter2.notifyDataSetChanged();
                    }
                    else
                    {
                        listView.getChildAt(j).setBackgroundColor(Color.WHITE);
                        adapter1.notifyDataSetChanged();
                        adapter2.notifyDataSetChanged();
                    }
                }
            }
            else if(adapterView.getSelectedItem().equals("야구") && adapterView.getSelectedItem().equals("기계") )
            {
                for(int j=0; j<listView.getCount(); j++)
                {
                    if(listView.getItemAtPosition(j).toString().contains(("야구")) && listView.getItemAtPosition(j).toString().contains(("기계")))
                    {
                        listView.getChildAt(j).setBackgroundColor(Color.CYAN);
                        adapter1.notifyDataSetChanged();
                        adapter2.notifyDataSetChanged();
                    }
                    else
                    {
                        listView.getChildAt(j).setBackgroundColor(Color.WHITE);
                        adapter1.notifyDataSetChanged();
                        adapter2.notifyDataSetChanged();
                    }
                }
            }
            else if(adapterView.getSelectedItem().equals("야구") && adapterView.getSelectedItem().equals("체육") )
            {
                for(int j=0; j<listView.getCount(); j++)
                {
                    if(listView.getItemAtPosition(j).toString().contains(("야구")) && listView.getItemAtPosition(j).toString().contains(("체육")))
                    {
                        listView.getChildAt(j).setBackgroundColor(Color.CYAN);
                        adapter1.notifyDataSetChanged();
                        adapter2.notifyDataSetChanged();
                    }
                    else
                    {
                        listView.getChildAt(j).setBackgroundColor(Color.WHITE);
                        adapter1.notifyDataSetChanged();
                        adapter2.notifyDataSetChanged();
                    }
                }
            }
            else if(spinner1.getSelectedItem().equals("배구") && spinner2.getSelectedItem().equals("컴공"))
            {
                for(int j=0; j<listView.getCount(); j++)
                {
                    if(listView.getItemAtPosition(j).toString().contains(("배구")) && listView.getItemAtPosition(j).toString().contains(("컴공")))
                    {
                        listView.getChildAt(j).setBackgroundColor(Color.CYAN);
                        adapter1.notifyDataSetChanged();
                        adapter2.notifyDataSetChanged();
                    }
                    else
                    {
                        listView.getChildAt(j).setBackgroundColor(Color.WHITE);
                        adapter1.notifyDataSetChanged();
                        adapter2.notifyDataSetChanged();
                    }
                }
            }
            else if(spinner1.getSelectedItem().equals("배구") && spinner2.getSelectedItem().equals("화공") )
            {
                for(int j=0; j<listView.getCount(); j++)
                {
                    if(listView.getItemAtPosition(j).toString().contains(("배구")) && listView.getItemAtPosition(j).toString().contains(("화공")))
                    {
                        listView.getChildAt(j).setBackgroundColor(Color.CYAN);
                        adapter1.notifyDataSetChanged();
                        adapter2.notifyDataSetChanged();
                    }
                    else
                    {
                        listView.getChildAt(j).setBackgroundColor(Color.WHITE);
                        adapter1.notifyDataSetChanged();
                        adapter2.notifyDataSetChanged();
                    }
                }
            }
            else if(adapterView.getSelectedItem().equals("배구") && adapterView.getSelectedItem().equals("기계") )
            {
                for(int j=0; j<listView.getCount(); j++)
                {
                    if(listView.getItemAtPosition(j).toString().contains(("배구")) && listView.getItemAtPosition(j).toString().contains(("기계")))
                    {
                        listView.getChildAt(j).setBackgroundColor(Color.CYAN);
                        adapter1.notifyDataSetChanged();
                        adapter2.notifyDataSetChanged();
                    }
                    else
                    {
                        listView.getChildAt(j).setBackgroundColor(Color.WHITE);
                        adapter1.notifyDataSetChanged();
                        adapter2.notifyDataSetChanged();
                    }
                }
            }
            else if(adapterView.getSelectedItem().equals("배구") && adapterView.getSelectedItem().equals("체육") )
            {
                for(int j=0; j<listView.getCount(); j++)
                {
                    if(listView.getItemAtPosition(j).toString().contains(("배구")) && listView.getItemAtPosition(j).toString().contains(("체육")))
                    {
                        listView.getChildAt(j).setBackgroundColor(Color.CYAN);
                        adapter1.notifyDataSetChanged();
                        adapter2.notifyDataSetChanged();
                    }
                    else
                    {
                        listView.getChildAt(j).setBackgroundColor(Color.WHITE);
                        adapter1.notifyDataSetChanged();
                        adapter2.notifyDataSetChanged();
                    }
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            listView.setBackgroundColor(Color.WHITE);
            adapter1.notifyDataSetChanged();
            adapter2.notifyDataSetChanged();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.clear();
        adapter.notifyDataSetChanged();
    }
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//    }

    @Override
    protected void onPause() {
        intent = new Intent(this,ChatRoomBgmService.class);
        stopService(intent);
        super.onPause();
    }

    @Override
    protected void onStop() {
        intent = new Intent(this,ChatRoomBgmService.class);
        stopService(intent);
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        intent = new Intent(this,ChatRoomBgmService.class);
        startService(intent);
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        intent = new Intent(this,ChatRoomBgmService.class);
        stopService(intent);
        super.onBackPressed();
    }

    public void onRestart(){
        super.onRestart();
    }
}
