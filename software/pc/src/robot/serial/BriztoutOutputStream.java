package robot.serial;


import java.io.BufferedOutputStream;
import java.io.OutputStream;

/**
 * Encapsule un OutputStream en implémentant une fonction de vidage du buffer propre au lieu de le vider, comme un schlag, dans la gueule de
 * la STM32 ; garde les mêmes fonctions d'un OutputStream
 * @author discord
 */
public class BriztoutOutputStream extends BufferedOutputStream
{
    /**
     * Taille maximale du buffer
     */
    private int bufferSize;

    /**
     * Encapsule l'OutputStream avec une taille de buffer par défaut de 8192
     * @param outputStream l'OutpuStream à encapsuler
     */
    public BriztoutOutputStream(OutputStream outputStream) {
        super(outputStream);
        this.bufferSize = 8192;
    }


    /**
     * Encapsule l'OutputStream avec une taille de buffer donnée
     * @param outputStream l'OutpuStream à encapsuler
     * @param size taille du buffer
     */
    public BriztoutOutputStream(OutputStream outputStream, int size) {
        super(outputStream);
        if(size > 0)
            this.bufferSize = size;
        else
            this.bufferSize = 8192;
    }

    /**
     * Vide le buffer proprement
     */
    public void clear()
    {
        this.buf = new byte[bufferSize];
        this.count = 0;
    }
}