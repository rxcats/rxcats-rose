package io.github.rxcats.rose.chat.ws;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HystrixThreadGroup {

    private String desc;

    private String groupName;

    private int threadSize;

    private int queueSize;

    private int timeout;

}
