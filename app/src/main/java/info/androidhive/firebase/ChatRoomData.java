package info.androidhive.firebase;

public class ChatRoomData
{
    private String roomName;
    private String contents;
    private String major;
    private int month;
    private int day;
    private String time;
    private String userName;

    public ChatRoomData() {}



    public ChatRoomData(String roomName, String contents, String major, int month, int day, String time, String userName)
    {
        this.roomName = roomName;
        this.contents = contents;
        this.major = major;
        this.month = month;
        this.day = day;
        this.time = time;
        this.userName = userName;
    }

    public String getRoomName()
    {
        return roomName;
    }

    public String getContents() { return contents; }

    public String getMajor() { return major; }

    public int getMonth() { return month; }

    public int getDay() { return day; }

    public String getTime() { return time; }

    public String getUserName() { return userName; }

}
