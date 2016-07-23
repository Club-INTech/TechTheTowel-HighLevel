package threads.dataHandlers;

import robot.RobotReal;
import table.Table;
import threads.AbstractThread;
import utils.Sleep;

import java.util.LinkedList;

/**
 *  Gestionnaire des events LL
 */
public class ThreadEvents extends AbstractThread
{

    Table table;

    RobotReal robot;

    LinkedList<String> events;

    public ThreadEvents(Table table, RobotReal robot, ThreadSerial serial)
    {
        this.table = table;
        this.robot = robot;
        events = serial.getEventBuffer();

    }

    @Override
    public void run()
    {
        String event = null;
        Thread.currentThread().setPriority(6);
        while(!ThreadSerial.shutdown)
        {

            Sleep.sleep(100);

            if(events.peek() != null)
                event = events.poll();

            if(event == null)
                continue;

            //==========

            // TODO Events et réactions

            //==========

            event = null;
        }

    }
}
