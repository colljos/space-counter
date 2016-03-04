/*
 * Copyright (c) 2012 GigaSpaces Technologies Ltd. All rights reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openspaces.example.counter.processor;

import java.util.logging.Logger;

import org.openspaces.core.GigaSpace;
import org.openspaces.events.EventDriven;
import org.openspaces.events.EventTemplate;
import org.openspaces.events.TransactionalEvent;
import org.openspaces.events.adapter.SpaceDataEvent;
import org.openspaces.events.polling.Polling;
import org.openspaces.events.polling.ReceiveHandler;
import org.openspaces.events.polling.receive.MultiTakeReceiveOperationHandler;
import org.openspaces.events.polling.receive.ReceiveOperationHandler;
import org.openspaces.example.counter.common.Counter;
import org.openspaces.example.counter.common.Message;

import com.gigaspaces.client.ChangeModifiers;
import com.gigaspaces.client.ChangeOperationResult;
import com.gigaspaces.client.ChangeResult;
import com.gigaspaces.client.ChangeSet;
import com.gigaspaces.client.ChangedEntryDetails;
import com.gigaspaces.query.IdQuery;
import com.gigaspaces.sync.change.IncrementOperation;

/**
 * Polling event container polling for {@link TokenCounter} instances and updating atomic counters accordingly.
 * 
 * @author dotan
 */
@EventDriven
@Polling(gigaSpace = "gigaSpace", concurrentConsumers = 1, maxConcurrentConsumers = 1)
@TransactionalEvent
public class MessageCounter {
    private static final Logger log = Logger.getLogger(MessageCounter.class.getName());
    private static final int BATCH_SIZE = 100;


    @ReceiveHandler
    ReceiveOperationHandler receiveHandler() {
        MultiTakeReceiveOperationHandler receiveHandler = new MultiTakeReceiveOperationHandler();
        receiveHandler.setMaxEntries(BATCH_SIZE);        
        return receiveHandler;
    }

    @EventTemplate
    Message message() {
        return new Message();
    }

    @SpaceDataEvent
    public void eventListener(Message message,GigaSpace gigaSpace) {

    	IdQuery<Counter> counterIdQuery = new IdQuery<Counter>(Counter.class, message.getInfo());
    	ChangeResult<Counter> changeResult = gigaSpace.change(counterIdQuery, new ChangeSet().increment("counter", 1), ChangeModifiers.RETURN_DETAILED_RESULTS);
    	//No counter for this token already exists - lets create a new one
    	if (changeResult.getNumberOfChangedEntries() == 0) {
        	log.info("Creating new counter for " + message.getInfo());    		
    		gigaSpace.write(new Counter(message.getInfo(), 1));
    	}
    	else {
    		
    	 	for(ChangedEntryDetails<Counter> changedEntryDetails : changeResult.getResults()) {
    	 		//Will get the first change which was applied to an entry, in our case we did only single increment so we will have only one change operation.
    	 		//The order is corresponding to the order of operation applied on the ChangeSet.
    	 		ChangeOperationResult operationResult = changedEntryDetails.getChangeOperationsResults().get(0);
    	 		Number newValue = IncrementOperation.getNewValue(operationResult);
    	   		log.info("Updating counter for " + message.getInfo() + " to <" + newValue + ">");   	 		
    	 	}
    	}
    	
    }

}
