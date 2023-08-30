package com.example.tdapiandroidjavaexample;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.tdapiandroidjavaexample.telegram.TelegramClient;
import com.example.tdapiandroidjavaexample.telegram.telegramRecyclerView.DataLine;
import com.example.tdapiandroidjavaexample.telegram.telegramRecyclerView.DataLineAdapter;

import org.drinkless.td.libcore.telegram.TdApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private SharedPreferences sharedPreferences;
    private TelegramClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences(getString(R.string.shared_preferences_name), Context.MODE_PRIVATE);

        List<DataLine> lines = getLines();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        DataLineAdapter adapter = new DataLineAdapter(this, lines, sharedPreferences);
        recyclerView.setAdapter(adapter);
    }
    private List<DataLine> getLines(){
        List<DataLine> l = new ArrayList<>();
        String rawApiHash = sharedPreferences.getString(getString(R.string.raw_api_hash), null);
        String rawApiId = sharedPreferences.getString(getString(R.string.raw_api_id), null);
        String rawPhoneNumber = sharedPreferences.getString(getString(R.string.raw_phone_number), null);
        String rawPublicChatName = sharedPreferences.getString(getString(R.string.raw_public_chat_name), null);

        l.add(new DataLine(this, DataLine.TelegramAttr.API_HASH, rawApiHash));
        l.add(new DataLine(this, DataLine.TelegramAttr.API_ID, rawApiId));
        l.add(new DataLine(this, DataLine.TelegramAttr.PHONE_NUMBER, rawPhoneNumber));
        l.add(new DataLine(this, DataLine.TelegramAttr.PUBLIC_CHAT_NAME, rawPublicChatName));
        return l;
    }


    public void onCreateClient(View view) {
        String rawApiHash = sharedPreferences.getString(getString(R.string.raw_api_hash), null);
        String rawApiId = sharedPreferences.getString(getString(R.string.raw_api_id), null);
        String rawPhoneNumber = sharedPreferences.getString(getString(R.string.raw_phone_number), null);
        String rawPublicChatName = sharedPreferences.getString(getString(R.string.raw_public_chat_name), null);
        List<String> parametersToFill = new ArrayList<>();
        if(rawApiHash == null) parametersToFill.add(getString(R.string.raw_api_hash));
        if(rawApiId == null) parametersToFill.add(getString(R.string.raw_api_id));
        if(rawPhoneNumber == null) parametersToFill.add(getString(R.string.raw_phone_number));
        if(rawPublicChatName == null) parametersToFill.add(getString(R.string.raw_public_chat_name));

        if(parametersToFill.size() != 0){
            AlertDialog alert = new AlertDialog.Builder(this)
                    .setTitle("Fill parameters")
                    .setMessage("Fill empty parameters: " + Arrays.toString(parametersToFill.toArray()))
                    .setPositiveButton("Ok", (dialogInterface, i) -> dialogInterface.cancel())
                    .create();
            alert.show();
            return;
        }

        client = new TelegramClient(this, Integer.parseInt(rawApiId), rawApiHash, rawPhoneNumber, rawPublicChatName);
        Toast.makeText(this, "Client created.", Toast.LENGTH_SHORT).show();
    }

    public void onSetParameters(View view) {
        client.sendParameters();
    }

    public void onCheckDatabaseEncryptionKey(View view) {
        client.sendDatabaseEncryptionKey();
    }
    public void onCheckAuthState(View view) {
        client.getAuthorizationState();
    }
    public void onSetPhoneNumber(View view) {
        client.setAuthenticationPhoneNumber();
    }

    public void onLogOut(View view) {
        client.clientLogOut();
    }

    public void onSendMessage(View view) {
        client.setChatIdAndSendMessage();
    }
}