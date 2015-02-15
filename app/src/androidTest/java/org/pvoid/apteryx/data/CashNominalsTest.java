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

import org.fest.reflect.core.Reflection;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class CashNominalsTest {
    @Test
    public void addCheck() throws Exception {
        CashNominals nominals = new CashNominals();
        nominals.add(2., 1);
        CashNominals.Pile[] piles = Reflection.field("mPiles").ofType(CashNominals.Pile[].class).in(nominals).get();
        Assert.assertEquals(1, nominals.getSize());
        Assert.assertNotNull(piles[0]);
        Assert.assertEquals(2., piles[0].getNominal(), 0);
        Assert.assertEquals(1, piles[0].getCount());
        nominals.add(10., 1);
        piles = Reflection.field("mPiles").ofType(CashNominals.Pile[].class).in(nominals).get();
        Assert.assertEquals(2, nominals.getSize());
        Assert.assertNotNull(piles[0]);
        Assert.assertEquals(2., piles[0].getNominal(), 0);
        Assert.assertEquals(1, piles[0].getCount());
        Assert.assertNotNull(piles[1]);
        Assert.assertEquals(10., piles[1].getNominal(), 0);
        Assert.assertEquals(1, piles[1].getCount());
        nominals.add(1., 1);
        piles = Reflection.field("mPiles").ofType(CashNominals.Pile[].class).in(nominals).get();
        Assert.assertEquals(3, nominals.getSize());
        Assert.assertNotNull(piles[0]);
        Assert.assertEquals(1., piles[0].getNominal(), 0);
        Assert.assertEquals(1, piles[0].getCount());
        Assert.assertNotNull(piles[1]);
        Assert.assertEquals(2., piles[1].getNominal(), 0);
        Assert.assertEquals(1, piles[1].getCount());
        Assert.assertNotNull(piles[2]);
        Assert.assertEquals(10., piles[2].getNominal(), 0);
        Assert.assertEquals(1, piles[2].getCount());
        nominals.add(4., 1);
        piles = Reflection.field("mPiles").ofType(CashNominals.Pile[].class).in(nominals).get();
        Assert.assertEquals(4, nominals.getSize());
        Assert.assertNotNull(piles[0]);
        Assert.assertEquals(1., piles[0].getNominal(), 0);
        Assert.assertEquals(1, piles[0].getCount());
        Assert.assertNotNull(piles[1]);
        Assert.assertEquals(2., piles[1].getNominal(), 0);
        Assert.assertEquals(1, piles[1].getCount());
        Assert.assertNotNull(piles[2]);
        Assert.assertEquals(4., piles[2].getNominal(), 0);
        Assert.assertEquals(1, piles[2].getCount());
        Assert.assertNotNull(piles[3]);
        Assert.assertEquals(10., piles[3].getNominal(), 0);
        Assert.assertEquals(1, piles[3].getCount());
        nominals.add(15., 1);
        piles = Reflection.field("mPiles").ofType(CashNominals.Pile[].class).in(nominals).get();
        Assert.assertEquals(5, nominals.getSize());
        Assert.assertNotNull(piles[0]);
        Assert.assertEquals(1., piles[0].getNominal(), 0);
        Assert.assertEquals(1, piles[0].getCount());
        Assert.assertNotNull(piles[1]);
        Assert.assertEquals(2., piles[1].getNominal(), 0);
        Assert.assertEquals(1, piles[1].getCount());
        Assert.assertNotNull(piles[2]);
        Assert.assertEquals(4., piles[2].getNominal(), 0);
        Assert.assertEquals(1, piles[2].getCount());
        Assert.assertNotNull(piles[3]);
        Assert.assertEquals(10., piles[3].getNominal(), 0);
        Assert.assertEquals(1, piles[3].getCount());
        Assert.assertNotNull(piles[4]);
        Assert.assertEquals(15., piles[4].getNominal(), 0);
        Assert.assertEquals(1, piles[4].getCount(), 0);
        nominals.add(13., 1);
        piles = Reflection.field("mPiles").ofType(CashNominals.Pile[].class).in(nominals).get();
        Assert.assertEquals(6, nominals.getSize());
        Assert.assertNotNull(piles[0]);
        Assert.assertEquals(1., piles[0].getNominal(), 0);
        Assert.assertEquals(1, piles[0].getCount());
        Assert.assertNotNull(piles[1]);
        Assert.assertEquals(2., piles[1].getNominal(), 0);
        Assert.assertEquals(1, piles[1].getCount());
        Assert.assertNotNull(piles[2]);
        Assert.assertEquals(4., piles[2].getNominal(), 0);
        Assert.assertEquals(1, piles[2].getCount());
        Assert.assertNotNull(piles[3]);
        Assert.assertEquals(10., piles[3].getNominal(), 0);
        Assert.assertEquals(1, piles[3].getCount());
        Assert.assertNotNull(piles[4]);
        Assert.assertEquals(13., piles[4].getNominal(), 0);
        Assert.assertEquals(1, piles[4].getCount());
        Assert.assertNotNull(piles[5]);
        Assert.assertEquals(15., piles[5].getNominal(), 0);
        Assert.assertEquals(1, piles[5].getCount());
        nominals.add(13., 1);
        piles = Reflection.field("mPiles").ofType(CashNominals.Pile[].class).in(nominals).get();
        Assert.assertEquals(6, nominals.getSize());
        Assert.assertNotNull(piles[0]);
        Assert.assertEquals(1., piles[0].getNominal(), 0);
        Assert.assertEquals(1, piles[0].getCount());
        Assert.assertNotNull(piles[1]);
        Assert.assertEquals(2., piles[1].getNominal(), 0);
        Assert.assertEquals(1, piles[1].getCount());
        Assert.assertNotNull(piles[2]);
        Assert.assertEquals(4., piles[2].getNominal(), 0);
        Assert.assertEquals(1, piles[2].getCount());
        Assert.assertNotNull(piles[3]);
        Assert.assertEquals(10., piles[3].getNominal(), 0);
        Assert.assertEquals(1, piles[3].getCount());
        Assert.assertNotNull(piles[4]);
        Assert.assertEquals(13., piles[4].getNominal(), 0);
        Assert.assertEquals(2, piles[4].getCount());
        Assert.assertNotNull(piles[5]);
        Assert.assertEquals(15., piles[5].getNominal(), 0);
        Assert.assertEquals(1, piles[5].getCount());
    }

    @Test
    public void countCheck() throws Exception {
        CashNominals nominals = new CashNominals(3);
        nominals.add(10., 15);
        nominals.add(5., 10);
        Assert.assertEquals(15, nominals.getCount(10.));
        Assert.assertEquals(10, nominals.getCount(5.));
        Assert.assertEquals(0, nominals.getCount(1.));
    }
}
