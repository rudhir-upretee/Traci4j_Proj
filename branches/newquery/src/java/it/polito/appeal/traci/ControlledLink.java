/*   
    Copyright (C) 2011 ApPeAL Group, Politecnico di Torino

    This file is part of TraCI4J.

    TraCI4J is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    TraCI4J is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with TraCI4J.  If not, see <http://www.gnu.org/licenses/>.
*/

package it.polito.appeal.traci;

import it.polito.appeal.traci.protocol.StringList;

import java.io.IOException;
import java.util.Iterator;

import de.uniluebeck.itm.tcpip.Storage;

public class ControlledLink {
	private final Lane incomingLane;
	private final Lane acrossLane;
	private final Lane outgoingLane;

	ControlledLink(Storage content, Repository<Lane> laneRepo)
			throws IOException {
		StringList list = new StringList(content, true);
		Iterator<String> listIt = list.iterator();
		incomingLane = laneRepo.getByID(listIt.next());
		outgoingLane = laneRepo.getByID(listIt.next());
		acrossLane = laneRepo.getByID(listIt.next());
	}

	public Lane getIncomingLane() {
		return incomingLane;
	}

	public Lane getAcrossLane() {
		return acrossLane;
	}

	public Lane getOutgoingLane() {
		return outgoingLane;
	}
}