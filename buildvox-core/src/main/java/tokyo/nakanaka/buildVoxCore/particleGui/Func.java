package tokyo.nakanaka.buildVoxCore.particleGui;

import tokyo.nakanaka.buildVoxCore.math.LineSegment3d;
import tokyo.nakanaka.buildVoxCore.math.region3d.Parallelepiped;
import tokyo.nakanaka.buildVoxCore.math.transformation.AffineTransformation3d;
import tokyo.nakanaka.buildVoxCore.math.vector.Vector3d;

import java.util.HashSet;
import java.util.Set;

@Deprecated
@SuppressWarnings("unused")
class Func {
    private Func(){
    }

    private static Set<Vector3d> particlePosSetOfCuboid(Vector3d[] posArray){
        Set<LineSegment3d> lineSet = new HashSet<>();
        Vector3d pos1 = posArray[0];
        Vector3d pos2 = posArray[1];
        if (pos1 != null) {
            lineSet.addAll(lineSetOfBlock(pos1));
        }
        if (pos2 != null) {
            lineSet.addAll(lineSetOfBlock(pos2));
        }
        if (pos1 != null && pos2 != null) {
            double px = Math.max(pos1.x(), pos2.x()) + 1;
            double py = Math.max(pos1.y(), pos2.y()) + 1;
            double pz = Math.max(pos1.z(), pos2.z()) + 1;
            double nx = Math.min(pos1.x(), pos2.x());
            double ny = Math.min(pos1.y(), pos2.y());
            double nz = Math.min(pos1.z(), pos2.z());
            lineSet.add(new LineSegment3d(nx, ny, nz, px, ny, nz));
            lineSet.add(new LineSegment3d(px, ny, nz, px, py, nz));
            lineSet.add(new LineSegment3d(px, py, nz, nx, py, nz));
            lineSet.add(new LineSegment3d(nx, py, nz, nx, ny, nz));
            lineSet.add(new LineSegment3d(nx, ny, nz, nx, ny, pz));
            lineSet.add(new LineSegment3d(px, ny, nz, px, ny, pz));
            lineSet.add(new LineSegment3d(px, py, nz, px, py, pz));
            lineSet.add(new LineSegment3d(nx, py, nz, nx, py, pz));
            lineSet.add(new LineSegment3d(nx, ny, pz, px, ny, pz));
            lineSet.add(new LineSegment3d(px, ny, pz, px, py, pz));
            lineSet.add(new LineSegment3d(px, py, pz, nx, py, pz));
            lineSet.add(new LineSegment3d(nx, py, pz, nx, ny, pz));
            lineSet.add(new LineSegment3d(pos1.add(0.5, 0.5, 0.5), pos2.add(0.5, 0.5, 0.5)));
        }
        Set<Vector3d> posSet = new HashSet<>();
        for(var line : lineSet){
            posSet.addAll(calcParticlePosSetOfLine(line));
        }
        return posSet;
    }

    private static Set<Vector3d> particlePosSetOfSphere(Vector3d[] posArray){
        Set<Vector3d> posSet = new HashSet<>();
        Set<LineSegment3d> lineSet = new HashSet<>();
        Vector3d pos1 = posArray[0];
        Vector3d pos2 = posArray[1];
        if(pos1 != null && pos2 != null) {
            double radius = pos1.distance(pos2) + 0.5;
            Set<LineSegment3d> unitSphereLines = sphereLineSet();
            Set<LineSegment3d> sphereLines = new HashSet<>();
            for (var line : unitSphereLines) {
                sphereLines.add(
                        line.affineTransform(AffineTransformation3d.ofScale(2 * radius, 2 * radius, 2 * radius))
                                .translate(pos1.x() + 0.5, pos1.y() + 0.5, pos1.z() + 0.5));
            }
            lineSet.addAll(sphereLines);
        }
        for(var line : lineSet){
            posSet.addAll(calcParticlePosSetOfLine(line));
        }
        return posSet;
    }

    private static Set<LineSegment3d> sphereLineSet(){
        Set<LineSegment3d> lineSet = new HashSet<>();
        Vector3d ppp = new Vector3d(0.5, 0.5, 0.5);
        Vector3d ppn = new Vector3d(0.5, 0.5, -0.5);
        Vector3d pnp = new Vector3d(0.5, -0.5, 0.5);
        Vector3d pnn = new Vector3d(0.5, -0.5, -0.5);
        Vector3d npp = new Vector3d(-0.5, 0.5, 0.5);
        Vector3d npn = new Vector3d(-0.5, 0.5, -0.5);
        Vector3d nnp = new Vector3d(-0.5, -0.5, 0.5);
        Vector3d nnn = new Vector3d(-0.5, -0.5, -0.5);
        double a = 1.0 / 2.0;
        Vector3d pppx = ppp.add(-a, 0, 0);
        Vector3d pppy = ppp.add(0, -a, 0);
        Vector3d pppz = ppp.add(0, 0, -a);
        Vector3d ppnx = ppn.add(-a, 0, 0);
        Vector3d ppny = ppn.add(0, -a, 0);
        Vector3d ppnz = ppn.add(0, 0, a);
        Vector3d pnpx = pnp.add(-a, 0, 0);
        Vector3d pnpy = pnp.add(0, a, 0);
        Vector3d pnpz = pnp.add(0, 0, -a);
        Vector3d pnnx = pnn.add(-a, 0, 0);
        Vector3d pnny = pnn.add(0, a, 0);
        Vector3d pnnz = pnn.add(0, 0, a);
        Vector3d nppx = npp.add(a, 0 , 0);
        Vector3d nppy = npp.add(0, -a , 0);
        Vector3d nppz = npp.add(0, 0 , -a);
        Vector3d npnx = npn.add(a, 0, 0);
        Vector3d npny = npn.add(0, -a, 0);
        Vector3d npnz = npn.add(0, 0, a);
        Vector3d nnpx = nnp.add(a, 0 ,0);
        Vector3d nnpy = nnp.add(0, a ,0);
        Vector3d nnpz = nnp.add(0, 0 ,-a);
        Vector3d nnnx = nnn.add(a, 0, 0);
        Vector3d nnny = nnn.add(0, a, 0);
        Vector3d nnnz = nnn.add(0, 0, a);
        lineSet.add(new LineSegment3d(nnnx, pnnx));
        lineSet.add(new LineSegment3d(pnny, ppny));
        lineSet.add(new LineSegment3d(ppnx, npnx));
        lineSet.add(new LineSegment3d(npny, nnny));
        lineSet.add(new LineSegment3d(nnnz, nnpz));
        lineSet.add(new LineSegment3d(pnnz, pnpz));
        lineSet.add(new LineSegment3d(ppnz, pppz));
        lineSet.add(new LineSegment3d(npnz, nppz));
        lineSet.add(new LineSegment3d(nnpx, pnpx));
        lineSet.add(new LineSegment3d(pnpy, pppy));
        lineSet.add(new LineSegment3d(pppx, nppx));
        lineSet.add(new LineSegment3d(nppy, nnpy));
        lineSet.add(new LineSegment3d(nnnx, nnny));
        lineSet.add(new LineSegment3d(nnny, nnnz));
        lineSet.add(new LineSegment3d(nnnz, nnnx));
        lineSet.add(new LineSegment3d(pnnx, pnny));
        lineSet.add(new LineSegment3d(pnny, pnnz));
        lineSet.add(new LineSegment3d(pnnz, pnnx));
        lineSet.add(new LineSegment3d(ppnx, ppny));
        lineSet.add(new LineSegment3d(ppny, ppnz));
        lineSet.add(new LineSegment3d(ppnz, ppnx));
        lineSet.add(new LineSegment3d(npnx, npny));
        lineSet.add(new LineSegment3d(npny, npnz));
        lineSet.add(new LineSegment3d(npnz, npnx));
        lineSet.add(new LineSegment3d(nnpx, nnpy));
        lineSet.add(new LineSegment3d(nnpy, nnpz));
        lineSet.add(new LineSegment3d(nnpz, nnpx));
        lineSet.add(new LineSegment3d(pnpx, pnpy));
        lineSet.add(new LineSegment3d(pnpy, pnpz));
        lineSet.add(new LineSegment3d(pnpz, pnpx));
        lineSet.add(new LineSegment3d(pppx, pppy));
        lineSet.add(new LineSegment3d(pppy, pppz));
        lineSet.add(new LineSegment3d(pppz, pppx));
        lineSet.add(new LineSegment3d(nppx, nppy));
        lineSet.add(new LineSegment3d(nppy, nppz));
        lineSet.add(new LineSegment3d(nppz, nppx));
        return lineSet;
    }

    public static Set<Vector3d> particlePosSetOfTriangle(Vector3d pos1, Vector3d pos2, Vector3d pos3){
        return particlePosSetOfTriangle(new Vector3d[]{pos1, pos2, pos3});
    }

    private static Set<Vector3d> particlePosSetOfTriangle(Vector3d[] posArray){
        Set<Vector3d> posSet = new HashSet<>();
        Set<LineSegment3d> lineSet = new HashSet<>();
        Vector3d pos1 = posArray[0];
        Vector3d pos2 = posArray[1];
        Vector3d pos3 = posArray[2];
        if (pos1 != null && pos2 != null) lineSet.add(new LineSegment3d(pos1, pos2).translate(0.5, 0.5, 0.5));
        if (pos2 != null && pos3 != null) lineSet.add(new LineSegment3d(pos2, pos3).translate(0.5, 0.5, 0.5));
        if (pos3 != null && pos1 != null) lineSet.add(new LineSegment3d(pos3, pos1).translate(0.5, 0.5, 0.5));
        for(var line : lineSet){
            posSet.addAll(calcParticlePosSetOfLine(line));
        }
        return posSet;
    }

    public static Set<Vector3d> particlePosSetOfTetrahedron(Vector3d pos1, Vector3d pos2, Vector3d pos3, Vector3d pos4){
        return particlePosSetOfTetrahedron(new Vector3d[]{pos1, pos2, pos3, pos4});
    }

    private static Set<Vector3d> particlePosSetOfTetrahedron(Vector3d[] posArray){
        Set<Vector3d> posSet = new HashSet<>();
        Set<LineSegment3d> lineSet = new HashSet<>();
        Vector3d pos1 = posArray[0];
        Vector3d pos2 = posArray[1];
        Vector3d pos3 = posArray[2];
        Vector3d pos4 = posArray[3];
        if (pos1 != null && pos2 != null) lineSet.add(new LineSegment3d(pos1, pos2).translate(0.5,  0.5, 0.5));
        if (pos1 != null && pos3 != null) lineSet.add(new LineSegment3d(pos1, pos3).translate(0.5, 0.5, 0.5));
        if (pos1 != null && pos4 != null) lineSet.add(new LineSegment3d(pos1, pos4).translate(0.5,  0.5, 0.5));
        if (pos2 != null && pos3 != null) lineSet.add(new LineSegment3d(pos2, pos3).translate(0.5,  0.5, 0.5));
        if (pos2 != null && pos4 != null) lineSet.add(new LineSegment3d(pos2, pos4).translate(0.5,  0.5, 0.5));
        if (pos3 != null && pos4 != null) lineSet.add(new LineSegment3d(pos3, pos4).translate(0.5,  0.5, 0.5));
        for(var line : lineSet){
            posSet.addAll(calcParticlePosSetOfLine(line));
        }
        return posSet;
    }

    private static Set<Vector3d> particlePosSetOfWall(Vector3d[] posArray){
        Set<Vector3d> posSet = new HashSet<>();
        Set<LineSegment3d> lineSet = new HashSet<>();
        Vector3d pos1 = posArray[0];
        Vector3d pos2 = posArray[1];
        Vector3d pos3 = posArray[2];
        if (pos1 != null && pos2 != null) {
            lineSet.add(new LineSegment3d(pos1.x(), pos1.y(), pos1.z(), pos1.x(), pos2.y(), pos1.z())
                    .translate(0.5, 0.5, 0.5));
        }
        if (pos1 != null && pos3 != null) {
            lineSet.add(new LineSegment3d(pos1.x(), pos1.y(), pos1.z(), pos3.x(), pos1.y(), pos3.z())
                    .translate(0.5, 0.5, 0.5));
        }
        if (pos1 != null && pos2 != null && pos3 != null) {
            lineSet.add(new LineSegment3d(pos3.x(), pos1.y(), pos3.z(), pos3.x(), pos2.y(), pos3.z())
                    .translate(0.5, 0.5, 0.5));
            lineSet.add(new LineSegment3d(pos1.x(), pos2.y(), pos1.z(), pos3.x(), pos2.y(), pos3.z())
                    .translate(0.5, 0.5, 0.5));
        }
        for(var line : lineSet){
            posSet.addAll(calcParticlePosSetOfLine(line));
        }
        return posSet;
    }

    private static Set<Vector3d> particlePosSetOfRamp(Vector3d[] posArray){
        Set<Vector3d> posSet = new HashSet<>();
        Set<LineSegment3d> lineSet = new HashSet<>();
        Vector3d pos1 = posArray[0];
        Vector3d pos2 = posArray[1];
        Vector3d pos3 = posArray[2];
        if (pos1 != null && pos2 != null) {
            lineSet.add(new LineSegment3d(pos1.x(), pos1.y(), pos1.z(), pos2.x(), pos1.y(), pos2.z())
                    .translate(0.5, 0.5, 0.5));
        }
        if (pos1 != null && pos2 != null && pos3 != null) {
            pos2 = new Vector3d(pos2.x(), pos1.y(), pos2.z());
            Vector3d vec21 = pos2.subtract(pos1);
            Vector3d vec31 = pos3.subtract(pos1);
            Vector3d cross2131 = vec21.crossProduct(vec31);
            double area = vec21.crossProduct(vec31).length();
            double l = area / (vec21.length());
            Vector3d vecH = cross2131.crossProduct(vec21).normalize().scalarMultiply(l);
            Vector3d pos1d = pos1.add(vecH);
            Vector3d pos2d = pos2.add(vecH);
            lineSet.add(new LineSegment3d(pos1, pos1d).translate(0.5, 0.5, 0.5));
            lineSet.add(new LineSegment3d(pos2, pos2d).translate(0.5, 0.5, 0.5));
            lineSet.add(new LineSegment3d(pos1d, pos2d).translate(0.5, 0.5, 0.5));
        }
        for(var line : lineSet){
            posSet.addAll(calcParticlePosSetOfLine(line));
        }
        return posSet;
    }

    private static Set<LineSegment3d> lineSetOfBlock(Vector3d pos){
        Set<LineSegment3d> lineSet = new HashSet<>();
        Vector3d ppp = pos.add(1, 1, 1);
        Vector3d ppn = pos.add(1, 1, 0);
        Vector3d pnp = pos.add(1, 0, 1);
        Vector3d pnn = pos.add(1, 0, 0);
        Vector3d npp = pos.add(0, 1, 1);
        Vector3d npn = pos.add(0, 1, 0);
        Vector3d nnp = pos.add(0, 0, 1);
        Vector3d nnn = pos.add(0, 0, 0);
        lineSet.add(new LineSegment3d(nnn, pnn));
        lineSet.add(new LineSegment3d(pnn, ppn));
        lineSet.add(new LineSegment3d(ppn, npn));
        lineSet.add(new LineSegment3d(npn, nnn));
        lineSet.add(new LineSegment3d(nnn, nnp));
        lineSet.add(new LineSegment3d(pnn, pnp));
        lineSet.add(new LineSegment3d(ppn, ppp));
        lineSet.add(new LineSegment3d(npn, npp));
        lineSet.add(new LineSegment3d(nnp, pnp));
        lineSet.add(new LineSegment3d(pnp, ppp));
        lineSet.add(new LineSegment3d(ppp, npp));
        lineSet.add(new LineSegment3d(npp, nnp));
        return lineSet;
    }

    public static Set<Vector3d> particlePosSetOfBlock(Vector3d pos){
        Set<LineSegment3d> lineSet = lineSetOfBlock(pos);
        Set<Vector3d> set = new HashSet<>();
        for(var line : lineSet){
            set.addAll(calcParticlePosSetOfLine(line));
        }
        return set;
    }

    public static Set<Vector3d> particlePosSetOfBound(Parallelepiped bound){
        Set<LineSegment3d> lineSet = new HashSet<>();
        Vector3d or = bound.vectorOR();
        Vector3d ra = bound.vectorRA();
        Vector3d rb = bound.vectorRB();
        Vector3d rc = bound.vectorRC();
        Vector3d oa = or.add(ra);
        Vector3d ob = or.add(rb);
        Vector3d oc = or.add(rc);
        Vector3d oad = or.add(rb).add(rc);
        Vector3d obd = or.add(rc).add(ra);
        Vector3d ocd = or.add(ra).add(rb);
        Vector3d ord = or.add(ra).add(rb).add(rc);
        lineSet.add(new LineSegment3d(or, oa));
        lineSet.add(new LineSegment3d(or, ob));
        lineSet.add(new LineSegment3d(or, oc));
        lineSet.add(new LineSegment3d(oa, obd));
        lineSet.add(new LineSegment3d(oa, ocd));
        lineSet.add(new LineSegment3d(ob, ocd));
        lineSet.add(new LineSegment3d(ob, oad));
        lineSet.add(new LineSegment3d(oc, oad));
        lineSet.add(new LineSegment3d(oc, obd));
        lineSet.add(new LineSegment3d(oad, ord));
        lineSet.add(new LineSegment3d(obd, ord));
        lineSet.add(new LineSegment3d(ocd, ord));
        Set<Vector3d> posSet = new HashSet<>();
        for(var line : lineSet){
            posSet.addAll(calcParticlePosSetOfLine(line));
        }
        return posSet;
    }

    public static Set<Vector3d> calcParticlePosSetOfLine(LineSegment3d line){
        Set<Vector3d> set = new HashSet<>();
        Vector3d pos1 = line.pos1();
        Vector3d pos2 = line.pos2();
        Vector3d v21 = pos2.subtract(pos1);
        if(v21.equals(Vector3d.ZERO)){
            set.add(pos1);
        }else {
            Vector3d e = v21.normalize();
            double d = 0;
            while (d <= v21.length()) {
                set.add(pos1.add(e.scalarMultiply(d)));
                d += 0.5;
            }
        }
        return set;
    }

}
