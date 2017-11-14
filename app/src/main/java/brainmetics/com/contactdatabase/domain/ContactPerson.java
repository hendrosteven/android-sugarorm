package brainmetics.com.contactdatabase.domain;

import com.orm.SugarRecord;

/**
 * Created by Hendro Steven on 14/11/2017.
 */

public class ContactPerson extends SugarRecord<ContactPerson> {
    private String fullName;
    private String phone;
    private String email;
    private String address;
    private String photo;

    public ContactPerson(){

    }

    public ContactPerson(String fullName, String phone, String email,
                         String address, String photo){
        this.setFullName(fullName);
        this.setPhone(phone);
        this.setEmail(email);
        this.setAddress(address);
        this.setPhoto(photo);
    }


    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
