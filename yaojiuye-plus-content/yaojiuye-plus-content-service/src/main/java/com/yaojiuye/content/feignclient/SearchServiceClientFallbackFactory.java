package com.yaojiuye.content.feignclient;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author itnan
 * @ClassName SearchServiceClientFallbackFactory
 * @Description 搜索服务降级
 * @Date 2025/4/5 23:30
 * @Version V1.0
 */
@Slf4j
@Component
public class SearchServiceClientFallbackFactory implements FallbackFactory<SearchServiceClient> {
    @Override
    public SearchServiceClient create(Throwable throwable) {

        return new SearchServiceClient() {

            @Override
            public Boolean add(CourseIndex courseIndex) {
                throwable.printStackTrace();
                log.debug("调用搜索发生熔断走降级方法,课程索引: {} ,熔断异常:{}", courseIndex, throwable.getMessage());
                return false;
            }
        };
    }
}
