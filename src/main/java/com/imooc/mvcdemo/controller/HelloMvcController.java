package com.imooc.mvcdemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

//告诉DispatcherServlet相关的容器， 这是一个Controller， 管理好这个bean哦
@Controller
//类级别的RequestMapping，告诉DispatcherServlet由这个类负责处理的URL。
//HandlerMapping依靠这个标签来工作
@RequestMapping("/hello")
public class HelloMvcController {
	
	//方法级别的RequestMapping， 限制并缩小了URL路径匹配，同类级别的标签协同工作，最终确定拦截到的URL由那个方法处理
	//URL：localhost:8080/hello/mvc
	@RequestMapping("/mvc")
	public String helloMvc() {

		//返回的内容为请求的结果jsp：在mvc-dispatcher-servlet.xml的<bean
		//		class="org.springframework.web.servlet.view.InternalResourceViewResolver">配置的前缀、后缀匹配模式
		//视图渲染，/WEB-INF/jsps/home.jsp
		return "home";
	}

}
