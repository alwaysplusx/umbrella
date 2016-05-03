//package com.harmony.umbrella.jackson;
//
//import java.io.IOException;
//
//import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.ObjectWriter;
//
///**
// * @author wuxii@foxmail.com
// */
//public class JacksonTest {
//
//    public static void main(String[] args) throws IOException {
//        ObjectMapper om = new ObjectMapper();
//        ObjectWriter ow = om.writer(new DefaultPrettyPrinter());
//        ow.writeValue(System.out, new User("1", "wuxii"));
//    }
//
//    public static class User {
//
//        String userId;
//        String name;
//
//        public User(String userId, String name) {
//            this.userId = userId;
//            this.name = name;
//        }
//
//        public String getUserId() {
//            return userId;
//        }
//
//        public void setUserId(String userId) {
//            this.userId = userId;
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        public void setName(String name) {
//            this.name = name;
//        }
//
//    }
//
//}
