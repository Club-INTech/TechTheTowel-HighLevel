package smartMath;

/**
 * Classe de calcul pour les arcs de cercle
 * @author discord
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

    /** Angle (direction) au départ modulo PI */
    public double startAngle;

    /** Angle (direction) à la fin modulo PI */
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
