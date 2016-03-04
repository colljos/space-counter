package org.openspaces.example.counter.processor;

import java.util.logging.Logger;

import javax.annotation.Resource;

import org.openspaces.example.counter.common.TokenCounter;
import org.openspaces.core.GigaSpace;
import org.openspaces.events.adapter.SpaceDataEvent;
import org.openspaces.example.counter.common.Message;

import com.gigaspaces.client.WriteModifiers;


/**
 * The processor is passed interesting Objects from its associated PollingContainer
 * <p>The PollingContainer removes objects from the GigaSpace that match the criteria
 * specified for it.
 * <p>Once the Processor receives each Object, it modifies its state and returns it to
 * the PollingContainer which writes them back to the GigaSpace
 * <p/>
 * <p>The PollingContainer is configured in the pu.xml file of this project
 */

public class Processor {
    Logger logger=Logger.getLogger(this.getClass().getName());

    private static final int WRITE_TIMEOUT = 5000;
    private static final int LEASE_TTL = 5000;
    
    @Resource(name = "clusteredGigaSpace")
    GigaSpace clusteredGigaSpace;

    @SpaceDataEvent
    public void processMessage(Message msg) {
        logger.info("Processor writing " + msg.getInfo() + " to cluster");
        String token = msg.getInfo();
        clusteredGigaSpace.write(new TokenCounter(token, 1), LEASE_TTL, WRITE_TIMEOUT, WriteModifiers.UPDATE_OR_WRITE);
    }

    public Processor() {
        logger.info("Processor instantiated, waiting for messages feed...");
    }
}
