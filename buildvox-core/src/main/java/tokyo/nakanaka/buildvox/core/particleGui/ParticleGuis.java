package tokyo.nakanaka.buildvox.core.particleGui;

import tokyo.nakanaka.buildvox.core.World;
import tokyo.nakanaka.buildvox.core.math.region3d.Parallelepiped;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.player.Player;
import tokyo.nakanaka.buildvox.core.selection.FillSelection;
import tokyo.nakanaka.buildvox.core.selection.PasteSelection;
import tokyo.nakanaka.buildvox.core.selection.Selection;

public class ParticleGuis {
    public static void updateParticleGui(Player player) {
        final ParticleGui particleGui = player.getParticleGui();
        if(!player.isParticleGuiVisible()) return;
        if(particleGui == null)return;
        particleGui.clearAllLines();
        final World posOrSelectionWorld = player.getEditWorld();
        if(posOrSelectionWorld == null)return;
        final Selection selection = player.getSelection();
        if(selection != null){
            Color color;
            if(selection instanceof PasteSelection){
                color = Color.YELLOW;
            }else if(selection instanceof FillSelection){
                color = Color.LIME;
            }else{
                color = Color.MAGENTA;
            }
            particleGui.addParallelepipedLines(color, posOrSelectionWorld, selection.getBound());
        }
        final Vector3i[] posData = player.getPosArrayClone();
        for(int i = 0; i < posData.length; ++ i){
            Vector3i pos = posData[i];
            if(pos != null){
                Color color;
                if(i == 0){
                    color = Color.RED;
                }else if(i == 1){
                    color = Color.BLUE;
                }else if(i == 2){
                    color = Color.YELLOW;
                }else if(i == 3){
                    color = Color.LIME;
                }else{
                    throw new InternalError();
                }
                particleGui.addBlockLines(color, posOrSelectionWorld, (int)Math.floor(pos.x()), (int)Math.floor(pos.y()), (int)Math.floor(pos.z()));
                for(int j = i + 1; j < posData.length; ++ j){
                    Vector3i posJ = posData[j];
                    if(posJ != null){
                        particleGui.addLine(Color.CYAN, posOrSelectionWorld,
                                pos.x() + 0.5, pos.y() + 0.5, pos.z() + 0.5,
                                posJ.x() + 0.5, posJ.y() + 0.5, posJ.z() + 0.5);
                    }
                }
            }
        }
        if(posData.length == 2){
            Vector3i pos0 = posData[0];
            Vector3i pos1 = posData[1];
            if(pos0 != null && pos1 != null){
                double px = Math.max(pos0.x(), pos1.x()) + 1;
                double py = Math.max(pos0.y(), pos1.y()) + 1;
                double pz = Math.max(pos0.z(), pos1.z()) + 1;
                double nx = Math.min(pos0.x(), pos1.x());
                double ny = Math.min(pos0.y(), pos1.y());
                double nz = Math.min(pos0.z(), pos1.z());
                Parallelepiped parallelepiped = new Parallelepiped(px, py, pz, nx, ny, nz);
                particleGui.addParallelepipedLines(Color.CYAN, posOrSelectionWorld, parallelepiped);
            }
        }
    }
}
