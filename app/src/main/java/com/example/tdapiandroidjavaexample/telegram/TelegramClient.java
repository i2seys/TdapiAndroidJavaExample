package com.example.tdapiandroidjavaexample.telegram;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tdapiandroidjavaexample.R;
import com.google.android.material.textfield.TextInputLayout;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

public class TelegramClient {
    private final String TAG = TelegramClient.class.getSimpleName();
    private Client client;
    private Context context;
    private int apiId;
    private String apiHash;
    private String publicChatName;
    private String phoneNumber;
    private TdApi.AuthorizationState authorizationState;
    private boolean sessionClosed;
    private String authCode;
    public TelegramClient(Context ctx, int apiId, String apiHash, String phoneNumber, String publicChatName){
        this.context = ctx;
        this.apiHash = apiHash;
        this.apiId = apiId;
        this.phoneNumber = phoneNumber;
        this.publicChatName = publicChatName;
        this.client = Client.create(
                object -> {
                    switch (object.getConstructor()){
                        case TdApi.UpdateFile.CONSTRUCTOR:
                            updateFileLogic(object);
                            break;
                        case TdApi.UpdateNewMessage.CONSTRUCTOR:
                            updateNewMessageLogic(object);
                            break;
                    }
                },
                e -> Log.d(TAG, "UpdateExceptionHandler: " + e.toString()),
                e -> Log.d(TAG, "DefaultExceptionHandler (Client) : " + e.toString()));
    }

    private void updateNewMessageLogic(TdApi.Object object) {
        TdApi.UpdateNewMessage msg = (TdApi.UpdateNewMessage) object;
        Log.d(TAG, msg.message.toString());
    }
    private void updateFileLogic(TdApi.Object object) {
        TdApi.UpdateFile updateFile = (TdApi.UpdateFile) object;
        Log.d(TAG, updateFile.toString());
    }

    public void clientLogOut(){
        client.send(new TdApi.LogOut(), object -> {
            checkCallback(object);
            Log.d(TAG, "ClientLogOut: " + object.toString());
            sessionClosed = true;
        }, e -> checkForException(e, "LogOut"));
    }
    public void closeConnection(){
        Log.d(TAG, "Close connection");
        client.close();
    }
    public void setAuthCode(String enterCode){
        client.send(new TdApi.CheckAuthenticationCode(enterCode), object -> {
            checkCallback(object);
            Log.d(TAG, "Client.send checkAuthCode: " + object.toString());
        }, e -> {
            checkForException(e, "CheckAuthenticationCode");
        });
    }
    public void setChatIdAndSendMessage(){
        client.send(new TdApi.SearchPublicChat(publicChatName), object -> {
            Log.d(TAG, "Search public chat: " + object);
            checkCallback(object);
            if(object.getConstructor() == TdApi.Chat.CONSTRUCTOR){
                TdApi.Chat chat = (TdApi.Chat) (object);
                sendMessage("Test message", chat.id);
            }
        });
    }
    private void sendMessage(String text, long chatId){
        client.send(new TdApi.SendMessage(
                chatId,
                0,
                0,
                null,
                null,
                new TdApi.InputMessageText(new TdApi.FormattedText(text, null), false, false)), object -> {
            Log.d(TAG, "Send Message: " + object);
            checkCallback(object);
        });
    }

    public void sendParameters() {
        TdApi.TdlibParameters parameters = new TdApi.TdlibParameters();
        File path = context.getExternalFilesDir(null);
        Path sessionPath = Paths.get(new File(path, "tdlib-session").getPath());

        parameters.apiHash = this.apiHash;
        parameters.apiId = this.apiId;
        parameters.systemLanguageCode = Locale.getDefault().getLanguage();
        parameters.deviceModel = getManufacturer();
        parameters.applicationVersion = "1.0";
        parameters.enableStorageOptimizer = true;
        parameters.useMessageDatabase = true;
        parameters.useSecretChats = true;
        parameters.databaseDirectory = sessionPath.resolve("data").toString();
        parameters.filesDirectory = sessionPath.resolve("downloads").toString();
        parameters.systemVersion = Build.VERSION.RELEASE;

        TdApi.SetTdlibParameters setTdlibParameters = new TdApi.SetTdlibParameters(parameters);
        client.send(setTdlibParameters, object -> {
            checkCallback(object);
            if(object instanceof TdApi.Error){
                Log.d(TAG, "SendTdlibParameters: ERROR: " + object.toString());
            }
            else{
                Log.d(TAG, "SendTdlibParameters: SUCCESS: " + object);
            }
        }, e -> {
            checkForException(e, "SetTdlibParameters ");
        });
    }
    public void sendDatabaseEncryptionKey(){
        client.send(new TdApi.CheckDatabaseEncryptionKey(new byte[]{55,12,6}),
                object -> {
                    checkCallback(object);
                    if(object instanceof TdApi.Error){
                        Log.d(TAG, "Client send checkDatabaseEncryptionKey ERROR: " + object);
                    }
                    else{
                        Log.d(TAG, "Client send checkDatabaseEncryptionKey RESULT: " + object);
                    }
                }, e -> {
                    checkForException(e, "CheckDatabaseEncryptionKey");
                });
    }
    public void setAuthenticationPhoneNumber()  {
        Log.d(TAG, "setAuthenticationPhoneNumber: start");
        if(authorizationState.getConstructor() == TdApi.AuthorizationStateWaitPhoneNumber.CONSTRUCTOR){
            Log.d(TAG, "Authorization state: AuthorizationStateWaitPhoneNumber");
            client.send(new TdApi.SetAuthenticationPhoneNumber(phoneNumber, null),
                    object -> {
                        checkCallback(object);
                        if(object instanceof TdApi.Error){
                            Log.d(TAG, "Client.send phoneNumberAuth: ERROR: " + object);
                        }
                        else{
                            ((Activity)context).runOnUiThread(this::showAlertForCode);
                            Log.d(TAG, "Client.send phoneNumberAuth: " + object);

                        }
                    }, e -> {
                        checkForException(e, "SetAuthenticationPhoneNumber");
                    }
            );
        }
        else{
            Log.d(TAG, "Wrong authorization state");
        }
    }
    public void getAuthorizationState(){
        Log.d(TAG, "Function: Start Client Authentication State");
        client.send(new TdApi.GetAuthorizationState(), object -> {
            checkCallback(object);
            if (object instanceof TdApi.AuthorizationState) {
                authorizationState = (TdApi.AuthorizationState) object;
                makeToast("Authorization State: " + authorizationState.getClass().getSimpleName());
                Log.d(TAG, "Authorization State: " + authorizationState.getClass().getSimpleName());
            } else {
                Log.d(TAG, "Wrong Authorization Code");

            }
        }, e -> {
            checkForException(e, "GetAuthorizationState");
        });
    }

    public Client getClient() {
        return client;
    }

    public boolean isSessionClosed() {
        return sessionClosed;
    }

    private String getManufacturer() {
        String res;
        try {
            res = Build.MANUFACTURER;
        }
        catch (Exception e){
            res = "Unknown manufacturer";
        }
        return res;
    }
    private void checkForException(Throwable e, String methodName){
        String message = methodName + ": ERROR - " + e.getMessage();
        Log.w(TAG, message);
        throw new RuntimeException(message);
    }
    private void makeToast(String text){
        ((Activity)context).runOnUiThread(() -> Toast.makeText(context, text, Toast.LENGTH_SHORT).show());
    }
    private void checkCallback(TdApi.Object o){
        if(o == null || o.getConstructor() == TdApi.Error.CONSTRUCTOR){
            makeToast("Error. See logs");
        }
        else{
            makeToast("OK");
        }
    }
    private void showAlertForCode(){
        Resources r = context.getResources();
        TextInputLayout textInputLayout = new TextInputLayout(context);
        EditText editText = new EditText(context);
        textInputLayout.setPadding(
                r.getDimensionPixelOffset(R.dimen.alert_padding_left),
                r.getDimensionPixelOffset(R.dimen.alert_padding_top),
                r.getDimensionPixelOffset(R.dimen.alert_padding_right),
                r.getDimensionPixelOffset(R.dimen.alert_padding_bottom));
        textInputLayout.addView(editText);

        AlertDialog enterCode = new AlertDialog.Builder(context)
                .setTitle("Code")
                .setMessage("Enter the code that came to the telegram at the specified phone number.")
                .setView(textInputLayout)
                .setPositiveButton("Enter", (dialog, which) -> {
                    setAuthCode(textInputLayout.getEditText().getText().toString());
                    dialog.cancel();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.cancel();
                })
                .create();
        enterCode.show();
    }
}

