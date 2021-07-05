package com.jwt.seckill.oauth2;

import com.jwt.seckill.dao.UserDao;
import com.jwt.seckill.entity.User;
import com.jwt.seckill.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class Oauth2UserDetailsService implements UserDetailsService {
    @Autowired
    UserDao dao;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = dao.queryByTel(username);
        if (user == null) {
            throw new UsernameNotFoundException("手机号"+username+"不存在");
        }
        return new Oauth2UserDetails(user);
    }
}
