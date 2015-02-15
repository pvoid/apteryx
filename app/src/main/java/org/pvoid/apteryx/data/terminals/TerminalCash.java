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

package org.pvoid.apteryx.data.terminals;

import android.support.annotation.NonNull;
import android.util.SparseIntArray;

import org.pvoid.apteryx.data.CashNominals;
import org.pvoid.apteryx.data.Currency;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TerminalCash {
    private final String mTerminalId;
    private final String mAgentId;
    private final Map<Currency, CashItem> mCash = new HashMap<>();

    public TerminalCash(String terminalId, String agentId) {
        mTerminalId = terminalId;
        mAgentId = agentId;
    }

    public TerminalCash(DataInputStream in) throws IOException {
        mTerminalId = in.readUTF();
        mAgentId = in.readUTF();
        for (int size = in.readInt(); size > 0; --size) {
            CashItem item = new CashItem(in);
            mCash.put(item.mCurrency, item);
        }
    }

    public String getTerminalId() {
        return mTerminalId;
    }

    public String getAgentId() {
        return mAgentId;
    }

    public void addCash(@NonNull CashItem cashItem) {
        mCash.put(cashItem.mCurrency, cashItem);
    }

    public Collection<CashItem> getCash() {
        return mCash.values();
    }

    public void store(DataOutputStream out) throws IOException {
        out.writeUTF(mTerminalId);
        out.writeUTF(mAgentId);
        out.writeInt(mCash.size());
        for (CashItem cash : mCash.values()) {
            cash.store(out);
        }
    }

    public static class CashItem {
        @NonNull
        private final Currency mCurrency;
        private double mAmmount = 0;
        @NonNull private final CashNominals mNotes = new CashNominals();
        private int mNotesGoByCount;
        private double mNotesGoBySum;
        @NonNull private final CashNominals mCoins = new CashNominals();
        private int mCoinsGoByCount;

        public CashItem(@NonNull Currency currency) {
            mCurrency = currency;
        }

        private CashItem(@NonNull DataInputStream in) throws IOException {
            Currency currency = Currency.fromCode(in.readInt());
            if (currency == null) {
                throw new IOException("Unknown currency id");
            }
            mCurrency = currency;
            mNotesGoByCount = in.readInt();
            mNotesGoBySum = in.readDouble();
            for (int size = in.readInt(); size > 0; --size) {
                mNotes.add(in.readDouble(), in.readInt());
            }
            mCoinsGoByCount = in.readInt();
            for (int size = in.readInt(); size > 0; --size) {
                mCoins.add(in.readDouble(), in.readInt());
            }
        }

        public void addNotes(int face, int count) {
            mNotes.add(face, count);
            mAmmount += face * count;
        }

        public void addNotesGoBy(double sum, int count) {
            mAmmount += sum;
            mNotesGoBySum = sum;
            mNotesGoByCount = count;
        }

        public void addCoins(double face, int count) {
            mCoins.add((int) (face * mCurrency.getCapacity()), count);
            mAmmount += face * count;
        }

        public void addCoinsGoBy(int count) {
            mCoinsGoByCount = count;
        }

        public Iterable<CashNominals.Pile> getNotes() {
            return mNotes;
        }

        public Iterable<CashNominals.Pile> getCoins() {
            return mCoins;
        }

        @NonNull
        public Currency getCurrency() {
            return mCurrency;
        }

        public double getAmmount() {
            return mAmmount;
        }

        public int getNotesGoByCount() {
            return mNotesGoByCount;
        }

        public double getNotesGoBySum() {
            return mNotesGoBySum;
        }

        public int getCoinsGoByCount() {
            return mCoinsGoByCount;
        }

        private void store(DataOutputStream out) throws IOException {
            out.writeInt(mCurrency.getCode());
            out.writeInt(mNotesGoByCount);
            out.writeDouble(mNotesGoBySum);
            out.writeInt(mNotes.getSize());
            for (CashNominals.Pile pile : mNotes) {
                out.writeDouble(pile.getNominal());
                out.writeInt(pile.getCount());
            }
            out.writeInt(mCoinsGoByCount);
            out.writeInt(mCoins.getSize());
            for (CashNominals.Pile pile : mCoins) {
                out.writeDouble(pile.getNominal());
                out.writeInt(pile.getCount());
            }
        }
    }

}
