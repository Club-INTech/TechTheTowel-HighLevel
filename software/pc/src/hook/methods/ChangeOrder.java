package hook.methods;

import hook.Executable;
import robot.Locomotion;
import smartMath.Vec2;

/**
 * Classe implémentant la méthode changement de consigne, utilisée pour avoir une trajectoire courbe.
 * @author pf, marsu
 *
 */

public class ChangeOrder implements Executable
{

        /** La nouvelle consigne */
        private Vec2 newOrder;
        
        /** le système de locomotion, de haut niveau */
        private Locomotion mLocomotion;
        
        /**
         * Instancie de quoi changer la consigne
         *
         * @param newOrder the nouvelle_consigne
         * @param locomotion le système de locomotion a modifier
         */
        public ChangeOrder(Vec2 newOrder, Locomotion locomotion)
        {
            this.mLocomotion = locomotion;
            this.newOrder = newOrder;
        }
        
        /* (non-Javadoc)
         * @see hook.Executable#execute()
         */
        @Override
        public boolean execute()
        {
            mLocomotion.setAim(newOrder);
            return true; // le robot doit bouger
        }
        
}
