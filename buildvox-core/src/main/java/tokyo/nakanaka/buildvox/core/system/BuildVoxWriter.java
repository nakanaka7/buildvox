package tokyo.nakanaka.buildvox.core.system;

import tokyo.nakanaka.buildvox.core.commandSender.CommandSender;

import java.io.IOException;
import java.io.Writer;

public class BuildVoxWriter extends Writer {
    private String colorCode;
    private MessageReceiver messageReceiver;
    private String str = "";
    private boolean closed = false;

    private BuildVoxWriter(String colorCode, MessageReceiver messageReceiver){
        this.colorCode = colorCode;
        this.messageReceiver = messageReceiver;
    }

    public static BuildVoxWriter newOutInstance(CommandSender cmdSender) {
        return new BuildVoxWriter("", cmdSender::sendOutMessage);
    }

    public static BuildVoxWriter newErrInstance(CommandSender cmdSender) {
        return new BuildVoxWriter("", cmdSender::sendErrMessage);
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        if(this.closed){
            throw new IOException();
        }
        if(off < 0 || len < 0 || off + len < 0 || off + len > cbuf.length){
            throw new IndexOutOfBoundsException();
        }
        char[] shifted = new char[len];
        System.arraycopy(cbuf, off, shifted, 0, len);
        String add = new String(shifted);
        this.str += add;
    }

    @Override
    public void flush() throws IOException {
        if(this.closed){
            throw new IOException();
        }
        this.str = str.replaceAll("\\r", "");
        String[] msgs = this.str.split("\\n", -1);
        String last = msgs[msgs.length - 1];
        if(last.isEmpty()){
            String[] lastRemoved = new String[msgs.length - 1];
            System.arraycopy(msgs, 0, lastRemoved, 0, msgs.length - 1);
            msgs = lastRemoved;
            this.str = "";
        }else{
            msgs[msgs.length - 1] += "...";
            this.str = "...";
        }
        for(String msg : msgs) {
            this.messageReceiver.println(colorCode + msg);
        }
    }

    @Override
    public void close() throws IOException {
        this.flush();
        this.closed = true;
    }

    /**
     * An entity to receive messages.
     */
    @Deprecated
    private interface MessageReceiver {
        /**
         * Prints the message as a line.
         * @param msg the message.
         */
        void println(String msg);

    }

}
