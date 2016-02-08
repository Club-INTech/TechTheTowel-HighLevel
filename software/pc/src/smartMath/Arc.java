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
     * @param angle l'angle
     * @param isEnd s'il s'agit de l'angle de fin
     */
    public Arc(Vec2 start, Vec2 end, double angle, boolean isEnd)
    {

        this.start = start;
        this.end = end;

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

        //On tourne le vecteur de l''angle de balayage et on ajoute les coordonnées du centre, on revoie le résultat
        return actualVect.turnNewVector(signe*sweepAngle).plusNewVector(tempArc.center);
    }


    //======================================================================
    // Fonctions de calul des composantes, utilisées par les constructeurs
    //======================================================================
    private void computeFromStartAngle()
    {
        /*
        double directeur1 = startAngle + Math.PI/2;
        double directeur2 = endAngle + Math.PI/2;

        Vec2 a = new Vec2((int)(1000*Math.cos(directeur1)), (int)(1000*Math.sin(directeur1)));
        Vec2 b = new Vec2((int)(1000*Math.cos(directeur2)), (int)(1000*Math.sin(directeur2)));

        Segment s = new Segment(start, start.plusNewVector(a));
        Segment d = new Segment(end, end.plusNewVector(b));
        this.center = Geometry.intersection(s,d);*/

        double alpha = startAngle - end.minusNewVector(start).angle();

        this.radius = start.distance(end) / (2.0*Math.cos(Math.PI/2 - Math.abs(alpha)));
        this.endAngle = startAngle - 2*alpha;
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
        int xc = (int)((((double)end.x*(double)end.x - (double)maxPos.x*(double)maxPos.x + (double)end.y*(double)end.y -(double)maxPos.y*(double)maxPos.y)/(2.0*((double)end.y-(double)maxPos.y)) - ((double)maxPos.x*(double)maxPos.x - (double)start.x*(double)start.x + (double)maxPos.y*(double)maxPos.y - (double)start.y*(double)start.y)/(2.0*((double)maxPos.y-(double)start.y)))/((((double)maxPos.x-(double)start.x)/((double)maxPos.y-(double)start.y)) - (((double)end.x-(double)maxPos.x)/((double)end.y-(double)maxPos.y))));
        int yc = (int)((-1.0)*(double)xc*(((double)maxPos.x-(double)start.x)/((double)maxPos.y-(double)start.y)) + (((double)maxPos.x*(double)maxPos.x - (double)start.x*(double)start.x + (double)maxPos.y*(double)maxPos.y - (double)start.y*(double)start.y)/(2*((double)maxPos.y-(double)start.y))));

        this.center = new Vec2(xc,yc);
        this.radius = start.distance(center);
        this.length = this.radius * Math.abs(end.minusNewVector(center).angle() - start.minusNewVector(center).angle());
    }

    private void computeRadiusLength()
    {
        this.radius = start.distance(center);
        this.length = this.radius * Math.abs(endAngle - startAngle);
    }

    private void computeMaxPos()
    {
        Vec2 sc = start.minusNewVector(center);
        double theta = 0.5*(endAngle - startAngle);
        this.maxPos = sc.turnNewVector(theta).plusNewVector(center);
    }

    private void computeAngles()
    {
        int signe1 = 1;
        int signe2 = 1;
        if(start.minusNewVector(center).angle() - end.minusNewVector(center).angle()>0)
            signe2=-1;
        if(center.y > start.y)
            signe1=-1;
        this.startAngle = (start.minusNewVector(center).angle() + signe1*Math.PI/2) % Math.PI;
        this.endAngle = (end.minusNewVector(center).angle() + signe2*Math.PI/2) % Math.PI;
    }
}