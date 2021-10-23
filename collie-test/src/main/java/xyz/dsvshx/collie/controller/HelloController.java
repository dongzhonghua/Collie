package xyz.dsvshx.collie.controller;

import xyz.dsvshx.ioc.annotation.mvc.GetMapping;
import xyz.dsvshx.ioc.annotation.mvc.RestController;

/**
 * @author dongzhonghua
 * Created on 2021-05-26
 */
@RestController
public class HelloController {
    @GetMapping(value = "/hello")
    public String hello() {
        return "hello";
    }
}
