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

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pvoid.apteryx.data.CashNominals;
import org.pvoid.apteryx.data.Currency;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class TerminalsCashTest {
    @Test
    public void serializeCheck() throws Exception {
        TerminalCash cash = new TerminalCash("TERMINAL0", "AGENT0");
        TerminalCash.CashItem item = new TerminalCash.CashItem(Currency.RUR);
        item.addNotesGoBy(5000.0, 10);
        item.addCoinsGoBy(12);
        item.addNotes(100, 5);
        item.addNotes(500, 2);
        item.addNotes(1000, 12);
        item.addNotes(5000, 1);
        item.addCoins(0.50, 10);
        item.addCoins(0.10, 8);
        final double ammount = 500 + 1000 + 12000 + 5000 + 5 + 0.8;
        Assert.assertEquals(ammount, item.getAmmount(), 0);
        cash.addCash(item);

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final DataOutputStream stream = new DataOutputStream(out);
        cash.store(stream);
        Assert.assertNotEquals(0, stream.size());

        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        cash = new TerminalCash(new DataInputStream(in));
        Assert.assertEquals("TERMINAL0", cash.getTerminalId());
        Assert.assertEquals("AGENT0", cash.getAgentId());
        Collection<TerminalCash.CashItem> items = cash.getCash();
        Assert.assertNotNull(item);
        Assert.assertEquals(1, items.size());
        Iterator<TerminalCash.CashItem> it = items.iterator();
        item = it.next();
        Assert.assertNotNull(item);
        Assert.assertEquals(Currency.RUR, item.getCurrency());
        Assert.assertEquals(5000.0, item.getNotesGoBySum(), 0);
        Assert.assertEquals(10, item.getNotesGoByCount());
        Assert.assertEquals(12, item.getCoinsGoByCount());
        Assert.assertEquals(ammount, item.getAmmount(), 0);

        Map<Double, Integer> vals = new HashMap<>();
        for (CashNominals.Pile pile : item.getNotes()) {
            vals.put(pile.getNominal(), pile.getCount());
        }
        Assert.assertEquals(4, vals.size());
        Assert.assertEquals(5, (int) vals.get(100.));
        Assert.assertEquals(2, (int) vals.get(500.));
        Assert.assertEquals(12, (int) vals.get(1000.));
        Assert.assertEquals(1, (int) vals.get(5000.));

        vals.clear();
        for (CashNominals.Pile pile : item.getCoins()) {
            vals.put(pile.getNominal(), pile.getCount());
        }
        Assert.assertEquals(2, vals.size());
        Assert.assertEquals(10, (int) vals.get(.50));
        Assert.assertEquals(8, (int) vals.get(.10));
    }
}
