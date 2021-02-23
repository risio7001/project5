package DTO;

public class MemberDTO {
    private String name;
//    private String sex;
    private String phone;


    public MemberDTO(String name, String phone) {
        this.name = name;
        this.phone = phone;
//        this.sex = sex;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public String getSex() {
//        return this.sex;
//    }
//
//    public void setSex(String sex) {
//        this.sex = sex;
//    }

}
