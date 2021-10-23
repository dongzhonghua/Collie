package xyz.dsvshx.collie.service;

import xyz.dsvshx.ioc.annotation.Service;

/**
 * @author dongzhonghua
 * Created on 2021-05-24
 */

@Service
public class UserService {
    public String getUserInfo(int age, String name) {
        return String.format("姓名：%s: %d岁", name, age);
    }
}
