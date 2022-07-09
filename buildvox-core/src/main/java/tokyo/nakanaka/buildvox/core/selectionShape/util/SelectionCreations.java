package tokyo.nakanaka.buildvox.core.selectionShape.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tokyo.nakanaka.buildvox.core.math.MaxMinCalculator;
import tokyo.nakanaka.buildvox.core.math.transformation.AffineTransformation3d;
import tokyo.nakanaka.buildvox.core.math.vector.PolarVector2d;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3d;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.Axis;
import tokyo.nakanaka.buildvox.core.math.region3d.*;
import tokyo.nakanaka.buildvox.core.selection.Selection;

/** The utility class which has selection creation methods. */
public class SelectionCreations {
    private SelectionCreations(){
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(SelectionCreations.class);

    /**
     * Creates a default selection of pos array depends on its length. The length 2, 3, and 4 pos array will return cuboid, triangle
     * (thickness 1) and tetrahedron selection respectively.
     * @param posArray the pos array
     * @return a selection of pos array depends on its size.
     * @throws IllegalArgumentException if the pos array length is not 2, 3, nor 4.
     */
    public static Selection createDefault(Vector3i... posArray) {
        int length = posArray.length;
        if(length == 2) {
            return createCuboid(posArray[0], posArray[1]);
        }else if(length == 3) {
            return createTriangle(posArray[0], posArray[1], posArray[2], 1);
        }else if(length == 4) {
            return createTetrahedron(posArray[0], posArray[1], posArray[2], posArray[3]);
        }else{
            throw new IllegalArgumentException();
        }
    }

    /**
     * Create a cuboid-shaped selection. The cuboid corner will be pos 0 and pos 1.
     * @param pos0 the corner position.
     * @param pos1 the corner position.
     * @return a cuboid selection. The cuboid corner will be pos 0 and pos 1.
     */
    public static Selection createCuboid(Vector3i pos0, Vector3i pos1) {
        int maxX = Math.max(pos0.x(), pos1.x());
        int maxY = Math.max(pos0.y(), pos1.y());
        int maxZ = Math.max(pos0.z(), pos1.z());
        int minX = Math.min(pos0.x(), pos1.x());
        int minY = Math.min(pos0.y(), pos1.y());
        int minZ = Math.min(pos0.z(), pos1.z());
        Cuboid cuboid = new Cuboid(maxX + 1, maxY + 1, maxZ + 1, minX, minY, minZ);
        return new Selection(cuboid, cuboid);
    }

    /**
     * Creates a line-shaped line.
     * @param pos0 the beginning position.
     * @param pos1 the end position.
     * @param thickness the diameter.
     * @return a line-shaped line.
     * @throws IllegalArgumentException if thickness is less than or equals to 0.
     */
    public static Selection createLine(Vector3i pos0, Vector3i pos1, int thickness) {
        return createLine(pos0.toVector3d(), pos1.toVector3d(), thickness);
    }

    /**
     * @throws IllegalArgumentException if thickness <= 0
     */
    private static Selection createLine(Vector3d pos1, Vector3d pos2, double thickness){
        if (thickness <= 0) {
            throw new IllegalArgumentException();
        }
        Vector3d p1 = pos1.add(0.5, 0.5, 0.5);
        Vector3d p2 = pos2.add(0.5, 0.5, 0.5);
        var line3d = new Line3d(p1.x(), p1.y(), p1.z(), p2.x(), p2.y(), p2.z(), thickness);
        double ubx = Math.max(p1.x(), p2.x()) + thickness / 2;
        double uby = Math.max(p1.y(), p2.y()) + thickness / 2;
        double ubz = Math.max(p1.z(), p2.z()) + thickness / 2;
        double lbx = Math.min(p1.x(), p2.x()) - thickness / 2;
        double lby = Math.min(p1.y(), p2.y()) - thickness / 2 ;
        double lbz = Math.min(p1.z(), p2.z()) - thickness / 2;
        var bound = new Parallelepiped(ubx, uby, ubz, lbx, lby, lbz);
        return new Selection(line3d, bound);
    }

    /**
     * Create a plane-shaped selection.
     * @param axis the axis which is parallel to the plane
     * @param thickness the thickness of the plane
     * @return a plane-shaped selection.
     * @throws IllegalArgumentException if thickness is less than or equals to 0.
     */
    public static Selection createPlane(Vector3i pos0, Vector3i pos1, Axis axis, int thickness) {
        if (thickness <= 0) {
            throw new IllegalArgumentException();
        }
        Vector3d p0 = pos0.toVector3d();
        Vector3d p1 = pos1.toVector3d();
        Selection selection;
        switch (axis) {
            case X -> {
                Vector3d pos10 = p1.subtract(p0);
                Vector3d rotZ90Pos10 = AffineTransformation3d.ofRotationZ(Math.PI / 2).apply(pos10);
                Selection plane = createWall(Vector3d.ZERO, rotZ90Pos10, thickness);
                plane = plane.rotateZ(- Math.PI / 2);
                selection = plane.translate(p0.x(), p0.y() + 1, p0.z());
            }
            case Z -> {
                Vector3d pos10 = p1.subtract(p0);
                Vector3d rotX90Pos10 = AffineTransformation3d.ofRotationX(Math.PI / 2).apply(pos10);
                Selection plane = createWall(Vector3d.ZERO, rotX90Pos10, thickness);
                plane = plane.rotateX(- Math.PI / 2);
                selection = plane.translate(p0.x(), p0.y(), p0.z() + 1);
            }
            default -> selection = createWall(p0, p1, thickness);
        }
        return selection;
    }

    private static Selection createWall(Vector3d pos1, Vector3d pos2, double thickness){
        Vector3d p1 = pos1.add(0.5, 0.5, 0.5);
        Vector3d p2 = pos2.add(0.5, 0.5, 0.5);
        if(p1.y() > p2.y()){
            Vector3d tp1 = p1;
            p1 = p2;
            p2 = tp1;
        }
        Vector3d ph = new Vector3d(p2.x(), p1.y(), p2.z());
        double height = p2.y() - p1.y() + 1;
        double length = ph.distance(p1) + 1;
        Cuboid cuboid = new Cuboid(0, 0, 0, thickness, height, length);
        Selection selection = new Selection(cuboid, 0, 0, 0, thickness, height, length);
        Vector3d vh1 = ph.subtract(p1);
        PolarVector2d pv = PolarVector2d.newInstance(vh1.z(), vh1.x());
        double arg = pv.argument();
        return selection.translate(- 0.5 * thickness, - 0.5, - 0.5)
                .rotateY(arg).translate(p1.x(), p1.y(),p1.z());
    }

    /**
     * Creates a cuboid frame-shaped selection.
     * @param pos0 the corner position.
     * @param pos1 the corner position.
     * @throws IllegalArgumentException if thickness is less than or equals to 0.
     */
    public static Selection createFrame(Vector3i pos0, Vector3i pos1, int thickness){
        if (thickness <= 0) {
            throw new IllegalArgumentException();
        }
        double px = Math.max(pos0.x(), pos1.x()) + 1;
        double py = Math.max(pos0.y(), pos1.y()) + 1;
        double pz = Math.max(pos0.z(), pos1.z()) + 1;
        double nx = Math.min(pos0.x(), pos1.x());
        double ny = Math.min(pos0.y(), pos1.y());
        double nz = Math.min(pos0.z(), pos1.z());
        var nnnToPnn = new Cuboid(nx, ny, nz, px, ny + thickness, nz + thickness);
        var nnnToNpn = new Cuboid(nx, ny, nz, nx + thickness, py, nz + thickness);
        var nnnToNnp = new Cuboid(nx, ny, nz, nx + thickness, ny + thickness, pz);
        var pnnToPpn = new Cuboid(px - thickness, ny, nz, px, py, nz + thickness);
        var pnnToPnp = new Cuboid(px - thickness, ny, nz, px, ny + thickness, pz);
        var npnToPpn = new Cuboid(nx, py - thickness, nz, px, py, nz + thickness);
        var npnToNpp = new Cuboid(nx, py - thickness, nz, nx + thickness, py, pz);
        var nnpToPnp = new Cuboid(nx, ny, pz - thickness, px, ny + thickness, pz);
        var nnpToNpp = new Cuboid(nx, ny, pz - thickness, nx + thickness, py, pz);
        var ppnToPpp = new Cuboid(px - thickness, py - thickness, nz, px, py, pz);
        var pnpToPpp = new Cuboid(px - thickness, ny, pz - thickness, px, py, pz);
        var nppToPpp = new Cuboid(nx, py - thickness, pz - thickness, px, py, pz);
        var region = new UnionRegion3d(nnnToPnn, nnnToNpn, nnnToNnp,
                pnnToPpn, pnnToPnp, npnToPpn, npnToNpp, nnpToPnp, nnpToNpp,
                ppnToPpp, pnpToPpp, nppToPpp);
        var bound = new Parallelepiped(px, py, pz, nx, ny, nz);
        return new Selection(region, bound);
    }

    /**
     * Creates a triangle-shaped selection.
     * @param pos0 the vertex.
     * @param pos1 the vertex.
     * @param pos2 the vertex.
     * @return a triangle-shaped selection
     * @throws IllegalArgumentException if thickness is less than or equals to 0.
     */
    public static Selection createTriangle(Vector3i pos0, Vector3i pos1, Vector3i pos2, int thickness) {
        if (thickness <= 0) {
            throw new IllegalArgumentException();
        }
        Vector3d pos0d = pos0.toVector3d();
        Vector3d pos1d = pos1.toVector3d();
        Vector3d pos2d = pos2.toVector3d();
        Vector3d p1 = pos0d.add(0.5, 0.5, 0.5);
        Vector3d p2 = pos1d.add(0.5, 0.5, 0.5);
        Vector3d p3 = pos2d.add(0.5, 0.5, 0.5);
        Vector3d pg = p1.add(p2).add(p3).scalarMultiply(1.0/3.0);
        Vector3d v1 = p1.subtract(pg);
        Vector3d v2 = p2.subtract(pg);
        Vector3d v3 = p3.subtract(pg);
        if(!v1.equals(Vector3d.ZERO)){
            v1 = v1.normalize().scalarMultiply(0.5);
        }
        if(!v2.equals(Vector3d.ZERO)){
            v2 = v2.normalize().scalarMultiply(0.5);
        }
        if(!v3.equals(Vector3d.ZERO)){
            v3 = v3.normalize().scalarMultiply(0.5);
        }
        p1 = p1.add(v1);
        p2 = p2.add(v2);
        p3 = p3.add(v3);
        var region = new Triangle(p1.x(), p1.y(), p1.z(),
                p2.x(), p2.y(), p2.z(),
                p3.x(), p3.y(), p3.z(), thickness);
        double ubx = MaxMinCalculator.max(p1.x(), p2.x(), p3.x()) + thickness / 2.0;
        double uby = MaxMinCalculator.max(p1.y(), p2.y(), p3.y()) + thickness / 2.0;
        double ubz = MaxMinCalculator.max(p1.z(), p2.z(), p3.z()) + thickness / 2.0;
        double lbx = MaxMinCalculator.min(p1.x(), p2.x(), p3.x()) - thickness / 2.0;
        double lby = MaxMinCalculator.min(p1.y(), p2.y(), p3.y()) - thickness / 2.0;
        double lbz = MaxMinCalculator.min(p1.z(), p2.z(), p3.z()) - thickness / 2.0;
        var bound = new Parallelepiped(ubx, uby, ubz, lbx, lby, lbz);
        return new Selection(region, bound);
    }

    /**
     * Create a tetrahedron-shaped selection.
     * @param pos0 the vertex.
     * @param pos1 the vertex.
     * @param pos2 the vertex.
     * @param pos3 the vertex.
     * @return a tetrahedron-shaped selection.
     */
    public static Selection createTetrahedron(Vector3i pos0, Vector3i pos1, Vector3i pos2, Vector3i pos3) {
        Vector3d p0 = pos0.toVector3d().add(0.5, 0.5, 0.5);
        Vector3d p1 = pos1.toVector3d().add(0.5, 0.5, 0.5);
        Vector3d p2 = pos2.toVector3d().add(0.5, 0.5, 0.5);
        Vector3d p3 = pos3.toVector3d().add(0.5, 0.5, 0.5);
        Vector3d pg = p0.add(p1).add(p2).add(p3).scalarMultiply(1.0/4.0);
        Vector3d v0 = p0.subtract(pg);
        Vector3d v1 = p1.subtract(pg);
        Vector3d v2 = p2.subtract(pg);
        Vector3d v3 = p3.subtract(pg);
        if(!v0.equals(Vector3d.ZERO)){
            v0 = v0.normalize().scalarMultiply(0.5);
        }
        if(!v1.equals(Vector3d.ZERO)){
            v1 = v1.normalize().scalarMultiply(0.5);
        }
        if(!v2.equals(Vector3d.ZERO)){
            v2 = v2.normalize().scalarMultiply(0.5);
        }
        if(!v3.equals(Vector3d.ZERO)){
            v3 = v3.normalize().scalarMultiply(0.5);
        }
        Vector3d q0 = p0.add(v0);
        Vector3d q1 = p1.add(v1);
        Vector3d q2 = p2.add(v2);
        Vector3d q3 = p3.add(v3);
        var region = new Tetrahedron(q0.x(), q0.y(), q0.z(),
                q1.x(), q1.y(), q1.z(),
                q2.x(), q2.y(), q2.z(),
                q3.x(), q3.y(), q3.z());
        double ubx = MaxMinCalculator.max(p0.x(), p1.x(), p2.x(), p3.x()) + 0.5;
        double uby = MaxMinCalculator.max(p0.y(), p1.y(), p2.y(), p3.y()) + 0.5;
        double ubz = MaxMinCalculator.max(p0.z(), p1.z(), p2.z(), p3.z()) + 0.5;
        double lbx = MaxMinCalculator.min(p0.x(), p1.x(), p2.x(), p3.x()) - 0.5;
        double lby = MaxMinCalculator.min(p0.y(), p1.y(), p2.y(), p3.y()) - 0.5;
        double lbz = MaxMinCalculator.min(p0.z(), p1.z(), p2.z(), p3.z()) - 0.5;
        var bound = new Parallelepiped(ubx, uby, ubz, lbx, lby, lbz);
        return new Selection(region, bound);
    }

    /**
     * Creates an ellipse-shaped selection in the cuboid.
     * @param pos0 the corner of the cuboid.
     * @param pos1 the corner of the cuboid.
     * @return an ellipse-shaped selection.
     */
    public static Selection createEllipse(Vector3i pos0, Vector3i pos1) {
        double maxX = Math.max(pos0.x(), pos1.x()) + 1;
        double maxY = Math.max(pos0.y(), pos1.y()) + 1;
        double maxZ = Math.max(pos0.z(), pos1.z()) + 1;
        double minX = Math.min(pos0.x(), pos1.x());
        double minY = Math.min(pos0.y(), pos1.y());
        double minZ = Math.min(pos0.z(), pos1.z());
        Vector3d center = new Vector3d(maxX + minX, maxY + minY, maxZ + minZ).scalarMultiply(0.5);
        double radiusX = (maxX - minX) / 2;
        double radiusY = (maxY - minY) / 2;
        double radiusZ = (maxZ - minZ) / 2;
        Sphere sphere = new Sphere(1);
        Selection selection = new Selection(sphere, 1, 1, 1, -1, -1, -1);
        selection = selection.affineTransform(AffineTransformation3d.ofScale(radiusX, radiusY, radiusZ));
        return selection.translate(center.x(), center.y(), center.z());
    }

    private static Selection createOriented(CuboidBoundShapeCreator callback, CuboidSelectionBound cuboidBound, Axis axis) {
        Direction dir = cuboidBound.calculateDirection(axis);
        return createOriented(callback, cuboidBound.pos0(), cuboidBound.pos1(), dir);
    }

        /** Creates a selection which direction is oriented. The cuboid bound of selection remains. */
    private static Selection createOriented(CuboidBoundShapeCreator callback, Vector3i pos0, Vector3i pos1, Direction dir) {
        if(dir == Direction.UP)return callback.create(pos0, pos1);
        double maxXd = Math.max(pos0.x(), pos1.x()) + 1;
        double maxYd = Math.max(pos0.y(), pos1.y()) + 1;
        double maxZd = Math.max(pos0.z(), pos1.z()) + 1;
        double minXd = Math.min(pos0.x(), pos1.x());
        double minYd = Math.min(pos0.y(), pos1.y());
        double minZd = Math.min(pos0.z(), pos1.z());
        AffineTransformation3d trans = switch (dir) {
            case EAST -> AffineTransformation3d.ofRotationZ(Math.PI / 2);
            case WEST -> AffineTransformation3d.ofRotationZ(-Math.PI / 2);
            case SOUTH -> AffineTransformation3d.ofRotationX(-Math.PI / 2);
            case NORTH -> AffineTransformation3d.ofRotationX(Math.PI / 2);
            case DOWN -> AffineTransformation3d.ofRotationZ(Math.PI);
            default -> AffineTransformation3d.IDENTITY;
        };
        Vector3d posMaxD = new Vector3d(maxXd, maxYd, maxZd);
        Vector3d posMinD = new Vector3d(minXd, minYd, minZd);
        Vector3d posTransMaxD = trans.apply(posMaxD);
        Vector3d posTransMinD = trans.apply(posMinD);
        int maxX0I = (int)Math.round(Math.max(posTransMaxD.x(), posTransMinD.x())) - 1;
        int maxY0I = (int)Math.round(Math.max(posTransMaxD.y(), posTransMinD.y())) - 1;
        int maxZ0I = (int)Math.round(Math.max(posTransMaxD.z(), posTransMinD.z())) - 1;
        int minX0I = (int)Math.round(Math.min(posTransMaxD.x(), posTransMinD.x()));
        int minY0I = (int)Math.round(Math.min(posTransMaxD.y(), posTransMinD.y()));
        int minZ0I = (int)Math.round(Math.min(posTransMaxD.z(), posTransMinD.z()));
        return callback.create(new Vector3i(maxX0I, maxY0I, maxZ0I), new Vector3i(minX0I, minY0I, minZ0I))
                .affineTransform(trans.inverse());
    }

    /** A functional interface to create a selection in the cuboid bound. The direction of the selection is lower to
     * upper y-axis. */
    private interface CuboidBoundShapeCreator {
        /** Creates a selection which bound is the cuboid by pos0 and pos1 */
        Selection create(Vector3i pos0, Vector3i pos1);
    }

    /**
     * Creates a hollow cylinder-shape selection in the cuboid.
     * @param pos0 the corner of the cuboid.
     * @param pos1 the corner of the cuboid.
     * @param axis the axis of the cylinder.
     * @param thickness the thickness of the wall.
     * @return a hollow cylinder-shape selection.
     */
    public static Selection createHollowCylinder(Vector3i pos0, Vector3i pos1, Axis axis, int thickness) {
        CuboidSelectionBound outerBound = new CuboidSelectionBound(pos0, pos1);
        Selection outerSel = createCylinder(outerBound, axis);
        CuboidSelectionBound innerBound;
        try {
            innerBound = outerBound.shrinkSides(axis, thickness);
        }catch (IllegalStateException ex) {
            return outerSel;
        }
        Region3d outerReg = outerSel.getRegion3d();
        Region3d innerReg = createCylinder(innerBound, axis).getRegion3d();
        Region3d hollowReg = new DifferenceRegion3d(outerReg, innerReg);
        return new Selection(hollowReg, outerSel.getBound());
    }

    /** Creates a cylinder in the cuboid bound. */
    private static Selection createCylinder(CuboidSelectionBound cuboidBound, Axis axis) {
        return createCylinder(cuboidBound.pos0(), cuboidBound.pos1(), axis);
    }

    /**
     * Creates a cylinder-shaped selection in the cuboid.
     * @param pos0 the corner of the cuboid.
     * @param pos1 the corner of the cuboid.
     * @param axis the axis of the cylinder.
     * @return a cylinder-shaped selection.
     */
    public static Selection createCylinder(Vector3i pos0, Vector3i pos1, Axis axis) {
        Direction dir = calculateDirection(pos0, pos1, axis);
        return createOriented(SelectionCreations::createCylinder, pos0, pos1, dir);
    }

    /**
     * Creates a positive y-oriented cylinder selection which is bounded by the cuboid by pos0 and pos1.
     * @param pos0 pos0
     * @param pos1 pos1
     * @return a cylinder selection
     */
    private static Selection createCylinder(Vector3i pos0, Vector3i pos1) {
        double maxX = Math.max(pos0.x(), pos1.x()) + 1;
        double maxY = Math.max(pos0.y(), pos1.y()) + 1;
        double maxZ = Math.max(pos0.z(), pos1.z()) + 1;
        double minX = Math.min(pos0.x(), pos1.x());
        double minY = Math.min(pos0.y(), pos1.y());
        double minZ = Math.min(pos0.z(), pos1.z());
        var baseCenter = new Vector3d((maxX + minX) / 2, minY, (maxZ + minZ) / 2);
        double radiusX = (maxX - minX) / 2;
        double radiusZ = (maxZ - minZ) / 2;
        double height = maxY - minY;
        Cylinder cylinder = new Cylinder(1, 1);
        return new Selection(cylinder, 1, 1, 1, -1, -1, 0)
                .rotateX(-Math.PI / 2)
                .scale(radiusX, height, radiusZ)
                .translate(baseCenter);
    }

    /**
     * Calculates a direction which is parallel to the axis and the coordinate of the direction is
     * from pos0's to pos1's coordinate.
     */
    private static Direction calculateDirection(Vector3i pos0, Vector3i pos1, Axis axis) {
        return new CuboidSelectionBound(pos0, pos1).calculateDirection(axis);
    }

    /**
     * Creates a hollow cone-shape selection in the cuboid.
     * @param pos0 the corner of the cuboid.
     * @param pos1 the corner of the cuboid.
     * @param axis the axis of the cylinder.
     * @param thickness the thickness of the wall.
     * @return a hollow cone-shape selection.
     */
    public static Selection createHollowCone(Vector3i pos0, Vector3i pos1, Axis axis, int thickness) {
        CuboidSelectionBound outerBound = new CuboidSelectionBound(pos0, pos1);
        Selection outerSel = createCone(outerBound, axis);
        CuboidSelectionBound innerBound;
        double a = outerBound.calculateLength(axis);
        double b = outerBound.calculateMaxSideLength(axis);
        int c = - (int)Math.floor(- (2 * a) / b);
        try {
            innerBound = outerBound.shrinkSides(axis, thickness).shrinkTop(axis, c);
        }catch (IllegalStateException ex) {
            return outerSel;
        }
        Region3d outerReg = outerSel.getRegion3d();
        Region3d innerReg = createCone(innerBound, axis).getRegion3d();
        Region3d hollowReg = new DifferenceRegion3d(outerReg, innerReg);
        return new Selection(hollowReg, outerSel.getBound());
    }

    /** Creates a cylinder in the cuboid bound. */
    private static Selection createCone(CuboidSelectionBound cuboidBound, Axis axis) {
        return createCone(cuboidBound.pos0(), cuboidBound.pos1(), axis);
    }

    /**
     * Creates a cone-shaped selection in the cuboid. The direction from base to apex is
     * from smaller to larger coordinate.
     * @param pos0 the corner of the cuboid.
     * @param pos1 the corner of the cuboid.
     * @param axis the axis.
     * @return a cone-shaped selection.
     */
    public static Selection createCone(Vector3i pos0, Vector3i pos1, Axis axis) {
        Direction dir = calculateDirection(pos0, pos1, axis);
        return createOriented(SelectionCreations::createCone, pos0, pos1, dir);
    }

    /**
     * Creates a positive y-oriented cone selection which is bounded by the cuboid by pos0 and pos1.
     * @param pos0 pos0
     * @param pos1 pos1
     * @return a cone selection
     */
    private static Selection createCone(Vector3i pos0, Vector3i pos1) {
        double maxX = Math.max(pos0.x(), pos1.x()) + 1;
        double maxY = Math.max(pos0.y(), pos1.y()) + 1;
        double maxZ = Math.max(pos0.z(), pos1.z()) + 1;
        double minX = Math.min(pos0.x(), pos1.x());
        double minY = Math.min(pos0.y(), pos1.y());
        double minZ = Math.min(pos0.z(), pos1.z());
        var baseCenter = new Vector3d((maxX + minX) / 2, minY, (maxZ + minZ) / 2);
        double radiusX = (maxX - minX) / 2;
        double radiusZ = (maxZ - minZ) / 2;
        double height = maxY - minY;
        var cone = new Cone(1, 1);
        return new Selection(cone, 1, 1, 1, -1, -1, 0)
                .rotateX(-Math.PI / 2)
                .scale(radiusX, height, radiusZ)
                .translate(baseCenter);
    }

    /**
     * Creates a hollow pyramid-shape selection in the cuboid.
     * @param pos0 the corner of the cuboid.
     * @param pos1 the corner of the cuboid.
     * @param axis the axis of the pyramid.
     * @param thickness the thickness of the wall.
     * @return a hollow pyramid-shape selection.
     */
    public static Selection createHollowPyramid(Vector3i pos0, Vector3i pos1, Axis axis, int thickness) {
        CuboidSelectionBound outerBound = new CuboidSelectionBound(pos0, pos1);
        Selection outerSel = createPyramid(outerBound, axis);
        CuboidSelectionBound innerBound;
        double a = outerBound.calculateLength(axis);
        double b = outerBound.calculateMaxSideLength(axis);
        int c = - (int)Math.floor(- (2 * a) / b);
        try {
            innerBound = outerBound.shrinkSides(axis, thickness).shrinkTop(axis, c);
        }catch (IllegalStateException ex) {
            return outerSel;
        }
        Region3d outerReg = outerSel.getRegion3d();
        Region3d innerReg = createPyramid(innerBound, axis).getRegion3d();
        Region3d hollowReg = new DifferenceRegion3d(outerReg, innerReg);
        return new Selection(hollowReg, outerSel.getBound());
    }

    private static Selection createPyramid(CuboidSelectionBound cuboidBound, Axis axis) {
        return createOriented(SelectionCreations::createPyramid, cuboidBound, axis);
    }

    /**
     * Creates a pyramid-shaped selection in the cuboid. The direction from base to apex is
     * from smaller to larger coordinate.
     * @param pos0 the corner of the cuboid.
     * @param pos1 the corner of the cuboid.
     * @param axis the axis.
     * @return a pyramid-shaped selection.
     */
    public static Selection createPyramid(Vector3i pos0, Vector3i pos1, Axis axis) {
        return createPyramid(new CuboidSelectionBound(pos0, pos1), axis);
    }

    /**
     * Creates a positive y-oriented pyramid selection which is bounded by the cuboid by pos0 and pos1.
     * @param pos0 pos0
     * @param pos1 pos1
     * @return a pyramid selection
     */
    private static Selection createPyramid(Vector3i pos0, Vector3i pos1) {
        double maxXd = Math.max(pos0.x(), pos1.x()) + 1;
        double maxYd = Math.max(pos0.y(), pos1.y()) + 1;
        double maxZd = Math.max(pos0.z(), pos1.z()) + 1;
        double minXd = Math.min(pos0.x(), pos1.x());
        double minYd = Math.min(pos0.y(), pos1.y());
        double minZd = Math.min(pos0.z(), pos1.z());
        var baseCenter = new Vector3d((maxXd + minXd) / 2, minYd, (maxZd + minZd) / 2);
        double sideX = maxXd - minXd;
        double sideZ = maxZd - minZd;
        double height = maxYd - minYd;
        var pyramid = new Pyramid(1, 1);
        return new Selection(pyramid, 0.5, 0.5, 1, -0.5, -0.5, 0)
                .rotateX(-Math.PI / 2)
                .scale(sideX, height, sideZ)
                .translate(baseCenter);
    }

    /**
     * Creates a hollow torus-shape selection in the cuboid.
     * @param pos0 the corner of the cuboid.
     * @param pos1 the corner of the cuboid.
     * @param thickness the thickness of the wall.
     * @return a hollow torus-shape selection.
     */
    public static Selection createHollowTorus(Vector3i pos0, Vector3i pos1, int thickness) {
        CuboidSelectionBound outerBound = new CuboidSelectionBound(pos0, pos1);
        Selection outerSel = createTorus(outerBound);
        CuboidSelectionBound innerBound;
        try {
            innerBound = outerBound.shrinkTop(Axis.Y, thickness)//any axis is ok
                    .shrinkBottom(Axis.Y, thickness)
                    .shrinkSides(Axis.Y, thickness);
        }catch (IllegalStateException ex) {
            return outerSel;
        }
        Region3d outerReg = outerSel.getRegion3d();
        Region3d innerReg = createTorus(innerBound).getRegion3d();
        Region3d hollowReg = new DifferenceRegion3d(outerReg, innerReg);
        return new Selection(hollowReg, outerSel.getBound());
    }

    private static Selection createTorus(CuboidSelectionBound cuboidBound) {
        return createTorus(cuboidBound.pos0(), cuboidBound.pos1());
    }

    /**
     * Creates a torus-shaped selection in the cuboid.
     * @param pos0 the corner of the cuboid.
     * @param pos1 the corner of the cuboid.
     * @return a torus-shaped selection.
     */
    public static Selection createTorus(Vector3i pos0, Vector3i pos1) {
        double maxX = Math.max(pos0.x(), pos1.x()) + 1;
        double maxY = Math.max(pos0.y(), pos1.y()) + 1;
        double maxZ = Math.max(pos0.z(), pos1.z()) + 1;
        double minX = Math.min(pos0.x(), pos1.x());
        double minY = Math.min(pos0.y(), pos1.y());
        double minZ = Math.min(pos0.z(), pos1.z());
        double lx = maxX - minX;
        double ly = maxY - minY;
        double lz = maxZ - minZ;
        double minL = MaxMinCalculator.min(lx, ly, lz);
        if(minL == lx) {
            return createTorus(pos0, pos1, Axis.X);
        }else if(minL == ly) {
            return createTorus(pos0, pos1, Axis.Y);
        }else if(minL == lz) {
            return createTorus(pos0, pos1, Axis.Z);
        }else{
            LOGGER.error("Unexpected");
            return new Selection(new Empty(), 0, 0, 0, 0, 0, 0);
        }
    }

    private static Selection createTorus(Vector3i pos0, Vector3i pos1, Axis axis) {
        Direction dir = calculateDirection(pos0, pos1, axis);
        return createOriented(SelectionCreations::createTorusAlongYAxis, pos0, pos1, dir);
    }

    private static Selection createTorusAlongYAxis(Vector3i pos0, Vector3i pos1) {
        double maxX = Math.max(pos0.x(), pos1.x()) + 1;
        double maxY = Math.max(pos0.y(), pos1.y()) + 1;
        double maxZ = Math.max(pos0.z(), pos1.z()) + 1;
        double minX = Math.min(pos0.x(), pos1.x());
        double minY = Math.min(pos0.y(), pos1.y());
        double minZ = Math.min(pos0.z(), pos1.z());
        var center = new Vector3d((maxX + minX) / 2, (maxY + minY) / 2, (maxZ + minZ) / 2);
        double minorRadius = (maxY - minY) / 2;
        double halfDx = (maxX - minX) / 2;
        double halfDz = (maxZ - minZ) / 2;
        double majorRadius = Math.min(halfDx, halfDz) - minorRadius;
        double scaleFacX = Math.max(1.0, halfDx / halfDz);
        double scaleFacZ = Math.max(1.0, halfDz / halfDx);
        Torus torus = new Torus(majorRadius, minorRadius);
        double l = majorRadius + minorRadius;
        return new Selection(torus, l, l, minorRadius, - l, - l, -minorRadius)
                .rotateX(-Math.PI / 2)
                .scale(scaleFacX, 1, scaleFacZ)
                .translate(center);
    }

}
