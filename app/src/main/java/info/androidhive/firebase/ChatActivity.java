package info.androidhive.firebase;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

    private StorageReference filepath;
    private ImageView imageView;
    private ListView listView;
    private ListView list_listView;
    private EditText editText;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ArrayAdapter list_adapter;
    private StorageReference mStorage;
    private ProgressDialog mProgressDialog;
    private static final int GALLERY_INTENT = 2;
    private SimpleAdapter adapter;
    List<Map<String,String>> retDataList = new ArrayList<Map<String, String>>();
    NotificationManager nm;
    Notification.Builder mBuilder;
    String temp_key;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);



        mStorage = FirebaseStorage.getInstance().getReference();
        mProgressDialog = new ProgressDialog(this);

        imageView = (ImageView)findViewById(R.id.imageView);
        listView = (ListView)findViewById(R.id.listView);
        editText = (EditText)findViewById(R.id.editText);

        temp_key = getIntent().getExtras().get("room_key").toString();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().getRoot().child("room").child(temp_key);

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.activity_chat_list);
        dialog.setTitle("Create Room");
        list_listView = (ListView)dialog.findViewById(R.id.listView);

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

        listView.post(new Runnable() {
            @Override
            public void run() {
                listView.setSelection(adapter.getCount()-1);
            }
        });



        list_adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                android.R.id.text1);

        databaseReference.child("image").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Picasso.with(ChatActivity.this).load(dataSnapshot.getValue().toString()).into(imageView);
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

        databaseReference.child("message").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatData chatData = dataSnapshot.getValue(ChatData.class);
//                adapter.add(chatData.getUserName() + ": " + chatData.getMessage());
//                adapter.notifyDataSetChanged();
                Map<String, String>data = new HashMap<String, String>();
                data.put("title", chatData.getUserName() + ": " + chatData.getMessage());
                data.put("comment", chatData.getTime());
                retDataList.add(data);
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

    public void pressBackButton(View view)
    {
        dialog.dismiss();
        list_adapter.clear();
        list_adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        list_adapter.clear();
        list_adapter.notifyDataSetChanged();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    public void pressPlusButton(View v)
    {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_INTENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK)
        {
            mProgressDialog.setMessage("Uploading...");
            mProgressDialog.show();

            Uri uri = data.getData();
            filepath = mStorage.child("Photos").child(uri.getPath());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                {
                    Toast.makeText(ChatActivity.this, "Upload Done.", Toast.LENGTH_LONG).show();
                    mProgressDialog.dismiss();

                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    ChatData chatData = new ChatData(downloadUri.toString());
                    databaseReference.child("image").setValue(chatData);
                }
            });
        }
    }

}
