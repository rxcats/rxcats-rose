package io.github.rxcats.rose.chat;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class ConcurrentHashMapTest {

    @Test
    void concurrentHashMap_Put_Remove_Return_Test() {
        var map = new ConcurrentHashMap<String, Object>();
        Object put = map.put("1", 1);
        assertThat(put).isNull();

        Object remove1 = map.remove("1");
        assertThat(remove1).isEqualTo(1);

        Object remove2 = map.remove("2");
        assertThat(remove2).isNull();
    }

}
