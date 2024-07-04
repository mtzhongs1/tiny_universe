package com.ailu.vo.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/7/4 上午11:34
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataAndCnt<T> {
    List<T> data;
    Integer cnt;
}
