/**
 * Baidu.com,Inc.
 * Copyright (c) 2000-2013 All Rights Reserved.
 */
package com.baidu.hsb.server.response;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.baidu.hsb.HeisenbergCluster;
import com.baidu.hsb.HeisenbergConfig;
import com.baidu.hsb.HeisenbergNode;
import com.baidu.hsb.HeisenbergServer;
import com.baidu.hsb.config.Alarms;
import com.baidu.hsb.config.Fields;
import com.baidu.hsb.config.model.config.HeisenbergNodeConfig;
import com.baidu.hsb.config.model.config.SchemaConfig;
import com.baidu.hsb.mysql.PacketUtil;
import com.baidu.hsb.net.mysql.EOFPacket;
import com.baidu.hsb.net.mysql.FieldPacket;
import com.baidu.hsb.net.mysql.ResultSetHeaderPacket;
import com.baidu.hsb.net.mysql.RowDataPacket;
import com.baidu.hsb.server.ServerConnection;
import com.baidu.hsb.util.IntegerUtil;
import com.baidu.hsb.util.StringUtil;

/**
 * @author xiongzhao@baidu.com
 */
public class ShowCobarCluster {

    private static final Logger alarm = Logger.getLogger("alarm");

    private static final int FIELD_COUNT = 2;
    private static final ResultSetHeaderPacket header = PacketUtil.getHeader(FIELD_COUNT);
    private static final FieldPacket[] fields = new FieldPacket[FIELD_COUNT];
    private static final EOFPacket eof = new EOFPacket();
    static {
        int i = 0;
        byte packetId = 0;
        header.packetId = ++packetId;
        fields[i] = PacketUtil.getField("HOST", Fields.FIELD_TYPE_VAR_STRING);
        fields[i++].packetId = ++packetId;
        fields[i] = PacketUtil.getField("WEIGHT", Fields.FIELD_TYPE_LONG);
        fields[i++].packetId = ++packetId;
        eof.packetId = ++packetId;
    }

    public static void response(ServerConnection c) {
        ByteBuffer buffer = c.allocate();

        // write header
        buffer = header.write(buffer, c);

        // write field
        for (FieldPacket field : fields) {
            buffer = field.write(buffer, c);
        }

        // write eof
        buffer = eof.write(buffer, c);

        // write rows
        byte packetId = eof.packetId;
        for (RowDataPacket row : getRows(c)) {
            row.packetId = ++packetId;
            buffer = row.write(buffer, c);
        }

        // last eof
        EOFPacket lastEof = new EOFPacket();
        lastEof.packetId = ++packetId;
        buffer = lastEof.write(buffer, c);

        // post write
        c.write(buffer);
    }

    private static List<RowDataPacket> getRows(ServerConnection c) {
        List<RowDataPacket> rows = new LinkedList<RowDataPacket>();
        HeisenbergConfig config = HeisenbergServer.getInstance().getConfig();
        HeisenbergCluster cluster = config.getCluster();
        Map<String, SchemaConfig> schemas = config.getSchemas();
        SchemaConfig schema = (c.getSchema() == null) ? null : schemas.get(c.getSchema());

        // 如果没有指定schema或者schema为null，则使用全部集群。
        if (schema == null) {
            Map<String, HeisenbergNode> nodes = cluster.getNodes();
            for (HeisenbergNode n : nodes.values()) {
                if (n != null && n.isOnline()) {
                    rows.add(getRow(n, c.getCharset()));
                }
            }
        } else {
            String group = (schema.getGroup() == null) ? "default" : schema.getGroup();
            List<String> nodeList = cluster.getGroups().get(group);
            if (nodeList != null && nodeList.size() > 0) {
                Map<String, HeisenbergNode> nodes = cluster.getNodes();
                for (String id : nodeList) {
                    HeisenbergNode n = nodes.get(id);
                    if (n != null && n.isOnline()) {
                        rows.add(getRow(n, c.getCharset()));
                    }
                }
            }
            // 如果schema对应的group或者默认group都没有有效的节点，则使用全部集群。
            if (rows.size() == 0) {
                Map<String, HeisenbergNode> nodes = cluster.getNodes();
                for (HeisenbergNode n : nodes.values()) {
                    if (n != null && n.isOnline()) {
                        rows.add(getRow(n, c.getCharset()));
                    }
                }
            }
        }

        if (rows.size() == 0) {
            alarm.error(Alarms.CLUSTER_EMPTY + c.toString());
        }

        return rows;
    }

    private static RowDataPacket getRow(HeisenbergNode node, String charset) {
        HeisenbergNodeConfig conf = node.getConfig();
        RowDataPacket row = new RowDataPacket(FIELD_COUNT);
        row.add(StringUtil.encode(conf.getHost(), charset));
        row.add(IntegerUtil.toBytes(conf.getWeight()));
        return row;
    }

}
