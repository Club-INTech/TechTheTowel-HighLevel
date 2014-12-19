package utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Classe statique qui gere les sauvegardes et chargements d'objets dans un fichier.
 * C'est utile si on vuet mettre des  trucs en cache et le cherger en cours de matc
 * 
 * @author Stud, marsu
 *
 */

public class ObjectSerializer
{        
   // TODO; gestion proptre des erreurs
	
	/**
     * Sauvegarde l'objet donné dans un fichier de nom spécifié
     *
     * @param <T> le type de l'objet a enregister
     * @param objectToSave L'objet a sauvegarder
     * @param fileName nom du fichier ou l'objet doit être stocké
     */
    public static <T> void saveObjectToFile(T objectToSave, String fileName)
    {
    	System.out.println("Sauvegarde de "+fileName);
    	try
    	{
			java.io.File fichier_creation;
			FileOutputStream fichier;
			ObjectOutputStream oos;
			
			fichier_creation = new java.io.File(fileName);
			fichier_creation.createNewFile();
			fichier = new FileOutputStream(fileName);
			oos = new ObjectOutputStream(fichier);
			oos.writeObject(objectToSave);
			oos.flush();
			oos.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
    }
    
    /**
     * Renvois l'objet qui a été sauvegardé dans le fichier spécifié
     *
     * @param fileName Le fichier a lire ou a été précédemment enregistré un objet
     * @return l'objet enregistré dans le fichier
     */
    public static Object loadObjectFromFile(String fileName)
    {
    	System.out.println("Chargement de "+fileName);
		try {
			FileInputStream fichier = new FileInputStream(fileName);
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
