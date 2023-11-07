package GUI;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class GuiStarter {

    public static void main(String args[]) {
        FileUpload fileUpload = new FileUpload();
        fileUpload.setDefaultCloseOperation(EXIT_ON_CLOSE);
        fileUpload.setTitle("Carica");
        fileUpload.setSize(400, 400);
        fileUpload.setResizable(false);
        fileUpload.setVisible(true);
    }
}
