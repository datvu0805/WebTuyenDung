package model;

// Tin nhắn
public class Message extends BaseEntity{
    private Users senderID;
    private Users receiverID;
    private String messageConntent;
    private boolean isRead;

    public Message() {
    }

    public Message(Users senderID, Users receiverID, String messageConntent, boolean isRead) {
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.messageConntent = messageConntent;
        this.isRead = isRead;
    }

    public Message(int id, Users senderID, Users receiverID, String messageConntent, boolean isRead) {
        super(id);
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.messageConntent = messageConntent;
        this.isRead = isRead;
    }

    public Users getSenderID() {
        return senderID;
    }

    public void setSenderID(Users senderID) {
        this.senderID = senderID;
    }

    public Users getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(Users receiverID) {
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
