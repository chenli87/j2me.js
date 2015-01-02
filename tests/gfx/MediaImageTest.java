package gfx;

import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;
import java.io.*;
import javax.microedition.io.*;
import javax.microedition.io.file.*;
import javax.microedition.media.*;
import javax.microedition.media.control.*;

public class MediaImageTest extends MIDlet implements PlayerListener {
    private VideoControl videoControl;

    byte[] read(InputStream is) throws IOException {
        int l = is.available();
        byte[] buffer = new byte[l+1];
        int length = 0;

        while ((l = is.read(buffer, length, buffer.length - length)) != -1) {
            length += l;
            if (length == buffer.length) {
                byte[] b = new byte[buffer.length + 4096];
                System.arraycopy(buffer, 0, b, 0, length);
                buffer = b;
            }
        }

        return buffer;
    }

    class TestCanvas extends Canvas {
        protected void paint(Graphics g) {
            videoControl.initDisplayMode(VideoControl.USE_DIRECT_VIDEO, this);
            try {
                videoControl.setDisplayLocation(20, 20);
                videoControl.setDisplaySize(getWidth() / 2, getHeight() / 2);
            } catch (MediaException me) {
                System.out.println("FAIL");
            }

            videoControl.setVisible(true);

            System.out.println("PAINTED");
        }
    }

    public void startApp() {
        try {
            FileConnection file = (FileConnection)Connector.open("file:////test.jpg", Connector.READ_WRITE);
            if (!file.exists()) {
                file.create();
            }
            OutputStream os = file.openDataOutputStream();
            InputStream is = getClass().getResourceAsStream("/org/mozilla/io/test.jpg");
            os.write(read(is));
            os.close();

            Player player = Manager.createPlayer("file:////test.jpg");

            player.addPlayerListener(this);

            player.realize();

            videoControl = (VideoControl)player.getControl("VideoControl");

            TestCanvas test = new TestCanvas();
            test.setFullScreenMode(true);
            Display.getDisplay(this).setCurrent(test);

            player.start();

            file.delete();
            file.close();
        } catch (Exception e) {
            System.out.println("FAIL - Unexpected exception: " + e);
            e.printStackTrace();
        }
    }

    public void playerUpdate(Player player, String event, Object eventData) {
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
    }
}

