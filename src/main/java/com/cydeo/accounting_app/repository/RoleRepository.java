package com.cydeo.accounting_app.repository;

import com.cydeo.accounting_app.dto.RoleDTO;
import com.cydeo.accounting_app.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findById(Long id);

    Optional<RoleDTO> getAllRoles();

}
