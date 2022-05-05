package tokyo.nakanaka.buildvox.core.math.region3d;

import tokyo.nakanaka.buildvox.core.math.MaxMinCalculator;
import tokyo.nakanaka.buildvox.core.math.transformation.AffineTransformation3d;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3d;

public class Parallelepiped {
    private Vector3d vecRA;
    private Vector3d vecRB;
    private Vector3d vecRC;
    private Vector3d vecOR;

    public Parallelepiped(double x1, double y1, double z1, double x2, double y2, double z2){
        var pos1 = new Vector3d(x1, y1, z1);
        var pos2 = new Vector3d(x2, y2, z2);
        var vec21 = pos2.subtract(pos1);
        this.vecRA = new Vector3d(vec21.x(), 0, 0);
        this.vecRB = new Vector3d(0, vec21.y(), 0);
        this.vecRC = new Vector3d(0, 0, vec21.z());
        this.vecOR = pos1;
    }

    private Parallelepiped(Vector3d vecRA, Vector3d vecRB, Vector3d vecRC, Vector3d vecOR) {
        this.vecRA = vecRA;
        this.vecRB = vecRB;
        this.vecRC = vecRC;
        this.vecOR = vecOR;
    }

    public Vector3d vectorOR(){
        return this.vecOR;
    }

    public Vector3d vectorRA(){
        return this.vecRA;
    }

    public Vector3d vectorRB(){
        return this.vecRB;
    }

    public Vector3d vectorRC(){
        return this.vecRC;
    }

    public Parallelepiped affineTransform(AffineTransformation3d trans){
        AffineTransformation3d linear = trans.linear();
        var vecRAt = linear.apply(this.vecRA);
        var vecRBt = linear.apply(this.vecRB);
        var vecRCt = linear.apply(this.vecRC);
        var vecORt = trans.apply(this.vecOR);
        return new Parallelepiped(vecRAt, vecRBt, vecRCt, vecORt);
    }

    public Parallelepiped translate(double dx, double dy, double dz){
        return this.affineTransform(AffineTransformation3d.ofTranslation(dx, dy, dz));
    }

    public double maxX(){
        double rx = this.vecOR.x();
        double ax = this.vecOR.x() + this.vecRA.x();
        double bx = this.vecOR.x() + this.vecRB.x();
        double cx = this.vecOR.x() + this.vecRC.x();
        double a0x = this.vecOR.x() + this.vecRB.x() + this.vecRC.x();
        double b0x = this.vecOR.x() + this.vecRC.x() + this.vecRA.x();
        double c0x = this.vecOR.x() + this.vecRA.x() + this.vecRB.x();
        double r0x = this.vecOR.x() + this.vecRA.x() + this.vecRB.x() + this.vecRC.x();
        return MaxMinCalculator.max(rx, ax, bx, cx, a0x, b0x, c0x, r0x);
    }

    public double maxY(){
        double ry = this.vecOR.y();
        double ay = this.vecOR.y() + this.vecRA.y();
        double by = this.vecOR.y() + this.vecRB.y();
        double cy = this.vecOR.y() + this.vecRC.y();
        double a0y = this.vecOR.y() + this.vecRB.y() + this.vecRC.y();
        double b0y = this.vecOR.y() + this.vecRC.y() + this.vecRA.y();
        double c0y = this.vecOR.y() + this.vecRA.y() + this.vecRB.y();
        double r0y = this.vecOR.y() + this.vecRA.y() + this.vecRB.y() + this.vecRC.y();
        return MaxMinCalculator.max(ry, ay, by, cy, a0y, b0y, c0y, r0y);
    }

    public double maxZ(){
        double rz = this.vecOR.z();
        double az = this.vecOR.z() + this.vecRA.z();
        double bz = this.vecOR.z() + this.vecRB.z();
        double cz = this.vecOR.z() + this.vecRC.z();
        double a0z = this.vecOR.z() + this.vecRB.z() + this.vecRC.z();
        double b0z = this.vecOR.z() + this.vecRC.z() + this.vecRA.z();
        double c0z = this.vecOR.z() + this.vecRA.z() + this.vecRB.z();
        double r0z = this.vecOR.z() + this.vecRA.z() + this.vecRB.z() + this.vecRC.z();
        return MaxMinCalculator.max(rz, az, bz, cz, a0z, b0z, c0z, r0z);
    }

    public double minX(){
        double rx = this.vecOR.x();
        double ax = this.vecOR.x() + this.vecRA.x();
        double bx = this.vecOR.x() + this.vecRB.x();
        double cx = this.vecOR.x() + this.vecRC.x();
        double a0x = this.vecOR.x() + this.vecRB.x() + this.vecRC.x();
        double b0x = this.vecOR.x() + this.vecRC.x() + this.vecRA.x();
        double c0x = this.vecOR.x() + this.vecRA.x() + this.vecRB.x();
        double r0x = this.vecOR.x() + this.vecRA.x() + this.vecRB.x() + this.vecRC.x();
        return MaxMinCalculator.min(rx, ax, bx, cx, a0x, b0x, c0x, r0x);
    }

    public double minY(){
        double ry = this.vecOR.y();
        double ay = this.vecOR.y() + this.vecRA.y();
        double by = this.vecOR.y() + this.vecRB.y();
        double cy = this.vecOR.y() + this.vecRC.y();
        double a0y = this.vecOR.y() + this.vecRB.y() + this.vecRC.y();
        double b0y = this.vecOR.y() + this.vecRC.y() + this.vecRA.y();
        double c0y = this.vecOR.y() + this.vecRA.y() + this.vecRB.y();
        double r0y = this.vecOR.y() + this.vecRA.y() + this.vecRB.y() + this.vecRC.y();
        return MaxMinCalculator.min(ry, ay, by, cy, a0y, b0y, c0y, r0y);
    }

    public double minZ(){
        double rz = this.vecOR.z();
        double az = this.vecOR.z() + this.vecRA.z();
        double bz = this.vecOR.z() + this.vecRB.z();
        double cz = this.vecOR.z() + this.vecRC.z();
        double a0z = this.vecOR.z() + this.vecRB.z() + this.vecRC.z();
        double b0z = this.vecOR.z() + this.vecRC.z() + this.vecRA.z();
        double c0z = this.vecOR.z() + this.vecRA.z() + this.vecRB.z();
        double r0z = this.vecOR.z() + this.vecRA.z() + this.vecRB.z() + this.vecRC.z();
        return MaxMinCalculator.min(rz, az, bz, cz, a0z, b0z, c0z, r0z);
    }

}
