package com.hmdp.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.dto.LoginFormDTO;
import com.hmdp.dto.Result;
import com.hmdp.entity.User;
import com.hmdp.mapper.UserMapper;
import com.hmdp.service.IUserService;
import com.hmdp.utils.RegexUtils;
import com.hmdp.utils.SystemConstants;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Override
    public Result sendCode(String phone, HttpSession session) {
        //1.校验手机号
        if (RegexUtils.isPhoneInvalid(phone)) {
            //2.如果不符合，返回错误信息
            return Result.fail("手机号格式错误！");
        }


        //3.符合，生成验证码(hutool工具类）
        String code = RandomUtil.randomNumbers(6);
        //4.保存验证码（用于校验用户输入的对不对）
        session.setAttribute("code", code);
        //5.发送验证码(模拟）
        log.debug("发送短信验证码成功，验证码：{}", code);

        //返回ok

        return Result.ok();
    }

    @Override
    public Result login(LoginFormDTO loginForm, HttpSession session) {

        //从登录信息获取手机号
        String phone = loginForm.getPhone();
        //1.校验手机号
        if (RegexUtils.isPhoneInvalid(phone)) {
            //2.如果不符合，返回错误信息
            return Result.fail("手机号格式错误！");
        }

        //2.校验验证码(从session中取出验证码，与用户传入的做比较）
        Object cacheCode = session.getAttribute("code");
        String userCode = loginForm.getCode();
        if(cacheCode == null || !cacheCode.toString().equals(userCode)){//验证码过期不存在或验证码不一致
            //3.不一致，报错
            return Result.fail("验证码错误");
        }//反向校验不用嵌套if


        //4.一致，根据手机号去数据库查询用户 select * from tb_user where phone = ？
        User user = query().eq("phone", phone).one();

        //5.判断用户是否存在
        if(user == null){
            //6.用户不存在，创建新用户并保存
            user = createUserWithPhone(phone);
        }
        //7.保存用户信息到session（存在不存在都要做）
        session.setAttribute("user", user);
        return Result.ok();
    }

    private User createUserWithPhone(String phone) {
        //创建用户
        User user = new User();
        user.setPhone(phone);
        user.setNickName(SystemConstants.USER_NICK_NAME_PREFIX +RandomUtil.randomString(10));//生成随机用户名
        //保存用户（mybatis-plus）
        save(user);
        return user;
    }
}
