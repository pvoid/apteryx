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
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import org.pvoid.apteryx.BuildConfig;
import org.pvoid.apteryx.data.persons.Person;
import org.pvoid.apteryx.net.commands.Command;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okio.BufferedSink;

public class OsmpRequest implements Parcelable {

    private static final String DEFAULT_ENCODING = "windows-1251";

    private static final MediaType MEDIA_TYPE = MediaType.parse("application/xml; charset=" + DEFAULT_ENCODING);
    private static final Uri SERVER_URI = Uri.parse(BuildConfig.SERVER_URL);
    @NonNull private final Person mPerson;
    @NonNull private final byte[] mBody;
    @NonNull private final String[] mCommands;

    private OsmpRequest(@NonNull Person person, @NonNull String[] commands, @NonNull byte[] data) {
        mBody = data;
        mPerson = person;
        mCommands = commands;
    }

    private OsmpRequest(@NonNull Parcel source) {
        mPerson = source.readParcelable(Person.class.getClassLoader());
        mCommands = source.createStringArray();
        mBody = source.createByteArray();
    }

    /* package */ Uri getUri() {
        return SERVER_URI;
    }

    /* package */ RequestBody createBody() {
        return new OsmpRequestBody();
    }

    /* package */ String[] getCommands() {
        return mCommands;
    }

    @NonNull
    public Person getPerson() {
        return mPerson;
    }

    public Builder buildUppon() {
        return new Builder(mPerson);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mPerson, flags);
        dest.writeStringArray(mCommands);
        dest.writeByteArray(mBody);
    }

    public static class Builder {

        @NonNull
        private final Person mPerson;
        @Nullable
        private Map<OsmpInterface, CommandsArrayList> mInterfaces = null;

        public Builder(@NonNull Person person) {
            mPerson = person;
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

            resultText.append("<auth login=\"").append(TextUtils.htmlEncode(mPerson.getLogin()))
                      .append("\" ").append("signAlg=\"MD5\" sign=\"")
                      .append(TextUtils.htmlEncode(mPerson.getPasswordHash())).append("\"/>");
            resultText.append("<client terminal=\"")
                      .append(TextUtils.htmlEncode(mPerson.getTerminal()))
                      .append("\" software=\"Dealer v0\" serial=\"\"/>");

            final List<String> commands = new ArrayList<>();
            if (mInterfaces != null) {
                for (Map.Entry<OsmpInterface, CommandsArrayList> i : mInterfaces.entrySet()) {
                    resultText.append("<").append(i.getKey().getName()).append(">");
                    for (Command command : i.getValue()) {
                        commands.add(command.getName());
                        resultText.append("<").append(command.getName());
                        if (command.isAsync()) {
                            resultText.append(" mode=\"async\"");
                        }
                        resultText.append(">");
                        Map<String, String> paramsMap = command.getParams();
                        if (paramsMap != null) {
                            for (Map.Entry<String, String> params :paramsMap.entrySet()){
                                resultText.append("<").append(params.getKey()).append(">");
                                resultText.append(TextUtils.htmlEncode(params.getValue()));
                                resultText.append("</").append(params.getKey()).append(">");
                            }
                        }
                        resultText.append("</").append(command.getName()).append(">");
                    }
                    resultText.append("</").append(i.getKey().getName()).append(">");
                }
            }

            resultText.append("</request>");

            return new OsmpRequest(mPerson, commands.toArray(new String[commands.size()]),
                    resultText.toString().getBytes(charset));
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

    public static final Creator<OsmpRequest> CREATOR = new Creator<OsmpRequest>() {
        @Override
        public OsmpRequest createFromParcel(Parcel source) {
            return new OsmpRequest(source);
        }

        @Override
        public OsmpRequest[] newArray(int size) {
            return new OsmpRequest[size];
        }
    };

    public interface CommandsList {
        boolean add(Command command);
    }
}
