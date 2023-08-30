package com.example.tdapiandroidjavaexample.telegram.telegramRecyclerView;

import android.content.Context;

import com.example.tdapiandroidjavaexample.R;

public class DataLine {
    public enum TelegramAttr {
        API_ID,
        API_HASH,
        PHONE_NUMBER,
        PUBLIC_CHAT_NAME
    }

    private final String value;
    private final TelegramAttr telegramAttribute;
    private final Context context;

    public DataLine(Context ctx, TelegramAttr telegramAttribute, String value) {
        this.context = ctx;
        this.telegramAttribute = telegramAttribute;
        this.value = value;
    }
    public String getNiceName(){
        switch (telegramAttribute)  {
            case API_HASH:
                return context.getString(R.string.nice_api_hash);
            case API_ID:
                return context.getString(R.string.nice_api_id);
            case PUBLIC_CHAT_NAME:
                return context.getString(R.string.nice_public_chat_name);
            case PHONE_NUMBER:
                return context.getString(R.string.nice_phone_number);
            default:
                return "";
        }
    }
    public String getRawName(){
        switch (telegramAttribute)  {
            case API_HASH:
                return context.getString(R.string.raw_api_hash);
            case API_ID:
                return context.getString(R.string.raw_api_id);
            case PUBLIC_CHAT_NAME:
                return context.getString(R.string.raw_public_chat_name);
            case PHONE_NUMBER:
                return context.getString(R.string.raw_phone_number);
            default:
                return "";
        }
    }

    public String getValue() {
        return value;
    }

    public TelegramAttr getTelegramAttribute() {
        return telegramAttribute;
    }
}
