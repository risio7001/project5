package DTO;

import java.util.Date;

public class Coment {
    private String name;
    private String content_C;
    private Date date;
    private String c_path;

    public Coment(String name, String content_C, Date date, String c_path) {
        this.name = name;
        this.content_C = content_C;
        this.date = date;
        this.c_path = c_path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent_C() {
        return content_C;
    }

    public void setContent_C(String content_C) {
        this.content_C = content_C;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    public String getC_path() {
        return c_path;
    }

    public void setC_path(String c_path) {
        this.c_path = c_path;
    }
}
