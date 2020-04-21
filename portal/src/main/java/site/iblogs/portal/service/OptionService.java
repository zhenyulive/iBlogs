package site.iblogs.portal.service;

import site.iblogs.common.model.ConfigKey;
import site.iblogs.model.Options;

import java.util.List;

public interface OptionService {
    List<Options> getAllOption();
    String getOption(ConfigKey key);
}
