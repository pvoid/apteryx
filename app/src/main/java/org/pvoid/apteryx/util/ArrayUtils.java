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

import android.support.annotation.NonNull;

import java.util.Comparator;

public final class ArrayUtils {
    private ArrayUtils() {
    }

    public static <N, T> int binarySearchLeft(@NonNull T[] array, @NonNull N needle,
                                           @NonNull SearchComparator<N, T> comparator) {
        int left = 0;
        int right = array.length - 1;

        if (right == -1) {
            return -1;
        }

        while (left != right) {
            int mid = (left + right) / 2;
            if (comparator.compare(needle, array[mid]) > 0) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return (comparator.compare(needle, array[left]) == 0) ? left : -1;
    }

    public static <N, T> int binarySearchRight(@NonNull T[] array, @NonNull N needle,
                                            @NonNull SearchComparator<N, T> comparator) {
        int left = 0;
        int right = array.length - 1;

        if (right == -1) {
            return -1;
        }

        while (left != right) {
            int mid = (left + right + 1) / 2;
            if (comparator.compare(needle, array[mid]) < 0) {
                right = mid - 1;
            } else {
                left = mid;
            }
        }

        return (comparator.compare(needle, array[left]) == 0) ? left : -1;
    }
}
