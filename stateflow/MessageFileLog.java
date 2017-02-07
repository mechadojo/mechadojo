package org.mechadojo.stateflow;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MessageFileLog extends MessageLog {
    public File file;
    public boolean firstLine = false;
    public MessageFileLog(String path, String filename, boolean deleteExisting) {
        super(path);
        file = new File(filename);
        if (file.exists() && deleteExisting) {
            file.delete();
        }
    }

    public void flush() {
        if (messages.size() == 0) return;

        try {
            if (!firstLine && !file.exists()) firstLine = true;

            FileOutputStream os = new FileOutputStream(file, true);
            while(messages.size() > 0) {
                Message msg = messages.poll();
                if (msg == null) continue;

                if (firstLine) {
                    os.write(msg.getLogHeader().getBytes());
                    firstLine = false;
                }

                os.write(msg.getLogRow().getBytes());
            }

        }
        catch(Exception ex) {
            controller.log("error", "MessageFileLog", ex.getMessage());
        }
    }


}
