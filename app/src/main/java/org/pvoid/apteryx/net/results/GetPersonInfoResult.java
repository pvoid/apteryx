/*
 * Copyright (C) 2010-2015  Dmitry "PVOID" Petuhov
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

package org.pvoid.apteryx.net.results;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class GetPersonInfoResult extends Result {

    private static final String ATTR_AGENT = "agent";
    private static final String ATTR_ENABLED = "enabled";
    private static final String ATTR_ID = "id";
    private static final String ATTR_LOGIN = "login";
    private static final String ATTR_NAME = "name";

    @Nullable private final String mId;
    @Nullable private final String mLogin;
    @Nullable private final String mName;
    @Nullable private final String mAgentId;
    private final boolean mIsEnabled;

    /*package*/ GetPersonInfoResult(@NonNull ResponseTag root) {
        super(root);
        String id = null;
        String login = null;
        String name = null;
        String agent = null;
        String enabled = "";
        try {
            ResponseTag person = root.nextChild();
            if (person != null && "person".equals(person.getName())) {
                id = person.getAttribute(ATTR_ID);
                login = person.getAttribute(ATTR_LOGIN);
                name = person.getAttribute(ATTR_NAME);
                agent = person.getAttribute(ATTR_AGENT);
                enabled = person.getAttribute(ATTR_ENABLED);
            }
        } catch (ResponseTag.TagReadException e) {
            LOG.error("Can't read GetPersonInfoResult", e);
        }
        mId = id;
        mLogin = login;
        mName = name;
        mAgentId = agent;
        mIsEnabled = "true".equals(enabled);
    }

    @Nullable
    public String getId() {
        return mId;
    }

    @Nullable
    public String getLogin() {
        return mLogin;
    }

    @Nullable
    public String getPersonName() {
        return mName;
    }

    @Nullable
    public String getAgentId() {
        return mAgentId;
    }

    public boolean isEnabled() {
        return mIsEnabled;
    }

    public static class Factory implements ResultFactory {
        @Nullable
        @Override
        public Result create(@NonNull ResponseTag tag) {
            return new GetPersonInfoResult(tag);
        }
    }
}
