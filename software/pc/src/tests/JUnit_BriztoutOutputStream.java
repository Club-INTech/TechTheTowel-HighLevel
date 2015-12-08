package tests;


import org.junit.Before;
import org.junit.Test;
import robot.serial.BriztoutOutputStream;

import java.io.IOException;
import java.io.OutputStream;

public class JUnit_BriztoutOutputStream
{
    private BriztoutOutputStream out;

    private OutputStream sub;

    byte [] val;


    @Before
    public void setUp()
    {
        this.sub = new OutputStream() {
            @Override
            public void write(int i) throws IOException {
                System.out.println(i);
            }
        };
        this.out = new BriztoutOutputStream(sub);
        this.val = new byte[]{1,2,3};
    }

    @Test
    public void test()
    {
        try {
            out.write(val);
            out.flush();
            out.write(val);
            out.clear();
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}