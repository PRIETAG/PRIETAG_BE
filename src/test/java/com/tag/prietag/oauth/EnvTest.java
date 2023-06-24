package com.tag.prietag.oauth;

import org.junit.jupiter.api.Test;

public class EnvTest {

    @Test
    public void env_test(){
        String myVar = System.getenv("SECRET");
        System.out.println(myVar);
    }
}
