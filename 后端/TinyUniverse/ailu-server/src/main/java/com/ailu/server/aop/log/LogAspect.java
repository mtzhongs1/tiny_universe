package com.ailu.server.aop.log;

import com.ailu.aop.HttpMethod;
import com.ailu.aop.Log;
import com.ailu.aop.OperationStatus;
import com.ailu.config.PropertyPreExcludeFilter;
import com.ailu.context.BaseContext;
import com.ailu.entity.LogEntity;
import com.ailu.server.service.Log.LogService;
import com.ailu.util.http.AddressUtils;
import com.ailu.util.http.IpUtils;
import com.ailu.util.http.ServletUtils;
import com.alibaba.fastjson2.JSON;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.NamedThreadLocal;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/7/8 上午1:00
 */
@Aspect
@Component
public class LogAspect {

    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    private LogService logService;

    private static final Logger log = LoggerFactory.getLogger(LogAspect.class);
    /** 排除敏感属性字段 */
    public static final String[] EXCLUDE_PROPERTIES = { "password" };
    /** 计算操作消耗时间 */
    private static final ThreadLocal<Long> TIME_THREADLOCAL = new NamedThreadLocal<Long>("Cost Time");
    /**
     * 处理请求前执行，计算请求最初的时间戳
     */

    //TODO:这里的controllerLog是根据形参得来的，这是@annotation的另一种写法
    @Before(value = "@annotation(controllerLog)")
    public void boBefore(JoinPoint joinPoint, Log controllerLog)
    {
        TIME_THREADLOCAL.set(System.currentTimeMillis());
    }

    /**
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     */

    //TODO：returning表示拦截方法的返回值并取名为jsonResult
    @AfterReturning(pointcut = "@annotation(controllerLog)", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Log controllerLog, Object jsonResult)
    {
        handleLog(joinPoint, controllerLog, null, jsonResult);
    }

    /**
     * 拦截异常操作
     *
     * @param joinPoint 切点
     * @param e 异常
     */
    @AfterThrowing(value = "@annotation(controllerLog)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Log controllerLog, Exception e)
    {
        handleLog(joinPoint, controllerLog, e, null);
    }

    protected void handleLog(final JoinPoint joinPoint, Log controllerLog, final Exception e, Object jsonResult)
    {
        try
        {
            // 获取当前的用户id
            Long userId = BaseContext.getCurrentId();
            // *========数据库日志=========*//
            LogEntity logEntity = new LogEntity();
            logEntity.setUserId(userId);
            logEntity.setStatus(OperationStatus.SUCCESS.ordinal());

            // 请求的地址
            String ip = IpUtils.getIpAddr();
            logEntity.setIp(ip);
            logEntity.setUrl(StringUtils.substring(ServletUtils.getRequest().getRequestURI(), 0, 255));
            logEntity.setLocation(AddressUtils.getRealAddressByIP(logEntity.getIp()));
            logEntity.setOperTime(LocalDateTime.now());
            if (e != null)
            {
                logEntity.setStatus(OperationStatus.FAIL.ordinal());
                logEntity.setErrorMsg(StringUtils.substring(e.getMessage(), 0, 2000));
            }
            // 设置方法
            //TODO.获取类：getTarget  获取方法：getSignature()
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            logEntity.setMethod(className + "." + methodName + "()");
            // 设置请求方式
            logEntity.setRequestMethod(ServletUtils.getRequest().getMethod());
            // 处理设置注解上的参数
            getControllerMethodDescription(joinPoint, controllerLog, logEntity, jsonResult);
            // 设置消耗时间
            logEntity.setCostTime(System.currentTimeMillis() - TIME_THREADLOCAL.get());
            // 保存数据库
            // AsyncManager.me().execute(AsyncFactory.recordOper(logEntity));
            threadPoolTaskExecutor.execute(() ->{
                logService.insertLog(logEntity);
            },50);
        }
        catch (Exception exp)
        {
            // 记录本地异常日志
            log.error("异常信息:{}", exp.getMessage());
        }
        finally
        {
            TIME_THREADLOCAL.remove();
        }
    }

    /**
     * 获取注解中对方法的描述信息 用于Controller层注解
     *
     * @param log 日志
     * @param logEntity 操作日志
     */
    public void getControllerMethodDescription(JoinPoint joinPoint, Log log, LogEntity logEntity, Object jsonResult) {
        // 设置action动作
        logEntity.setOperationType(log.operationType().ordinal());
        // 设置标题
        logEntity.setTitle(log.title());
        // 设置操作人类别
        logEntity.setOperatorType(log.operatorType().ordinal());
        // 是否需要保存request，参数和值
        if (log.isSaveRequestData())
        {
            // 获取参数的信息，传入到数据库中。
            setRequestValue(joinPoint, logEntity, log.excludeParamNames());
        }
        // 是否需要保存response，参数和值
        if (log.isSaveResponseData() && !ObjectUtils.isEmpty(jsonResult))
        {
            logEntity.setJsonResult(StringUtils.substring(JSON.toJSONString(jsonResult), 0, 2000));
        }
    }

    /**
     * 获取请求的参数，放到log中
     *
     * @param logEntity 操作日志
     */
    private void setRequestValue(JoinPoint joinPoint, LogEntity logEntity, String[] excludeParamNames) {
        Map<?, ?> paramsMap = ServletUtils.getParamMap(ServletUtils.getRequest());
        String requestMethod = logEntity.getRequestMethod();
        if (ObjectUtils.isEmpty(paramsMap)
                && (HttpMethod.PUT.name().equals(requestMethod) || HttpMethod.POST.name().equals(requestMethod)))
        {
            String params = argsArrayToString(joinPoint.getArgs(), excludeParamNames);
            logEntity.setOperParam(StringUtils.substring(params, 0, 2000));
        }
        else
        {
            logEntity.setOperParam(StringUtils.substring(JSON.toJSONString(paramsMap, excludePropertyPreFilter(excludeParamNames)), 0, 2000));
        }
    }

    /**
     * 参数拼装
     */
    private String argsArrayToString(Object[] paramsArray, String[] excludeParamNames)
    {
        StringBuilder params = new StringBuilder();
        if (paramsArray != null)
        {
            for (Object o : paramsArray)
            {
                if (ObjectUtils.isNotEmpty(o) && !isFilterObject(o))
                {
                    try
                    {
                        //excludePropertyPreFilter(excludeParamNames) ： 是参数过滤器，排除指定的参数
                        String jsonObj = JSON.toJSONString(o, excludePropertyPreFilter(excludeParamNames));
                        params.append(jsonObj).append(" ");
                    }
                    catch (Exception ignored)
                    {
                    }
                }
            }
        }
        return params.toString().trim();
    }

    /**
     * 忽略敏感属性
     */
    public PropertyPreExcludeFilter excludePropertyPreFilter(String[] excludeParamNames)
    {
        return new PropertyPreExcludeFilter().addExcludes(ArrayUtils.addAll(EXCLUDE_PROPERTIES, excludeParamNames));
    }

    /**
     * 判断是否需要过滤的对象。
     *
     * @param o 对象信息。
     * @return 如果是需要过滤的对象，则返回true；否则返回false。
     */
    @SuppressWarnings("rawtypes")
    public boolean isFilterObject(final Object o)
    {
        Class<?> clazz = o.getClass();
        if (clazz.isArray())
        {
            return clazz.getComponentType().isAssignableFrom(MultipartFile.class);
        }
        else if (Collection.class.isAssignableFrom(clazz))
        {
            Collection collection = (Collection) o;
            for (Object value : collection)
            {
                return value instanceof MultipartFile;
            }
        }
        else if (Map.class.isAssignableFrom(clazz))
        {
            Map map = (Map) o;
            for (Object value : map.entrySet())
            {
                Map.Entry entry = (Map.Entry) value;
                return entry.getValue() instanceof MultipartFile;
            }
        }
        return o instanceof MultipartFile || o instanceof HttpServletRequest || o instanceof HttpServletResponse
                || o instanceof BindingResult;
    }
}
