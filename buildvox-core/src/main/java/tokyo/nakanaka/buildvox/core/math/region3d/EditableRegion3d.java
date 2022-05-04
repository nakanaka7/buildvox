package tokyo.nakanaka.buildvox.core.math.region3d;

import tokyo.nakanaka.buildvox.core.math.transformation.AffineTransformation3d;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3d;

public class EditableRegion3d implements Region3d {
    private Region3d original;
    private AffineTransformation3d invTrans;

    public EditableRegion3d(Region3d original) {
        this.original = original;
        this.invTrans = AffineTransformation3d.IDENTITY;
    }

    public EditableRegion3d(Region3d original, AffineTransformation3d invTrans) {
        this.original = original;
        this.invTrans = invTrans;
    }

    @Override
    public boolean contains(double x, double y, double z) {
        var invPos = this.invTrans.apply(new Vector3d(x, y, z));
        return this.original.contains(invPos.x(), invPos.y(), invPos.z());
    }

    public EditableRegion3d affineTransform(AffineTransformation3d trans){
        var invAddTrans = trans.inverse();
        var newInvTrans = this.invTrans.compose(invAddTrans);
        return new EditableRegion3d(this.original, newInvTrans);
    }

    public EditableRegion3d translate(double dx, double dy, double dz){
        return this.affineTransform(AffineTransformation3d.ofTranslation(dx, dy, dz));
    }

    /**
     * Returns the instance which represents the rotated region by the specified angle radian
     * about the x-axis. The right-hand rule is applied.
     * @param angle
     * @return
     */
    public EditableRegion3d rotateX(double angle){
        return this.affineTransform(AffineTransformation3d.ofRotationX(angle));
    }

    /**
     * Returns the instance which represents the rotated region by the specified angle radian
     * about the y-axis. The right-hand rule is applied.
     * @param angle
     * @return
     */
    public EditableRegion3d rotateY(double angle){
        return this.affineTransform(AffineTransformation3d.ofRotationY(angle));
    }

    /**
     * Returns the instance which represents the rotated region by the specified angle radian
     * about the z-axis. The right-hand rule is applied.
     * @param angle
     * @return
     */
    public EditableRegion3d rotateZ(double angle){
        return this.affineTransform(AffineTransformation3d.ofRotationZ(angle));
    }

}
