package tokyo.nakanaka.buildvox.core.event.listener;

import org.greenrobot.eventbus.Subscribe;
import tokyo.nakanaka.buildvox.core.event.CommandEvent;
import tokyo.nakanaka.buildvox.core.event.TabCompletionEvent;
import tokyo.nakanaka.buildvox.core.system.commandHandler.BvdCommandHandler;

public class bvdCommandListener {
    private final BvdCommandHandler cmdHandler = new BvdCommandHandler();

    @Subscribe
    public void onCommand(CommandEvent evt) {
        if(!evt.label().equals("bvd"))return;
        cmdHandler.onCommand(evt.args(), evt.worldId(), evt.x(), evt.y(), evt.z(), evt.messageReceiver(), evt.playerId());
    }

    @Subscribe
    public void onTabCompletion(TabCompletionEvent evt) {
        if(!evt.label().equals("bvd"))return;
        evt.candidates().addAll(cmdHandler.onTabComplete(evt.args()));
    }

}
