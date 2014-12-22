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

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import org.pvoid.apteryx.data.Account;
import org.pvoid.apteryx.net.commands.Command;
import org.pvoid.apteryxaustralis.BuildConfig;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okio.BufferedSink;

public class OsmpRequest {

    private static final String DEFAULT_ENCODING = "windows-1251";

    private static final MediaType MEDIA_TYPE = MediaType.parse("application/xml; charset=" + DEFAULT_ENCODING);
    private static final Uri SERVER_URI = Uri.parse(BuildConfig.SERVER_URL);
    @NonNull private final Account mAccount;
    @NonNull private final byte[] mBody;

    private OsmpRequest(@NonNull Account account, @NonNull byte[] data) {
        mBody = data;
        mAccount = account;
    }

    /* package */ Uri getUri() {
        return SERVER_URI;
    }

    /* package */ RequestBody createBody() {
        return new OsmpRequestBody();
    }

    public Builder buildUppon() {
        return new Builder(mAccount);
    }

    public static class Builder {

        @NonNull
        private final Account mAccount;
        @Nullable
        private Map<OsmpInterface, CommandsArrayList> mInterfaces = null;

        public Builder(@NonNull Account account) {
            mAccount = account;
        }

        @NonNull
        public CommandsList getInterface(OsmpInterface osmpInterface) {
            if (mInterfaces == null) {
                mInterfaces = new HashMap<>();
            }
            CommandsArrayList commands = mInterfaces.get(osmpInterface);
            if (commands == null) {
                commands = new CommandsArrayList();
                mInterfaces.put(osmpInterface, commands);
            }
            return commands;
        }

        @Nullable
        public OsmpRequest create() {

            if (!Charset.isSupported(DEFAULT_ENCODING)) {
                return null;
            }

            Charset charset = Charset.forName(DEFAULT_ENCODING);

            StringBuilder resultText = new StringBuilder();
            resultText.append("<?xml version=\"1.0\" encoding=\"windows-1251\"?>");
            resultText.append("<request>");

            resultText.append("<auth login=\"").append(TextUtils.htmlEncode(mAccount.getLogin()))
                      .append("\" ").append("signAlg=\"MD5\" sign=\"")
                      .append(TextUtils.htmlEncode(mAccount.getPasswordHash())).append("\"/>");
            resultText.append("<client terminal=\"")
                      .append(TextUtils.htmlEncode(mAccount.getTerminal()))
                      .append("\" software=\"Dealer v0\" serial=\"\"/>");

            if (mInterfaces != null) {
                for (Map.Entry<OsmpInterface, CommandsArrayList> i : mInterfaces.entrySet()) {
                    resultText.append("<").append(i.getKey().getName()).append(">");
                    for (Command command : i.getValue()) {
                        resultText.append("<").append(command.getName());
                        if (command.isAsync()) {
                            resultText.append(" mode=\"async\"");
                        }
                        resultText.append(">");
                        for (Map.Entry<String, String> params : command.getParams().entrySet()) {
                            resultText.append("<").append(params.getKey()).append(">");
                            resultText.append(TextUtils.htmlEncode(params.getValue()));
                            resultText.append("</").append(params.getKey()).append(">");
                        }
                        resultText.append("</").append(command.getName()).append(">");
                    }
                    resultText.append("</").append(i.getKey().getName()).append(">");
                }
            }

            resultText.append("</request>");

            return new OsmpRequest(mAccount, resultText.toString().getBytes(charset));
        }
    }

    private class OsmpRequestBody extends RequestBody {
        @Override
        public MediaType contentType() {
            return MEDIA_TYPE;
        }

        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            sink.write(mBody);
        }

        @Override
        public long contentLength() {
            return mBody.length;
        }
    }

    private static class CommandsArrayList extends ArrayList<Command> implements CommandsList {
    }

    public interface CommandsList {
        boolean add(Command command);
    }
}
