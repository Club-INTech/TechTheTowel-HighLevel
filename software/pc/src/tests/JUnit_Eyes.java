package tests;

import enums.ServiceNames;
import exceptions.ContainerException;
import exceptions.serial.SerialManagerException;
import org.junit.Test;

/**
 * JUnit classique pour tester les yeux du robot
 * @author discord
 */
public class JUnit_Eyes extends JUnit_Test
{

    @Test
    public void test() throws SerialManagerException, ContainerException {
        container.getService(ServiceNames.THREAD_EYES);
        //container.startInstanciedThreads();
    }
}