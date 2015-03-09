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

import org.pvoid.apteryx.data.agents.Agent;

import java.util.ArrayList;
import java.util.List;

public class GetAgentsResult extends Result {

    private static final String ATTR_ID = "agt_id";
    private static final String ATTR_PARENT_ID = "agt_distributor_id";
    private static final String ATTR_INN = "agt_inn";
    private static final String ATTR_JUR_ADDRESS = "agt_jur_address";
    private static final String ATTR_NAME = "agt_name";
    private static final String ATTR_PHYS_ADDRESS = "agt_phys_address";
    private static final String ATTR_CITY = "city";
    private static final String ATTR_FISCAL_MODE = "fiscal_mode";
    private static final String ATTR_KMM = "kkm_registration_number";
    private static final String ATTR_TAX_REGNUM = "taxpayer_regnum";

    @Nullable
    private final Agent[] mAgents;

    /* package */ GetAgentsResult(@NonNull ResponseTag root) {
        super(root);
        ResponseTag tag;
        List<Agent> agents = null;
        try {
            while ((tag = root.nextChild()) != null) {
                if (!"row".equals(tag.getName())) {
                    continue;
                }
                Agent agent = new Agent(tag.getAttribute(ATTR_ID), tag.getAttribute(ATTR_PARENT_ID),
                        tag.getAttribute(ATTR_INN), tag.getAttribute(ATTR_JUR_ADDRESS),
                        tag.getAttribute(ATTR_PHYS_ADDRESS), tag.getAttribute(ATTR_NAME),
                        tag.getAttribute(ATTR_CITY), tag.getAttribute(ATTR_FISCAL_MODE),
                        tag.getAttribute(ATTR_KMM), tag.getAttribute(ATTR_TAX_REGNUM));
                if (agents == null) {
                    agents = new ArrayList<>();
                }
                agents.add(agent);
            }
        } catch (ResponseTag.TagReadException e) {
            LOG.error("Can't create GetAgentsResult", e);
        }
        if (agents == null) {
            mAgents = null;
        } else {
            Agent[] a = new Agent[agents.size()];
            a = agents.toArray(a);
            mAgents = a;
        }
    }

    @Nullable
    public Agent[] getAgents() {
        return mAgents;
    }

    public static class Factory implements ResultFactory {
        @Nullable
        @Override
        public Result create(@NonNull ResponseTag tag) {
            return new GetAgentsResult(tag);
        }
    }
}
