package tokyo.nakanaka.buildvox.core.system.commandHandler;

import java.util.List;

public record TabCompletionEvent(String label, String[] args, List<String> candidates) {
}
