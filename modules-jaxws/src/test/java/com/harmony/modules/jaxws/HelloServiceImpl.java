package com.harmony.modules.jaxws;

public class HelloServiceImpl implements HelloService {

    @Override
    public String sayHi(String name) {
        return "Hi " + name;
    }

}
