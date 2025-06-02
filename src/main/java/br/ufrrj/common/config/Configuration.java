package br.ufrrj.common.config;

import java.io.InputStream;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public class Configuration {

    private final Map<String, Object> config;

    public Configuration(String fileName) {
        Yaml yaml = new Yaml(); 
        InputStream inputStream = Configuration.class.getClassLoader().getResourceAsStream(fileName);
        this.config = yaml.load(inputStream);
    }

    public Map<String, Object> getConfig() {
        return this.config;
    }

    private Object getConfigurationValue(String key) {
        String[] keys = key.split("\\.");
        Object value = this.config.get(keys[0]);

        for (int i = 1; i < keys.length; i++) {
            if (i == 0) {
                value = this.config.get(keys[i]);
            } else {
                value = ((Map<String, Object>) value).get(keys[i]);
            }
        }

        return value;
    }

    public String getString(String key) {
        return (String) getConfigurationValue(key);
    }

    public int getInt(String key) {
        return (Integer) getConfigurationValue(key);
    }

}
