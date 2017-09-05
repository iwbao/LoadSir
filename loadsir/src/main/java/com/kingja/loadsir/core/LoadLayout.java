package com.kingja.loadsir.core;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.FrameLayout;

import com.kingja.loadsir.Util;
import com.kingja.loadsir.callback.Callback;
import com.kingja.loadsir.callback.EmptyCallback;
import com.kingja.loadsir.callback.ErrorCallback;
import com.kingja.loadsir.callback.LoadingCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * Description:TODO
 * Create Time:2017/9/2 17:02
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */

class LoadLayout extends FrameLayout {
    private static final String TAG = "LoadLayout";
    private Map<Class<? extends Callback>, Callback> callbacks = new HashMap<>();
    private Context context;
    private Callback.OnReloadListener onReloadListener;

    public LoadLayout(@NonNull Context context) {
        super(context);
        setupCallback(new ErrorCallback());
        setupCallback(new EmptyCallback());
        setupCallback(new LoadingCallback());
    }

    public LoadLayout(@NonNull Context context, Callback.OnReloadListener onReloadListener) {
        this(context);
        this.context = context;
        this.onReloadListener = onReloadListener;
    }

    public void setupCallback(Callback callback) {
        callback.setCallback(null, context, onReloadListener);
        addCallback(callback);
    }

    public void addCallback(Callback callback) {
        addView(callback.getRootView());
        callbacks.put(callback.getClass(), callback);
    }

    public void showStatus(final Class<? extends Callback> status) {
        if (!callbacks.containsKey(status)) {
            throw new IllegalArgumentException(String.format("The Callback (%s) is nonexistent.", status
                    .getSimpleName()));
        }
        if (Util.isMainThread()) {
            setCallbackVisibility(status);
        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    setCallbackVisibility(status);
                }
            });
        }
    }
    private void setCallbackVisibility(Class<? extends Callback> status) {
        for (Class key : callbacks.keySet()) {
            Callback callback = callbacks.get(key);
            if (key == status) {
                callback.show();
            } else {
                callback.hide();
            }
        }
    }

}
