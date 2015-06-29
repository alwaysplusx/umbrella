package com.harmony.umbrella.ws.jaxrs;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JaxRsTest {

    public static void main(String[] args) {
        JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
        sf.setResourceClasses(UserService.class);
        sf.setResourceProvider(UserService.class, new SingletonResourceProvider(new UserService()));
        sf.setAddress("http://localhost:9000");
        sf.create();
    }

    @XmlRootElement(name = "user")
    public static class User {

        private long id;
        private String username;
        private int age;

        public User() {
        }

        public User(long id, String username, int age) {
            this.id = id;
            this.username = username;
            this.age = age;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + (int) (id ^ (id >>> 32));
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            User other = (User) obj;
            if (id != other.id)
                return false;
            return true;
        }

        @Override
        public String toString() {
            return "User [id=" + id + ", username=" + username + ", age=" + age + "]";
        }

    }

    @Path("/")
    @Produces("text/xml")
    public static class UserService {

        Map<Long, User> users = new HashMap<Long, User>();
        public static final Logger log = LoggerFactory.getLogger(UserService.class);

        public UserService() {
            init();
        }

        @GET
        @Path("/users/{id}")
        public User getUser(@PathParam("id") Long id) {
            log.info("get user, id:{}", id);
            return users.get(id);
        }

        @POST
        @Path("/users/")
        public Response addUser(User user) {
            log.info("add user, user:{}", user);
            users.put(user.getId(), user);
            return Response.ok().build();
        }

        @DELETE
        @Path("/users/{id}")
        public Response deleteUser(@PathParam("id") Long id) {
            if (users.containsKey(id)) {
                users.remove(id);
                log.info("remove user, id:{}", id);
                return Response.ok().build();
            } else {
                log.info("user not exist,id {}", id);
                return Response.notModified().build();
            }
        }

        @PUT
        @Path("/users/")
        public Response updateUser(User user) {
            Long id = user.getId();
            if (users.containsKey(id)) {
                users.put(id, user);
                log.info("update user, user:{}", user);
                return Response.ok().build();
            }
            log.info("user not exist, user:{}", user);
            return Response.notModified().build();
        }

        private void init() {
            users.put(0l, new User(0l, "user_0", 23));
            users.put(1l, new User(1l, "wuxii", 23));
        }

    }
}