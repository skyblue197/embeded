package info.androidhive.firebase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private StorageReference filepath;  //사진url을 저장하는 변수
    private ImageView imageView;
    private ListView listView;
    private EditText editText;
    private FirebaseDatabase firebaseDatabase;  //firebase의 데이터베이스를 사용하기 위한 변수
    private DatabaseReference databaseReference;
    private StorageReference mStorage;  //firebase의 storage(사진) 데이터베이스를 사용하기 위한 변수
    private ProgressDialog mProgressDialog;
    private static final int GALLERY_INTENT = 2;    //갤러리 intent로 이동하기 위한 변수
    private SimpleAdapter adapter;  //채팅내용, 시간 등을 저장하기 위한 adapter
    List<Map<String,String>> retDataList = new ArrayList<Map<String, String>>();
    String temp_key;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#676767")));

        mStorage = FirebaseStorage.getInstance().getReference();    //firebase의 database를 참조

        imageView = (ImageView)findViewById(R.id.imageView);
        listView = (ListView)findViewById(R.id.listView);
        editText = (EditText)findViewById(R.id.editText);

        temp_key = getIntent().getExtras().get("room_key").toString();  //채팅방으로 intent가 넘어올 때, 채팅방의 key값을 받는다.

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().getRoot().child("room").child(temp_key);    //firebase의 "room" 의 "key" 데이터베이스를 참조

        adapter = new SimpleAdapter(this, retDataList,
                android.R.layout.simple_list_item_2, new String[] { "message", "currentTime" },
                new int[] {android.R.id.text1, android.R.id.text2 }){

            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                text2.setTextColor(Color.GRAY); //메시지를 보낸 날짜와 시간 text의 색을 gray로 바꾼다.
                return view;
            };
        };  //adapter에 채팅 메시지와 보낸 날짜, 시간을 add한다.

        listView.post(new Runnable() {
            @Override
            public void run() {
                listView.setSelection(adapter.getCount()-1);
            }
        });     //채팅방 안에 가장 아래(최신 메시지)로 스크롤을 위치시킨다.

        databaseReference.child("image").addChildEventListener(new ChildEventListener() {   //firebase의 "image"데이터 베이스를 읽어온다
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Picasso.with(ChatActivity.this).load(dataSnapshot.getValue().toString()).into(imageView);   //"image"데이터 베이스에 들어있는 사진uri를 받아와 채팅방에 그린다.
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Picasso.with(ChatActivity.this).load(dataSnapshot.getValue().toString()).into(imageView);
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

        databaseReference.child("message").addChildEventListener(new ChildEventListener() {     //firebase의 "message"데이터 베이스를 읽어온다
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatData chatData = dataSnapshot.getValue(ChatData.class);  //dataSnapshot을 이용하여 "message"를 ChatData객체를 생성한다.
                Map<String, String>data = new HashMap<String, String>();
                data.put("message", chatData.getUserName() + ": " + chatData.getMessage());   //Map에 이름과 메시지 내용을 put한다.
                data.put("currentTime", chatData.getTime());    //Map에 날짜(시간)을 put한다.
                retDataList.add(data);  //listView에 Map을 add한다.
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
        listView.setAdapter(adapter);
    }

    public void pressSendButton(View view)  //채팅방에서 message를 입력하고 send 버튼을 눌렀을 때 불러지는 method
    {
        String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName(); //본인의 접속 이름을 저장한다.
        if (userName == null) {
            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();  //본인의 접속 이메일을 저장한다.
            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date()); //현재 시간을 가져온다.
            ChatData chatData = new ChatData(email, editText.getText().toString(), currentDateTimeString ); //ChatData 클래스에 이메일, 채팅내용, 시간을 담은 객체를 생성한다.
            databaseReference.child("message").push().setValue(chatData);   //firebase의 "message" 데이터베이스에 chatData객체를 push한다.
            editText.setText("");

            listView.post(new Runnable() {
                @Override
                public void run() {
                    listView.setSelection(adapter.getCount()-1);
                }
            });     //가장 아래(최신 메시지)로 스크롤을 위치시킨다.
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
            });     // 채팅방에서 메시지를 보내고 가장 아래(최신 메시지)로 스크롤을 위치 시킨다.
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    public void pressPlusButton(View v)     //채팅방안에서 사진을 보내기 위해 이미지를 클릭했을 때 불려지는 method
    {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_INTENT);     //gallery intent가 시작된다.
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK)
        {
            mProgressDialog.setMessage("Uploading...");
            mProgressDialog.show(); //progressdialog를 보여준다.

            Uri uri = data.getData();   //gallery에서 선택한 사진의 uri를 저장한다.
            filepath = mStorage.child("Photos").child(uri.getPath());   //firebase의 storage 데이터베이스의 경로를 uri로 지정한다.
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                {
                    Toast.makeText(ChatActivity.this, "Upload Done.", Toast.LENGTH_LONG).show();
                    mProgressDialog.dismiss();//    //progressdialog가 사라진다.

                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    ChatData chatData = new ChatData(downloadUri.toString());
                    databaseReference.child("image").setValue(chatData);    //firebase의 "image"데이터베이스에 사진정보를 넣는다.
                }
            });
        }
    }
}
