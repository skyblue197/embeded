package info.androidhive.firebase;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChatRoomActivity extends AppCompatActivity {

    private EditText roomeditText;
    private ListView listView;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ArrayAdapter adapter;
    ArrayAdapter user_list_adapter;
    final static ArrayList<String> items = new ArrayList<String>();
    Dialog dialog;
    Dialog userDialog;
    ListView userDialoglistView;
    Button roomButton;
    Spinner spinner1;
    Spinner spinner2;
    Spinner Contentspinner;
    Spinner Majorspinner;
    Spinner Timespinner;
    Intent intent;

//    String roomName;
    String message;

    String roomName;
    String cspinner;
    String mspinner;
    String tspinner;
    String userName;
    String Key;

    int checked;
    int month;
    int day;

    ChatRoomData chatRoomData;
    private ArrayList<String> childKeys = new ArrayList<>();
    ArrayAdapter adapter1;
    ArrayAdapter adapter2;

    UserData userData;
    CalendarView calendar;

    private GoogleApiClient client;

    public void pressMapButton(View view) {
        try {
            EditText et = (EditText) findViewById(R.id.editText);
            String s = et.getText().toString();

            Intent intent2 = new Intent(this, MapsActivity.class);
            Geocoder geo = new Geocoder(this, Locale.KOREAN);
            List<Address> addr = geo.getFromLocationName(s, 2);
            double latitude = addr.get(0).getLatitude();
            double longitude = addr.get(0).getLongitude();
            String la = String.valueOf(latitude);
            String lo = String.valueOf(longitude);
            intent2.putExtra("latitude", la);
            intent2.putExtra("longitude", lo);
            startActivity(intent2);
        } catch (IOException e) { }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        user_list_adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);

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

        userDialog = new Dialog(this);
        userDialog.setContentView(R.layout.activity_user_list);
        userDialog.setTitle("User");
        userDialog.setCanceledOnTouchOutside(false);
        userDialog.setCancelable(false);

        userDialoglistView = (ListView) userDialog.findViewById(R.id.user_list_listView);

        roomButton = (Button) dialog.findViewById(R.id.makeButton);
        roomeditText = (EditText) dialog.findViewById(R.id.roomeditText);
        Contentspinner = (Spinner) dialog.findViewById(R.id.MySpinner1);
        Majorspinner = (Spinner) dialog.findViewById(R.id.MySpinner2);
        Timespinner = (Spinner) dialog.findViewById(R.id.MySpinner3);

        calendar = (CalendarView)dialog.findViewById(R.id.calendarView2);

//        cal = Calendar.getInstance();

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
                    adapter.add(chatRoomData.getRoomName() + "   (" + chatRoomData.getContents() + "/ " + chatRoomData.getMajor() + "/ "  + chatRoomData.getMonth() + "월 " + chatRoomData.getDay() + "일"+ "/ " + chatRoomData.getTime() + "시)");
                    adapter.notifyDataSetChanged();
                    listView.setAdapter(adapter);
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

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int i, int i1, int i2) {
                month = i1 + 1;
                day = i2;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    public void pressUserDialogBackButton(View view)
    {
        userDialog.dismiss();
        user_list_adapter.clear();
        user_list_adapter.notifyDataSetChanged();
    }

    public void pressUserButton(View view)
    {
        userDialog.show();

        databaseReference.child("user").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                userData = dataSnapshot.getValue(UserData.class);
                if(dataSnapshot.exists())
                {
                    user_list_adapter.add(userData.getUserName());
                    user_list_adapter.notifyDataSetChanged();
                    userDialoglistView.setAdapter(user_list_adapter);
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
        roomName = roomeditText.getText().toString();
        cspinner = String.valueOf(Contentspinner.getSelectedItem());
        mspinner = String.valueOf(Majorspinner.getSelectedItem());
        tspinner = String.valueOf(Timespinner.getSelectedItem());
        String username = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        ChatRoomData chatRoomData = new ChatRoomData(roomName, cspinner, mspinner, month, day, tspinner, username);
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
        int count;
        count = adapter.getCount();

        if (count > 0) {
            checked = listView.getCheckedItemPosition();
            if (checked > -1 && checked < count) {
                adapter.getItem(checked);
                roomName = listView.getAdapter().getItem(checked).toString();
                message = String.valueOf(checked);
                Key = childKeys.get(checked);

                userName = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                databaseReference.child("room").child(Key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ChatRoomData userUser = dataSnapshot.getValue(ChatRoomData.class);
                        if(userName.equals(userUser.getUserName()))
                        {
                            items.remove(checked);
                            adapter.remove(checked);
                            databaseReference.child("room").child(Key).removeValue();
                            childKeys.remove(checked);
                            databaseReference.child(message).removeValue();
                            listView.clearChoices();
                        }
                        else
                        {
                            Toast.makeText(ChatRoomActivity.this, "don't have authority!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
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
            if(spinner1.getSelectedItem().equals("기타") && spinner2.getSelectedItem().equals("기타"))
            {
                for(int j=0; j<listView.getCount(); j++)
                {
                    if(listView.getItemAtPosition(j).toString().contains(("기타")) && listView.getItemAtPosition(j).toString().contains(("기타")))
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.CYAN);
                    }

                    else
                    {
                        if (listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.WHITE);
                    }
                }
            }

            else if(spinner1.getSelectedItem().equals("영화") && spinner2.getSelectedItem().equals("컴공"))
            {
                for(int j=0; j<listView.getCount(); j++)
                {
                    if(listView.getItemAtPosition(j).toString().contains(("영화")) && listView.getItemAtPosition(j).toString().contains(("컴공")))
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.CYAN);
                    }

                    else
                    {
                        if (listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.WHITE);
                    }
                }
            }
            else if(spinner1.getSelectedItem().equals("영화") && spinner2.getSelectedItem().equals("정통") )
            {
                for(int j=0; j<listView.getCount(); j++)
                {
                    if(listView.getItemAtPosition(j).toString().contains(("영화")) && listView.getItemAtPosition(j).toString().contains(("정통")))
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.CYAN);
                    }
                    else
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.WHITE);
                    }
                }
            }
            else if(spinner1.getSelectedItem().equals("영화") && spinner2.getSelectedItem().equals("교통") )
            {
                for(int j=0; j<listView.getCount(); j++)
                {
                    if(listView.getItemAtPosition(j).toString().contains(("영화")) && listView.getItemAtPosition(j).toString().contains(("교통")))
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.CYAN);
                    }
                    else
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.WHITE);
                    }
                }
            }
            else if(spinner1.getSelectedItem().equals("영화") && spinner2.getSelectedItem().equals("뮤지컬") )
            {
                for(int j=0; j<listView.getCount(); j++)
                {
                    if(listView.getItemAtPosition(j).toString().contains(("영화")) && listView.getItemAtPosition(j).toString().contains(("뮤지컬")))
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.CYAN);
                    }
                    else
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.WHITE);
                    }
                }
            }
            else if(spinner1.getSelectedItem().equals("경기관람") && spinner2.getSelectedItem().equals("컴공"))
            {
                for(int j=0; j<listView.getCount(); j++)
                {
                    if(listView.getItemAtPosition(j).toString().contains(("경기관람")) && listView.getItemAtPosition(j).toString().contains(("컴공")))
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.CYAN);
                    }
                    else
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.WHITE);
                    }
                }
            }
            else if(spinner1.getSelectedItem().equals("경기관람") && spinner2.getSelectedItem().equals("정통") )
            {
                for(int j=0; j<listView.getCount(); j++)
                {
                    if(listView.getItemAtPosition(j).toString().contains(("경기관람")) && listView.getItemAtPosition(j).toString().contains(("정통")))
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.CYAN);
                    }
                    else
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.WHITE);
                    }
                }
            }
            else if(spinner1.getSelectedItem().equals("경기관람") && spinner2.getSelectedItem().equals("교통") )
            {
                for(int j=0; j<listView.getCount(); j++)
                {
                    if(listView.getItemAtPosition(j).toString().contains(("경기관람")) && listView.getItemAtPosition(j).toString().contains(("교통")))
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.CYAN);
                    }
                    else
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.WHITE);
                    }
                }
            }
            else if(spinner1.getSelectedItem().equals("경기관람") && spinner2.getSelectedItem().equals("뮤지컬") )
            {
                for(int j=0; j<listView.getCount(); j++)
                {
                    if(listView.getItemAtPosition(j).toString().contains(("경기관람")) && listView.getItemAtPosition(j).toString().contains(("뮤지컬")))
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.CYAN);
                    }
                    else
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.WHITE);
                    }
                }
            }
            else if(spinner1.getSelectedItem().equals("공연") && spinner2.getSelectedItem().equals("컴공"))
            {
                for(int j=0; j<listView.getCount(); j++)
                {
                    if(listView.getItemAtPosition(j).toString().contains(("공연")) && listView.getItemAtPosition(j).toString().contains(("컴공")))
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.CYAN);
                    }
                    else
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.WHITE);
                    }
                }
            }
            else if(spinner1.getSelectedItem().equals("공연") && spinner2.getSelectedItem().equals("정통") )
            {
                for(int j=0; j<listView.getCount(); j++)
                {
                    if(listView.getItemAtPosition(j).toString().contains(("공연")) && listView.getItemAtPosition(j).toString().contains(("정통")))
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.CYAN);
                    }
                    else
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.WHITE);
                    }
                }
            }
            else if(spinner1.getSelectedItem().equals("공연") && spinner2.getSelectedItem().equals("교통") )
            {
                for(int j=0; j<listView.getCount(); j++)
                {
                    if(listView.getItemAtPosition(j).toString().contains(("공연")) && listView.getItemAtPosition(j).toString().contains(("교통")))
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.CYAN);
                    }
                    else
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.WHITE);
                    }
                }
            }
            else if(spinner1.getSelectedItem().equals("공연") && spinner2.getSelectedItem().equals("뮤지컬") )
            {
                for(int j=0; j<listView.getCount(); j++)
                {
                    if(listView.getItemAtPosition(j).toString().contains(("공연")) && listView.getItemAtPosition(j).toString().contains(("뮤지컬")))
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.CYAN);
                    }
                    else
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.WHITE);
                    }
                }
            }
            else if(spinner1.getSelectedItem().equals("운동") && spinner2.getSelectedItem().equals("컴공"))
            {
                for(int j=0; j<listView.getCount(); j++)
                {
                    if(listView.getItemAtPosition(j).toString().contains(("운동")) && listView.getItemAtPosition(j).toString().contains(("컴공")))
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.CYAN);
                    }
                    else
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.WHITE);
                    }
                }
            }
            else if(spinner1.getSelectedItem().equals("운동") && spinner2.getSelectedItem().equals("정통") )
            {
                for(int j=0; j<listView.getCount(); j++)
                {
                    if(listView.getItemAtPosition(j).toString().contains(("운동")) && listView.getItemAtPosition(j).toString().contains(("정통")))
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.CYAN);
                    }
                    else
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.WHITE);
                    }
                }
            }
            else if(spinner1.getSelectedItem().equals("운동") && spinner2.getSelectedItem().equals("교통") )
            {
                for(int j=0; j<listView.getCount(); j++)
                {
                    if(listView.getItemAtPosition(j).toString().contains(("운동")) && listView.getItemAtPosition(j).toString().contains(("교통")))
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.CYAN);
                    }
                    else
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.WHITE);
                    }
                }
            }
            else if(spinner1.getSelectedItem().equals("운동") && spinner2.getSelectedItem().equals("뮤지컬") )
            {
                for(int j=0; j<listView.getCount(); j++)
                {
                    if(listView.getItemAtPosition(j).toString().contains(("운동")) && listView.getItemAtPosition(j).toString().contains(("뮤지컬")))
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.CYAN);
                    }
                    else
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.WHITE);
                    }
                }
            }
            else if(spinner1.getSelectedItem().equals("스터디") && spinner2.getSelectedItem().equals("컴공"))
            {
                for(int j=0; j<listView.getCount(); j++)
                {
                    if(listView.getItemAtPosition(j).toString().contains(("스터디")) && listView.getItemAtPosition(j).toString().contains(("컴공")))
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.CYAN);
                    }
                    else
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.WHITE);
                    }
                }
            }
            else if(spinner1.getSelectedItem().equals("스터디") && spinner2.getSelectedItem().equals("정통") )
            {
                for(int j=0; j<listView.getCount(); j++)
                {
                    if(listView.getItemAtPosition(j).toString().contains(("스터디")) && listView.getItemAtPosition(j).toString().contains(("정통")))
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.CYAN);
                    }
                    else
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.WHITE);
                    }
                }
            }
            else if(spinner1.getSelectedItem().equals("스터디") && spinner2.getSelectedItem().equals("교통") )
            {
                for(int j=0; j<listView.getCount(); j++)
                {
                    if(listView.getItemAtPosition(j).toString().contains(("스터디")) && listView.getItemAtPosition(j).toString().contains(("교통")))
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.CYAN);
                    }
                    else
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.WHITE);
                    }
                }
            }
            else if(spinner1.getSelectedItem().equals("스터디") && spinner2.getSelectedItem().equals("뮤지컬") )
            {
                for(int j=0; j<listView.getCount(); j++)
                {
                    if(listView.getItemAtPosition(j).toString().contains(("스터디")) && listView.getItemAtPosition(j).toString().contains(("뮤지컬")))
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.CYAN);
                    }
                    else
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.WHITE);
                    }
                }
            }
        }


        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            listView.setBackgroundColor(Color.WHITE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.clear();
        adapter.notifyDataSetChanged();
        user_list_adapter.clear();
        user_list_adapter.notifyDataSetChanged();
    }

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

        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
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

        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
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

