package com.harmony.umbrella.xml;

import java.util.List;

/**
 * @author wuxii@foxmail.com
 */
public class Customer {

    private Long customerId;
    private String customerName;
    private User user;

    private List<User> family;

    public Customer() {
    }

    public Customer(Long customerId, String customerName) {
        this.customerId = customerId;
        this.customerName = customerName;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<User> getFamily() {
        return family;
    }

    public void setFamily(List<User> family) {
        this.family = family;
    }

    @Override
    public String toString() {
        return "customer: {customerId:" + customerId + ", customerName:" + customerName + ", user:" + user + ", family:" + family + "}";
    }

}
