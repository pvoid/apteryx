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

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class ResultTest {
    @Test
    public void constructorCheck() throws Exception {
        ResponseTag tag = Mockito.mock(ResponseTag.class);
        Mockito.when(tag.getName()).thenReturn("TAG_NAME");
        // empty attributes
        Result result = new Result(tag);
        Assert.assertEquals("TAG_NAME", result.getName());
        Assert.assertEquals(Result.INVALID_VALUE, result.getQueueId());
        Assert.assertEquals(Result.INVALID_VALUE, result.getResult());
        Assert.assertEquals(Result.INVALID_VALUE, result.getStatus());
        // incorrect attributes
        Mockito.when(tag.getAttribute("quid")).thenReturn("aaa");
        Mockito.when(tag.getAttribute("result")).thenReturn("cv");
        Mockito.when(tag.getAttribute("status")).thenReturn("as");
        result = new Result(tag);
        Assert.assertEquals(Result.INVALID_VALUE, result.getQueueId());
        Assert.assertEquals(Result.INVALID_VALUE, result.getResult());
        Assert.assertEquals(Result.INVALID_VALUE, result.getStatus());
        // correct attributes
        Mockito.when(tag.getAttribute("quid")).thenReturn("30");
        Mockito.when(tag.getAttribute("result")).thenReturn("1");
        Mockito.when(tag.getAttribute("status")).thenReturn("4");
        result = new Result(tag);
        Assert.assertEquals(30, result.getQueueId());
        Assert.assertEquals(1, result.getResult());
        Assert.assertEquals(4, result.getStatus());
    }

    @Test
    public void syncResponseStatusCheck() throws Exception {
        ResponseTag tag = Mockito.mock(ResponseTag.class);
        Mockito.when(tag.getName()).thenReturn("TAG_NAME");

        Result result = new Result(tag);
        Assert.assertFalse(result.isPending());
        Assert.assertFalse(result.isAsync());
        Assert.assertFalse(result.isReady());
        Assert.assertTrue(result.isFailed());

        Mockito.when(tag.getAttribute("result")).thenReturn("1");
        result = new Result(tag);
        Assert.assertFalse(result.isPending());
        Assert.assertFalse(result.isAsync());
        Assert.assertFalse(result.isReady());
        Assert.assertTrue(result.isFailed());

        Mockito.when(tag.getAttribute("result")).thenReturn("0");
        result = new Result(tag);
        Assert.assertFalse(result.isPending());
        Assert.assertFalse(result.isAsync());
        Assert.assertTrue(result.isReady());
        Assert.assertFalse(result.isFailed());
    }

    @Test
    public void asyncResponseStatusCheck() throws Exception {
        ResponseTag tag = Mockito.mock(ResponseTag.class);
        Mockito.when(tag.getName()).thenReturn("TAG_NAME");
        Result result;

        Mockito.when(tag.getAttribute("quid")).thenReturn("30");
        Mockito.when(tag.getAttribute("result")).thenReturn("0");
        Mockito.when(tag.getAttribute("status")).thenReturn("1");
        result = new Result(tag);
        Assert.assertTrue(result.isPending());
        Assert.assertTrue(result.isAsync());
        Assert.assertFalse(result.isReady());
        Assert.assertFalse(result.isFailed());

        Mockito.when(tag.getAttribute("quid")).thenReturn("30");
        Mockito.when(tag.getAttribute("result")).thenReturn("0");
        Mockito.when(tag.getAttribute("status")).thenReturn("2");
        result = new Result(tag);
        Assert.assertTrue(result.isPending());
        Assert.assertTrue(result.isAsync());
        Assert.assertFalse(result.isReady());
        Assert.assertFalse(result.isFailed());

        Mockito.when(tag.getAttribute("quid")).thenReturn("30");
        Mockito.when(tag.getAttribute("result")).thenReturn("0");
        Mockito.when(tag.getAttribute("status")).thenReturn("4");
        result = new Result(tag);
        Assert.assertFalse(result.isPending());
        Assert.assertTrue(result.isAsync());
        Assert.assertFalse(result.isReady());
        Assert.assertTrue(result.isFailed());

        Mockito.when(tag.getAttribute("quid")).thenReturn("30");
        Mockito.when(tag.getAttribute("result")).thenReturn("0");
        Mockito.when(tag.getAttribute("status")).thenReturn("5");
        result = new Result(tag);
        Assert.assertFalse(result.isPending());
        Assert.assertTrue(result.isAsync());
        Assert.assertFalse(result.isReady());
        Assert.assertTrue(result.isFailed());

        Mockito.when(tag.getAttribute("quid")).thenReturn("30");
        Mockito.when(tag.getAttribute("result")).thenReturn("0");
        Mockito.when(tag.getAttribute("status")).thenReturn("6");
        result = new Result(tag);
        Assert.assertFalse(result.isPending());
        Assert.assertTrue(result.isAsync());
        Assert.assertFalse(result.isReady());
        Assert.assertTrue(result.isFailed());

        Mockito.when(tag.getAttribute("quid")).thenReturn("30");
        Mockito.when(tag.getAttribute("result")).thenReturn("0");
        Mockito.when(tag.getAttribute("status")).thenReturn("3");
        result = new Result(tag);
        Assert.assertFalse(result.isPending());
        Assert.assertTrue(result.isAsync());
        Assert.assertTrue(result.isReady());
        Assert.assertFalse(result.isFailed());

        Mockito.when(tag.getAttribute("quid")).thenReturn("30");
        Mockito.when(tag.getAttribute("result")).thenReturn("1");
        Mockito.when(tag.getAttribute("status")).thenReturn("3");
        result = new Result(tag);
        Assert.assertFalse(result.isPending());
        Assert.assertTrue(result.isAsync());
        Assert.assertFalse(result.isReady());
        Assert.assertTrue(result.isFailed());
    }
}
