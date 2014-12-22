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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.apache.http.HttpStatus;
import org.apache.http.protocol.HTTP;
import org.pvoid.apteryx.net.results.ResponseTag;
import org.pvoid.apteryx.util.LogHelper;
import org.pvoid.apteryxaustralis.BuildConfig;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;

/* package */ class RequestWork implements Runnable {

    private static final String TAG = "RequestWork";
    private static final XmlPullParserFactory PARSER_FACTORY;

    @NonNull private final OsmpRequest mRequest;
    @NonNull private final ResultFactories mFactories;
    @NonNull private final ResultHandler mHandler;
    @NonNull private final RequestScheduler mScheduler;
    @Nullable private final OsmpResponse mResponse;
    private final int mCount;

    static {
        XmlPullParserFactory factory = null;
        try {
            factory = XmlPullParserFactory.newInstance();
        } catch (XmlPullParserException e) {
            LogHelper.error(TAG, e.getMessage());
        }
        PARSER_FACTORY = factory;
    }

    /* package */ RequestWork(@NonNull RequestScheduler scheduler, @NonNull OsmpRequest osmpRequest,
                              @NonNull ResultFactories factories, @NonNull ResultHandler handler) {
        mRequest = osmpRequest;
        mFactories = factories;
        mHandler = handler;
        mCount = 0;
        mResponse = null;
        mScheduler = scheduler;
    }

    /* package */ RequestWork(@NonNull RequestWork src, @NonNull OsmpRequest request,
                        @Nullable OsmpResponse response) {
        mRequest = request;
        mFactories = src.mFactories;
        mHandler = src.mHandler;
        mResponse = response;
        mScheduler = src.mScheduler;
        mCount = src.mCount + 1;
    }

    @Override
    public void run() {
        XmlPullParser parser = null;

        if (PARSER_FACTORY != null) {
            try {
                parser = PARSER_FACTORY.newPullParser();
            } catch (XmlPullParserException e) {
                LogHelper.error(TAG, e.getMessage());
            }
        }

        if (mHandler.isCanceled()) {
            return;
        }

        if (parser == null) {
            mHandler.onError();
            return;
        }

        OkHttpClient client = new OkHttpClient();
        Request.Builder builder = new Request.Builder();
        builder.url(mRequest.getUri().toString()).post(mRequest.createBody());
        builder.addHeader(HTTP.USER_AGENT, BuildConfig.USER_AGENT);
        try {
            Response resp = client.newCall(builder.build()).execute();
            if (resp.code() != HttpStatus.SC_OK) {
                LogHelper.error(TAG, "Server return error: " + resp.code());
                mHandler.onError();
                return;
            }
            parser.setInput(resp.body().byteStream(), "windows-1251");
            OsmpResponseReader reader = new OsmpResponseReader(parser);
            ResponseTag tag = reader.next();
            if (tag != null) {
                OsmpResponse response = new OsmpResponse(tag, mFactories);
                if (!mHandler.isCanceled()) {
                    if (mResponse != null) {
                        mResponse.update(response);
                        response = mResponse;
                    }

                    if (!response.hasAsyncResponse()) {
                        mHandler.onSuccess(response);
                    } else if (mCount < 5) {
                        mHandler.markPending();
                        OsmpRequest.Builder request = mRequest.buildUppon();
                        response.fillAsyncRequest(request);
                        OsmpRequest req = request.create();
                        if (req != null) {
                            mScheduler.schedule(new RequestWork(this, req, response));
                            return;
                        }
                    }
                }
                return;
            }
        } catch (IOException | XmlPullParserException | ResponseTag.TagReadException e) {
            LogHelper.error(TAG, e.getMessage());
        }
        mHandler.onError();
    }
}
