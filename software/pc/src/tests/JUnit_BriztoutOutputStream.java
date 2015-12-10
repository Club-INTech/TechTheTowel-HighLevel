package tests;


import enums.ServiceNames;
import exceptions.ContainerException;
import exceptions.serial.SerialManagerException;
import org.junit.Before;
import org.junit.Test;
import robot.Locomotion;
import robot.serial.BriztoutOutputStream;
import utils.Log;

import java.io.IOException;
import java.io.OutputStream;

public class JUnit_BriztoutOutputStream extends JUnit_Test
{
    private BriztoutOutputStream out;

    private OutputStream sub;

    byte [] val;


    @Before
    public void setUp() throws Exception {
        super.setUp();
        this.sub = new OutputStream() {
            @Override
            public void write(int i) throws IOException {
                System.out.println(i);
            }
        };
        this.out = new BriztoutOutputStream(sub);
        this.val = new byte[]{1,2,3};
    }

    //@Test
    public void test()
    {
        try {
            System.out.println("hoy");
            out.clear();
            out.flush();
            out.write(val);
            out.flush();
            out.write(val);
            out.clear();
            out.flush();
            System.out.println("hey");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testRobot()
    {
        Locomotion loc;
        Log log;
        try {
            log = (Log)container.getService(ServiceNames.LOG);
            loc = (Locomotion)container.getService(ServiceNames.LOCOMOTION);
            log.debug(loc.getPosition());
        } catch (ContainerException e) {
            e.printStackTrace();
        } catch (SerialManagerException e) {
            e.printStackTrace();
        }
    }
}