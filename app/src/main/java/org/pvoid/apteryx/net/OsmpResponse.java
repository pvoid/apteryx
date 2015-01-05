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

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.pvoid.apteryx.net.commands.Command;
import org.pvoid.apteryx.net.results.Result;
import org.pvoid.apteryx.net.results.ResponseTag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class OsmpResponse implements Parcelable {
    private static final String ROOT_TAG_NAME = "response";
    private static final String ATTR_RESULT = "result";
    private static final String ATTR_RESULT_DESCRIPTION = "result-description";

    private final int mResult;
    @Nullable private final String mResultDescription;
    @NonNull private final Map<OsmpInterface, ResultMap> mInterfaces = new HashMap<>();

    /* package */ OsmpResponse(@NonNull ResponseTag tag,
                               @NonNull ResultFactories factory) throws ResponseTag.TagReadException {
        int result = 0;
        if (!ROOT_TAG_NAME.equals(tag.getName())) {
            throw new ResponseTag.TagReadException("Invalid root tag name: " + tag.getName());
        }
        try {
            String val = tag.getAttribute(ATTR_RESULT);
            if (!TextUtils.isEmpty(val)) {
                result = Integer.parseInt(val);
            }
        } catch (NumberFormatException e) {
            throw new ResponseTag.TagReadException(e);
        }
        mResult = result;
        mResultDescription = tag.getAttribute(ATTR_RESULT_DESCRIPTION);

        ResponseTag interfaceTag;
        while ((interfaceTag = tag.nextChild()) != null) {
            OsmpInterface i = OsmpInterface.fromName(interfaceTag.getName());
            if (i == null) {
                continue;
            }
            ResponseTag commandTag;
            ResultMap commands = null;
            while ((commandTag = interfaceTag.nextChild()) != null) {
                Result command = factory.build(commandTag);
                if (command == null) {
                    continue;
                }
                if (commands == null) {
                    commands = new ResultMap();
                    mInterfaces.put(i, commands);
                }
                commands.put(command.getName(), command);
            }
        }
    }

    private OsmpResponse(@NonNull Parcel parcel) {
        mResult = parcel.readInt();
        mResultDescription = parcel.readString();

        int interfacesCount = parcel.readInt();
        final ClassLoader classLoader = OsmpResponse.class.getClassLoader();
        while (interfacesCount-- > 0) {
            OsmpInterface iface = OsmpInterface.fromName(parcel.readString());
            int itemsCount = parcel.readInt();
            ResultMap map = new ResultMap();
            while (itemsCount-- > 0) {
                Result result = parcel.readParcelable(classLoader);
                map.put(result.getName(), result);
            }
            if (iface != null && map.size() != 0) {
                mInterfaces.put(iface, map);
            }
        }
    }

    public int getResult() {
        return mResult;
    }

    /* package */ boolean hasAsyncResponse() {
        for (ResultMap results : mInterfaces.values()) {
            for (Result result : results.values()) {
                if (result.isPending()) {
                    return true;
                }
            }
        }
        return false;
    }

    /* package */ void fillAsyncRequest(@NonNull OsmpRequest.Builder requestBuilder) {
        for (Map.Entry<OsmpInterface, ResultMap> results : mInterfaces.entrySet()) {
            for (Result result : results.getValue()) {
                if (result.isPending()) {
                    requestBuilder.getInterface(results.getKey())
                            .add(new AsyncRequestCommand(result.getName(), result.getQueueId()));
                }
            }
        }
    }

    /* package */ void update(@NonNull OsmpResponse response) {
        for (Map.Entry<OsmpInterface, ResultMap> results : response.mInterfaces.entrySet()) {
            for (Result result : results.getValue()) {
                if (!result.isPending()) {
                    mInterfaces.get(results.getKey()).put(result.getName(), result);
                }
            }
        }
    }

    @Nullable
    public String getResultDescription() {
        return mResultDescription;
    }

    @Nullable
    public Results getInterface(@NonNull OsmpInterface iface) {
        return mInterfaces.get(iface);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mResult);
        dest.writeString(mResultDescription);

        dest.writeInt(mInterfaces.size());
        for (Map.Entry<OsmpInterface, ResultMap> entry : mInterfaces.entrySet()) {
            dest.writeString(entry.getKey().getName());
            dest.writeInt(entry.getValue().size());
            for (Result result : entry.getValue()) {
                dest.writeParcelable(result, flags);
            }
        }
    }

    private static class ResultMap extends HashMap<String, Result> implements Results {
        @Override
        public <T extends Result> T get(String command) {
            //noinspection unchecked
            return (T) super.get(command);
        }

        @Override
        public Iterator<Result> iterator() {
            return values().iterator();
        }
    }

    public static class AsyncRequestCommand implements Command {

        @NonNull private final String mName;
        private final Map<String, String> mParams;

        private AsyncRequestCommand(@NonNull String name, final int queId) {
            mName = name;
            mParams = new HashMap<String, String>(1) {{
                put(Result.ATTR_QUEUQ_ID, String.valueOf(queId));
            }};
        }

        private AsyncRequestCommand(@NonNull Parcel parcel) {
            mName = parcel.readString();
            mParams = new HashMap<>();
            parcel.readMap(mParams, AsyncRequestCommand.class.getClassLoader());
        }

        @Override
        @NonNull
        public String getName() {
            return mName;
        }

        @Override
        public Map<String, String> getParams() {
            return mParams;
        }

        @Override
        public boolean isAsync() {
            return false;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(mName);
            dest.writeMap(mParams);
        }

        public static final Creator<AsyncRequestCommand> CREATOR = new Creator<AsyncRequestCommand>() {
            @Override
            public AsyncRequestCommand createFromParcel(Parcel source) {
                return new AsyncRequestCommand(source);
            }

            @Override
            public AsyncRequestCommand[] newArray(int size) {
                return new AsyncRequestCommand[size];
            }
        };
    }

    public interface Results extends Iterable<Result> {
        <T extends Result> T get(String command);
        int size();
    }

    public static final Creator<OsmpResponse> CREATOR = new Creator<OsmpResponse>() {
        @Override
        public OsmpResponse createFromParcel(Parcel source) {
            return new OsmpResponse(source);
        }

        @Override
        public OsmpResponse[] newArray(int size) {
            return new OsmpResponse[size];
        }
    };
}
