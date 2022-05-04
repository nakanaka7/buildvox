package tokyo.nakanaka.buildVoxCore.math.region3d;

import tokyo.nakanaka.buildVoxCore.math.vector.Vector3d;

public class Line3d implements Region3d{
    private Vector3d startPos;
    private OriginStartingLine3d originLine3d;

    /**
     * @param x1 the x-coordinate of position 1
     * @param y1 the y-coordinate of position 1
     * @param z1 the z-coordinate of position 1
     * @param x2 the x-coordinate of position 2
     * @param y2 the y-coordinate of position 2
     * @param z2 the z-coordinate of position 2
     * @param thickness the thickness(diameter) of the line
     */
    public Line3d(double x1, double y1, double z1, double x2, double y2, double z2, double thickness){
        this.startPos = new Vector3d(x1, y1, z1);
        this.originLine3d = new OriginStartingLine3d(x2-x1, y2-y1, z2-z1, thickness);
    }

    @Override
    public boolean contains(double x, double y, double z) {
        return this.originLine3d.contains(x-startPos.x(), y-startPos.y(), z-startPos.z());
    }

    private static class OriginStartingLine3d implements Region3d{
        private double ax;
        private double ay;
        private double az;
        private double thickness;

        public OriginStartingLine3d(double ax, double ay, double az, double thickness) {
            if(thickness <= 0) {
                throw new IllegalArgumentException();
            }
            this.ax = ax;
            this.ay = ay;
            this.az = az;
            this.thickness = thickness;
        }

        @Override
        public boolean contains(double x, double y, double z) {
            Vector3d a = new Vector3d(ax, ay, az);
            Vector3d p = new Vector3d(x, y, z);
            if(p.length() <= this.thickness / 2){
                return true;
            }
            if(p.subtract(a).length() <= this.thickness / 2){
                return true;
            }
            Vector3d ea = a.normalize();
            double l = p.dotProduct(ea);
            Vector3d pl = ea.scalarMultiply(l);
            double distance = p.subtract(pl).length();
            return 0 <= l && l <= a.length() && distance <= this.thickness / 2;
        }
    }
}
