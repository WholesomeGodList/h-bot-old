package bot.modules;

public class BookTracker {
	private String authorId;
	private String messageId;
	private int currentPg;
	private String readLink;
	private BookMode mode;

	public BookTracker(String authorId, String messageId, int currentPg, String readLink, BookMode mode) {
		this.authorId = authorId;
		this.messageId = messageId;
		this.currentPg = currentPg;
		this.mode = mode;
		this.readLink = readLink;
	}

	public String getReadLink() {
		return readLink;
	}

	public void setReadLink(String readLink) {
		this.readLink = readLink;
	}

	public BookMode getMode() {
		return mode;
	}

	public void setMode(BookMode mode) {
		this.mode = mode;
	}

	public String getAuthorId() {
		return authorId;
	}

	public void setAuthorId(String authorId) {
		this.authorId = authorId;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public int getCurrentPg() {
		return currentPg;
	}

	public void setCurrentPg(int currentPg) {
		this.currentPg = currentPg;
	}

	public enum BookMode {
		CONFIRM,
		READ
	}
}
