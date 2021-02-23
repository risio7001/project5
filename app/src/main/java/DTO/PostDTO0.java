package DTO;

import java.util.Date;

public class PostDTO0 {
    private int su;
    private String title;
    private String content;
    private String mediaPath;
    private String person;
    private Date date;
    private String check;

    public PostDTO0(int su, String title, String content, String mediaPath, String person, Date date, String check) {
        this.su = su;
        this.title = title;
        this.content = content;
        this.mediaPath = mediaPath;
        this.person = person;
        this.date = date;
        this.check = check;
    }
    public int getSu() {
        return su;
    }

    public void setSu(int su) {
        this.su = su;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMediaPath() {
        return mediaPath;
    }

    public void setMediaPath(String mediaPath) {
        this.mediaPath = mediaPath;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCheck() {
        return check;
    }

    public void setCheck(String check) {
        this.check = check;
    }
}
