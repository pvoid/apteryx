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

package org.pvoid.apteryx.data;

import android.support.annotation.Nullable;

public enum Currency {
    RUR(643, "Ꝑ", 100),
    USD(840, "$", 100),
    EUR(978, "€", 100),
    UAH(980, "₴", 100),
    GEL(981, "GEL", 100),
    TJS(972, "TJS", 100);

    private final int mCode;
    private final String mCodeName;
    private final int mCapacity;

    Currency(int code, String codeName, int capacity) {
        mCode = code;
        mCodeName = codeName;
        mCapacity = capacity;
    }

    public int getCode() {
        return mCode;
    }

    public String getCodeName() {
        return mCodeName;
    }

    public int getCapacity() {
        return mCapacity;
    }

    @Nullable
    public static Currency fromCode(int code) {
        for (Currency currency : values()) {
            if (currency.mCode == code) {
                return currency;
            }
        }
        return null;
    }
}
