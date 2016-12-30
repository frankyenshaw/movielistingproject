package com.frankyenshaw.movielistingproject.Models;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.youtube.player.YouTubeIntents;

/**
 * Created by frankyenshaw on 12/29/16.
 */

public class IntentItem {

    public enum IntentType {
        PLAY_VIDEO,
        POPULAR_MOVIES,
        UPCOMING_MOVIES,
        NOW_PLAYING_MOVIES,
        TOP_RATED,
        GET_VIDEOS;
    }

    private final Context context;
    private final String title;
    private final IntentType type;
    private final Class targetClass;

    public static final String TAG = IntentItem.class.getSimpleName();

    public IntentItem(Context context, Class targetClass,IntentType type, String title) {
        this.context = context;
        this.title = title;
        this.type = type;
        this.targetClass = targetClass;
    }

    /**
     * Constructor used for 3rd party intents since we do not have a targetClass e.g. YouTubeIntents
     * @param context
     * @param type
     */
    public IntentItem(Context context,IntentType type) {
        this.context = context;
        this.title = "";
        this.type = type;
        this.targetClass = null;
    }

    public Intent getIntent(String key){
        switch(type){
            case PLAY_VIDEO:
                return YouTubeIntents.createPlayVideoIntentWithOptions(context, key, true, false);
            case POPULAR_MOVIES:
            case UPCOMING_MOVIES:
            case NOW_PLAYING_MOVIES:
            case TOP_RATED:
                return new Intent(context,targetClass);
            default:
                Log.e(TAG,"Unknown Intent Type: "+type);
                break;
        }
        return null;
    }

    public String getTitle() {
        return title;
    }
    public IntentType getType() { return type; }
    public Class getTargetClass() {
        return targetClass;
    }

}
