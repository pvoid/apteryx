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

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Comparator;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class ArrayUtilsTest {
    @Test
    public void binarySearchLeftCheck() throws Exception {
        Long arr[] = new Long[] {0l, 1l, 2l, 3l, 4l, 4l, 4l, 4l, 4l, 9l, 11l};
        SearchComparator<Integer, Long> comparator = new SearchComparator<Integer, Long>() {
            @Override
            public int compare(Integer lhs, Long rhs) {
                return (int)(lhs - rhs);
            }
        };
        Assert.assertEquals(-1, ArrayUtils.binarySearchLeft(new Long[0], 10, comparator));
        Assert.assertEquals(4, ArrayUtils.binarySearchLeft(arr, 4, comparator));
        Assert.assertEquals(-1, ArrayUtils.binarySearchLeft(arr, 5, comparator));

        arr = new Long[] {4l, 4l, 4l, 4l, 4l, 9l, 11l, 15l, 20l};
        Assert.assertEquals(0, ArrayUtils.binarySearchLeft(arr, 4, comparator));
        arr = new Long[] {0l, 1l, 2l, 3l, 3l, 3l, 4l, 4l, 4l, 4l, 4l};
        Assert.assertEquals(6, ArrayUtils.binarySearchLeft(arr, 4, comparator));
    }

    @Test
    public void binarySearchRightCheck() throws Exception {
        Long arr[] = new Long[] {0l, 1l, 2l, 3l, 4l, 4l, 4l, 4l, 4l, 9l, 11l};
        SearchComparator<Integer, Long> comparator = new SearchComparator<Integer, Long>() {
            @Override
            public int compare(Integer lhs, Long rhs) {
                return (int) (lhs - rhs);
            }
        };
        Assert.assertEquals(-1, ArrayUtils.binarySearchRight(new Long[0], 10, comparator));
        Assert.assertEquals(8, ArrayUtils.binarySearchRight(arr, 4, comparator));
        Assert.assertEquals(-1, ArrayUtils.binarySearchRight(arr, 5, comparator));

        arr = new Long[] {4l, 4l, 4l, 4l, 4l, 9l, 11l, 15l, 20l};
        Assert.assertEquals(4, ArrayUtils.binarySearchRight(arr, 4, comparator));
        arr = new Long[] {0l, 1l, 2l, 3l, 3l, 3l, 4l, 4l, 4l, 4l, 4l};
        Assert.assertEquals(10, ArrayUtils.binarySearchRight(arr, 4, comparator));
    }
}
