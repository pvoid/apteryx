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
import android.text.TextUtils;

import org.pvoid.apteryx.data.Currency;
import org.pvoid.apteryx.data.terminals.TerminalCash;
import org.pvoid.apteryx.util.LogHelper;
import org.pvoid.apteryx.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class GetTerminalsCashResult extends Result {

    private static final String TAG_TERMINAL = "terminal";
    private static final String ATTR_TERMINAL_ID = "id";
    private static final String ATTR_AGENT_ID = "agent";

    @Nullable
    private final TerminalCash[] mCash;

    protected GetTerminalsCashResult(@NonNull ResponseTag root) {
        super(root);
        ResponseTag terminal;
        List<TerminalCash> result = null;
        try {
            while ((terminal = root.nextChild()) != null) {
                if (!TAG_TERMINAL.equals(terminal.getName())) {
                    continue;
                }
                final String terminalId = terminal.getAttribute(ATTR_TERMINAL_ID);
                final String agentId = terminal.getAttribute(ATTR_AGENT_ID);
                if (TextUtils.isEmpty(terminalId) || TextUtils.isEmpty(agentId)) {
                    continue;
                }
                TerminalCash cash = new TerminalCash(terminalId, agentId);
                ResponseTag currency;
                while ((currency = terminal.nextChild()) != null) {
                    String currencyId = currency.getAttribute("id");
                    TerminalCash.CashItem item;
                    try {
                        Currency cur = Currency.fromCode(Integer.parseInt(currencyId));
                        if (cur == null) {
                            continue;
                        }
                        item = new TerminalCash.CashItem(cur);
                    } catch (NumberFormatException e) {
                        LogHelper.error("Network", "Error while reading getTerminalsCash result", e);
                        continue;
                    }

                    ResponseTag notes;
                    while ((notes = currency.nextChild()) != null) {
                        if ("notes".equals(notes.getName())) {
                            item.addNotesGoBy(StringUtils.parseDouble(notes.getAttribute("SumGoBy"), 0.),
                                    StringUtils.parseInt(notes.getAttribute("GoBy"), 0));
                            ResponseTag nominal;
                            while ((nominal = notes.nextChild()) != null) {
                                if (!"nominal".equals(nominal.getName())) {
                                    continue;
                                }
                                item.addNotes((int) StringUtils.parseDouble(nominal.getAttribute("value"), 0.),
                                        StringUtils.parseInt(nominal.getAttribute("count"), 0));
                            }
                        } else if ("coins".equals(notes.getName())) {
                            item.addCoinsGoBy(StringUtils.parseInt(notes.getAttribute("GoBy"), 0));
                            ResponseTag nominal;
                            while ((nominal = notes.nextChild()) != null) {
                                if (!"nominal".equals(nominal.getName())) {
                                    continue;
                                }
                                item.addCoins(StringUtils.parseDouble(nominal.getAttribute("value"), 0.f),
                                        StringUtils.parseInt(nominal.getAttribute("count"), 0));
                            }
                        }
                    }
                    cash.addCash(item);
                }
                if (result == null) {
                    result = new ArrayList<>();
                }
                result.add(cash);
            }
        } catch (ResponseTag.TagReadException e) {
            LogHelper.error("Network", "Error while reading getTerminalsCash result", e);
        }

        if (result == null) {
            mCash = null;
        } else {
            mCash = result.toArray(new TerminalCash[result.size()]);
        }
    }

    @Nullable
    public TerminalCash[] getCash() {
        return mCash;
    }

    public static class Factory implements ResultFactory {
        @Nullable
        @Override
        public Result create(@NonNull ResponseTag tag) {
            return new GetTerminalsCashResult(tag);
        }
    }
}
