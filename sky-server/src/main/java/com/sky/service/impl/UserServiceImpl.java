package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import springfox.documentation.spring.web.json.Json;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    public static final String WX_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";//微信登录url，发送给微信服务器的url
    @Autowired
    private WeChatProperties weChatProperties;
    @Autowired
    private UserMapper userMapper;

    @Override
    public User wxlogin(UserLoginDTO userLoginDTO) {
     Map<String,String> map = new HashMap<>();
     map.put("appid",weChatProperties.getAppid());
     map.put("secret", weChatProperties.getSecret());
     map.put("js_code",userLoginDTO.getCode());
     map.put("grant_type","authorization_code");
     //map集合用来封装数据，发送给微信服务端,需要转化为json数据
        String json = HttpClientUtil.doGet(WX_LOGIN_URL, map);//发送请求，返回json数据

        JSONObject jsonObject = JSON.parseObject(json);//将json数据转化为json对象
        String openid = jsonObject.getString("openid");//获取openid
        if(openid == null){//如果openid为空，则登录失败，抛出异常
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        User user = userMapper.getByOpenid(openid);//mapper查询数据库，判断openid是否存在,如果不存在
        if(user == null){//如果openid是新的，则插入数据库也就相当于注册
            user = User.builder()//封装user对象的数据，把openid封装进user对象
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);//插入数据库
        }

        return user;
    }
}
