package advertisementFileFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;

import net.jxta.document.Advertisement;
import net.jxta.document.AdvertisementFactory;
import net.jxta.document.Document;
import net.jxta.document.Element;
import net.jxta.document.MimeMediaType;
import net.jxta.document.StructuredDocument;
import net.jxta.document.StructuredDocumentFactory;
import net.jxta.document.TextElement;
import net.jxta.id.ID;
import net.jxta.id.IDFactory;

public class FileAdvertisement extends Advertisement {

	public final static String AdvertisementType = "jxta:FileAdvertisement";

	private ID AdvertisementID = ID.nullID;
	private String TheName = "";
	private String Owner = "";

	private final static String IDTag = "FileID";
	private final static String NameTag = "FileName";
	private final static String OwnerTag = "Owner";

	private final static String[] IndexableFields = { IDTag, NameTag, OwnerTag };

	public FileAdvertisement() {

	}

	public FileAdvertisement(Element Root) {
		TextElement MyTextElement = (TextElement) Root;
		Enumeration TheElements = MyTextElement.getChildren();
		while (TheElements.hasMoreElements()) {
			TextElement TheElement = (TextElement) TheElements.nextElement();
			ProcessElement(TheElement);
		}
	}

	public void ProcessElement(TextElement TheElement) {
		String TheElementName = TheElement.getName();
		String TheTextValue = TheElement.getTextValue();

		if (TheElementName.compareTo(IDTag) == 0) {
			try {
				URI ReadID = new URI(TheTextValue);
				AdvertisementID = IDFactory.fromURI(ReadID);
				return;
			} catch (URISyntaxException Ex) {
				// Issue with ID format
				Ex.printStackTrace();
			} catch (ClassCastException Ex) {
				// Issue with ID type
				Ex.printStackTrace();
			}
		}

		if (TheElementName.compareTo(NameTag) == 0) {
			TheName = TheTextValue;
			return;
		}
		if (TheElementName.compareTo(OwnerTag) == 0) {
			Owner = TheTextValue;
			return;
		}
	}

	@Override
	public Document getDocument(MimeMediaType TheMimeMediaType) {
		// TODO Auto-generated method stub

		StructuredDocument TheResult = StructuredDocumentFactory
				.newStructuredDocument(TheMimeMediaType, AdvertisementType);

		Element MyTempElement;
		MyTempElement = TheResult.createElement(NameTag, TheName);
		TheResult.appendChild(MyTempElement);
		MyTempElement = TheResult.createElement(OwnerTag, Owner);
		TheResult.appendChild(MyTempElement);

		return TheResult;
	}

	@Override
	public ID getID() {
		// TODO Auto-generated method stub
		return AdvertisementID;
	}

	public void setAdvertisementID(ID advertisementID) {
		AdvertisementID = advertisementID;
	}

	public String getTheName() {
		return TheName;
	}

	public void setTheName(String theName) {
		TheName = theName;
	}

	public String getOwner() {
		return Owner;
	}

	public void setOwner(String owner) {
		Owner = owner;
	}

	@Override
	public String[] getIndexFields() {
		// TODO Auto-generated method stub
		return IndexableFields;
	}

	@Override
	public FileAdvertisement clone() throws CloneNotSupportedException {
		FileAdvertisement Result = (FileAdvertisement) super.clone();

		Result.AdvertisementID = this.AdvertisementID;
		Result.TheName = this.TheName;
		Result.Owner = this.Owner;

		return Result;
	}

	public static String getAdvertisementType() {
		return AdvertisementType;
	}

	@Override
	public String getAdvType() {
		return FileAdvertisement.class.getName();
	}

	public static class Instantiator implements
			AdvertisementFactory.Instantiator {

		public String getAdvertisementType() {
			return FileAdvertisement.getAdvertisementType();
		}

		public Advertisement newInstance() {
			return new FileAdvertisement();
		}

		public Advertisement newInstance(net.jxta.document.Element root) {
			return new FileAdvertisement(root);
		}

	}
}
