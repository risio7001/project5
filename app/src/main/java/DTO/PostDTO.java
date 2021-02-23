package DTO;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class PostDTO implements Parcelable {
    private String title;
    private String content;
    private String mediaPath;
    private String person;
    private Date date;
    private String check;
    private String docPath;
    private String coPath;
    private String firstPath;

    public PostDTO(String title, String content, String mediaPath, String person, Date date, String check, String docPath, String coPath, String firstPath) {
        this.title = title;
        this.content = content;
        this.mediaPath = mediaPath;
        this.person = person;
        this.date = date;
        this.check = check;
        this.docPath = docPath;
        this.coPath = coPath;
        this.firstPath = firstPath;
    }


    public static final Creator<PostDTO> CREATOR = new Creator<PostDTO>() {
        @Override
        public PostDTO createFromParcel(Parcel in) {
            return new PostDTO(in);
        }

        @Override
        public PostDTO[] newArray(int size) {
            return new PostDTO[size];
        }
    };

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {  this.content = content; }

    public String getMediaPath() {
        return this.mediaPath;
    }

    public void setMediaPath(String mediaPath) {
        this.mediaPath = mediaPath;
    }

    public String getPerson() {
        return this.person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCheck() {
        return this.check;
    }

    public void setCheck(String check) {
        this.check = check;
    }

    public String getDocPath() {
        return this.docPath;
    }

    public void setDocPath(String docPath) {
        this.docPath = docPath;
    }

    public String getCoPath() {
        return this.coPath;
    }

    public void setCoPath(String coPath) {
        this.coPath = coPath;
    }

    public String getFirstPath() {
        return this.firstPath;
    }

    public void setFirstPath(String firstPath) {
        this.firstPath = firstPath;
    }


    public PostDTO(Parcel in) {
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in){
        title = in.readString();
        content = in.readString();
        mediaPath = in.readString();
        person = in.readString();
        date = (Date) in.readSerializable();
        check = in.readString();
        docPath = in.readString();
        coPath = in.readString();
        firstPath = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(mediaPath);
        dest.writeString(person);
        dest.writeSerializable(date);
        dest.writeString(check);
        dest.writeString(docPath);
        dest.writeString(coPath);
        dest.writeString(firstPath);
    }
}
