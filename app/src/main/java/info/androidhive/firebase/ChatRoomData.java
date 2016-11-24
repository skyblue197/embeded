package info.androidhive.firebase;

public class ChatRoomData
{
    private String roomName;
    private String contents;
    private String major;

    public ChatRoomData() {}

    public ChatRoomData(String roomName, String contents, String major)
    {
        this.roomName = roomName;
        this.contents = contents;
        this.major = major;
    }

    public String getRoomName()
    {
        return roomName;
    }

    public String getContents() { return contents; }

    public String getMajor() { return major; }

}
