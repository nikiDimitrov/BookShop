package org.book.bookshop.service.impl;

import org.book.bookshop.model.Role;
import org.book.bookshop.repository.RoleRepository;
import org.book.bookshop.service.RoleService;

import java.util.Optional;

public class RoleServiceImpl implements RoleService {
    private RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository){
        this.roleRepository = roleRepository;
    }
    @Override
    public Role getRoleByName(String name) {
        Optional<Role> role = roleRepository.getRoleByName(name);
        return role.stream().findFirst().orElse(null);
    }
}
