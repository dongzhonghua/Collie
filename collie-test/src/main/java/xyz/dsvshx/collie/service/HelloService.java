package xyz.dsvshx.collie.service;

import lombok.extern.slf4j.Slf4j;
import xyz.dsvshx.ioc.annotation.Autowired;
import xyz.dsvshx.ioc.annotation.Service;

/**
 * @author dongzhonghua
 * Created on 2021-05-24
 */
@Service
@Slf4j
public class HelloService {
    @Autowired
    private UserService userService;

    public void getUser() {
        log.info(userService.getUserInfo(24, "tom"));
    }
}
