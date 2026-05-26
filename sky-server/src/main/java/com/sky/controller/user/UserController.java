package com.sky.controller.user;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.UserService;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user/user")
@Api(tags = "用户相关接口")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtProperties jwtProperties;


    @PostMapping("/login")
    @ApiOperation("用户微信登录")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO){//前端返回给后端数据就是code,这个code每次登录时都会生成一个随机值
        log.info("用户微信登录：{}", userLoginDTO);
       User user = userService.wxlogin(userLoginDTO);//调用wxlogin方法用来检验是否登录成功，
        // 如果登录成功就返回一个user对象数据，返回的user包含用户登录微信的openid，id,其他信息后续在其它接口实现


        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID,user.getId());//用一个Map集合封装用户的id
        String token =  JwtUtil.createJWT(jwtProperties.getAdminSecretKey(),jwtProperties.getAdminTtl(),claims);
        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .openid(user.getOpenid())
                .token(token)
                .build();

return  Result.success(userLoginVO);
    }

}
