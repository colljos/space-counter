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

package org.openspaces.example.counter.feeder;

import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import javax.annotation.Resource;

import org.openspaces.core.GigaSpace;
import org.openspaces.core.SpaceInterruptedException;
import org.openspaces.example.counter.common.Message;

/**
 * A {{Runnable}} that writes data to a remote space periodically using scheduled task.
 */
public class ListBasedFeederTask implements Runnable {
	
    private static final Logger log = Logger.getLogger(ListBasedFeederTask.class.getSimpleName());

    @Resource
    private List<String> premiershipTeamList;

    @Resource
    private GigaSpace gigaSpace;

    private Integer counter = 1;
    
    //use random seed for consistent test behavior
    private Random randomGenerator = new Random(0);

    @Override
	public void run() {
        try {
//            SpaceDocument teamName = selectRandomTeam();
        	Message teamName = selectRandomTeam();
            log.info("Step 0 --- FEEDER WRITING " + teamName);
            gigaSpace.write(teamName);
        } catch (SpaceInterruptedException e) {
            log.fine("We are being shutdown " + e.getMessage());
        } catch (Exception e) {
            log.warning(e.getMessage());
        }
    }
    
    private Message selectRandomTeam() {
        String teamName = premiershipTeamList.get(randomGenerator.nextInt(premiershipTeamList.size()));
        return new Message(counter++, teamName);
    }

//    private SpaceDocument selectRandomTeam() {
//        String teamName = premiershipTeamList.get(randomGenerator.nextInt(premiershipTeamList.size()));
//        return buildData(counter++ 
//                , teamName 
//                , currentTimeMillis());
//    }
//
//    public long getCounter() {
//        return counter;
//    }
//
//
//    public SpaceDocument buildData(long id, String text, long createdAt) {
//        return new SpaceDocument("Tweet", new DocumentProperties() 
//                .setProperty("Id", id) 
//                .setProperty("Text", text) 
//                .setProperty("CreatedAt", new Date(createdAt)) 
//                .setProperty("Processed", false));
//    }
}
