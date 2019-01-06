package com.springJdbcPlus.entity;

import core.entity.SuperEntity;
import core.entity.TableName;
import core.entity.UserDefined;

@TableName(value = "t_user")
public class User extends SuperEntity {

    private Integer id;
    private String username;
    private String password;
    private Double account;
    @UserDefined(transitionFunction="cast('1' as NUMERIC)")
    private Integer gender;
    private GenderEnum genderEnum;
    /*@UserDefined(transitionSource="gender", transitionFunction = "local(\"gender\", \"test1\") as gender")
    private String genderStr;*/
    private String idCard;

    private Boolean perset;

    public User()
    {
        super();
    }

    public User(Integer id, String username, String password, Double account)
    {
        super();
        this.id = id;
        this.username = username;
        this.password = password;
        this.account = account;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Double getAccount() {
        return account;
    }

    public void setAccount(Double account) {
        this.account = account;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public GenderEnum getGenderEnum() {
        return genderEnum;
    }

    public void setGenderEnum(GenderEnum genderEnum) {
        this.genderEnum = genderEnum;
    }

    public Boolean getPerset() {
        return perset;
    }

    public void setPerset(Boolean perset) {
        this.perset = perset;
    }
}
