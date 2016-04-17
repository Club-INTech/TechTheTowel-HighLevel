package tests;

import enums.ServiceNames;
import exceptions.ContainerException;
import exceptions.serial.SerialManagerException;
import org.junit.Test;
import org.junit.runner.JUnitCore;

/**
 * JUnit classique pour tester les yeux du robot
 * @author discord
 */
public class JUnit_Eyes extends JUnit_Test
{

    public static void main(String[] args) throws Exception {

        JUnitCore.main("tests.JUnit_Eyes");
    }

    @Test
    public void test() throws SerialManagerException, ContainerException, InterruptedException {
        container.getService(ServiceNames.THREAD_EYES);
        container.startInstanciedThreads();
        while(true)
        {
            Thread.sleep(1000);
        }
    }
}