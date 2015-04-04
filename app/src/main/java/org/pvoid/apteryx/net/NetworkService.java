/*
 * Copyright (C) 2010-2014  Dmitry "PVOID" Petuhov
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.pvoid.apteryx.net;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.pvoid.apteryx.ApteryxApplication;
import org.pvoid.apteryx.BuildConfig;
import org.pvoid.apteryx.net.results.ResponseTag;
import org.pvoid.apteryx.util.log.Loggers;
import org.slf4j.Logger;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;

// TODO: add ability to stop request
public class NetworkService extends Service {

    public static final int MSG_SEND_REQUEST = 1;

    private static final Logger LOG = Loggers.getLogger(Loggers.Network);
    private static final XmlPullParserFactory PARSER_FACTORY;
    private final HandlerThread mMessageThread = new HandlerThread("NetworkThread");
    private final HandlerThread mCallbackThread = new HandlerThread("CallbackThread");
    private Messenger mMessenger;

    static {
        XmlPullParserFactory factory = null;
        try {
            factory = XmlPullParserFactory.newInstance();
        } catch (XmlPullParserException e) {
            LOG.error("Can't create xml parser factory", e);
        }
        PARSER_FACTORY = factory;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LOG.info("Network service created");
        ApteryxApplication application = (ApteryxApplication) getApplication();
        ResultFactories factories = application.getGraph().get(ResultFactories.class);
        mMessageThread.start();
        mCallbackThread.start();
        final CallbackHandler callbackHandler = new CallbackHandler(mCallbackThread.getLooper());
        final Handler msgHandler = new NetworkHandler(factories, mMessageThread.getLooper(), callbackHandler);
        mMessenger = new Messenger(msgHandler);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMessageThread.quit();
        mCallbackThread.quit();
        LOG.info("Network service destroyed");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return 0;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    private static class NetworkHandler extends Handler {
        @NonNull
        private final ResultFactories mFactories;
        @NonNull
        private final Handler mResultHandler;

        public NetworkHandler(@NonNull final ResultFactories factories,
                              @NonNull final Looper looper,
                              @NonNull final Handler callbackHandler) {
            super(looper);
            mFactories = factories;
            mResultHandler = callbackHandler;
        }

        @Override
        public void dispatchMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MSG_SEND_REQUEST:
                    sendRequest((OsmpRequest) msg.obj, (ResultCallback) msg.getCallback());
                    return;
            }
            super.dispatchMessage(msg);
        }

        private void sendRequest(@NonNull OsmpRequest request, @Nullable ResultCallback callback) {
            XmlPullParser parser = null;

            if (PARSER_FACTORY != null) {
                try {
                    parser = PARSER_FACTORY.newPullParser();
                } catch (XmlPullParserException e) {
                    LOG.error("Can't create xml parser", e);
                }
            }

            if (callback != null && callback.isCanceled()) {
                return;
            }

            if (parser == null) {
                if (callback != null) {
                    mResultHandler.post(callback);
                }
                return;
            }

            LOG.info(">>> Account: '{}'. Commands: {}", request.getPerson(), request.getCommands());

            OkHttpClient client = new OkHttpClient();
            Request.Builder builder = new Request.Builder();
            builder.url(request.getUri().toString()).post(request.createBody());
            builder.addHeader("User-Agent", BuildConfig.USER_AGENT);
            try {
                Response resp = client.newCall(builder.build()).execute();
                LOG.info("<<< Account: '{}'. HTTP/{} {}", request.getPerson(), resp.code(), resp.message());
                if (!resp.isSuccessful()) {
                    LOG.error("Server return HTTP error: {}", resp.code());
                    if (callback != null) {
                        mResultHandler.post(callback);
                    }
                    return;
                }
                parser.setInput(resp.body().byteStream(), "windows-1251");
                OsmpResponseReader reader = new OsmpResponseReader(parser);
                ResponseTag tag = reader.next();
                if (tag != null) {
                    OsmpResponse response = new OsmpResponse(tag, mFactories);
                    LOG.info("<<< Account: '{}'. Commands {}", request.getPerson(), response.getCommands());
                    if (callback != null && !callback.isCanceled()) {
                        if (!response.hasAsyncResponse()) {
                            callback.setResponse(response);
                            mResultHandler.post(callback);
                        } else {
                            // TODO (pvoid@): implement async requests
                        }
                    }
                    return;
                }
            } catch (XmlPullParserException | ResponseTag.TagReadException e) {
                LOG.error("Error while reading response XML", e);
            } catch (IOException e) {
                LOG.error("Error while reading response", e);
            }
            if (callback != null) {
                mResultHandler.post(callback);
            }
        }
    }

    private static class CallbackHandler extends Handler {
        public CallbackHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            final ResultCallback callback = (ResultCallback) msg.getCallback();
            if (callback != null && !callback.isCanceled()) {
                callback.run();
            }
        }
    }
}
