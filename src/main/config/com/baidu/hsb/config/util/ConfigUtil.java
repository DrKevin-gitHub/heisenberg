/*
 * Copyright 1999-2012 Alibaba Group.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baidu.hsb.config.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.baidu.hsb.util.StringUtil;

/**
 * @author xiongzhao@baidu.com 2011-1-10 下午03:35:06
 */
public class ConfigUtil {

    public static String filter(String text) {
        return filter(text, System.getProperties());
    }

    public static String filter(String text, Properties properties) {
        StringBuilder s = new StringBuilder();
        int cur = 0;
        int textLen = text.length();
        int propStart = -1;
        int propStop = -1;
        String propName = null;
        String propValue = null;
        for (; cur < textLen; cur = propStop + 1) {
            propStart = text.indexOf("${", cur);
            if (propStart < 0) {
                break;
            }
            s.append(text.substring(cur, propStart));
            propStop = text.indexOf("}", propStart);
            if (propStop < 0) {
                throw new ConfigException("Unterminated property: " + text.substring(propStart));
            }
            propName = text.substring(propStart + 2, propStop);
            propValue = properties.getProperty(propName);
            if (propValue == null) {
                s.append("${").append(propName).append('}');
            } else {
                s.append(propValue);
            }
        }
        return s.append(text.substring(cur)).toString();
    }

    public static Document getDocument(InputStream xml) throws ParserConfigurationException,
                                                       SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        factory.setNamespaceAware(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        //不需要dtd校验
        //        builder.setEntityResolver(new EntityResolver() {
        //            @Override
        //            public InputSource resolveEntity(String publicId, String systemId) {
        //                return new InputSource(dtd);
        //            }
        //        });
        //        builder.setErrorHandler(new ErrorHandler() {
        //            @Override
        //            public void warning(SAXParseException e) {
        //            }
        //
        //            @Override
        //            public void error(SAXParseException e) throws SAXException {
        //                throw e;
        //            }
        //
        //            @Override
        //            public void fatalError(SAXParseException e) throws SAXException {
        //                throw e;
        //            }
        //        });
        return builder.parse(xml);
    }

    public static Map<String, Object> loadAttributes(Element e) {
        Map<String, Object> map = new HashMap<String, Object>();
        NamedNodeMap nm = e.getAttributes();
        for (int j = 0; j < nm.getLength(); j++) {
            Node n = nm.item(j);
            if (n instanceof Attr) {
                Attr attr = (Attr) n;
                map.put(attr.getName(), attr.getNodeValue());
            }
        }
        return map;
    }

    public static Element loadElement(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 1) {
            throw new ConfigException(tagName + " elements length  over one!");
        }
        if (nodeList.getLength() == 1) {
            return (Element) nodeList.item(0);
        } else {
            return null;
        }
    }

    public static Map<String, Object> loadElements(Element parent) {
        Map<String, Object> map = new HashMap<String, Object>();
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node instanceof Element) {
                Element e = (Element) node;
                String name = e.getNodeName();
                if ("property".equals(name)) {
                    String key = e.getAttribute("name");
                    NodeList nl = e.getElementsByTagName("bean");
                    if (nl.getLength() == 0) {
                        String value = e.getTextContent();
                        map.put(key, StringUtil.isEmpty(value) ? null : value.trim());
                    } else {
                        map.put(key, loadBean((Element) nl.item(0)));
                    }
                }
            }
        }
        return map;
    }

    public static BeanConfig loadBean(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 1) {
            throw new ConfigException(tagName + " elements length over one!");
        }
        return loadBean((Element) nodeList.item(0));
    }

    public static BeanConfig loadBean(Element e) {
        if (e == null) {
            return null;
        }
        BeanConfig bean = new BeanConfig();
        bean.setName(e.getAttribute("name"));
        Element element = loadElement(e, "className");
        if (element != null) {
            bean.setClassName(element.getTextContent());
        } else {
            bean.setClassName(e.getAttribute("class"));
        }
        bean.setParams(loadElements(e));
        return bean;
    }

}
