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
import android.text.TextUtils;

import org.pvoid.apteryx.net.results.Result;
import org.pvoid.apteryx.net.results.ResponseTag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OsmpResponse {
    private static final String ROOT_TAG_NAME = "response";
    private static final String ATTR_RESULT = "result";
    private static final String ATTR_RESULT_DESCRIPTION = "result-description";

    private final int mResult;
    @Nullable private final String mResultDescription;
    @NonNull private final Map<OsmpInterface, List<Result>> mInterfaces = new HashMap<>();

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
            List<Result> commands = null;
            while ((commandTag = interfaceTag.nextChild()) != null) {
                Result command = factory.build(commandTag);
                if (command == null) {
                    continue;
                }
                if (commands == null) {
                    commands = new ArrayList<>();
                    mInterfaces.put(i, commands);
                }
                commands.add(command);
            }
        }
    }

    public int getResult() {
        return mResult;
    }

    @Nullable
    public String getResultDescription() {
        return mResultDescription;
    }

    @Nullable
    public List<Result> getInterface(@NonNull OsmpInterface iface) {
        return mInterfaces.get(iface);
    }
}
