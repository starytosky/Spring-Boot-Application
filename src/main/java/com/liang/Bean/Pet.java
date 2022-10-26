package com.liang.Bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author liang
 * @Package com.liang.Bean
 * @date 2022/10/20 11:04
 */
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class Pet {
    private String name;
}
