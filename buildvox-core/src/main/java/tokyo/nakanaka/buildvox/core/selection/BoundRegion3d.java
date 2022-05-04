package tokyo.nakanaka.buildvox.core.selection;

import tokyo.nakanaka.buildvox.core.math.region3d.Cuboid;
import tokyo.nakanaka.buildvox.core.math.region3d.EditableRegion3d;
import tokyo.nakanaka.buildvox.core.math.region3d.Parallelepiped;
import tokyo.nakanaka.buildvox.core.math.region3d.Region3d;
import tokyo.nakanaka.buildvox.core.math.transformation.AffineTransformation3d;

@Deprecated
class BoundRegion3d implements Region3d {
    private EditableRegion3d region;
    private Parallelepiped piped;

    public BoundRegion3d(Region3d region, double ubx, double uby, double ubz,
                         double lbx, double lby, double lbz) {
        if(region instanceof EditableRegion3d editRegion){
            this.region = editRegion;
        }else{
            this.region = new EditableRegion3d(region);
        }
        this.piped = new Parallelepiped(ubx, uby, ubz, lbx, lby, lbz);
    }

    public BoundRegion3d(Region3d region, Parallelepiped piped) {
        this.region = new EditableRegion3d(region);
        this.piped = piped;
    }

    public BoundRegion3d(Region3d region, Cuboid bound) {
        this.region = new EditableRegion3d(region);
        this.piped = new Parallelepiped(bound.x1(), bound.y1(), bound.z1(), bound.x2(), bound.y2(), bound.z2());
    }

    public boolean contains(double x, double y, double z) {
        return this.region.contains(x, y, z);
    }

    public Region3d region(){
        return this.region;
    }

    public Parallelepiped bound(){
        return this.piped;
    }

    public BoundRegion3d affineTransform(AffineTransformation3d trans){
        var transRegion = this.region.affineTransform(trans);
        var transPiped = this.piped.affineTransform(trans);
        return new BoundRegion3d(transRegion, transPiped);
    }

    public BoundRegion3d translate(double dx, double dy, double dz){
        return this.affineTransform(AffineTransformation3d.ofTranslation(dx, dy, dz));
    }

    /**
     * Returns the rotated bound region about x-axis.
     * @param angle the angle(radian)
     * @return the rotated bound region about x-axis.
     */
    public BoundRegion3d rotateX(double angle){
        return this.affineTransform(AffineTransformation3d.ofRotationX(angle));
    }

    /**
     * Returns the rotated bound region about y-axis.
     * @param angle the angle(radian)
     * @return the rotated bound region about y-axis.
     */
    public BoundRegion3d rotateY(double angle){
        return this.affineTransform(AffineTransformation3d.ofRotationY(angle));
    }

    /**
     * Returns the rotated bound region about z-axis.
     * @param angle the angle(radian)
     * @return the rotated bound region about z-axis.
     */
    public BoundRegion3d rotateZ(double angle){
        return this.affineTransform(AffineTransformation3d.ofRotationZ(angle));
    }



}
