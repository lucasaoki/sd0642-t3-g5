package utilitesFileSystem;

import java.io.IOException;
import java.io.InputStream;
import net.jxta.document.MimeMediaType;
import net.jxta.endpoint.ByteArrayMessageElement;
import net.jxta.endpoint.Message;
import net.jxta.endpoint.MessageElement;
import net.jxta.endpoint.StringMessageElement;

public class MsgFileSystem implements PipeMensageUtilites,
        UtilitesMsgFileSystem {

    public static void addStringToMessage(Message message, String nameSpace,
            String elemName, String string) {

        message.addMessageElement(nameSpace, new StringMessageElement(elemName,
                string, null));
    }

    public void addByteArrayToMessage(Message message, String nameSpace,
            String elemName, byte data[]) {
        byte buffer[] = data;

        message.addMessageElement(nameSpace, new ByteArrayMessageElement(elemName,
                MimeMediaType.AOS, buffer, null));
    }

    // sender ou receiver null equivalente a ser o NóCentral enviando/recebendo
    // os dados
    public static void createMessageCentralNodeFileSystem(Message message,
            String sender, String receiver, String function, String fileName,
            String response) {

        addStringToMessage(message, null, PipeMensageUtilites.function,
                function);
        addStringToMessage(message, null, PipeMensageUtilites.sender, sender);
        addStringToMessage(message, null, PipeMensageUtilites.receiver,
                receiver);
        addStringToMessage(message, null, PipeMensageUtilites.response,
                response);
        addStringToMessage(message, null, PipeMensageUtilites.fileName,
                fileName);
    }
 
   public int functionFromMessage(Message message) {

        int response = -1;

        String function = message.getMessageElement(null,
                PipeMensageUtilites.function).toString();

        if (function.equals(PipeMensageUtilites.create)) {
            return CREATE_MSG;
        }
        if (function.equals(PipeMensageUtilites.delete)) {
            return DELETE_MSG;
        }
        if (function.equals(PipeMensageUtilites.move)) {
            return MOVE_MSG;
        }
        if (function.equals(PipeMensageUtilites.read)) {
            return READ_MSG;
        }
        if (function.equals(PipeMensageUtilites.write)) {
            return WRITE_MSG;
        }

        // exclusivas dos nos clientes
        if (function.equals(PipeMensageUtilites.readFile)) {
            return READ_FILE;
        }
        if (function.equals(PipeMensageUtilites.writeFile)) {
            return WRITE_FILE;
        }
        if (function.equals(PipeMensageUtilites.deleteFile)) {
            return DELETE_FILE;
        }
        if (function.equals(PipeMensageUtilites.moveFile)) {
            return MOVE_FILE;
        }

        return response;
    }

    // -1 -> Nó Central
    public int getSenderFromMessage(Message message) {
        int response = -1;

        String function = message.getMessageElement(null,
                PipeMensageUtilites.sender).toString();
        if (function != null) {
            response = Integer.parseInt(function);
        }

        return response;
    }

    // -1 -> Nó Central
    public int getReceiverFromMessage(Message message) {
        int response = -1;

        String function = message.getMessageElement(null,
                PipeMensageUtilites.receiver).toString();

        if (function != null) {
            response = Integer.parseInt(function);
        }

        return response;
    }

    public String getResponseFromMessage(Message message) {
        String response = message.getMessageElement(null,
                PipeMensageUtilites.response).toString();
        return response;
    }

    public String getFileNameFromMessage(Message message) {
        String fileName = message.getMessageElement(null,
                PipeMensageUtilites.fileName).toString();
        return fileName;
    }

    public InputStream getInputStreamFromMessage(Message message) throws IOException {
        InputStream result = null;
        MessageElement element = message.getMessageElement(null, PipeMensageUtilites.stream);
        
        if (element == null) {
            return null;
        }
        
        if (element.getMimeType().equals(MimeMediaType.AOS)) {
            result = element.getStream();
        }
        return result;
    }
}
