package cn.joey.utils;

import cn.joey.solr.constant.DefaultConstant;
import org.apache.commons.lang3.StringUtils;

/**
 * @author dunhanson
 * @date 2019-05-14
 */
public class CommonUtils {

    /**
     * 获取配置文件名称
     * @return
     */
    public static String getConfigFileName() {
        //默认文件名称
        String configFileName = DefaultConstant.CONFIG_FILE_NAME;
        String active = System.getenv(DefaultConstant.ENV_PROFILES_ACTIVE);
        if(StringUtils.isNotBlank(active)) {
            String name = configFileName.substring(0, configFileName.indexOf("."));
            String suffix = configFileName.substring(configFileName.indexOf("."));
            configFileName = (name + "-" + active + suffix);
        }
        return configFileName;
    }

}
