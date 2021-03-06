package cn.pumch.web.service.impl;

import java.util.List;
import javax.annotation.Resource;

import cn.pumch.core.generic.GenericDao;
import cn.pumch.core.generic.GenericServiceImpl;
import cn.pumch.web.dao.RoleMapper;
import cn.pumch.web.model.Role;
import cn.pumch.web.service.RoleService;
import org.springframework.stereotype.Service;

/**
 * 角色Service实现类
 *
 * @author StarZou
 * @since 2014年6月10日 下午4:16:33
 */
@Service
public class RoleServiceImpl extends GenericServiceImpl<Role, Long> implements RoleService {

    @Resource
    private RoleMapper roleMapper;

    @Override
    public GenericDao<Role, Long> getDao() {
        return roleMapper;
    }

    @Override
    public List<Role> selectRolesByUserId(Long userId) {
        return roleMapper.selectRolesByUserId(userId);
    }

    @Override
    public List<Role> selectRolesByOperId(Long userId) {
        return roleMapper.selectRolesByUserId(userId);
    }

    @Override
    public boolean userRoleAssociated(Long userId, Long roleId) {
        if(userId>0&&roleId>0) {
            return roleMapper.roleAssociated(userId, roleId)>0;
        }
        return false;
    }

    @Override
    public List<Role> selectList() {
        return roleMapper.selectAll();
    }

}
