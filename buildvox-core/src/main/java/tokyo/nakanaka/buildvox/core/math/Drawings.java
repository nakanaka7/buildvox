package tokyo.nakanaka.buildvox.core.math;

import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;

import java.util.ArrayList;
import java.util.List;

/** A utility class of voxel drawings */
public class Drawings {
    private Drawings() {
    }
    /**
     * Gets the voxel points of the line based on Bresenham’s algorithm.
     * {@link <a href="https://www.geeksforgeeks.org/bresenhams-algorithm-for-3-d-line-drawing/">Bresenham’s Algorithm for 3-D Line Drawing</a>}
     * Rewrote for java, the original code is for python.
     * @param pos0 the initial point positions.
     * @param pos1 the end point position.
     * @return the voxel positions from pos0 to pos1.
     */
    public static List<Vector3i> line(Vector3i pos0, Vector3i pos1) {
        int x0 = pos0.x();
        int y0 = pos0.y();
        int z0 = pos0.z();
        int x1 = pos1.x();
        int y1 = pos1.y();
        int z1 = pos1.z();
        List<Vector3i> points = new ArrayList<>();
        points.add(new Vector3i(x0, y0, z0));
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int dz = Math.abs(z1 - z0);
        int xs; if (x1 > x0) {xs = 1;} else {xs = -1;}
        int ys; if (y1 > y0) {ys = 1;} else {ys = -1;}
        int zs; if (z1 > z0) {zs = 1;} else {zs = -1;}
        //Driving axis is X-axis
        if (dx >= dy && dx >= dz) {
            int p1 = 2 * dy - dx;
            int p2 = 2 * dz - dx;
            while (x0 != x1) {
                x0 += xs;
                if (p1 >= 0) {
                    y0 += ys;
                    p1 -= 2 * dx;
                }
                if (p2 >= 0) {
                    z0 += zs;
                    p2 -= 2 * dx;
                }
                p1 += 2 * dy;
                p2 += 2 * dz;
                points.add(new Vector3i(x0, y0, z0));
            }
        //Driving axis is Y-axis
        } else if (dy >= dx && dy >= dz) {
            int p1 = 2 * dx - dy;
            int p2 = 2 * dz - dy;
            while (y0 != y1) {
                y0 += ys;
                if (p1 >= 0) {
                    x0 += xs;
                    p1 -= 2 * dy;
                }
                if (p2 >= 0) {
                    z0 += zs;
                    p2 -= 2 * dy;
                }
                p1 += 2 * dx;
                p2 += 2 * dz;
                points.add(new Vector3i(x0, y0, z0));
            }
        //Driving axis is Z-axis
        } else {
            int p1 = 2 * dy - dz;
            int p2 = 2 * dx - dz;
            while (z0 != z1) {
                z0 += zs;
                if (p1 >= 0) {
                    y0 += ys;
                    p1 -= 2 * dz;
                }
                if (p2 >= 0) {
                    x0 += xs;
                    p2 -= 2 * dz;
                }
                p1 += 2 * dy;
                p2 += 2 * dx;
                points.add(new Vector3i(x0, y0, z0));
            }
        }
        return points;
    }

}
