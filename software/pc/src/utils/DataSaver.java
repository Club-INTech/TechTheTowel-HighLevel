package utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

// TODO: Auto-generated Javadoc
/**
 * Classe statique qui gere les sauvegardes et chargements.
 * @author Stud
 *
 */

public class DataSaver {

    /**
     * Instantiates a new data saver.
     */
    private DataSaver()
    {
    }
        
    /**
     * Sauvegarder.
     *
     * @param <T> the generic type
     * @param obj the obj
     * @param filename the filename
     */
    public static <T> void sauvegarder(T obj, String filename)
    {
    	System.out.println("Sauvegarde de "+filename);
    	try {
			java.io.File fichier_creation;
			FileOutputStream fichier;
			ObjectOutputStream oos;
			
			fichier_creation = new java.io.File(filename);
			fichier_creation.createNewFile();
			fichier = new FileOutputStream(filename);
			oos = new ObjectOutputStream(fichier);
			oos.writeObject(obj);
			oos.flush();
			oos.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
    }
    
    /**
     * Charger.
     *
     * @param filename the filename
     * @return the object
     */
    public static Object charger(String filename)
    {
    	System.out.println("Chargement de "+filename);
		try {
			FileInputStream fichier = new FileInputStream(filename);
			ObjectInputStream ois = new ObjectInputStream(fichier);
			Object obj = ois.readObject();
			ois.close();
			return obj;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
    }
}
