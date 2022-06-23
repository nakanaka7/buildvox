package tokyo.nakanaka.buildvox.core.math;

import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;

import java.util.HashSet;
import java.util.Set;

/** A utility class of voxel drawings */
public class Drawings {
    private Drawings() {
    }
    /**
     * Gets the voxel points of the line based on Bresenham’s algorithm.
     * {@link <a href="https://www.geeksforgeeks.org/bresenhams-algorithm-for-3-d-line-drawing/">Bresenham’s Algorithm for 3-D Line Drawing</a>}
     * Rewrote for java, the original code is for python.
     */
    public static Set<Vector3i> line(Vector3i pos1, Vector3i pos2) {
        int x1 = pos1.x();
        int y1 = pos1.y();
        int z1 = pos1.z();
        int x2 = pos2.x();
        int y2 = pos2.y();
        int z2 = pos2.z();
        Set<Vector3i> points = new HashSet<>();
        points.add(new Vector3i(x1, y1, z1));
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int dz = Math.abs(z2 - z1);
        int xs; if (x2 > x1) {xs = 1;} else {xs = -1;}
        int ys; if (y2 > y1) {ys = 1;} else {ys = -1;}
        int zs; if (z2 > z1) {zs = 1;} else {zs = -1;}
        //Driving axis is X-axis
        if (dx >= dy && dx >= dz) {
            int p1 = 2 * dy - dx;
            int p2 = 2 * dz - dx;
            while (x1 != x2) {
                x1 += xs;
                if (p1 >= 0) {
                    y1 += ys;
                    p1 -= 2 * dx;
                }
                if (p2 >= 0) {
                    z1 += zs;
                    p2 -= 2 * dx;
                }
                p1 += 2 * dy;
                p2 += 2 * dz;
                points.add(new Vector3i(x1, y1, z1));
            }
        //Driving axis is Y-axis
        } else if (dy >= dx && dy >= dz) {
            int p1 = 2 * dx - dy;
            int p2 = 2 * dz - dy;
            while (y1 != y2) {
                y1 += ys;
                if (p1 >= 0) {
                    x1 += xs;
                    p1 -= 2 * dy;
                }
                if (p2 >= 0) {
                    z1 += zs;
                    p2 -= 2 * dy;
                }
                p1 += 2 * dx;
                p2 += 2 * dz;
                points.add(new Vector3i(x1, y1, z1));
            }
        //Driving axis is Z-axis
        } else {
            int p1 = 2 * dy - dz;
            int p2 = 2 * dx - dz;
            while (z1 != z2) {
                z1 += zs;
                if (p1 >= 0) {
                    y1 += ys;
                    p1 -= 2 * dz;
                }
                if (p2 >= 0) {
                    x1 += xs;
                    p2 -= 2 * dz;
                }
                p1 += 2 * dy;
                p2 += 2 * dx;
                points.add(new Vector3i(x1, y1, z1));
            }
        }
        return points;
    }

}
