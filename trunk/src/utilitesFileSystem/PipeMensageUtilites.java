package utilitesFileSystem;

//Interface usada para criar os headers das mensagens
public interface PipeMensageUtilites {

	//Quem envia e quem recebe
	public final static String sender = "senderMessage";  
	public final static String receiver = "receiverMessage";	
	
	//Funcionalidades que o nó central tem q saber
	public final static String create = "createFile";
	public final static String delete = "deleteFile";
	public final static String move = "moveFile";
	public final static String where = "whereIsFile";
	
	public final static String read = "readFile";
	public final static String write = "writeFile";
	
	public final static String open = "openFile";
	public final static String newPosition = "newPosition";
	
	//Nome do arquivo para funcionamento
	public final static String fileName = "fileName";
	
	//Respostas e Perguntas
	public final static String response = "serverResponse";
	public final static String function = "serverFunction";
	public final static String okCreate = "ok inserted";
	public final static String failCreate = "fail inserted";
	public final static String okRemove = "ok remove";
	public final static String failRemove = "fail remove";
	
}
