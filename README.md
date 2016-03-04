=====================================================================================
=== Counting Data Items with GigaSpaces (exercise)
===
=== Jose Coll
=== 3 March 2016 (using XAP 10.2.1)
=====================================================================================

Requirement
Create an application that writes and counts Data Item entries into the Gigaspaces IMDG.

Solution
The solution demonstrates feeding in data items (Premiership football team names) into the system , then processing them in realtime, calculating counters for realtime analysis, using GigaSpaces XAP.

The solution utilises 2 processing units (Feeder, Processor), 3 operations (write, take, change) and 2 Data Types (Message, Counter). The Change operation also returns details of the change made for reporting purposes (using ChangeModifiers.RETURN_DETAILED_RESULTS).

Deployment
The Feeder uses a scheduler to send events the the IMDG (default one per sec). This can be changed in it's Spring configuration (pu.xml).
The SLA for each processing unit is configured to use a cluster or 2 active instances and 1 backup instance. 

(Embedded) IDE deployment and testing:
Components are deployed in a Unicast environment by specifying:
-Dcom.gs.jini_lus.locators=colljos-DELLXPS:4174 -Dcom.gs.multicast.enabled=false

XAP Standalone deployment and testing:
start /min gs-agent.bat
gs deploy ..\processor\target\space-counter-processor1.0.jar
gs deploy ..\feeder\target\space-counter-feeder1.0

Operational
Once the application is running, you can use the XAP UI tools to view your application , access the data and the counters and manage the application. For example, you can obtain an ordered list of all entries in the IMDG, by issuing the following Query: 
select uid,* from org.openspaces.example.counter.common.Counter order by counter desc