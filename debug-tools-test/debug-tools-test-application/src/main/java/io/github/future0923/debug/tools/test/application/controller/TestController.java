/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.future0923.debug.tools.test.application.controller;

import io.github.future0923.debug.tools.test.application.dao.UserDao;
import io.github.future0923.debug.tools.test.application.service.Test1Service;
import io.github.future0923.debug.tools.test.application.service.Test2Service;
import io.github.future0923.debug.tools.test.application.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author future0923
 */
@RestController
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    private final Test1Service test1Service;

    private final Test2Service test2Service;

    private final UserDao userDao;

    @GetMapping("/hot")
    public String ok() {
        return "asd";
    }


    @GetMapping("/hot1")
    public String ok1() {
        return "asd1";
    }

    @GetMapping("/hot2")
    public String ok2() {
        return "asd1";
    }

    @GetMapping("/hot3")
    public String ok3() {
        return test2Service.test();
    }

    @GetMapping("/hot4")
    public String ok4() {
        return test2Service.test();
    }

    @GetMapping("/insertBatchSomeColumn")
    public String insertBatchSomeColumn() {
        return testService.insertBatchSomeColumn();
    }

    @GetMapping("/testDao")
    public String test(Integer id) {
        return "success:" + testService.testDao(id);
    }

}
