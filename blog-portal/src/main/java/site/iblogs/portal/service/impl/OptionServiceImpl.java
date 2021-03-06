package site.iblogs.portal.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import site.iblogs.common.model.ConfigKey;
import site.iblogs.mapper.OptionsMapper;
import site.iblogs.model.Options;
import site.iblogs.model.OptionsExample;
import site.iblogs.portal.service.OptionService;
import site.iblogs.portal.service.RedisService;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

@Service
public class OptionServiceImpl implements OptionService {

    private OptionsMapper optionsMapper;

    private RedisService<Options> redisService;

    private static boolean initializedOptionsToRedis = false;
    private static String optionsPreKey = "IBLOGS.SITE.OPTIONS";

    public OptionServiceImpl(OptionsMapper optionsMapper, RedisService<Options> redisService) {
        this.optionsMapper = optionsMapper;
        this.redisService = redisService;
        if (!initializedOptionsToRedis) {
            InitOptionsToRedis();
        }
    }

    private void InitOptionsToRedis() {
        if (initializedOptionsToRedis) {
            return;
        }
        List<Options> allOptions = getAllOption();
        allOptions.forEach(u -> {
            if (!StringUtils.isEmpty(u.getValue())) {
                redisService.set(optionsPreKey + u.getName(), u);
            }
        });
        initializedOptionsToRedis = true;
    }

    @Override
    public Options getOption(ConfigKey key) {
        Options value = redisService.get(optionsPreKey + key.name());
        if (value == null || StringUtils.isEmpty(value.getValue())) {
            OptionsExample example = new OptionsExample();
            OptionsExample.Criteria criteria=example.createCriteria();
            criteria.andDeletedEqualTo(false);
            criteria.andNameEqualTo(key.toString());
            List<Options> options = optionsMapper.selectByExample(example);
            if (options.size() > 0) {
                value = options.get(0);
                redisService.set(optionsPreKey + value.getName(), value);
            }
        }
        return value;
    }

    @Override
    public Hashtable<String, String> getOptions(ArrayList<String> keys) {
        Hashtable<String, String> result = new Hashtable<>();
        if (keys == null) {
            return null;
        }

        for (String s : keys) {
            try {
                ConfigKey key = ConfigKey.valueOf(s);
                Options option = getOption(key);
                if (option == null || !option.getVisible()) {
                    result.put(s, "NULL");
                } else {
                    String value = option.getValue();
                    if (value == null) {
                        value = "NULL";
                    }
                    result.put(s, value);
                }
            } catch (IllegalArgumentException ex) {
                result.put(s, "NULL");
            }
        }
        return result;
    }

    @Override
    public List<Options> getAllOption() {
        OptionsExample example=new OptionsExample();
        example.createCriteria().andDeletedEqualTo(false);
        return optionsMapper.selectByExample(example);
    }
}
