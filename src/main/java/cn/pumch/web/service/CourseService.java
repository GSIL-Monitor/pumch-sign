package cn.pumch.web.service;

import cn.pumch.web.model.Course;

import java.util.List;

public interface CourseService {

    /**
     * 通过ID查询课程
     * @param courseId
     * @return
     */
    Course getCourseById(Long courseId);

    /**
     * 查询课程记录
     * @param page
     * @param pageSize
     * @param courseName
     * @return
     */
    List<Course> getCoursesByConditionsInPage(int page, int pageSize, String courseName);

    /**
     * 查询课程数量
     * @param courseName
     * @return
     */
    int getCoursesCountByCondition(String courseName);

    /**
     * 查询指定教师的课程列表
     * @param page
     * @param pageSize
     * @param tId
     * @return
     */
    List<Course> getCourseByTIdInPage(int page, int pageSize, Long tId);

    /**
     * 查询指定教师的课程数量
     * @param tId
     * @return
     */
    int getCourseCountByTId(Long tId);

    /**
     * 根据教师ID创建新课程
     * @param courseName
     * @param tId
     * @return
     */
    String createCourse(String courseName, String courseType, Long tId);

    /**
     * 根据课程ID查询教师的姓名
     * @param courseId
     * @return
     */
    String getTNameByCourseId(Long courseId);
}
