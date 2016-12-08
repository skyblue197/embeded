package info.androidhive.firebase;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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
import android.widget.TimePicker;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ChatRoomActivity extends AppCompatActivity {

    private EditText roomeditText;
    private ListView listView;
    private FirebaseDatabase firebaseDatabase;  //firebase의 데이터베이스를 참조하기 위한 변수
    private DatabaseReference databaseReference;
    private ArrayAdapter adapter;   //채팅방 목록을 저장하기 위한 adapter 변수
    private ArrayAdapter user_list_adapter; //전체 user 목록을 저장하기 위한 adapter 변수
    final static ArrayList<String> items = new ArrayList<String>();
    private Dialog dialog;
    private Dialog userDialog;
    private ListView userDialoglistView;

    private Spinner spinner1;
    private Spinner spinner2;
    private Spinner Contentspinner;
    private Spinner Majorspinner;
    private Spinner Timespinner;
    private Intent intent;
    private Button roomButton;

    private String message;
    private String roomName;
    private String cspinner;
    private String mspinner;
    private String tspinner;
    private String userName;
    private String Key;

    private int checked;
    private int month;
    private int day;
    private int hour;
    private int minute;

    ChatRoomData chatRoomData;
    private ArrayList<String> childKeys = new ArrayList<>();    //방 목록을 구분하기 위해 각 방에 대한 Key값을 담아놓기 위한 변수
    ArrayAdapter adapter1;
    ArrayAdapter adapter2;

    UserData userData;

    CalendarView calendar;
    Calendar cal;
    TimePicker timePicker;

    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#676767")));

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        user_list_adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);

        spinner1 = (Spinner) findViewById(R.id.spinner1);
        adapter1 = ArrayAdapter.createFromResource(
                this, R.array.contents_array, android.R.layout.simple_spinner_item);    //adapter1에 Contents에 해당하는 item 목록을 저장
        adapter1.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner1.setAdapter(adapter1);  //Spinner1에 adapter1을 적용
        spinner1.setOnItemSelectedListener(new MyOnItemSelectedListener());    //Spinner1의 item을 클릭했을 때 값을 넘기기 위한 listener를 붙인다.

        adapter2 = ArrayAdapter.createFromResource(
                this, R.array.department_array, android.R.layout.simple_spinner_item);  //adapter2에 Major에 해당하는 item 목록을 저장
        adapter2.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        spinner2.setAdapter(adapter2);  //Spinner2에 adapter2를 적용
        spinner2.setOnItemSelectedListener(new MyOnItemSelectedListener());     //Spinner2의 item을 클릭했을 때 값을 넘기기 위한 listener를 붙인다.

        listView = (ListView) findViewById(R.id.listView);

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.activity_addroom);
        dialog.setTitle("Create Room");

        userDialog = new Dialog(this);
        userDialog.setContentView(R.layout.activity_user_list);
        userDialog.setTitle("User");
        userDialog.setCanceledOnTouchOutside(false);
        userDialog.setCancelable(false);

        userDialoglistView = (ListView) userDialog.findViewById(R.id.user_list_listView);   //user목록을 보여주기 위한 dialog

        roomButton = (Button) dialog.findViewById(R.id.makeButton);
        roomeditText = (EditText) dialog.findViewById(R.id.roomeditText);
        Contentspinner = (Spinner) dialog.findViewById(R.id.MySpinner1);
        Majorspinner = (Spinner) dialog.findViewById(R.id.MySpinner2);

        calendar = (CalendarView)dialog.findViewById(R.id.calendarView2);   //CalendarView의 달력을 가져오기 위한 변수
        timePicker = (TimePicker)dialog.findViewById(R.id.timePicker);


        cal = Calendar.getInstance();
        month = cal.get(Calendar.MONTH)+1;
        day = cal.get(Calendar.DATE);
        hour = cal.get(Calendar.HOUR);
        minute = cal.get(Calendar.MINUTE);



        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().getRoot();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice,
                items);     //방 목록중 채팅방을 선택할 수 있도록 adapter 설정

        databaseReference.child("room").addChildEventListener(new ChildEventListener() {    //firebase의 "message"데이터 베이스를 읽어온다
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                chatRoomData = dataSnapshot.getValue(ChatRoomData.class);   //dataSnapshot을 이용하여 "message"를 ChatData객체를 생성한다.
                if (dataSnapshot.exists()) {
                    childKeys.add(dataSnapshot.getKey());   //각 방의 key값을 childKey 변수에 add한다.
                    adapter.add(chatRoomData.getRoomName() + "   (" + chatRoomData.getContents() + "/ " + chatRoomData.getMajor() + "/ "  + chatRoomData.getMonth() + "월 " + chatRoomData.getDay() + "일"+ "/ " + chatRoomData.getHour() + "시" + chatRoomData.getMinute() + " 분)");
                    // adapter에 방 이름, 방에서 선택한 Contents/Major, 날짜, 시간의 정보를 add한다.
                    adapter.notifyDataSetChanged(); //adapter에 들어있는 값이 변화할 때 마다 알려준다.
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

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {  //CalendarView에서 선택한 날짜에 대한 월,일에 대한 값
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int i, int i1, int i2) {
                month = i1 + 1;
                day = i2;
            }
        });

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                hour = i;
                minute = i1;
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
                .setName("Main Page")
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    public void pressMapButton(View view)
    {     // 검색한 위치가 표시된 구글 맵을 열어주는 버튼
        try {
            EditText et = (EditText) findViewById(R.id.editText);   // 입력한 텍스트
            String s = et.getText().toString();             // 텍스트를 받아서 String으로 바꿔준다.

            if (TextUtils.isEmpty(s))
            {
                Toast.makeText(getApplicationContext(), "장소를 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent2 = new Intent(this, MapsActivity.class);
            Geocoder geo = new Geocoder(this, Locale.KOREAN);       // Gedcoder를 사용하기 위함
            List<Address> addr = geo.getFromLocationName(s, 2);     // 입력한 텍스트 스트링 주소를 받아와서 리스트에 저장
            double latitude = addr.get(0).getLatitude();        // 경도 값
            double longitude = addr.get(0).getLongitude();      // 위도 값
            String la = String.valueOf(latitude);           // 경도 값을 String값으로 만들어 줌
            String lo = String.valueOf(longitude);          // 위도 값을 String값으로 만들어 줌
            intent2.putExtra("latitude", la);
            intent2.putExtra("longitude", lo);
            startActivity(intent2);                 // 경도, 위도 값을 담은 intent2를 쏴줌.
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "유효한 장소를 입력하세요.", Toast.LENGTH_SHORT).show();   //유효하지 않은 장소를 입력했을 때 예외처리
            return;
        }
    }

    public void pressUserDialogBackButton(View view)    //user목록을 보여주는 dialog에서 back버튼을 눌렀을 때 불러지는 method
    {
        userDialog.dismiss();   //userDialog가 사라진다.
        user_list_adapter.clear();  //버튼을 누를때 마다 다시 그리기 때문에 adapter의 내용을 clear한다.
        user_list_adapter.notifyDataSetChanged();   //adapter의 변화를 알린다.
    }

    public void pressUserButton(View view)  //모든 user를 보기위한 버튼을 눌렀을 때 불러지는 method
    {
        userDialog.show();

        databaseReference.child("user").addChildEventListener(new ChildEventListener() {    //firebase의 "user"데이터 베이스를 읽어온다
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                userData = dataSnapshot.getValue(UserData.class);   //dataSnapshot을 이용하여 ChatRoomData 객체를 생성한다.
                if(dataSnapshot.exists())
                {
                    user_list_adapter.add(userData.getUserName());  //adapter에 user의 이름을 추가한다.
                    user_list_adapter.notifyDataSetChanged();   //adapter의 변화를 알린다.
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
    }   //대기방에서 방 만들기 버튼을 누르면 dialog를 보여준다.

    public void pressmakeButton(View view)  //dialog 안에서 make버튼을 눌렀을 때 불러지는 method
    {
        roomName = roomeditText.getText().toString();
        cspinner = String.valueOf(Contentspinner.getSelectedItem());
        mspinner = String.valueOf(Majorspinner.getSelectedItem());
        String username = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        if (TextUtils.isEmpty(roomName))
        {
            Toast.makeText(getApplicationContext(), "방 제목을 입력하세요", Toast.LENGTH_SHORT).show();
            return;
        }

        ChatRoomData chatRoomData = new ChatRoomData(roomName, cspinner, mspinner, month, day, hour, minute, username);
        //방을 만들 때 입력한 방 이름, Contents/Major, 날짜 등의 내용을 담은 ChatRoomData객체를 생성한다.
        databaseReference.child("room").push().setValue(chatRoomData);  //firebase의 "room"데이터베이스에 chatRoomData 객체를 push한다.
        roomeditText.setText("");
        dialog.dismiss();   //dialog가 사라진다.
    }

    public void pressEnterButton(View view) //방 목록중, 방을 선택하여 들어가기 버튼을 눌렀을 때 불러지는 method
    {
        int count, checked;
        count = adapter.getCount();

        if(count > 0)
        {
            checked = listView.getCheckedItemPosition();
            if(checked > -1 && checked < count)
            {
                Intent intent = new Intent(this, ChatActivity.class);
                intent.putExtra("room_key", childKeys.get(checked));    //선택한 방의 Key값을 intent값으로 넘긴다.
                startActivity(intent);
                adapter.notifyDataSetChanged();
            }
        }
    }


    public void pressDeleteButton(View view)    //방 삭제버튼을 눌렀을 때 불러지는 method
    {
        int count;
        count = adapter.getCount();

        if (count > 0) {
            checked = listView.getCheckedItemPosition();    //listView에서 유저가 체크한 목록을 받는다
            if (checked > -1 && checked < count) {
                adapter.getItem(checked);   //adapter로부터 체크한 방을 받아온다.
                roomName = listView.getAdapter().getItem(checked).toString();   //방 이름을 listView의 adapter에서 삭제한다.
                message = String.valueOf(checked);
                Key = childKeys.get(checked);   //childKey로 부터 받은 해당 방의 key값을 저장한다.

                userName = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                databaseReference.child("room").child(Key).addListenerForSingleValueEvent(new ValueEventListener() {    //firebase의 "room"의 Key값에 해다아는 데이터 베이스를 읽어온다
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ChatRoomData userUser = dataSnapshot.getValue(ChatRoomData.class);  //dataSnapshot을 이용하여 ChatRoomData객체를 생성한다.
                        if(userName.equals(userUser.getUserName()))
                        {
                            items.remove(checked);
                            adapter.remove(checked);    //adapter에서 선택된 방 목록을 제거한다.
                            databaseReference.child("room").child(Key).removeValue();   //firebase의 데이터베이스에서 방을 삭제한다.
                            childKeys.remove(checked);  //key값을 모아놓은 ChildKey에서 선택된 방의 key값을 삭제한다.
                            databaseReference.child(message).removeValue(); //firebase의 데이터베이스에서 방에 들어있는 message를 삭제한다.
                            listView.clearChoices();    //listView에서 선택한 것을 clear한다.
                            Toast.makeText(ChatRoomActivity.this, "방을 삭제하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(ChatRoomActivity.this, "방장만 삭제할 수 있습니다.", Toast.LENGTH_SHORT).show();
                            //방을 만든 사람이 아니라면 방을 지울 수 없기 때문에 Toast Message를 보여준다.
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

    public void pressSettingButton(View view)   //setting 버튼을 눌렀을 때 불러지는 method
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    class MyOnItemSelectedListener implements AdapterView.OnItemSelectedListener    //Spinner에서 선택한 값들에 대한 값을 listener로 넘겨주기 위한 inner class
    {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
        {
            if(spinner1.getSelectedItem().equals("영화") && spinner2.getSelectedItem().equals("컴공"))
            {
                for(int j=0; j<listView.getCount(); j++)
                {
                    if(listView.getItemAtPosition(j).toString().contains(("영화")) && listView.getItemAtPosition(j).toString().contains(("컴공")))
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.parseColor("#b6afa1"));
                    }

                    else
                    {
                        if (listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.parseColor("#f2e6cf"));
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
                            listView.getChildAt(j).setBackgroundColor(Color.parseColor("#b6afa1"));
                    }
                    else
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.parseColor("#f2e6cf"));
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
                            listView.getChildAt(j).setBackgroundColor(Color.parseColor("#b6afa1"));
                    }
                    else
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.parseColor("#f2e6cf"));
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
                            listView.getChildAt(j).setBackgroundColor(Color.parseColor("#b6afa1"));
                    }
                    else
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.parseColor("#f2e6cf"));
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
                            listView.getChildAt(j).setBackgroundColor(Color.parseColor("#b6afa1"));
                    }
                    else
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.parseColor("#f2e6cf"));
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
                            listView.getChildAt(j).setBackgroundColor(Color.parseColor("#b6afa1"));
                    }
                    else
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.parseColor("#f2e6cf"));
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
                            listView.getChildAt(j).setBackgroundColor(Color.parseColor("#b6afa1"));
                    }
                    else
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.parseColor("#f2e6cf"));
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
                            listView.getChildAt(j).setBackgroundColor(Color.parseColor("#b6afa1"));
                    }
                    else
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.parseColor("#f2e6cf"));
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
                            listView.getChildAt(j).setBackgroundColor(Color.parseColor("#b6afa1"));
                    }
                    else
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.parseColor("#f2e6cf"));
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
                            listView.getChildAt(j).setBackgroundColor(Color.parseColor("#b6afa1"));
                    }
                    else
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.parseColor("#f2e6cf"));
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
                            listView.getChildAt(j).setBackgroundColor(Color.parseColor("#b6afa1"));
                    }
                    else
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.parseColor("#f2e6cf"));
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
                            listView.getChildAt(j).setBackgroundColor(Color.parseColor("#b6afa1"));
                    }
                    else
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.parseColor("#f2e6cf"));
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
                            listView.getChildAt(j).setBackgroundColor(Color.parseColor("#b6afa1"));
                    }
                    else
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.parseColor("#f2e6cf"));
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
                            listView.getChildAt(j).setBackgroundColor(Color.parseColor("#b6afa1"));
                    }
                    else
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.parseColor("#f2e6cf"));
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
                            listView.getChildAt(j).setBackgroundColor(Color.parseColor("#b6afa1"));
                    }
                    else
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.parseColor("#f2e6cf"));
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
                            listView.getChildAt(j).setBackgroundColor(Color.parseColor("#b6afa1"));
                    }
                    else
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.parseColor("#f2e6cf"));
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
                            listView.getChildAt(j).setBackgroundColor(Color.parseColor("#b6afa1"));
                    }
                    else
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.parseColor("#f2e6cf"));
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
                            listView.getChildAt(j).setBackgroundColor(Color.parseColor("#b6afa1"));
                    }
                    else
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.parseColor("#f2e6cf"));
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
                            listView.getChildAt(j).setBackgroundColor(Color.parseColor("#b6afa1"));
                    }
                    else
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.parseColor("#f2e6cf"));
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
                            listView.getChildAt(j).setBackgroundColor(Color.parseColor("#b6afa1"));
                    }
                    else
                    {
                        if(listView.getChildAt(j) != null)
                            listView.getChildAt(j).setBackgroundColor(Color.parseColor("#f2e6cf"));
                    }
                }
            }
        }


        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            listView.setBackgroundColor(Color.parseColor("#f2e6cf"));
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

