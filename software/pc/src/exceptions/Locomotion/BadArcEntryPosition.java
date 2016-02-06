package exceptions.Locomotion;

import smartMath.Vec2;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Lancée si l'on demande un déplacement selon un arc, mais que le robot n'est pas à la position d'entrée
 * @author discord
 */
public class BadArcEntryPosition extends Exception
{
    public Vec2 aim;

    public Vec2 robotPos;

    public BadArcEntryPosition(Vec2 aim, Vec2 robotPos)
    {
        super();
        this.aim = aim;
        this.robotPos = robotPos;
    }

    public String logStack()
    {
        StringWriter sw = new StringWriter();
        this.printStackTrace(new PrintWriter(sw));

        String exceptionAsString = sw.toString();
        exceptionAsString = exceptionAsString.replaceAll("(\r\n|\n\r|\r|\n)", " -> ");

        return exceptionAsString;
    }
}
