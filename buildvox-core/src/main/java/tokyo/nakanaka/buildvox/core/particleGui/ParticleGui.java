package tokyo.nakanaka.buildvox.core.particleGui;

import tokyo.nakanaka.buildvox.core.Scheduler;
import tokyo.nakanaka.buildvox.core.math.LineSegment3d;
import tokyo.nakanaka.buildvox.core.math.region3d.Parallelepiped;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3d;
import tokyo.nakanaka.buildvox.core.system.BuildVoxSystem;
import tokyo.nakanaka.buildvox.core.world.World;

import java.util.HashSet;
import java.util.Set;

/**
 * The object which draws particle lines. Setting a colored particle drawer is needed to spawn particles.
 */
public class ParticleGui implements AutoCloseable {
    private ColoredParticleSpawner out = (color, world, x, y, z) -> {};
    private Scheduler scheduler;
    private Set<ParticleSpawnData> spawnDataSet = new HashSet<>();
    private boolean drawing;

    /**
     * Creates a new instance.
     * @param out the output colored particle spawner
     */
    public ParticleGui(ColoredParticleSpawner out) {
        this.out = out;
        this.scheduler = BuildVoxSystem.environment.scheduler();
        drawing = true;
        tickTask();
    }

    private static record ParticleSpawnData(Color color, World world, Set<Vector3d> posSet){
    }

    /**
     * Set the output particle spawner of this drawer.
     * @param out the output particle spawner.
     */
    public void setOut(ColoredParticleSpawner out){
        this.out = out;
    }

    /**
     * Add a line between point 1 and 2.
     * @param color the color of the line.
     * @param world the world which the line is in.
     * @param x1 the x-coordinate of the point 1.
     * @param y1 the y-coordinate of the point 1.
     * @param z1 the z-coordinate of the point 1.
     * @param x2 the x-coordinate of the point 2.
     * @param y2 the y-coordinate of the point 2.
     * @param z2 the z-coordinate of the point 2.
     */
    public void addLine(Color color, World world, double x1, double y1, double z1, double x2, double y2, double z2){
        Set<Vector3d> posSet = calcParticlePosSetOfLine(new LineSegment3d(x1, y1, z1, x2, y2, z2));
        ParticleSpawnData data = new ParticleSpawnData(color, world, posSet);
        this.spawnDataSet.add(data);
    }

    /**
     * Add lines which are borders of a block(1x1x1 cube).
     * @param color the color of the lines.
     * @param world the world which the line is in.
     * @param x the x-coordinate of the block.
     * @param y the y-coordinate of the block.
     * @param z the z-coordinate of the block.
     */
    public void addBlockLines(Color color, World world, int x, int y, int z){
        Vector3d p0 = new Vector3d(x, y, z);
        Vector3d p1 = new Vector3d(x + 0.5, y, z);
        Vector3d p2 = new Vector3d(x, y + 0.5, z);
        Vector3d p3 = new Vector3d(x, y, z + 0.5);
        Vector3d p4 = new Vector3d(x + 1, y, z);
        Vector3d p5 = new Vector3d(x, y + 1, z);
        Vector3d p6 = new Vector3d(x, y, z + 1);
        Vector3d p7 = new Vector3d(x + 1, y + 0.5, z);
        Vector3d p8 = new Vector3d(x + 1, y, z + 0.5);
        Vector3d p9 = new Vector3d(x + 0.5, y + 1, z);
        Vector3d p10 = new Vector3d(x, y + 1, z + 0.5);
        Vector3d p11 = new Vector3d(x + 0.5, y, z + 1);
        Vector3d p12 = new Vector3d(x, y + 0.5, z + 1);
        Vector3d p13 = new Vector3d(x, y + 1, z + 1);
        Vector3d p14 = new Vector3d(x + 1, y, z + 1);
        Vector3d p15 = new Vector3d(x + 1, y + 1, z);
        Vector3d p16 = new Vector3d(x + 0.5, y + 1, z + 1);
        Vector3d p17 = new Vector3d(x + 1, y + 0.5, z + 1);
        Vector3d p18 = new Vector3d(x + 1, y + 1, z + 0.5);
        Vector3d p19 = new Vector3d(x + 1, y + 1, z + 1);
        ParticleSpawnData data = new ParticleSpawnData(color, world, Set.of(p0, p1, p2, p3, p4,
                p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19));
        this.spawnDataSet.add(data);
    }

    /**
     * Add lines which are borders of a parallelepiped.
     * @param color the color of the lines.
     * @param world the world which the line is in.
     * @param parallelepiped the parallelepiped.
     */
    public void addParallelepipedLines(Color color, World world, Parallelepiped parallelepiped){
        Set<LineSegment3d> lineSet = new HashSet<>();
        Vector3d or = parallelepiped.vectorOR();
        Vector3d ra = parallelepiped.vectorRA();
        Vector3d rb = parallelepiped.vectorRB();
        Vector3d rc = parallelepiped.vectorRC();
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
        ParticleSpawnData data = new ParticleSpawnData(color, world, posSet);
        this.spawnDataSet.add(data);
    }

    private static Set<Vector3d> calcParticlePosSetOfLine(LineSegment3d line){
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

    /**
     * Clear all the lines.
     */
    public void clearAllLines() {
        this.spawnDataSet = new HashSet<>();
    }

    /**
     * Disables the colored particle spawner to spawn particles. This instance will not access the scheduler from the
     * time when invoking this method.
     */
    @Override
    public void close(){
        drawing = false;
    }

    private void tickTask(){
        if(!drawing){
            return;
        }
        for(ParticleSpawnData data : spawnDataSet){
            for(Vector3d pos : data.posSet) {
                out.spawnParticle(data.color, data.world, pos.x(), pos.y(), pos.z());
            }
        }
        scheduler.schedule(this::tickTask, 5);
    }


}
