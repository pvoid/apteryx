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

package org.pvoid.apteryx.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import org.pvoid.apteryx.R;
import org.pvoid.apteryx.data.Currency;
import org.pvoid.apteryx.data.terminals.TerminalCash;
import org.pvoid.apteryx.data.terminals.TerminalState;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public final class StringUtils {

    private static final NumberFormat sCashFormat;
    private static final DateFormat sDateFormat;

    static {
        sCashFormat = NumberFormat.getInstance(Locale.ENGLISH);
        sCashFormat.setGroupingUsed(true);
        if (sCashFormat instanceof DecimalFormat) {
            DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.ENGLISH);
            symbols.setDecimalSeparator('.');
            symbols.setGroupingSeparator(' ');
            ((DecimalFormat) sCashFormat).setDecimalFormatSymbols(symbols);
        }
        sDateFormat = new SimpleDateFormat("d MMMM, kk:mm", Locale.getDefault());
    }

    private StringUtils() {
    }

    public static float parseFloat(@Nullable String value, float defaultValue) {
        if (value != null) {
            try {
                return Float.parseFloat(value);
            } catch (NumberFormatException e) {
                // nope
            }
        }
        return defaultValue;
    }

    public static int parseInt(@Nullable String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static long parseLong(@Nullable String value, long defaultValue) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static double parseDouble(@Nullable String value, double defaultValue) {
        if (value != null) {
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                // nope
            }
        }
        return defaultValue;
    }

    @NonNull
    public static Spannable formatCashSummary(@NonNull TerminalCash cash, int currencyColor) {
        SpannableStringBuilder result = new SpannableStringBuilder();
        for (TerminalCash.CashItem item : cash.getCash()) {
            if (result.length() > 0) {
                result.append(", ");
            }
            final Currency currency = item.getCurrency();
            sCashFormat.setMinimumFractionDigits(currency.getmFractionDigits());
            result.append(sCashFormat.format(item.getAmmount()));
            int length = result.length();
            result.append(currency.getCodeName());
            result.setSpan(new ForegroundColorSpan(currencyColor), length, result.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        return result;
    }

    @NonNull
    public static CharSequence formatFullDate(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return sDateFormat.format(calendar.getTime());
    }
}
