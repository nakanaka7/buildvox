package tokyo.nakanaka.buildvox.core.event.listener;

import org.greenrobot.eventbus.Subscribe;
import tokyo.nakanaka.buildvox.core.event.ClickBlockEvent;
import tokyo.nakanaka.buildvox.core.system.ToolType;
import tokyo.nakanaka.buildvox.core.system.clickBlockEventHandler.PosMarkerClickBlockEventHandler;

public class PosMarkerClickListener {
    private final PosMarkerClickBlockEventHandler handler = new PosMarkerClickBlockEventHandler();

    @Subscribe
    public void onClick(ClickBlockEvent event) {
        if(event.tool() != ToolType.POS_MARKER)return;
        switch (event.button()) {
            case LEFT -> handler.onLeft(event.playerId(), event.worldId(), event.x(), event.y(), event.z(), event.messageReceiver());
            case RIGHT -> handler.onRight(event.playerId(), event.worldId(), event.x(), event.y(), event.z(), event.messageReceiver());
        }
    }

}
