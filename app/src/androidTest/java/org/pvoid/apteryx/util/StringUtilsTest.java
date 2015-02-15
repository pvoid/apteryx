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

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class StringUtilsTest {
    @Test
    public void parseIntCheck() throws Exception {
        Assert.assertEquals(100, StringUtils.parseInt(null, 100));
        Assert.assertEquals(100, StringUtils.parseInt("", 100));
        Assert.assertEquals(100, StringUtils.parseInt("asd", 100));
        Assert.assertEquals(200, StringUtils.parseInt("200", 100));
    }

    @Test
    public void parseFLoatCheck() throws Exception {
        Assert.assertEquals(100.1f, StringUtils.parseFloat(null, 100.1f), 0);
        Assert.assertEquals(100.1f, StringUtils.parseFloat("", 100.1f), 0);
        Assert.assertEquals(100.1f, StringUtils.parseFloat("asd", 100.1f), 0);
        Assert.assertEquals(200.05f, StringUtils.parseFloat("200.05", 100.1f), 0);
    }

    @Test
    public void parseLongCheck() throws Exception {
        Assert.assertEquals(100l, StringUtils.parseLong(null, 100l));
        Assert.assertEquals(100l, StringUtils.parseLong("", 100l));
        Assert.assertEquals(100l, StringUtils.parseLong("asd", 100l));
        Assert.assertEquals(200l, StringUtils.parseLong("200", 100l));
    }

    @Test
    public void parseDoubleCheck() throws Exception {
        Assert.assertEquals(100., StringUtils.parseDouble(null, 100.), 0);
        Assert.assertEquals(100., StringUtils.parseDouble("", 100.), 0);
        Assert.assertEquals(100., StringUtils.parseDouble("asd", 100.), 0);
        Assert.assertEquals(200., StringUtils.parseDouble("200", 100.), 0);
    }
}
