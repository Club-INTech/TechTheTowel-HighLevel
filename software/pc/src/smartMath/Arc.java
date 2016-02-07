package smartMath;

/**
 * Classe de calcul pour les arcs de cercle
 * @author discord(et wikipedia...)
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
    public Vec2 maxPos;

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
        this.start = start;
        this.end = end;
        this.maxPos = maxPos;

        computeCenterRadiusLength();
        computeAngles();

        if(endAngle < startAngle)
            this.radius *= -1;
    }

    /***
     * Créé un arc à partir de deux point et les angles des tangentes à la fin et au début
     * @param start le point de départ
     * @param end le point d'arrivée
     * @param startAngle l'angle au départ
     * @param endAngle l'angle à l'arrivée
     */
    public Arc(Vec2 start, Vec2 end, double startAngle, double endAngle)
    {
        this.start = start;
        this.end = end;
        this.startAngle = Geometry.modulo(startAngle, 2*Math.PI);
        this.endAngle = Geometry.modulo(endAngle, 2*Math.PI);

        computeCenterFromAngles();
        computeMaxPos();
        computeRadiusLength();

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
        Arc tempArc = new Arc(startingPos, actualPos, this.startAngle, actualOrientation);

        //L'angle de balayage que va faire le robot, distance est la corde
        double sweepAngle = 2 * Math.asin(distance / (2 * Math.abs(this.radius)));

        //Inversion de signe d'angle selon le sens de rotation, si on tourne dans le sens anti-trigo, on doit
        //   mettre un angle de balayage négatif
        int signe = 1;
        if(radius<0)
            signe = -1;

        //Ce vecteur va du centre de l'arc vers le point actuel
        Vec2 actualVect = actualPos.minusNewVector(tempArc.center);

        //On tourne le vecteur de l''angle de balayage et on ajoute les coordonnées du centre, on revoie le résultat
        return actualVect.turnNewVector(signe*sweepAngle).plusNewVector(tempArc.center);
    }


    //======================================================================
    // Fonctions de calul des composantes, utilisées par les constructeurs
    //======================================================================
    private void computeCenterFromAngles()
    {
        double directeur1 = startAngle + Math.PI/2;
        double directeur2 = endAngle + Math.PI/2;

        Vec2 a = new Vec2((int)(1000*Math.cos(directeur1)), (int)(1000*Math.sin(directeur1)));
        Vec2 b = new Vec2((int)(1000*Math.cos(directeur2)), (int)(1000*Math.sin(directeur2)));

        Segment s = new Segment(start, start.plusNewVector(a));
        Segment d = new Segment(end, end.plusNewVector(b));
        this.center = Geometry.intersection(s,d);

    }

    private void computeCenterRadiusLength()
    {
        Vec2 sm = maxPos.minusNewVector(start);
        Vec2 me = end.minusNewVector(maxPos);

        this.center = new Vec2((me.x - sm.x)/(me.y - sm.y), sm.y*((me.x - sm.x)/(me.y - sm.y)) - sm.x);
        this.radius = start.distance(center);
        this.length = this.radius * Math.abs(end.minusNewVector(center).angle() - start.minusNewVector(center).angle());
    }

    private void computeRadiusLength()
    {
        this.radius = start.distance(center);
        this.length = this.radius * Math.abs(end.minusNewVector(center).angle() - start.minusNewVector(center).angle());
    }

    private void computeMaxPos()
    {
        Vec2 sc = start.minusNewVector(center);
        double theta = 0.5*(end.minusNewVector(center).angle() - start.minusNewVector(center).angle());
        this.maxPos = sc.turnNewVector(theta).plusNewVector(center);
    }

    private void computeAngles()
    {
        this.startAngle = Geometry.modulo(start.minusNewVector(center).angle() - Math.PI/2, 2*Math.PI);
        this.endAngle = Geometry.modulo(end.minusNewVector(center).angle() - Math.PI/2, 2*Math.PI);
    }
}
