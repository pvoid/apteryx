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

import org.pvoid.apteryx.net.results.ResponseTag;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/* package */ class OsmpResponseReader {
    private final XmlPullParser mParser;
    private int mEvent;
    private int mTagInStack = 0;

    /* package */ OsmpResponseReader(XmlPullParser parser) throws XmlPullParserException {
        mParser = parser;
        mEvent = parser.getEventType();
    }

    @Nullable
    public ResponseTag next() throws XmlPullParserException, IOException {
        return nextTagForLevel(0);
    }

    private Tag nextTagForLevel(int level) throws XmlPullParserException, IOException {
        // skip all current opened tags
        while (mTagInStack > level) {
            if (mEvent == XmlPullParser.END_TAG) {
                --mTagInStack;
            } else if (mEvent == XmlPullParser.START_TAG) {
                ++mTagInStack;
            } else if (mEvent == XmlPullParser.END_DOCUMENT) {
                return null;
            }
            mEvent = mParser.next();
        }
        // find next tag start
        while (mEvent != XmlPullParser.END_DOCUMENT) {
            if (mEvent == XmlPullParser.START_TAG) {
                return new Tag();
            } else if (mEvent == XmlPullParser.END_TAG) {
                --mTagInStack;
                mEvent = mParser.next();
                return null;
            }
            mEvent = mParser.next();
        }
        return null;
    }

    private class Tag implements ResponseTag {
        @NonNull
        private final String mName;
        @Nullable
        private final String mText;
        @Nullable
        private final Map<String, String> mAttributes;
        private final int mTagLevel;
        private boolean mIsClosed = false;

        private Tag() throws XmlPullParserException, IOException {
            mTagLevel = ++mTagInStack;
            mName = mParser.getName();
            mIsClosed = mParser.isEmptyElementTag();
            final int attrCount = mParser.getAttributeCount();
            if (attrCount != 0) {
                mAttributes = new HashMap<>();
                for (int index = 0; index < attrCount; ++index) {
                    mAttributes.put(mParser.getAttributeName(index), mParser.getAttributeValue(index));
                }
            } else {
                mAttributes = null;
            }
            mEvent = mParser.next();
            if (mEvent == XmlPullParser.TEXT) {
                String tagText = mParser.getText().trim();
                mText = TextUtils.isEmpty(tagText) ? null : tagText;
                mEvent = mParser.next();
            } else {
                mText = null;
            }
        }

        @Override
        @Nullable
        public String getAttribute(@NonNull String name) {
            if (mAttributes == null) {
                return null;
            }
            return mAttributes.get(name);
        }

        @NonNull
        @Override
        public String getName() {
            return mName;
        }

        @Nullable
        @Override
        public String getText() {
            return mText;
        }

        @Nullable
        @Override
        public ResponseTag nextChild() throws TagReadException {
            if (mIsClosed) {
                return null;
            }
            try {
                Tag result = nextTagForLevel(mTagLevel);
                if (result == null) {
                    mIsClosed = true;
                }
                return result;
            } catch (IOException | XmlPullParserException e) {
                throw new TagReadException(e);
            }
        }
    }
}
