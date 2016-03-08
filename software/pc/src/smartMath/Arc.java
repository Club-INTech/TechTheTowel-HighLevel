package smartMath;



/**
 * Classe de calcul pour les arcs de cercle
 * @author discord, paul(formules mathématiques)
 */
public class Arc
{
    /** Rayon d'arc */
    public double radius;

    /** Longueur d'arc */
    public double length;

    /** Début d'arc */
    public Vec2 start;

    /** Fin d'arc */
    public Vec2 end;

    /** Position du maximum */
    private Vec2 maxPos;

    /** Angle (direction) au départ modulo 2PI */
    public double startAngle;

    /** Angle (direction) à la fin modulo 2PI */
    public double endAngle;

    /** Centre du cercle dont l'arc provient */
    private Vec2 center;

    /**
     * Créé un arc à partir de 3 points
     * @param start le début
     * @param end la fin
     * @param maxPos le point à la flèche
     */
    public Arc(Vec2 start, Vec2 end, Vec2 maxPos)
    {
        this.start = start.clone();
        this.end = end.clone();
        this.maxPos = maxPos.clone();

        computeCenterRadiusLength();
        computeAngles();

        if(endAngle < startAngle)
            this.radius *= -1;
    }

    /***
     * Créé un arc à partir de deux point et l'angles de la tangente à la fin ou au début
     * @param start le point de départ
     * @param end le point d'arrivée
     * @param angle l'angle
     * @param isEnd s'il s'agit de l'angle de fin
     */
    public Arc(Vec2 start, Vec2 end, double angle, boolean isEnd)
    {

        this.start = start.clone();
        this.end = end.clone();

        if(isEnd)
        {
            this.endAngle = angle;
            computeFromEndAngle();
        }
        else
        {
            this.startAngle = angle;
            computeFromStartAngle();
        }

        computeCenter(true);

        if(endAngle < startAngle)
            this.radius *= -1;
    }


    /**
     * Inverse le mouvement, l'arrivée devient la fin et inversement
     */
    public void setReverse()
    {
        Vec2 temp = this.end;
        this.end = this.start;
        this.start = temp;
        this.length *= -1;
        this.startAngle = Geometry.modulo(startAngle + Math.PI, 2*Math.PI);
        this.endAngle = Geometry.modulo(endAngle + Math.PI, 2*Math.PI);
    }

    /**
     * Renvoie le point sur la courbe à une distance donnée, méthode absolue et indépendante de start et end de l'arc
     * @param startingPos le point par lequel le robot a commencé le parcours de l''arc
     * @param actualPos la position actuelle du robot
     * @param actualOrientation l'orientation actuelle du robot
     * @param distance la distance souhaitée
     * @return le point à une distance donnée sur l'arc
     */
    public Vec2 getNextPosition(Vec2 startingPos, Vec2 actualPos, double actualOrientation, double distance)
    {
        //On créé un arc temporaire pour obtenir des infos comme le centre
        Arc tempArc = new Arc(startingPos, actualPos, this.startAngle, false);

        //L'angle de balayage que va faire le robot, distance est la corde
        double sweepAngle = 2 * Math.asin(distance / (2 * Math.abs(this.radius)));

        //Inversion de signe d'angle selon le sens de rotation, si on tourne dans le sens anti-trigo, on doit
        //   mettre un angle de balayage négatif
        int signe = 1;
        if(radius<0)
            signe = -1;

        //Ce vecteur va du centre de l'arc vers le point actuel
        Vec2 actualVect = actualPos.minusNewVector(tempArc.center);

        //On tourne le vecteur de l'angle de balayage et on ajoute les coordonnées du centre, on revoie le résultat
        return actualVect.turnNewVector(signe*sweepAngle).plusNewVector(tempArc.center);
    }


    //======================================================================
    // Fonctions de calul des composantes, utilisées par les constructeurs
    //======================================================================
    private void computeFromStartAngle()
    {
        double alpha = startAngle - end.minusNewVector(start).angle();

        this.radius = start.distance(end) / (2.0*Math.cos(Math.PI/2 - Math.abs(alpha)));
        this.endAngle = startAngle + 2*alpha;
        this.length = this.radius*2.0*Math.abs(alpha);
    }

    private void computeFromEndAngle()
    {
        double alpha = -endAngle + end.minusNewVector(start).angle();

        this.radius = start.distance(end) / (2.0*Math.cos(Math.PI/2 - Math.abs(alpha)));
        this.startAngle = endAngle + 2*alpha;
        this.length = this.radius*2.0*Math.abs(alpha);
    }

    private void computeCenterRadiusLength()
    {
        double x1 = start.x;
        double x2 = maxPos.x;
        double x3 = end.x;
        double y1 = start.y;
        double y2 = maxPos.y;
        double y3 = end.y;
        double xc = (-1)*(y2*(y3*y3-y1*y1+x3*x3-x1*x1)+y1*(-y3*y3-x3*x3+x2*x2)+y1*y1*y3-x2*x2*y3 +x1*x1*y3+y2*y2*(y1-y3)) / (2*x2*y3-2*x1*y3+(2*x1-2*x3)*y2+(2*x3-2*x2)*y1);
        double yc = (x2*(y3*y3+x3*x3-x1*x1)+x1*(-y3*y3-x3*x3)+(x1-x3)*y2*y2+(x3-x2)*y1*y1 +x1*x1*x3+x2*x2*(x1-x3)) /(2*x2*y3-2*x1*y3+(2*x1-2*x3)*y2+(2*x3-2*x2)*y1);
        this.center = new Vec2((int)xc,(int)yc);
        this.radius = start.distance(center);
        this.length = this.radius * Math.abs(end.minusNewVector(center).angle() - start.minusNewVector(center).angle());
    }

    private void computeRadiusLength()
    {
        this.radius = start.distance(center);
        this.length = this.radius * Math.abs(endAngle - startAngle);
    }

    private void computeCenter(boolean way)
    {
        double theta;
        if(way)
            theta = startAngle + Math.PI/2;
        else
            theta= startAngle - Math.PI/2;

        Vec2 movement = new Vec2((int)(this.radius * Math.cos(theta)),(int)(this.radius * Math.sin(theta)));
        Vec2 supposedcenter = start.plusNewVector(movement);

        if(way && (end.minusNewVector(supposedcenter).length() - this.radius) > 2)
            this.computeCenter(false);
        else
            this.center = supposedcenter;
    }

    private void computeAngles()
    {
        int signe1 = 1;
        int signe2 = 1;
        if(start.minusNewVector(center).angle() - end.minusNewVector(center).angle()>0)
            signe2=-1;
        if(center.y < start.y)
            signe1=-1;
        this.startAngle = (start.minusNewVector(center).angle() + signe1*Math.PI/2) % Math.PI;
        this.endAngle = (end.minusNewVector(center).angle() + signe2*Math.PI/2) % Math.PI;
    }
}