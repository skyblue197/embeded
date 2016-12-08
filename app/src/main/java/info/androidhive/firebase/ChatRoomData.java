package info.androidhive.firebase;

public class ChatRoomData   //채팅 대기방에서 이용되는 방 이름, contents/major, 시간 기록등을 set/get하기 위한 클래스
{
    private String roomName;
    private String contents;
    private String major;
    private int month;
    private int day;
    private String time;
    private String userName;
    private int hour;
    private int minute;

    public ChatRoomData() {}



    public ChatRoomData(String roomName, String contents, String major, int month, int day, int hour, int minute, String userName)
    {
        this.roomName = roomName;
        this.contents = contents;
        this.major = major;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
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

    public int getHour()
    {
        return hour;
    }

    public int getMinute()
    {
        return minute;
    }

}
