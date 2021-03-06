package com.resin.tool;

import com.resin.beans.Config;
import com.resin.daos.ConfigDao;
import org.springframework.beans.factory.annotation.Autowired;

import static com.resin.common.utils.CheckUtil.check;

public class ConfigUtil {

    private static ConfigDao configDao;

    public static void setConfigDao(ConfigDao configDao) {
        ConfigUtil.configDao = configDao;
    }

    public static  String get(String name){
        Config config =  configDao.findByName(name);

        check(config != null, "config.not.exist", name);

        return config.getValue();
    }

    public static  String get(String name, String defaultValue){
        Config config =  configDao.findByName(name);
        return config != null ? config.getValue() : defaultValue;
    }

    public static  String getInt(String name){
        throw new UnsupportedOperationException("等你实现");
    }
}
