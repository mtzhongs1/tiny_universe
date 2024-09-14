package com.ailu.server.service.bottle;

import com.ailu.entity.Bottle;

import java.util.List;

/**
 * @author: ailu
 * @description: TODO
 * @date: 2024/9/2 下午1:40
 */

public interface BottleService {
    Bottle getBottle();

    void saveBottle(Bottle bottle);

    List<Bottle> getBottles(Integer type);

    List<Bottle> getBottles(Long parentId);

}
