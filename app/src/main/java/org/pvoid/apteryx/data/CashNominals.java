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

import android.support.annotation.NonNull;

import java.util.Arrays;
import java.util.Iterator;

public class CashNominals implements Iterable<CashNominals.Pile> {
    private int mCount = 0;
    private Pile[] mPiles;

    public CashNominals() {
        this(5);
    }

    public CashNominals(int capacity) {
        mPiles = new Pile[capacity];
    }

    private void resize(int size, int anchor) {
        Pile[] piles = new Pile[size];
        System.arraycopy(mPiles, 0, piles, 0, anchor);
        System.arraycopy(mPiles, anchor, piles, anchor + 1, mCount - anchor);
        mPiles = piles;
    }

    public void add(double nominal, int count) {
        int position = Arrays.binarySearch(mPiles, 0, mCount, new Pile(nominal));
        if (position < 0) {
            ++mCount;
            position = -(position + 1);
            if (mCount < mPiles.length) {
                System.arraycopy(mPiles, position, mPiles, position + 1, mCount - position);
            } else {
                resize(mPiles.length + mPiles.length / 2, position);
            }
            mPiles[position] = new Pile(nominal);
        }
        mPiles[position].mCount += count;
    }

    public int getCount(double nominal) {
        int position = Arrays.binarySearch(mPiles, 0, mCount, new Pile(nominal));
        if (position < 0) {
            return 0;
        }
        return mPiles[position].mCount;
    }

    public int getSize() {
        return mCount;
    }

    @Override
    public Iterator<Pile> iterator() {
        return new NominalsIterator();
    }

    private class NominalsIterator implements Iterator<Pile> {

        int mIndex = 0;

        @Override
        public boolean hasNext() {
            return mIndex < mCount;
        }

        @Override
        public Pile next() {
            return mPiles[mIndex++];
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public static class Pile implements Comparable<Pile> {
        private final double mNominal;
        private int mCount;

        private Pile(double nominal) {
            mNominal = nominal;
        }

        public double getNominal() {
            return mNominal;
        }

        public int getCount() {
            return mCount;
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            return (o instanceof Pile) && (mNominal == ((Pile) o).mNominal);
        }

        @Override
        public int compareTo(@NonNull Pile another) {
            return Double.compare(mNominal, another.mNominal);
        }
    }
}
