package utilitesFileSystem;

import net.jxta.endpoint.Message;
import net.jxta.endpoint.StringMessageElement;

public class MsgFileSystem implements PipeMensageUtilites {

	public static void addStringToMessage(Message message, String nameSpace,
			String elemName, String string) {

		message.addMessageElement(nameSpace, new StringMessageElement(elemName,
				string, null));
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

		if (function.equals(PipeMensageUtilites.create))
			return 0;
		if (function.equals(PipeMensageUtilites.delete))
			return 1;
		if (function.equals(PipeMensageUtilites.move))
			return 2;
		if (function.equals(PipeMensageUtilites.where))
			return 3;
		if (function.equals(PipeMensageUtilites.read))
			return 4;
		if (function.equals(PipeMensageUtilites.write))
			return 5;
		
		return response;
	}

	// -1 -> Nó Central
	public int getSenderFromMessage(Message message) {
		int response = -1;

		String function = message.getMessageElement(null,
				PipeMensageUtilites.sender).toString();
		if (function != null)
			response = Integer.parseInt(function);

		return response;
	}

	// -1 -> Nó Central
	public int getReceiverFromMessage(Message message) {
		int response = -1;

		String function = message.getMessageElement(null,
				PipeMensageUtilites.receiver).toString();

		if (function != null)
			response = Integer.parseInt(function);

		return response;
	}

	public String getFileNameFromMessage(Message message) {
		String fileName = message.getMessageElement(null,
				PipeMensageUtilites.fileName).toString();
		return fileName;
	}
}
