package cn.pumch.web.dao;

import cn.pumch.web.model.SignIn;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SignInMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SignIn record);

    int insertSelective(SignIn record);

    SignIn selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SignIn record);

    int updateByPrimaryKey(SignIn record);

    /**
     * 根据条件进行查询指定教师课程的签到情况
     * @param start
     * @param count
     * @param tId
     * @param courseName
     * @param sName
     * @return
     */
    List<SignIn> selectByTIdInPage(@Param("start") int start,
                                   @Param("count") int count,
                                   @Param("tId") Long tId,
                                   @Param("courseName") String courseName,
                                   @Param("sName") String sName);

    /**
     * 根据条件进行查询指定教师课程的签到数量
     * @param tId
     * @param courseName
     * @param sName
     * @return
     */
    int selectCountByTId(@Param("tId") Long tId,
                         @Param("courseName") String courseName,
                         @Param("sName") String sName);

    /**
     * 根据条件进行查询指定学生课程的签到情况
     * @param start
     * @param count
     * @param signerId
     * @param courseName
     * @param sName
     * @return
     */
    List<SignIn> selectBySIdInPage(@Param("start") int start,
                                   @Param("count") int count,
                                   @Param("signerId") Long signerId,
                                   @Param("courseName") String courseName,
                                   @Param("sName") String sName);

    /**
     * 根据条件进行查询指定学生的签到数量
     * @param signerId
     * @param courseName
     * @param sName
     * @return
     */
    int selectCountBySId(@Param("signerId") Long signerId,
                         @Param("courseName") String courseName,
                         @Param("sName") String sName);
}