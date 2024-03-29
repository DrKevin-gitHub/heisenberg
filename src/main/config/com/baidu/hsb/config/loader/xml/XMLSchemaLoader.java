/**
 * Baidu.com,Inc.
 * Copyright (c) 2000-2013 All Rights Reserved.
 */
package com.baidu.hsb.config.loader.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.baidu.hsb.config.loader.SchemaLoader;
import com.baidu.hsb.config.model.RealTableCache;
import com.baidu.hsb.config.model.config.DataNodeConfig;
import com.baidu.hsb.config.model.config.DataSourceConfig;
import com.baidu.hsb.config.model.config.SchemaConfig;
import com.baidu.hsb.config.model.config.TableConfig;
import com.baidu.hsb.config.model.config.TableRuleConfig;
import com.baidu.hsb.config.util.ConfigException;
import com.baidu.hsb.config.util.ConfigUtil;
import com.baidu.hsb.config.util.ParameterMapping;
import com.baidu.hsb.route.util.StringUtil;
import com.baidu.hsb.util.SplitUtil;

/**
 * 
 * 
 * @author xiongzhao@baidu.com
 * @version $Id: XMLSchemaLoader.java, v 0.1 2013年12月21日 下午12:53:36 HI:brucest0078 Exp $
 */
@SuppressWarnings("unchecked")
public class XMLSchemaLoader implements SchemaLoader {
    //private final static String                 DEFAULT_DTD = "/schema.dtd";
    private final static String                 DEFAULT_XML = "/schema.xml";

    private final Map<String, TableRuleConfig>  tableRules;

    private final Map<String, DataSourceConfig> dataSources;
    private final Map<String, DataNodeConfig>   dataNodes;
    private final Map<String, SchemaConfig>     schemas;

    public XMLSchemaLoader(String configPath) {
        String schemaFile = null;
        String ruleFile = null;
        if (!StringUtil.isEmpty(configPath)) {
            File f = new File(configPath);
            schemaFile = f.getPath() + File.separator + DEFAULT_XML;
            ruleFile = f.getPath() + File.separator + XMLRuleLoader.DEFAULT_XML;
        }

        XMLRuleLoader ruleLoader = new XMLRuleLoader(ruleFile);
        this.tableRules = ruleLoader.getTableRules();
        ruleLoader = null;
        this.dataSources = new HashMap<String, DataSourceConfig>();
        this.dataNodes = new HashMap<String, DataNodeConfig>();
        this.schemas = new HashMap<String, SchemaConfig>();
        if (schemaFile == null) {
            this.load(DEFAULT_XML, true);
        } else {
            this.load(schemaFile, false);
        }

    }

    public XMLSchemaLoader() {
        this(null);
    }

    @Override
    public Map<String, TableRuleConfig> getTableRules() {
        return tableRules;
    }

    @Override
    public Map<String, DataSourceConfig> getDataSources() {
        return (Map<String, DataSourceConfig>) (dataSources.isEmpty() ? Collections.emptyMap()
            : dataSources);
    }

    @Override
    public Map<String, DataNodeConfig> getDataNodes() {
        return (Map<String, DataNodeConfig>) (dataNodes.isEmpty() ? Collections.emptyMap()
            : dataNodes);
    }

    @Override
    public Map<String, SchemaConfig> getSchemas() {
        return (Map<String, SchemaConfig>) (schemas.isEmpty() ? Collections.emptyMap() : schemas);
    }

    //    @Override
    //    public Set<RuleConfig> listRuleConfig() {
    //        return rules;
    //    }

    private void load(String xmlFile, boolean isCp) {
        InputStream xml = null;
        try {
            if (isCp) {
                xml = XMLSchemaLoader.class.getResourceAsStream(xmlFile);
            } else {
                xml = new FileInputStream(xmlFile);
            }

            Element root = ConfigUtil.getDocument(xml).getDocumentElement();
            loadDataSources(root);
            loadDataNodes(root);
            loadSchemas(root);
        } catch (ConfigException e) {
            throw e;
        } catch (Throwable e) {
            throw new ConfigException(e);
        } finally {

            if (xml != null) {
                try {
                    xml.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private void loadSchemas(Element root) {
        NodeList list = root.getElementsByTagName("schema");
        for (int i = 0, n = list.getLength(); i < n; i++) {
            Element schemaElement = (Element) list.item(i);
            String name = schemaElement.getAttribute("name");
            String dataNode = schemaElement.getAttribute("dataNode");
            // 在非空的情况下检查dataNode是否存在
            if (dataNode != null && dataNode.length() != 0) {
                checkDataNodeExists(dataNode);
            } else {
                dataNode = "";// 确保非空
            }
            String group = "default";
            if (schemaElement.hasAttribute("group")) {
                group = schemaElement.getAttribute("group").trim();
            }
            Map<String, TableConfig> tables = loadTables(schemaElement);
            if (schemas.containsKey(name)) {
                throw new ConfigException("schema " + name + " duplicated!");
            }
            boolean keepSqlSchema = false;
            if (schemaElement.hasAttribute("keepSqlSchema")) {
                keepSqlSchema = Boolean.parseBoolean(schemaElement.getAttribute("keepSqlSchema")
                    .trim());
            }
            schemas.put(name, new SchemaConfig(name, dataNode, group, keepSqlSchema, tables));
        }
    }

    private Map<String, TableConfig> loadTables(Element node) {
        Map<String, TableConfig> tables = new HashMap<String, TableConfig>();
        NodeList nodeList = node.getElementsByTagName("table");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element tableElement = (Element) nodeList.item(i);
            String name = tableElement.getAttribute("name");
            String dataNode = tableElement.getAttribute("dataNode");
            TableRuleConfig tableRule = null;
            if (tableElement.hasAttribute("rule")) {
                String ruleName = tableElement.getAttribute("rule");
                tableRule = tableRules.get(ruleName);
                if (tableRule == null) {
                    throw new ConfigException("rule " + ruleName + " is not found!");
                }
            }
            boolean ruleRequired = false;
            if (tableElement.hasAttribute("ruleRequired")) {
                ruleRequired = Boolean.parseBoolean(tableElement.getAttribute("ruleRequired"));
            }

            String[] tableNames = SplitUtil.split(name, ',', true);
            for (String tableName : tableNames) {
                //
                RealTableCache.put(tableName);
                //
                TableConfig table = new TableConfig(tableName, dataNode, tableRule, ruleRequired);
                checkDataNodeExists(table.getDataNodes());
                if (tables.containsKey(table.getNameUp())) {
                    throw new ConfigException("table " + tableName + " duplicated!");
                }
                tables.put(table.getNameUp(), table);
            }
        }
        return tables;
    }

    private void checkDataNodeExists(String... nodes) {
        if (nodes == null || nodes.length < 1) {
            return;
        }
        for (String node : nodes) {
            if (!dataNodes.containsKey(node)) {
                throw new ConfigException("dataNode '" + node + "' is not found!");
            }
        }
    }

    private void loadDataNodes(Element root) {
        NodeList list = root.getElementsByTagName("dataNode");
        for (int i = 0, n = list.getLength(); i < n; i++) {
            Element element = (Element) list.item(i);
            String dnNamePrefix = element.getAttribute("name");

            List<DataNodeConfig> confList = new ArrayList<DataNodeConfig>();
            try {
                Element dsElement = findPropertyByName(element, "dataSource");
                Element rwRule = findPropertyByName(element, "rwRule");
                if (dsElement == null) {
                    throw new NullPointerException("dataNode xml Element with name of "
                                                   + dnNamePrefix + " has no dataSource Element");
                }

                NodeList dataSourceList = dsElement.getElementsByTagName("dataSourceRef");
                String dataSources[][] = new String[dataSourceList.getLength()][];
                for (int j = 0, m = dataSourceList.getLength(); j < m; ++j) {
                    Element ref = (Element) dataSourceList.item(j);
                    String dsString = ref.getTextContent();
                    dataSources[j] = SplitUtil.split(dsString, ',', '$', '-', '[', ']');
                }
                if (dataSources.length <= 0) {
                    throw new ConfigException("no dataSourceRef defined!");
                }
                for (String[] dss : dataSources) {
                    if (dss.length != dataSources[0].length) {
                        throw new ConfigException("dataSource number not equals!");
                    }
                }
                for (int k = 0, limit = dataSources[0].length; k < limit; ++k) {
                    StringBuilder dsString = new StringBuilder();
                    for (int dsIndex = 0; dsIndex < dataSources.length; ++dsIndex) {
                        if (dsIndex > 0) {
                            dsString.append(',');
                        }
                        dsString.append(dataSources[dsIndex][k]);
                    }
                    DataNodeConfig conf = new DataNodeConfig();
                    ParameterMapping.mapping(conf, ConfigUtil.loadElements(element));
                    confList.add(conf);
                    switch (k) {
                        case 0:
                            conf.setName((limit == 1) ? dnNamePrefix : dnNamePrefix + "[" + k + "]");
                            break;
                        default:
                            conf.setName(dnNamePrefix + "[" + k + "]");
                            break;
                    }
                    if (rwRule != null) {
                        String rwStr = StringUtil.trim(rwRule.getTextContent());
                        if (StringUtil.isNotEmpty(rwStr)) {
                            conf.setNeedWR(true);
                            String[] rwStrArr = StringUtil.split(rwStr, ',');
                            int mW = 0;
                            int sW = 0;
                            for (String rwArr : rwStrArr) {
                                String[] pair = rwArr.split(":");
                                if (StringUtil.equals(pair[0], "m")) {
                                    mW = NumberUtils.toInt(pair[1]);
                                }
                                if (StringUtil.equals(pair[0], "s")) {
                                    sW = NumberUtils.toInt(pair[1]);
                                }
                            }
                            conf.setMasterReadWeight(mW);
                            conf.setSlaveReadWeight(sW);
                        }
                    }

                    conf.setDataSource(dsString.toString());
                }
            } catch (Exception e) {
                throw new ConfigException("dataNode " + dnNamePrefix + " define error", e);
            }

            for (DataNodeConfig conf : confList) {
                if (dataNodes.containsKey(conf.getName())) {
                    throw new ConfigException("dataNode " + conf.getName() + " duplicated!");
                }
                dataNodes.put(conf.getName(), conf);
            }
        }
    }

    private void loadDataSources(Element root) {
        NodeList list = root.getElementsByTagName("dataSource");
        for (int i = 0, n = list.getLength(); i < n; ++i) {
            Element element = (Element) list.item(i);
            ArrayList<DataSourceConfig> dscList = new ArrayList<DataSourceConfig>();
            String dsNamePrefix = element.getAttribute("name");
            try {
                String dsType = element.getAttribute("type");
                Element locElement = findPropertyByName(element, "location");
                if (locElement == null) {
                    throw new NullPointerException("dataSource xml Element with name of "
                                                   + dsNamePrefix + " has no location Element");
                }
                NodeList locationList = locElement.getElementsByTagName("location");
                int dsIndex = 0;
                for (int j = 0, m = locationList.getLength(); j < m; ++j) {
                    String locStr = ((Element) locationList.item(j)).getTextContent();
                    int colonIndex = locStr.indexOf(':');
                    int slashIndex = locStr.indexOf('/');
                    String dsHost = locStr.substring(0, colonIndex).trim();
                    int dsPort = Integer.parseInt(locStr.substring(colonIndex + 1, slashIndex)
                        .trim());
                    String[] schemas = SplitUtil.split(locStr.substring(slashIndex + 1).trim(),
                        ',', '$', '-');
                    for (String dsSchema : schemas) {
                        DataSourceConfig dsConf = new DataSourceConfig();
                        ParameterMapping.mapping(dsConf, ConfigUtil.loadElements(element));
                        dscList.add(dsConf);
                        switch (dsIndex) {
                            case 0:
                                dsConf.setName(dsNamePrefix);
                                break;
                            case 1:
                                dscList.get(0).setName(dsNamePrefix + "[0]");
                            default:
                                dsConf.setName(dsNamePrefix + "[" + dsIndex + "]");
                        }
                        dsConf.setType(dsType);
                        dsConf.setDatabase(dsSchema);
                        dsConf.setHost(dsHost);
                        dsConf.setPort(dsPort);
                        ++dsIndex;
                    }
                }
            } catch (Exception e) {
                throw new ConfigException("dataSource " + dsNamePrefix + " define error", e);
            }
            for (DataSourceConfig dsConf : dscList) {
                if (dataSources.containsKey(dsConf.getName())) {
                    throw new ConfigException("dataSource name " + dsConf.getName() + "duplicated!");
                }
                dataSources.put(dsConf.getName(), dsConf);
            }
        }
    }

    private static Element findPropertyByName(Element bean, String name) {
        NodeList propertyList = bean.getElementsByTagName("property");
        for (int j = 0, m = propertyList.getLength(); j < m; ++j) {
            Node node = propertyList.item(j);
            if (node instanceof Element) {
                Element p = (Element) node;
                if (name.equals(p.getAttribute("name"))) {
                    return p;
                }
            }
        }
        return null;
    }

}
