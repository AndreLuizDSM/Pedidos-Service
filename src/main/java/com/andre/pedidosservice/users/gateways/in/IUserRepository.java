package com.andre.pedidosservice.users.gateways.in;

import com.andre.pedidosservice.users.core.domain.UserDomain;
import com.andre.pedidosservice.users.core.StatusEnum;

import java.util.Optional;

public interface IUserRepository {

    UserDomain saveUser (UserDomain domain);

    String loginUser(UserDomain domain);

    Optional<UserDomain> findUserById (String id);

    void deleteUser (String id);

    UserDomain patchUserStatus(String id , StatusEnum status);


}
