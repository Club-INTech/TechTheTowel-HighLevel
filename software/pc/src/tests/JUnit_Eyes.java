package tests;

import enums.EyesEvent;
import enums.ServiceNames;
import exceptions.ContainerException;
import exceptions.serial.SerialManagerException;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import threads.ThreadEyes;

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
        ThreadEyes eyes = (ThreadEyes) container.getService(ServiceNames.THREAD_EYES);
        container.startInstanciedThreads();
        log.debug("Bite armée");
        Thread.sleep(5000);
        eyes.setEvent(EyesEvent.BEGIN);
        log.debug("BEGIN");
        Thread.sleep(3000);
        eyes.setEvent(EyesEvent.BLOCKED);
        log.debug("BLOCKED");
        Thread.sleep(3000);
        eyes.setEvent(EyesEvent.END);
        log.debug("END");
        Thread.sleep(3000);
        eyes.setEvent(EyesEvent.ENNEMY);
        log.debug("ENNEMY");
        Thread.sleep(3000);
        eyes.setEvent(EyesEvent.POWERON);
        log.debug("POWER ON BIATCH");
        while(true)
        {
            Thread.sleep(1000);
        }
    }
}