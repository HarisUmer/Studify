package com.handlandmarker.MainPages;

import static com.handlandmarker.LocalDatabase.MyDatabaseHelperKt.parseTimeString;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.studify.R;
import com.handlandmarker.accets.CurrentUser;
import com.handlandmarker.accets.Message;
import com.handlandmarker.accets.My_Group;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class textChat extends AppCompatActivity {

    RecyclerView rv_textchat;
    My_Group Group;
    txtChatAdapter MessageAdapter;
    ArrayList<Message> arr;

    My_Group  findGroup(String id)
    {
        for(My_Group grp : CurrentUser.Groups)
        {
            if(grp.getGroupID().contains(id))
            {
                return grp;
            }
        }
        return  null;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_chat);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String ID = extras.getString("groupID");
            Group = findGroup(ID);
        }


        init();
        Button SendButton = findViewById(R.id.SendMessage);
        EditText Txt = findViewById(R.id.Message_tobe_sent);
        SendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mes = Txt.getText().toString().trim();
                if(!mes.isEmpty())
                {
                    FirebaseHelper fb = new FirebaseHelper();
                    fb.sendMessageToGroup(Group.getGroupID(),mes,Group.getCount());
                    Group.setCount(Group.getCount()+1);
                    EditText text= findViewById(R.id.Message_tobe_sent);
                    text.setText("");
                }
            }
        });
        listenForMessages();
    }
    private void init(){
        rv_textchat=findViewById(R.id.grp_chat_rv);
        arr = new ArrayList<>();
        MessageAdapter = new txtChatAdapter(arr);
        rv_textchat.setAdapter(MessageAdapter);
        rv_textchat.setLayoutManager(new LinearLayoutManager(this));
    }

    private void listenForMessages() {
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.listenForGroupChats(Group.getGroupID(), new FirebaseHelper.OnMessageReceivedListener(){
            @Override
            public void onMessageReceived(String userID, String message, String time, int Count) {
                LocalDateTime time1 = parseTimeString(time);
                if(Count > arr.size()-1) {
                    Message newMessage = new Message(Count, message, userID, CurrentUser.CurrentGroup.getGroupID(), time1);
                    arr.add(newMessage);

                    // Add the message to your adapter

                    MessageAdapter.notifyItemInserted(arr.size() - 1);
                    if(arr.size()-1>10) {
                        rv_textchat.scrollToPosition(arr.size() - 1);
                    }

                }
                // Notify the adapter that new data has been added
                // Scroll the RecyclerView to the bottom to show the latest message
            }

        },Group.getCount());

    }


}