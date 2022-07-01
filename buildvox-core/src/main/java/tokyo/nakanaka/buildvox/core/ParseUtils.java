package tokyo.nakanaka.buildvox.core;

public class ParseUtils {
    private ParseUtils() {
    }

    public static record NameStateEntity(String name, String state, String entity) {
    }

    /**
     * Decompose "name[state]{entity}". "[state]" or "{entity}" may be skipped.
     * @return the name-state-entity.
     * @throws IllegalArgumentException
     */
    public static NameStateEntity parseNameStateEntity(String s) {
        String name;
        String state;
        String entity;
        if(s.contains("[") || s.contains("{")) {
            int a = s.indexOf("[");
            int b = s.indexOf("{");
            int c;
            if(a == -1) c = b;
            else if(b == -1) c = a;
            else c = Math.min(a, b);
            name = s.substring(0, c);
        } else {
            name = s;
        }
        s = s.substring(name.length());
        if(s.startsWith("[")) {
            int e = s.indexOf("]");
            if(e == -1) throw new IllegalArgumentException();
            state = s.substring(1, e);
            s = s.substring(e + 1);
        }else{
            state = "";
        }
        if(s.startsWith("{")) {
            int e = s.indexOf("}");
            if(e == -1) throw new IllegalArgumentException();
            entity = s.substring(1, e);
            s = s.substring(e + 1);
        } else {
            entity = "";
        }
        if(!s.isEmpty()) throw new IllegalArgumentException();
        return new NameStateEntity(name, state, entity);
    }

}
