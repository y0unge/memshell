package com.imooc.mvcdemo.controller;

import com.imooc.mvcdemo.model.Course;
import com.imooc.mvcdemo.service.CourseService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Map;

@Controller//标识这个类是一个Controller
@RequestMapping("/courses")
//controller拦截所有 /courses/** 的请求
public class CourseController {

    private static Logger log = LoggerFactory.getLogger(CourseController.class);
    private CourseService courseService;

    @Autowired
    public void setCourseService(CourseService courseService) {
        this.courseService = courseService;
    }

    //本方法将处理：/courses/view?courseId=123的URL的Get类型请求
    @RequestMapping(value = "/view", method = RequestMethod.GET)
    public String viewCourse(@RequestParam("courseId") Integer courseId, Model model) {//通过RequestParam绑定参数到URL参数（这种是非RESTFUL风格的URL）
        log.debug("In viewCourse ,courseiD = {}", courseId);//通过日志查看参数courseId绑定是否正确
        //通过service完成业务逻辑
        Course course = courseService.getCoursebyId(courseId);
        //将结果设置到model中，Model是spring mvc特有的，可以实现将这里添加的对象+成员变量名称对应到jsp（course_overview.jsp）中通过${course.imgPath}方式取值
        model.addAttribute(course);
        //由于web.xml -> mvc-dispatcher-servlet.xml -> 使用了InternalResourceViewResolver，其中通过前缀，后缀指定的展示哪个jsp页面，所以这里返回jsp页面的名称即可。返回
        return "course_overview";
    }


    /**
     * restful风格的请求（现代方式的Controller）
     * <p>
     * 本方法将处理：/courses/view2/{courseId}的URL的Get类型请求，如：http://localhost:8080/courses/view2/123
     */
    @RequestMapping(value = "/view2/{courseId}", method = RequestMethod.GET)//{courseId}表示他是一个路径变量
    public String viewCourse2(@PathVariable("courseId") Integer courseId, Map<String, Object> model) {//courseId绑定到URL中某一个部分，也就是在方法参数中通过@PathVariable("courseId")指明其修饰的参数courseId对应这个路径变量
        log.debug("In viewCourse2 ,courseiD = {}", courseId);//通过日志查看参数courseId绑定是否正确
        Course course = courseService.getCoursebyId(courseId);
        model.put("course", course);//spring mvc可以使用3种形式的model：map、object、XXX?
        return "course_overview";
    }

    /**
     * Spring mvc集成传统的HttpServletRequest方式
     * <p>
     * 参数：HttpServletRequest需要增加新的依赖
     * <p>
     * 本方法将处理：/courses/view3?/courseId=123的请求
     * <p>
     * 测试访问URL:http://localhost:8080/courses/view3?courseId=456
     */
    @RequestMapping("view3")
    public String viewCourse3(HttpServletRequest request) {//courseId绑定到URL中某一个部分，也就是在方法参数中通过@PathVariable("courseId")指明其修饰的参数courseId对应这个路径变量
        //1、通过request获取查询参数
        Integer courseId = Integer.parseInt(request.getParameter("courseId"));
        //2、根据courseId检索课程
        Course course = courseService.getCoursebyId(courseId);
        //3、将检索到的课程，放入request中，供页面使用
        request.setAttribute("course", course);
        return "course_overview";
    }

    //-------------------binding-------------------------start

    /**
     * 1、value = "/admin"：方法级URL
     * 2、method = RequestMethod.GET：GET类型请求
     * 3、params = "add"：限制请求参数中需要有add
     *
     * @return
     */
    @RequestMapping(value = "/admin", method = RequestMethod.GET, params = "add")
    public String createCourse() {
        //调用此url，将跳转至一个编辑页面
        //由于web.xml -> mvc-dispatcher-servlet.xml -> 使用了InternalResourceViewResolver，其中通过前缀，后缀指定的展示哪个jsp页面，所以这里返回jsp页面的名称（包括相对路径）即可。返回
        return "course_admin/edit";
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String doSave(@ModelAttribute Course course) {
        log.debug("Info of Course : ");
        log.debug(ReflectionToStringBuilder.toString(course));//ReflectionToStringBuilder是apache的common-lang工具包中的，用来把一个对象属性转换成键值对形式
        //在此进行业务操作，比如数据库持久化
        course.setCourseId(123);
        //这里使用请求重定向的方式（还有请求转发forward的方式，这两个概念面试经常会被问到）
        return "redirect:view2/" + course.getCourseId();
    }
    //-------------------binding-------------------------end

    //-------------------文件上传-------------------------start
    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    public String showUploadPage() {
        return "course_admin/file";
    }

    /**
     * RequestParam注解使得controller方法参数和一个表单元素相关联，这里的file对应jsp页面中：<input type="file" name="file"/>的name值
     *
     * @param multipartFile
     * @return
     */
    @RequestMapping(value = "/doUpload", method = RequestMethod.POST)
    public String doUploadFile(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        //服务器端获取到文件后，将文件进行存储，供其他业务使用
        if (!multipartFile.isEmpty()) {
            log.debug("Process file: {} ", multipartFile.getOriginalFilename());
            //这里只将文件保存到一个临时目录
            FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), new File("E:\\01.study\\04.springmvc\\workspace\\springmvc\\temp", System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename()));
        }
        return "success";
    }
    //-------------------文件上传-------------------------end

    //-------------------支持JSON1-------------------------start

    /**
     * 本方法将处理：/courses/view4/{courseId}的URL的Get类型请求，如：http://localhost:8080/courses/view4/123
     * <p>
     * method = RequestMethod.GET：GET类型请求
     * <p>
     * 使用@PathVariable关联查询参数
     * <p>
     * 通过@ResponseBody注解在方法的返回值上可以以最简单的方式将返回的模型数据呈现成JSON格式
     *
     * @param courseId
     * @return
     */
    @RequestMapping(value = "/view4/{courseId}", method = RequestMethod.GET)
    public @ResponseBody
    Course getCourseInJSON(@PathVariable Integer courseId) {
        return courseService.getCoursebyId(courseId);
    }

    //-------------------支持JSON1-------------------------end

    //-------------------支持JSON2-------------------------start

    /**
     * 本方法将处理：/jsontype/{courseId}的URL的Get类型请求，如：http://localhost:8080/courses/jsontype/123
     * <p>
     * method = RequestMethod.GET：GET类型请求
     * <p>
     * 使用@PathVariable关联查询参数
     * <p>
     * 通过定义方法返回值为：ResponseEntity<Course>，其中ResponseEntity是spring mvc为我们抽象的实体，通过泛型的方式包装我们的业务实体，此时不需要通过@ResponseBody对返回值进行修饰，这样就可以向浏览器返回一个JSON的数据
     *
     * @param courseId
     * @return
     */
    @RequestMapping(value = "/jsontype/{courseId}", method = RequestMethod.GET)
    public ResponseEntity<Course> getCourseInJSON2(@PathVariable Integer courseId) {
        Course course = courseService.getCoursebyId(courseId);
        return new ResponseEntity<Course>(course, HttpStatus.OK);
    }

    //-------------------支持JSON2-------------------------end


}
