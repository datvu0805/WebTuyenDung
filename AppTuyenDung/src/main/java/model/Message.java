package model;

// Tin nhắn
public class Message extends BaseEntity{
    private int senderID;
    private int receiverID;
    private String messageConntent;
    private boolean isRead;

    public Message() {
    }

    public Message(int senderID, int receiverID, String messageConntent, boolean isRead) {
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.messageConntent = messageConntent;
        this.isRead = isRead;
    }

    public Message(int id, int senderID, int receiverID, String messageConntent, boolean isRead) {
        super(id);
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.messageConntent = messageConntent;
        this.isRead = isRead;
    }

    public int getSenderID() {
        return senderID;
    }

    public void setSenderID(int senderID) {
        this.senderID = senderID;
    }

    public int getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(int receiverID) {
        this.receiverID = receiverID;
    }

    public String getMessageConntent() {
        return messageConntent;
    }

    public void setMessageConntent(String messageConntent) {
        this.messageConntent = messageConntent;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    @Override
    public String getInfo() {
        return "";
    }
}
