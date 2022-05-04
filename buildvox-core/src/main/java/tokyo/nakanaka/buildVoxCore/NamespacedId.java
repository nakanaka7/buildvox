package tokyo.nakanaka.buildVoxCore;

/**
 * Represents namespaced ID
 */
public record NamespacedId(String namespace, String name) {

    /**
     * Gets a namespace value of the given arg.
     * @param arg the argument. It must contain 0 or 1 ":".
     * @throws IllegalArgumentException if arg contains more than 1 ":"
     * @return a namespaced ID. If arg contains one ":", the namespace will be the left side of ":",
     * and the name will be the right side of ":". If arg contains no ":", the namespace will be "minecraft",
     * and the name will be the arg.
     */
    public static NamespacedId valueOf(String arg){
        String namespace;
        String name;
        String[] split = arg.split(":");
        if(split.length == 1) {
            return new NamespacedId("minecraft", arg);
        }else if(split.length == 2){
            namespace = split[0];
            name = split[1];
            return new NamespacedId(namespace, name);
        }else{
            throw new IllegalArgumentException("must contain 0 or 1 :");
        }
    }

    /**
     * Gets the String of "namespace:name"
     * @return the String of "namespace:name"
     */
    @Override
    public String toString(){
        return namespace + ":" + name;
    }

}
