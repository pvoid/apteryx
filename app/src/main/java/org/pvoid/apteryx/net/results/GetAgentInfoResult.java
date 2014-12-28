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

package org.pvoid.apteryx.net.results;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.pvoid.apteryx.net.commands.GetAgentInfoCommand;
import org.pvoid.apteryx.util.LogHelper;

public class GetAgentInfoResult extends Result {

    private static final String ATTR_ADDRESS = "address";
    private static final String ATTR_FIO = "fio";
    private static final String ATTR_ID = "id";
    private static final String ATTR_INFO = "info";
    private static final String ATTR_INN = "inn";
    private static final String ATTR_NAME = "name";
    private static final String ATTR_PHONE = "phone";
    private static final String ATTR_WWW = "www";
    private static final String ATTR_CONTACT_PHONE = "cnt-phone";
    private static final String ATTR_CONTACT_EMAIL = "cnt-email";

    @Nullable private final String mAddress;
    @Nullable private final String mFIO;
    @Nullable private final String mId;
    @Nullable private final String mInfo;
    @Nullable private final String mINN;
    @Nullable private final String mName;
    @Nullable private final String mPhone;
    @Nullable private final String mUrl;
    @Nullable private final String mContactPhone;
    @Nullable private final String mContactEmail;

    /* package */ GetAgentInfoResult(@NonNull ResponseTag root) {
        super(root);
        ResponseTag tag = null;
        try {
            tag = root.nextChild();
        } catch (ResponseTag.TagReadException e) {
            LogHelper.error("Network", "Can't parse " + GetAgentInfoCommand.NAME + " tag", e);
        }
        if (tag != null && TextUtils.equals("agent", tag.getName())) {
            mAddress = tag.getAttribute(ATTR_ADDRESS);
            mFIO = tag.getAttribute(ATTR_FIO);
            mId = tag.getAttribute(ATTR_ID);
            mInfo = tag.getAttribute(ATTR_INFO);
            mINN = tag.getAttribute(ATTR_INN);
            mName = tag.getAttribute(ATTR_NAME);
            mPhone = tag.getAttribute(ATTR_PHONE);
            mUrl = tag.getAttribute(ATTR_WWW);
            mContactPhone = tag.getAttribute(ATTR_CONTACT_PHONE);
            mContactEmail = tag.getAttribute(ATTR_CONTACT_EMAIL);
        } else {
            mAddress = null;
            mFIO = null;
            mId = null;
            mInfo = null;
            mINN = null;
            mName = null;
            mPhone = null;
            mUrl = null;
            mContactPhone = null;
            mContactEmail = null;
        }
    }

    @Nullable
    public String getAgentAddress() {
        return mAddress;
    }

    @Nullable
    public String getAgentFIO() {
        return mFIO;
    }

    @Nullable
    public String getAgentId() {
        return mId;
    }

    @Nullable
    public String getAgentInfo() {
        return mInfo;
    }

    @Nullable
    public String getAgentINN() {
        return mINN;
    }

    @Nullable
    public String getAgentName() {
        return mName;
    }

    @Nullable
    public String getAgentPhone() {
        return mPhone;
    }

    @Nullable
    public String getAgentUrl() {
        return mUrl;
    }

    @Nullable
    public String getAgentContactPhone() {
        return mContactPhone;
    }

    @Nullable
    public String getAgentContactEmail() {
        return mContactEmail;
    }

    public static class Factory implements ResultFactory {
        @Nullable
        @Override
        public Result create(@NonNull ResponseTag tag) {
            return new GetAgentInfoResult(tag);
        }
    }
}
