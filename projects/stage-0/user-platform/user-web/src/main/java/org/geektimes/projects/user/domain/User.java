package org.geektimes.projects.user.domain;

import org.geektimes.projects.user.validator.bean.validation.UserValid;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

import static javax.persistence.GenerationType.AUTO;

/**
 * Domain model
 */
@Entity
@Table(name = "users")
@UserValid(pwFloor = 6, pwCeiling = 32, phonenolength = 11)
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = AUTO)
//    @NotNull
    @Min(1)
    // 必须大于 0 的整数
    private Long id;

    @Column
    private String name;

    @Column
//    @Length(min = 6, max = 32)
    // 6-32 位
    private String password;

    @Column
    @Email
    private String email;

    @Column
//    @Length(min = 11, max = 11)
    // 采用中国大陆方式（11 位校验）
    private String phoneNumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(name, user.name) && Objects.equals(password, user.password) && Objects.equals(email, user.email) && Objects.equals(phoneNumber, user.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, password, email, phoneNumber);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
