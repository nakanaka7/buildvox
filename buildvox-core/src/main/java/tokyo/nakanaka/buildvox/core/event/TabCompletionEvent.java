package tokyo.nakanaka.buildvox.core.event;

import java.util.List;

public record TabCompletionEvent(String label, String[] args, List<String> candidates) {
}
